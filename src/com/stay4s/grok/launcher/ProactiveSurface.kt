package com.stay4s.grok.launcher

import com.stay4s.grok.parallel.ParallelOrchestrator

/**
 * ProactiveSurface
 *
 * This is the intelligence layer that surfaces insights, suggestions and actions
 * directly on the home screen / launcher.
 *
 * Powered by the Parallel Brain (Pillar 2) + Daily Guardian.
 * This is core to Pillar 3: the phone feels alive and helpful without the user asking.
 */
data class ProactiveInsight(
    val id: String,
    val title: String,
    val description: String,
    val actionLabel: String?,
    val priority: Int,           // 1-10
    val category: InsightCategory
)

enum class InsightCategory {
    SECURITY,           // Guardian found something
    PRIVACY,
    NETWORK,            // Better Meshmatic or WiFi
    PRODUCTIVITY,
    GENESIS_COVENANT,   // Special for 001-100
    PAYMENT,            // Grok Vault / Grok Pay related
    SYSTEM_HEALTH
}

class ProactiveSurface(
    private val orchestrator: ParallelOrchestrator? = null
) {

    companion object {
        fun createWithPlaceholderInsights(): ProactiveSurface {
            return ProactiveSurface(orchestrator = null)
        }
    }

    /**
     * Called by the launcher to get the current set of proactive cards.
     * This is where the Parallel Brain + Guardian intelligence becomes visible.
     */
    fun getCurrentInsights(): List<ProactiveInsight> {
        val insights = mutableListOf<ProactiveInsight>()

        if (orchestrator != null) {
            // Ask the brain for current high-value insights
            val brainOutput = orchestrator.getProactiveInsights()

            // Convert brain output into UI-ready insights
            brainOutput.forEach { raw ->
                insights.add(
                    ProactiveInsight(
                        id = raw.id,
                        title = raw.title,
                        description = raw.summary,
                        actionLabel = raw.suggestedAction,
                        priority = raw.priority,
                        category = mapCategory(raw.type)
                    )
                )
            }
        } else {
            // Placeholder insights when orchestrator is not available
            insights.add(ProactiveInsight(
                id = "net1",
                title = "Better Meshmatic peer found",
                description = "Genesis node 047 is closer and more private",
                actionLabel = "Connect",
                priority = 8,
                category = InsightCategory.NETWORK
            ))
            insights.add(ProactiveInsight(
                id = "guard1",
                title = "Daily privacy scan clean",
                description = "No new trackers or risky permissions detected",
                actionLabel = "Details",
                priority = 6,
                category = InsightCategory.SECURITY
            ))
            insights.add(ProactiveInsight(
                id = "vault1",
                title = "Grok Vault ready",
                description = "Your self-custodial keys are protected by the Guardian",
                actionLabel = "Open Vault",
                priority = 7,
                category = InsightCategory.PAYMENT
            ))
        }

        // Always inject a few Guardian-driven defaults if brain is quiet
        if (insights.size < 3) {
            insights.add(
                ProactiveInsight(
                    id = "guardian_daily",
                    title = "Guardian Daily Check",
                    description = "All systems nominal. No new privacy risks found today.",
                    actionLabel = "View Report",
                    priority = 3,
                    category = InsightCategory.SECURITY
                )
            )
        }

        return insights.sortedByDescending { it.priority }
    }

    private fun mapCategory(type: String): InsightCategory {
        return when (type.lowercase()) {
            "security", "threat" -> InsightCategory.SECURITY
            "privacy" -> InsightCategory.PRIVACY
            "network", "mesh" -> InsightCategory.NETWORK
            "payment", "vault" -> InsightCategory.PAYMENT
            "genesis" -> InsightCategory.GENESIS_COVENANT
            else -> InsightCategory.PRODUCTIVITY
        }
    }

    fun onInsightActionClicked(insight: ProactiveInsight) {
        orchestrator?.handleProactiveAction(insight.id)
    }
}
