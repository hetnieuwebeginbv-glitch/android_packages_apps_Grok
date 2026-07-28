package com.stay4s.grok.launcher

/**
 * Raw insight coming from the Parallel Brain / Orchestrator.
 * The ProactiveSurface converts these into beautiful launcher cards.
 */
data class BrainInsight(
    val id: String,
    val title: String,
    val summary: String,
    val suggestedAction: String?,
    val priority: Int,
    val type: String
)
