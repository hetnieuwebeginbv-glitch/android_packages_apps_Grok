package com.stay4s.grok.launcher.sample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stay4s.grok.launcher.interfaces.IGrokLauncherBridge
import com.stay4s.grok.launcher.proactive.ContextualAction
import com.stay4s.grok.launcher.proactive.ProactiveSurface
import com.stay4s.grok.parallel.ParallelExecutionResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Grok Launcher.
 *
 * This shows how a real launcher would manage state while talking to the
 * Parallel Grok AI and Guardian through the bridge.
 */
class GrokLauncherViewModel(
    private val bridge: IGrokLauncherBridge
) : ViewModel() {

    private val _proactiveSurfaces = MutableStateFlow<List<ProactiveSurface>>(emptyList())
    val proactiveSurfaces: StateFlow<List<ProactiveSurface>> = _proactiveSurfaces

    private val _contextualActions = MutableStateFlow<List<ContextualAction>>(emptyList())
    val contextualActions: StateFlow<List<ContextualAction>> = _contextualActions

    private val _isGenesis = MutableStateFlow(false)
    val isGenesis: StateFlow<Boolean> = _isGenesis

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            _isGenesis.value = bridge.isGenesisDevice()

            _proactiveSurfaces.value = bridge.getProactiveSurfaces()
            _contextualActions.value = bridge.getContextualActions()

            _isLoading.value = false
        }
    }

    fun onSurfaceClicked(surface: ProactiveSurface, onOpenGrok: (String) -> Unit) {
        viewModelScope.launch {
            when (surface.actionType) {
                com.stay4s.grok.launcher.proactive.SurfaceActionType.OPEN_GROK_CHAT -> {
                    onOpenGrok(surface.title)
                }
                else -> {
                    // Handle other actions
                }
            }
        }
    }

    fun requestFullReasoning(query: String, onResult: (ParallelExecutionResult) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = bridge.requestFullReasoning(query)
            _isLoading.value = false
            onResult(result)
        }
    }

    fun refresh() {
        loadInitialData()
    }
}
