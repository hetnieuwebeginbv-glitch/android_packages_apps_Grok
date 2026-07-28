package com.stay4s.grok.parallel

import com.stay4s.grok.partnership.PartnershipVerifier
import com.stay4s.grok.audit.AuditLogger
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.ConcurrentHashMap

/**
 * The long-term memory and shared context for the entire Parallel Grok Brain.
 * All paths read and write here. Protected by the grok_agent domain.
 */
class SharedEvolvingContextGraph(
    private val partnership: PartnershipVerifier,
    private val audit: AuditLogger
) {
    private val nodes = ConcurrentHashMap<String, ContextNode>()
    private val _version = MutableStateFlow(0L)

    fun addFact(key: String, value: Any, provenance: String = "agent") {
        if (!partnership.verifyPartnership()) return

        nodes[key] = ContextNode(key, value, System.currentTimeMillis(), provenance)
        _version.value += 1
        audit.log("CONTEXT_GRAPH_UPDATE", key)
    }

    fun snapshot(): ContextSnapshot {
        return ContextSnapshot(nodes.toMap(), _version.value)
    }
}

data class ContextNode(val key: String, val value: Any, val timestamp: Long, val provenance: String)
data class ContextSnapshot(val facts: Map<String, ContextNode>, val version: Long)
