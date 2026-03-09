# Task 029: SQLRegistry.checkVersions returns early for all tables

**Category:** Bug
**Severity:** Medium
**File:** `src/main/java/com/synload/framework/sql/SQLRegistry.java`
**Line:** 182

## Description

`return;` exits the entire `checkVersions` method if any single table has missing info, preventing all subsequent tables from being checked. The collation comparison logic (lines 201-202) is also inverted - it triggers updates when collations ARE equal.

## Action

Change `return;` to `continue;`. Fix collation comparison to use `!equals`.
