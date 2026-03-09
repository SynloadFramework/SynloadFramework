# Task 003: Thread-unsafe shared mutable collections

**Category:** Bug / Thread Safety
**Severity:** Critical
**Files:**
- `src/main/java/com/synload/framework/SynloadFramework.java` (lines 59-86)
- `src/main/java/com/synload/framework/sql/Model.java` (line 31)
- `src/main/java/com/synload/framework/modules/ModuleLoader.java` (line 42)
- `src/main/java/com/synload/framework/modules/ModuleRegistry.java` (lines 7, 9)
- `src/main/java/com/synload/eventsystem/HandlerRegistry.java` (line 10)
- `src/main/java/com/synload/talksystem/eventShare/EventShare.java` (line 32)

## Description

Nearly all shared state uses plain `ArrayList` and `HashMap` instances accessed from multiple threads (HTTP handler threads, WebSocket threads, background threads) with no synchronization. This causes `ConcurrentModificationException`, data races, and potential infinite loops in `HashMap`.

## Action

Replace with `ConcurrentHashMap`, `CopyOnWriteArrayList`, or add proper synchronization.
