# Task 048: Unbounded recursive reconnection causes StackOverflowError

**Category:** Bug
**Severity:** Critical
**File:** `src/main/java/com/synload/talksystem/Client.java`
**Lines:** 103-115

## Description

The `reconnect` method calls itself recursively in the `catch` block. If the server is down, this recurses until a `StackOverflowError` crashes the application. There is no maximum retry limit.

## Action

Convert to an iterative loop with exponential backoff and a maximum retry count.
