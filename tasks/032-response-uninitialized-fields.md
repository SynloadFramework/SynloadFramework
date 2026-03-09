# Task 032: Uninitialized fields in Request and Response classes

**Category:** Bug
**Severity:** Medium
**Files:**
- `src/main/java/com/synload/framework/handlers/Request.java` (line 12)
- `src/main/java/com/synload/framework/handlers/Response.java` (lines 17-18, 24)

## Description

Multi-variable declarations like `public String method, action, reference = "";` only initialize the last variable. `method` and `action` are null. In Response, `redirect` map is null while `data` is initialized. This causes NPEs when these fields are accessed.

## Action

Initialize each field individually or in the constructor.
