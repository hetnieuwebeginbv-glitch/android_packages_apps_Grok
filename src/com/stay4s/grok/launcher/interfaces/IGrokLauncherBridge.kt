package com.stay4s.grok.launcher.interfaces

import com.stay4s.grok.launcher.proactive.ProactiveSurface
import com.stay4s.grok.launcher.proactive.ContextualAction
import com.stay4s.grok.parallel.ParallelExecutionResult

/**
 * Main bridge interface between the Grok Launcher (own software) and the
 * powerful Parallel Grok AI Brain + Guardian.
 *
 * The launcher only depends on this interface. The real implementation lives
 * in the privileged Grok core (system level).
 *
 * All calls must pass Partnership verification internally.
 */
interface IGrokLauncherBridge {

    /**
     * Request current proactive surfaces / cards that the launcher can show.
     * This is the main way the Daily Guardian pushes value to the home screen.
     */
    suspend fun getProactiveSurfaces(): List<ProactiveSurface>

    /**
     * Request contextual quick actions based on current context.
     * Powered by the Parallel Grok Brain.
     */
    suspend fun getContextualActions(): List<ContextualAction>

    /**
     * Request a full reasoning session from the Parallel Grok Brain.
     * Used when the user wants deep intelligence (e.g. opens the full Grok UI from the launcher).
     */
    suspend fun requestFullReasoning(
        query: String,
        contextSnapshot: Map<String, Any>? = null
    ): ParallelExecutionResult

    /**
     * Ask the brain to explain why a specific proactive surface was shown.
     * Important for transparency and trust.
     */
    suspend fun explainSurface(surfaceId: String): String?

    /**
     * Returns true if this is a Genesis 001-100 device and extra covenant
     * behavior should be enabled in the launcher.
     */
    fun isGenesisDevice(): Boolean

    /**
     * Request any Genesis-specific launcher data (subtle visuals, extra context, etc.).
     * Only meaningful on actual Genesis devices.
     */
    suspend fun getGenesisEnhancements(): GenesisEnhancementData?
}

data class GenesisEnhancementData(
    val subtleCovenantHints: Boolean,
    val specialMeshPeers: List<String> = emptyList(),
    val extraProactiveSurfaces: List<ProactiveSurface> = emptyList()
)
