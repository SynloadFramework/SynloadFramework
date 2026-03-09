# Task 010: Deprecated Thread.stop() usage

**Category:** Bug
**Severity:** Critical
**File:** `src/main/java/com/synload/framework/ws/WSHandler.java`
**Line:** 63

## Description

`sendingThreadVar.stop()` is deprecated and unsafe. It throws `ThreadDeath` at an arbitrary point, potentially corrupting shared state. `sendingThreadVar` could also be `null` if `onWebSocketClose` is called before `onWebSocketConnect` completes.

## Action

Use a volatile boolean flag or `Thread.interrupt()` for cooperative thread termination.
