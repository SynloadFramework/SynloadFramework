# Task 038: EventShare concurrent modification of handler registry

**Category:** Bug
**Severity:** High
**File:** `src/main/java/com/synload/talksystem/eventShare/EventShare.java`
**Lines:** 63, 80, 92

## Description

`HandlerRegistry.getHandlers()` is iterated and modified during iteration (line 80 calls `remove(trigger)`). In `transmitEvents()` (line 92), the same map is iterated without synchronization. Concurrent modification from other threads causes `ConcurrentModificationException`.

## Action

Use thread-safe collections or create defensive copies before iteration and modification.
