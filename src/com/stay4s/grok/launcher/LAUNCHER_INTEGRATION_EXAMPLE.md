# Launcher Integration Example

This document shows how the Grok Launcher (own software) would use the bridge in practice.

## Example: Loading Proactive Surfaces on Home Screen

```kotlin
// In the Grok Launcher (e.g. in a ViewModel or Repository)
class GrokLauncherRepository(
    private val bridge: IGrokLauncherBridge
) {

    suspend fun loadHomeSurfaces(): List<ProactiveSurface> {
        val surfaces = bridge.getProactiveSurfaces()

        // Filter Genesis-only surfaces if this is not a Genesis device
        return if (bridge.isGenesisDevice()) {
            surfaces
        } else {
            surfaces.filter { !it.isGenesisOnly }
        }
    }
}
```

## Example: User Taps a Proactive Surface

```kotlin
suspend fun onSurfaceTapped(surface: ProactiveSurface) {
    when (surface.actionType) {
        SurfaceActionType.OPEN_GROK_CHAT -> {
            val result = bridge.requestFullReasoning(
                query = surface.body ?: surface.title,
                contextSnapshot = mapOf("source" to "launcher_surface", "id" to surface.id)
            )
            // Show full Grok conversation with the result
        }

        SurfaceActionType.PERFORM_ACTION -> {
            // Handle direct action (e.g. switch network, open specific screen)
        }

        SurfaceActionType.SHOW_MORE_INFO -> {
            val explanation = bridge.explainSurface(surface.id)
            // Show explanation to user
        }

        else -> { /* handle other cases */ }
    }
}
```

## Example: Requesting Contextual Quick Actions

```kotlin
suspend fun loadQuickActions(): List<ContextualAction> {
    return bridge.getContextualActions()
}
```

---

This pattern keeps the launcher clean while giving it access to the full power of the Parallel Grok AI and Guardian through well-defined, auditable contracts.
