# Task 014: Copy-paste bug in Model._set and _unset

**Category:** Bug
**Severity:** High
**File:** `src/main/java/com/synload/framework/sql/Model.java`
**Lines:** 504, 552

## Description

In `_unset()` line 504: `HasOne.class.isInstance(local[1])` should be `HasOne.class.isInstance(remote[1])`. In `_set()` line 552: same issue - checks and casts `local[1]` when it should use `remote[1]`. This causes incorrect relationship logic.

## Action

Replace `local[1]` with `remote[1]` in the affected conditions.
