# Task 015: Reference comparison instead of .equals() on Strings/Objects

**Category:** Bug
**Severity:** High
**Files:**
- `src/main/java/com/synload/framework/sql/QuerySet.java` (lines 71, 126, 148)
- `src/main/java/com/synload/framework/sql/Model.java` (line 576)
- `src/main/java/com/synload/framework/http/HTTPRegistry.java` (line 19)

## Description

`where != ""` and `out != ""` use reference comparison (`!=`) instead of `.equals()`. These will almost always evaluate to `true` because string literals and variables are different objects. In Model.java, `!=` on Objects in `_merge` causes unnecessary database updates.

## Action

Replace `!=` and `==` with `.equals()` or `.isEmpty()` for string/object comparisons.
