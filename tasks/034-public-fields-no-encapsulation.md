# Task 034: Public fields with no encapsulation throughout codebase

**Category:** Code Smell
**Severity:** Low
**Files:**
- `src/main/java/com/synload/framework/ws/WSResponse.java` (lines 7-9)
- `src/main/java/com/synload/framework/http/HttpRequest.java` (lines 9-13)
- `src/main/java/com/synload/framework/handlers/Request.java` (lines 12-14)
- `src/main/java/com/synload/framework/handlers/Response.java` (lines 17-24)
- `src/main/java/com/synload/framework/sql/QuerySet.java` (lines 18-24)
- `src/main/java/com/synload/framework/sql/Model.java` (line 31)
- `src/main/java/com/synload/talksystem/eventShare/EventShare.java` (lines 28-31)

## Description

Nearly all model/handler classes use `public` fields instead of `private` fields with getters/setters. This bypasses encapsulation and prevents future validation or change detection.

## Action

Make fields private and use accessor methods.
