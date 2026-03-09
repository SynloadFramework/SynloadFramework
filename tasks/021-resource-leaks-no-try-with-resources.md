# Task 021: Resource leaks - missing try-with-resources

**Category:** Bug
**Severity:** Medium
**Files:**
- `src/main/java/com/synload/framework/SynloadFramework.java` (lines 131, 155-178)
- `src/main/java/com/synload/framework/http/HTTPRouting.java` (line 91)
- `src/main/java/com/synload/framework/sql/SQLRegistry.java` (lines 47-49, 59-61, 97-99, etc.)
- `src/main/java/com/synload/framework/sql/Model.java` (line 333)
- `src/main/java/com/synload/framework/handlers/Response.java` (line 234)

## Description

FileInputStream, OutputStream, PreparedStatement, and ResultSet objects are opened without try-with-resources. If exceptions occur, these resources are never closed, causing file handle and connection leaks.

## Action

Wrap all closeable resources in try-with-resources blocks.
