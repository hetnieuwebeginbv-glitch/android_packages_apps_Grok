package com.stay4s.grok.parallel

import com.stay4s.grok.ParsedCommand

import com.stay4s.grok.partnership.PartnershipVerifier
import com.stay4s.grok.audit.AuditLogger

/**
 * Base contract for every reasoning path in the Parallel Grok Brain.
 * Every path must respect the Partnership and contribute to the shared ContextGraph.
 */
interface ReasoningPath {
    val name: String

    suspend fun run(
        command: ParsedCommand,
        context: ContextSnapshot
    ): ReasoningPathResult?
}

data class ReasoningPathResult(
    val pathName: String,
    val score: Double,
    val confidence: Double,
    val privacyRisk: Double,
    val resourceCost: Double,
    val trace: List<String>,
    val result: Any? = null,
    val metadata: Map<String, Any> = emptyMap()
)
