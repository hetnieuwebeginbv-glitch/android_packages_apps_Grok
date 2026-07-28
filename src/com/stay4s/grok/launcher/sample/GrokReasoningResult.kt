package com.stay4s.grok.launcher.sample

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stay4s.grok.parallel.ParallelExecutionResult

/**
 * Displays the result of a full Parallel Grok Brain reasoning session.
 * This is what you would show when the user opens the full AI from the launcher.
 */
@Composable
fun GrokReasoningResult(
    result: ParallelExecutionResult,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Grok's reasoning",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = result.summary,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Best path: ${result.bestPath.pathName} (score: ${"%.2f".format(result.bestPath.score)})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = result.recommendedAction,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
