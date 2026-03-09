# Task 006: Path traversal in ModuleResource.get()

**Category:** Security Vulnerability
**Severity:** High
**File:** `src/main/java/com/synload/framework/modules/ModuleResource.java`
**Line:** 23

## Description

`get(String module, String file)` constructs a file path via `module+"/"+file` with no sanitization. An attacker can use path traversal (e.g., `module="../../etc"`, `file="passwd"`) to read arbitrary files.

## Action

Validate and sanitize path components. Ensure resolved path stays within allowed directory.
