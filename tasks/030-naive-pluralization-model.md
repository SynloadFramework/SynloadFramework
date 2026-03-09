# Task 030: Naive pluralization logic in Model._tableName

**Category:** Bug
**Severity:** Medium
**File:** `src/main/java/com/synload/framework/sql/Model.java`
**Lines:** 101-108

## Description

Any class name ending in "y" gets pluralized as "ies" (e.g., "Key" becomes "keies", "Day" becomes "daies"). English pluralization rules for words ending in a vowel+"y" are not handled. This silently generates wrong table names.

## Action

Improve pluralization logic or allow explicit table name specification via annotation.
