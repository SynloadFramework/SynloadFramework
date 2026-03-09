# Task 016: Hardcoded table name "tags" in SQLRegistry.dropIndex

**Category:** Bug
**Severity:** High
**File:** `src/main/java/com/synload/framework/sql/SQLRegistry.java`
**Line:** 84

## Description

`"ALTER TABLE tags DROP INDEX "` uses a hardcoded table name `tags` instead of `Model._tableName(table.getSimpleName())`. This method always operates on the wrong table for any class that is not "Tag".

## Action

Replace hardcoded `tags` with the dynamically generated table name.
