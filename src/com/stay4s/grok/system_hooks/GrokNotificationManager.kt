package com.stay4s.grok.system_hooks

import com.stay4s.grok.ai_runtime.ISystemActor
import com.stay4s.grok.guardian.DailyGuardianAgent

/**
 * GrokNotificationManager
 *
 * Replaces normal Android notifications with intelligent, Guardian-vetted, brain-summarized ones.
 *
 * Normal phone: Shows everything.
 * GrokPhone: The brain decides what is important, summarizes, and protects attention/privacy.
 */
class GrokNotificationManager(
    private val systemActor: ISystemActor,
    private val guardian: DailyGuardianAgent
) {

    fun handleIncomingNotification(packageName: String, title: String, text: String) {
        val risk = guardian.assessNotificationRisk(packageName, title, text)

        if (risk.isHighRisk) {
            systemActor.sendNotification(
                "Grok blocked high-risk notification",
                "From $packageName — ${risk.reason}",
                priority = 0
            )
            return
        }

        // Let the brain summarize
        val summary = "Smart summary would come from Parallel Brain here"

        systemActor.sendNotification(title, summary, priority = risk.priority)
    }
}
