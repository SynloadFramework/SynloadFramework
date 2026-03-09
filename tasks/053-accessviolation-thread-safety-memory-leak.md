# Task 053: AccessViolation thread safety issues and memory leak

**Category:** Bug
**Severity:** High
**File:** `src/main/java/com/synload/framework/security/AccessViolation.java`
**Lines:** 12-29

## Description

`failedAttempts` and `bannedIPs` are accessed without synchronization from multiple threads. The `for (long attempt : attempts)` loop risks `ConcurrentModificationException`. Old attempts are never cleaned up, causing a memory leak and increasingly slow iteration.

## Action

Use thread-safe collections. Implement cleanup of expired entries. Add null check for `ipAddress`.
