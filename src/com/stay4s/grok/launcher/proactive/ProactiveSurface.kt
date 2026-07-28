package com.stay4s.grok.launcher.proactive

/**
 * Represents a proactive card / surface that the launcher can display.
 * These come primarily from the Daily Guardian and the Parallel Grok Brain.
 */
data class ProactiveSurface(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val body: String? = null,
    val priority: Int,                    // Higher = more important
    val source: SurfaceSource,            // Guardian, ParallelBrain, Genesis, etc.
    val actionType: SurfaceActionType,    // What happens when user taps
    val actionData: Map<String, String> = emptyMap(),
    val isGenesisOnly: Boolean = false,
    val expiresAt: Long? = null           // Optional TTL
)

enum class SurfaceSource {
    DAILY_GUARDIAN,
    PARALLEL_BRAIN,
    GENESIS_COVENANT,
    SYSTEM
}

enum class SurfaceActionType {
    OPEN_GROK_CHAT,           // Opens full AI with this context
    PERFORM_ACTION,           // Direct action (e.g. switch mesh network)
    SHOW_MORE_INFO,           // Expand the card
    DISMISS                    // Just dismiss
}
