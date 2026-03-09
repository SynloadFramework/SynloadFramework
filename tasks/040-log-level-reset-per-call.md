# Task 040: Log level reset on every log call

**Category:** Performance Issue
**Severity:** Low
**File:** `src/main/java/com/synload/framework/Log.java`
**Lines:** 8-9, 14-15, 20-21

## Description

Every call to `info()`, `debug()`, or `error()` calls `Logger.getLogger()` and then `logger.setLevel()`. Resetting the level on every log call is wasteful and not thread-safe. The `error()` method also doesn't accept a `Throwable`, forcing use of `e.printStackTrace()` throughout.

## Action

Set the log level once during initialization. Add a `Throwable` parameter overload to `error()`.
