package com.stay4s.grok.ai_runtime

/**
 * IContextProvider
 *
 * Abstract interface for all context the AI Brain needs.
 * This is the key to stronger separation between the Grok AI Runtime and Android.
 *
 * Goal: The core reasoning engine should be testable and portable with minimal Android dependencies.
 */
interface IContextProvider {
    fun getCurrentLocation(): String?
    fun getBatteryLevel(): Int
    fun getNetworkType(): String
    fun getActiveApps(): List<String>
    fun getUserHabitSummary(): Map<String, Any>
    fun getMeshmaticPeers(): List<String>
}
