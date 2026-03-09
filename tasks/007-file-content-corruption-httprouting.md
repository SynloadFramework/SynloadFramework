# Task 007: File content corruption in HTTPRouting

**Category:** Bug
**Severity:** Critical
**File:** `src/main/java/com/synload/framework/http/HTTPRouting.java`
**Lines:** 100-121, 258

## Description

`is.read(buffer)` return value is ignored. The entire 8KB buffer is written to the response regardless of how many bytes were actually read. For files not exactly divisible by 8192 bytes, the last chunk contains stale data from the previous read, corrupting the output.

## Action

Store the return value of `is.read(buffer)` and only write that many bytes to the output.
