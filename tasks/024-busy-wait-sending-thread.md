# Task 024: Busy-wait polling loop in WSHandler sending thread

**Category:** Performance Issue
**Severity:** Medium
**File:** `src/main/java/com/synload/framework/ws/WSHandler.java`
**Line:** 169

## Description

`while (true)` with `Thread.sleep(1)` is a busy-wait polling loop checking `ws.queue.size()`. This wastes CPU cycles. The compound check-then-act on the Vector (size() then get(0)) is also not atomic and can cause `ArrayIndexOutOfBoundsException`.

## Action

Replace with a `BlockingQueue` using `take()` for efficient blocking.
