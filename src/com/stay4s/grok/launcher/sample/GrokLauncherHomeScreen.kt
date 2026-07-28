package com.stay4s.grok.launcher.sample

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stay4s.grok.launcher.proactive.ProactiveSurface
import com.stay4s.grok.parallel.ParallelExecutionResult

/**
 * A more complete example of what the Grok Launcher Home Screen could look like.
 *
 * This demonstrates real integration with the Parallel Grok AI and Guardian
 * through the bridge.
 */
@Composable
fun GrokLauncherHomeScreen(
    viewModel: GrokLauncherViewModel,
    onOpenFullGrok: (initialQuery: String?) -> Unit
) {
    val proactiveSurfaces by viewModel.proactiveSurfaces.collectAsState()
    val contextualActions by viewModel.contextualActions.collectAsState()
    val isGenesis by viewModel.isGenesis.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showReasoningResult by remember { mutableStateOf<ParallelExecutionResult?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = if (isGenesis) "Genesis • First Covenant" else "Grok Edition",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Jouw telefoon. Jouw intelligentie.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Proactive intelligence from the Guardian + Parallel Brain
        if (proactiveSurfaces.isNotEmpty()) {
            Text(
                text = "Voor jou vandaag",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(proactiveSurfaces) { surface ->
                    GrokIntelligenceSurface(
                        surface = surface,
                        onClick = {
                            viewModel.onSurfaceClicked(surface) { query ->
                                onOpenFullGrok(query)
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Contextual quick actions
        if (contextualActions.isNotEmpty()) {
            Text(
                text = "Snelle acties",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            contextualActions.forEach { action ->
                OutlinedButton(
                    onClick = {
                        onOpenFullGrok(action.label)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(action.label)
                        action.description?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Main entry point to the full Parallel Grok AI
        Button(
            onClick = { onOpenFullGrok(null) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Praat met Grok")
        }

        if (isGenesis) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Je maakt deel uit van de First Covenant",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    // Show full reasoning result if available (demo)
    showReasoningResult?.let { result ->
        AlertDialog(
            onDismissRequest = { showReasoningResult = null },
            title = { Text("Grok's antwoord") },
            text = {
                GrokReasoningResult(result = result)
            },
            confirmButton = {
                TextButton(onClick = { showReasoningResult = null }) {
                    Text("Sluiten")
                }
            }
        )
    }
}
