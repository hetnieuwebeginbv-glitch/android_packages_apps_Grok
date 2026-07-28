package com.stay4s.grok

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.stay4s.aethercore.AIResponse
import com.stay4s.aethercore.IAetherCoreService
import com.stay4s.aethercore.TaskRequest
import com.stay4s.grok.guardian.DailyGuardianAgent
import com.stay4s.grok.parallel.*
import com.stay4s.grok.partnership.PartnershipVerifier
import kotlinx.coroutines.*

/**
 * GrokAgentCoreService
 *
 * The central privileged service for the full Stay4S Grok Edition.
 * This is the heart of Pillar 2 (Parallel Brain + Daily Guardian).
 *
 * Responsibilities:
 * - Start and manage the ParallelOrchestrator (multiple reasoning paths)
 * - Start and keep alive the DailyGuardianAgent
 * - Enforce Partnership verification (owner binding)
 * - Bind to AetherCore for device-level AI operations (AIDL)
 * - Provide the main @grok entry point for the rest of the system
 *
 * Runs in the isolated :agent process under the grok_agent SELinux domain.
 */
class GrokAgentCoreService : Service() {

    companion object {
        private const val TAG = "GrokAgentCore"
        private const val CHANNEL_ID = "grok_agent_channel"
        private const val NOTIFICATION_ID = 4242

        // AetherCore binding
        private const val AETHERCORE_PKG = "com.stay4s.aethercore"
        private const val AETHERCORE_CLS = "com.stay4s.aethercore.AetherCoreService"
    }

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private lateinit var orchestrator: ParallelOrchestrator
    private lateinit var dailyGuardian: DailyGuardianAgent
    private lateinit var partnership: PartnershipVerifier
    private lateinit var audit: com.stay4s.grok.audit.AuditLogger
    private lateinit var contextGraph: SharedEvolvingContextGraph

    // AetherCore AIDL binding
    private var aetherCore: IAetherCoreService? = null
    private var aetherConnected = false
    private var clientId: String? = null

    private val aetherConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            aetherCore = IAetherCoreService.Stub.asInterface(service)
            aetherConnected = true
            Log.i(TAG, "Connected to AetherCore")
            audit.log("AETHERCORE_CONNECTED")

            // Register as a client
            try {
                clientId = aetherCore?.registerClient("GrokAgent", 1)
                Log.i(TAG, "Registered with AetherCore, clientId=$clientId")

                // Ping to verify connection
                val ok = aetherCore?.ping() ?: false
                Log.i(TAG, "AetherCore ping: $ok")
            } catch (e: RemoteException) {
                Log.e(TAG, "AetherCore registration failed", e)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            aetherCore = null
            aetherConnected = false
            clientId = null
            Log.w(TAG, "Disconnected from AetherCore")
            audit.log("AETHERCORE_DISCONNECTED")
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "GrokAgentCoreService onCreate — All 3 Pillars starting")

        createNotificationChannel()

        // Partnership + Audit
        audit = com.stay4s.grok.audit.AuditLogger(this)
        partnership = PartnershipVerifier(this)

        val partnerOk = partnership.verifyPartnership()
        if (!partnerOk) {
            Log.e(TAG, "Partnership verification failed — agent will have limited functionality")
        }

        // Initialize SharedEvolvingContextGraph (long-term memory)
        contextGraph = SharedEvolvingContextGraph(partnership, audit)

        // Start the Parallel Brain (Pillar 2) — correct 3-param constructor
        orchestrator = ParallelOrchestrator(partnership, audit, contextGraph)

        // Start the Daily Guardian
        dailyGuardian = DailyGuardianAgent(partnership, audit)
        dailyGuardian.startDailyCycle()

        // Wire Pillar 3 Vault protection into the Guardian
        com.stay4s.grok.vault.GrokVault.initialize(this, dailyGuardian)

        // Deep ContextGraph integration
        com.stay4s.grok.context.GrokContextGraph.remember("device_boot", System.currentTimeMillis(), importance = 10)
        com.stay4s.grok.context.GrokContextGraph.remember("genesis_device", true, importance = 9)

        // Bind to AetherCore for device-level AI operations
        bindAetherCore()

        startForeground(NOTIFICATION_ID, buildForegroundNotification())

