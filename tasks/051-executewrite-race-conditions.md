# Task 051: Race conditions in ExecuteWrite queue access

**Category:** Bug
**Severity:** High
**File:** `src/main/java/com/synload/talksystem/ExecuteWrite.java`
**Lines:** 36-37, 55-56

## Description

`queue.get(0)` followed by `queue.remove(0)` is not atomic. Another thread can modify the queue between these calls, causing `IndexOutOfBoundsException` or processing the wrong item. Integer division truncation at line 29 (`queue.size()/10` instead of `/10.0`) also makes `Math.ceil` ineffective.

## Action

Use a `BlockingQueue` or synchronize queue access. Fix integer division.
