package com.stay4s.grok.ai_runtime

import android.content.Context
import android.os.BatteryManager

/**
 * AndroidContextProvider
 *
 * Concrete implementation of IContextProvider for Android.
 * This is the only place where direct Android calls live for context.
 * The rest of the AI runtime stays clean.
 */
class AndroidContextProvider(
    private val context: Context
) : IContextProvider {

    override fun getCurrentLocation(): String? {
        // In real version: use fused location with Guardian approval
        return "Amsterdam (stub - privacy controlled)"
    }

    override fun getBatteryLevel(): Int {
        val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

    override fun getNetworkType(): String {
        return "MESH + WiFi (stub)"
    }

    override fun getActiveApps(): List<String> {
        return listOf("com.stay4s.grok", "system") // stub
    }

    override fun getUserHabitSummary(): Map<String, Any> {
        return mapOf(
            "night_usage" to "low",
            "meshmatic_preference" to "high",
            "vault_activity" to "medium"
        )
    }

    override fun getMeshmaticPeers(): List<String> {
        return listOf("GENESIS-047", "GENESIS-012") // stub
    }
}
