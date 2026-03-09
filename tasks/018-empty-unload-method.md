# Task 018: ModuleLoader.unload() is a no-op

**Category:** Bug
**Severity:** High
**File:** `src/main/java/com/synload/framework/modules/ModuleLoader.java`
**Lines:** 190-204

## Description

The `unload` method body is entirely commented out. When `CheckNewJar` calls `ModuleLoader.unload(modData)`, nothing happens. Old module event handlers, SQL registrations, and class references are never cleaned up, causing duplicate registrations and memory leaks.

## Action

Implement the unload logic to clean up event handlers, SQL registrations, and class references.
