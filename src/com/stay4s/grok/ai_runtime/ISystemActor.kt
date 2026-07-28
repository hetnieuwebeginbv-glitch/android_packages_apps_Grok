package com.stay4s.grok.ai_runtime

/**
 * ISystemActor
 *
 * Interface for actions the AI can safely take on the device.
 * All privileged actions go through here so they can be logged, reasoned about, and (in future) require owner approval.
 */
interface ISystemActor {
    fun adjustDozeSettings(profile: String)
    fun blockApp(packageName: String, reason: String)
    fun sendNotification(title: String, body: String, priority: Int)
    fun triggerMeshmaticSync()
    fun requestOwnerConfirmation(action: String, details: String): Boolean
}
