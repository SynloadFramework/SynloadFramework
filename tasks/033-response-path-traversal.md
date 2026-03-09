# Task 033: Path traversal in Response.getTemplate()

**Category:** Security Vulnerability
**Severity:** Medium
**File:** `src/main/java/com/synload/framework/handlers/Response.java`
**Lines:** 203-220

## Description

`getTemplate(String tmpl)` accepts a file path and reads it from disk if the extension matches a whitelist. There is no path traversal validation - an attacker who controls `tmpl` could read arbitrary files with allowed extensions from the filesystem.

## Action

Validate the resolved path stays within the expected template directory.
