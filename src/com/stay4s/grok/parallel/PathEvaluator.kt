package com.stay4s.grok.parallel

import com.stay4s.grok.ParsedCommand

/**
 * Best-of-N evaluator + lightweight debate for the Parallel Grok Brain.
 * Scores paths on usefulness, safety, privacy, and covenant alignment.
 */
class PathEvaluator {

    fun evaluateAndSelect(
        results: List<ReasoningPathResult>,
        command: ParsedCommand
    ): ParallelExecutionResult {

        if (results.isEmpty()) {
            return ParallelExecutionResult.failure("No paths produced results")
        }

        // Weighted scoring (can be made much more sophisticated with local models)
        val scored = results.map { result ->
            val finalScore = (result.score * 0.5) +
                             (result.confidence * 0.25) -
                             (result.privacyRisk * 0.15) -
                             (result.resourceCost * 0.1)

            result.copy(score = finalScore.coerceIn(0.0, 1.0))
        }.sortedByDescending { it.score }

        val best = scored.first()

        return ParallelExecutionResult(
            bestPath = best,
            allPaths = scored,
            summary = "Best path: ${best.pathName} (score=${"%.2f".format(best.score)})",
            recommendedAction = best.result?.toString() ?: "No direct action"
        )
    }
}

data class ParallelExecutionResult(
    val bestPath: ReasoningPathResult,
    val allPaths: List<ReasoningPathResult>,
    val summary: String,
    val recommendedAction: String
) {
    companion object {
        fun failure(message: String) = ParallelExecutionResult(
            bestPath = ReasoningPathResult("Failure", 0.0, 0.0, 0.0, 0.0, listOf(message)),
            allPaths = emptyList(),
            summary = message,
            recommendedAction = "No action taken due to failure"
        )
    }
}
