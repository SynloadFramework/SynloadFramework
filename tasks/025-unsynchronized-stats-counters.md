# Task 025: Unsynchronized statistics counters

**Category:** Bug
**Severity:** Medium
**File:** `src/main/java/com/synload/framework/ws/WSHandler.java`
**Lines:** 180, 255

## Description

`TransmissionStats.ws_sent` and `ws_receive` are incremented with `+=` from multiple threads. This is a non-atomic read-modify-write operation, leading to lost updates.

## Action

Use `AtomicLong` or `LongAdder` for thread-safe counters.