        Log.i(TAG, "Parallel Brain + Daily Guardian started (All 3 Pillars connected)")
        Log.i(TAG, "GenesisPath and all reasoning paths ready")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Tell Guardian to do an immediate check (proactive behaviour)
        dailyGuardian.triggerImmediateCheck()

        // Process command if present
        val rawCommand = intent?.getStringExtra("command")
        if (rawCommand != null) {
            scope.launch {
                processGrokCommand(rawCommand)
            }
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.w(TAG, "GrokAgentCoreService destroyed — attempting to keep Guardian alive")
        dailyGuardian.shutdown()
        orchestrator.shutdown()
        scope.cancel()
        if (aetherConnected) {
            try {
                clientId?.let { aetherCore?.unregisterClient(it) }
            } catch (e: RemoteException) {
                Log.w(TAG, "Failed to unregister from AetherCore", e)
            }
            unbindService(aetherConnection)
        }
    }

    // ─── AetherCore Binding ────────────────────────────────────

    private fun bindAetherCore() {
        val intent = Intent().apply {
            component = ComponentName(AETHERCORE_PKG, AETHERCORE_CLS)
        }
        try {
            val bound = bindService(intent, aetherConnection, Context.BIND_AUTO_CREATE)
            if (bound) {
                Log.i(TAG, "AetherCore bindService initiated")
            } else {
                Log.w(TAG, "AetherCore bindService returned false — service may not be running yet")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to bind to AetherCore", e)
        }
    }

    fun isAetherCoreConnected(): Boolean = aetherConnected

    /**
     * Query an AetherCore agent (e.g. "device_agent", "battery_optimizer").
     * Returns the agent's JSON response or null if AetherCore is not connected.
     */
    fun queryAetherAgent(agentId: String, args: Bundle = Bundle()): String? {
        if (!aetherConnected) return null
        return try {
            aetherCore?.queryAgent(agentId, args)
        } catch (e: RemoteException) {
            Log.e(TAG, "queryAgent failed", e)
            null
        }
    }

    /**
     * Submit a task to AetherCore for processing.
     * Returns the AIResponse or null if AetherCore is not connected.
     */
    fun submitAetherTask(agentId: String, action: String, params: Bundle = Bundle(),
                         privacyLevel: Int = 0, timeoutMs: Long = 30000): AIResponse? {
        if (!aetherConnected) return null
        return try {
            val request = TaskRequest().apply {
                this.agentId = agentId
                this.action = action
                this.params = params
                this.privacyLevel = privacyLevel
                this.timeoutMs = timeoutMs
                this.clientId = this@GrokAgentCoreService.clientId ?: ""
            }
            aetherCore?.submitTask(request)
        } catch (e: RemoteException) {
            Log.e(TAG, "submitTask failed", e)
            null
        }
    }

    // ─── Command Processing ────────────────────────────────────

    /**
     * Public API for other components (launcher, browser, etc.).
     * This is the main entry point for @grok commands.
     * Uses execute() (suspend) in a coroutine.
     * Also forwards device-level queries to AetherCore when appropriate.
     */
    suspend fun processGrokCommand(command: String): String {
        val parsed = GrokCommandParser.parse(command)
        val result = orchestrator.execute(parsed)

        // If the command is a device query, also ask AetherCore
        if (parsed.intent == "query" && aetherConnected) {
            try {
                val deviceInfo = aetherCore?.queryAgent("device_agent", Bundle())
                if (deviceInfo != null) {
                    audit.log("AETHERCORE_DEVICE_QUERY", deviceInfo)
                }
            } catch (e: RemoteException) {
                Log.w(TAG, "AetherCore device query failed", e)
            }
        }

        return result.summary
    }

    fun getParallelOrchestrator(): ParallelOrchestrator = orchestrator

    fun getDailyGuardian(): DailyGuardianAgent = dailyGuardian

    /**
     * Called by the launcher when user wants proactive intelligence surfaces.
     */
    fun getProactiveInsightsForLauncher(): List<com.stay4s.grok.launcher.BrainInsight> {
        return orchestrator.getProactiveInsights()
    }

    // ─── Notification ──────────────────────────────────────────

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Grok Agent",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Stay4S Grok Edition intelligence layer"
            setShowBadge(false)
        }
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(channel)
    }

    private fun buildForegroundNotification(): Notification {
        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Grok")
            .setContentText("Parallel Brain + Guardian active • All 3 Pillars")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .build()
    }
}
