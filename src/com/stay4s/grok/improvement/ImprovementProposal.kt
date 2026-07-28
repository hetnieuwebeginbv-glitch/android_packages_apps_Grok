package com.stay4s.grok.improvement

import java.util.UUID

/**
 * ImprovementProposal
 *
 * Represents a concrete, explainable improvement the Guardian + Parallel Brain
 * propose to the owner. This is the foundation of the Self-Improvement Proposal System.
 *
 * Future-proof design: Proposals are versioned, auditable, and can be rolled back.
 */
data class ImprovementProposal(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val category: ImprovementCategory,
    val riskLevel: RiskLevel,
    val estimatedImpact: String,
    val proposedChanges: List<String>,
    val reasoningTrace: String,           // From Parallel Brain
    val createdAt: Long = System.currentTimeMillis(),
    val requiresExplicitApproval: Boolean = true,
    var status: ProposalStatus = ProposalStatus.PROPOSED
) {
    enum class ImprovementCategory {
        PERFORMANCE, PRIVACY, SECURITY, USABILITY, NETWORK, VAULT, GENESIS, SYSTEM
    }

    enum class RiskLevel { LOW, MEDIUM, HIGH, COVENANT_CRITICAL }

    enum class ProposalStatus { PROPOSED, APPROVED, REJECTED, APPLIED, ROLLED_BACK }
}
