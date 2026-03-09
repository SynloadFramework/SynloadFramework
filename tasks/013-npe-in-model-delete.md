# Task 013: NullPointerException in Model._delete and _sqlFetch

**Category:** Bug
**Severity:** High
**File:** `src/main/java/com/synload/framework/sql/Model.java`
**Lines:** 373, 237

## Description

In `_delete`, if no field has auto-increment set, `autoincrement` remains `null` and `autoincrement.getName()` throws NPE. In `_sqlFetch` (line 237), `index` can be null when the cache miss path is taken, causing NPE on `rs.getString(index.getName())`. Same pattern in `QuerySet.exec()` (line 108).

## Action

Add null checks before dereferencing. Handle the case where no auto-increment field exists.
