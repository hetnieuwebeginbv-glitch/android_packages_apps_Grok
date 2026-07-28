# Grok Launcher Integration Architecture

**How the Custom Grok Launcher Communicates with the Parallel Grok AI Brain + Guardian**

## Design Goals

- The launcher is **own software** (part of the Grok Edition experience).
- The heavy intelligence (ParallelOrchestrator, DailyGuardianAgent, ContextGraph) lives in the core Grok agent.
- Clean, type-safe, auditable contracts between launcher and brain.
- Full respect for Partnership verification on every call.
- Easy to extend with new proactive surfaces or AI-powered UI elements.
- Special handling for Genesis 001-100 devices.

---

## High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Grok Launcher (Own Software)             │
│                                                             │
│  - Calm Canvas                                              │
│  - Proactive Cards / Surfaces                               │
│  - Quick Actions                                            │
│  - Full AI Entry Point (voice/text)                         │
└───────────────────────────────┬─────────────────────────────┘
                                │
                IGrokLauncherBridge (interface)
                                │
┌───────────────────────────────▼─────────────────────────────┐
│              Grok Core (System-level, privileged)           │
│                                                             │
│  • ParallelOrchestrator                                     │
│  • DailyGuardianAgent                                       │
│  • SharedEvolvingContextGraph                               │
│  • PartnershipVerifier                                      │
└─────────────────────────────────────────────────────────────┘
```

The launcher never talks directly to the heavy brain. It goes through well-defined bridges/interfaces that enforce security and covenant rules.

---

## Key Interfaces

### 1. IGrokLauncherBridge

This is the main contract the launcher uses.

Responsibilities:
- Request proactive surfaces / cards from the Guardian + Brain.
- Request contextual quick actions.
- Trigger full Parallel reasoning (for when the user opens the full AI).
- Get Genesis-specific enhancements (if applicable).

### 2. IProactiveSurfaceProvider

Specialized interface for the Daily Guardian to push or be queried for high-quality, relevant cards/suggestions.

### 3. IGenesisEnhancementProvider (optional, only active on Genesis devices)

Provides extra behavior, visuals, or data that only the first 100 devices should have.

---

## Data Flow Examples

### Example 1: Proactive Card from Guardian

1. DailyGuardianAgent runs its daily cycle.
2. It decides there is something useful for the user.
3. It calls into the launcher bridge (or the bridge polls the Guardian).
4. A `ProactiveSurface` object is delivered to the launcher.
5. Launcher renders it calmly on the home screen.
6. If user taps it → launcher can request deeper reasoning from the ParallelOrchestrator (with full context).

### Example 2: User opens full Grok AI from launcher

1. User taps/holds the Grok entry point in the launcher.
2. Launcher calls `requestFullReasoningSession(...)` on the bridge.
3. Bridge verifies Partnership + owner binding.
4. ParallelOrchestrator is invoked.
5. Result (or ongoing conversation) is handed back to the launcher for display.

---

## Security & Covenant Rules

- Every call from the launcher to the brain must pass through `PartnershipVerifier`.
- On Genesis devices, extra `GenesisPath` behavior can be requested explicitly.
- The launcher itself should never hold sensitive long-term memory — that stays in the protected `SharedEvolvingContextGraph`.
- All proactive surfaces must be explainable (user can ask "why did you show me this?").

---

## Implementation Notes (Current Status)

We have built a solid, production-oriented foundation in this session:

- Full `IGrokLauncherBridge` interface (with Genesis support)
- Concrete data models: `ProactiveSurface` + `ContextualAction`
- Real reference implementation: `DefaultGrokLauncherBridge` (shows exactly how the launcher talks to ParallelOrchestrator + Guardian)
- Working example Compose screen: `GrokLauncherHomeScreen`
- Practical usage examples in `LAUNCHER_INTEGRATION_EXAMPLE.md`

Everything respects Partnership verification and is designed to feel calm yet extremely powerful.

---

## Next Logical Steps (if you want to continue)

- Real connection between the bridge and the actual Guardian / Orchestrator instances
- More sophisticated proactive surface generation logic
- Polished, calm Compose UI components for the launcher
- Deeper Genesis 001-100 specific launcher behaviors

This architecture is now ready to become the real custom launcher for the Grok Edition.

---

This architecture ensures the launcher can feel magical and deeply intelligent while the heavy, sensitive Parallel Grok AI stays properly isolated and covenant-protected.

Would you like me to continue immediately with the actual interface definitions and data models?
