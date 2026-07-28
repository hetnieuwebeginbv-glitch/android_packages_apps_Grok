package com.stay4s.grok.integration.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.stay4s.grok.GrokAgentCoreService
import com.stay4s.grok.guardian.DailyGuardianAgent

/**
 * Grok Boot Receiver — Ensures the Guardian and Agent start very early after boot.
 * This is part of Pillar 1 + Pillar 2 integration.
 */
class GrokBootReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "GrokBootReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "Boot completed — starting Grok Agent + Guardian (Pillar 1+2)")

        // Start the main agent service
        val agentIntent = Intent(context, GrokAgentCoreService::class.java)
        context.startForegroundService(agentIntent)

        // Trigger Daily Guardian proactive loop (Pillar 2)
        val guardianIntent = Intent(context, DailyGuardianAgent::class.java)
        context.startForegroundService(guardianIntent)
    }
}
