package com.stay4s.grok.vault

/**
 * Result of the Guardian + Parallel Brain analyzing a payment.
 * Used by GrokVault before any value moves.
 */
data class TransactionRiskReasoning(
    val isSafe: Boolean,
    val riskScore: Int,           // 0-100
    val reason: String,
    val recommendedAction: String
)
