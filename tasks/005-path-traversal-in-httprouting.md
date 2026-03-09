# Task 005: Path traversal vulnerability in HTTPRouting

**Category:** Security Vulnerability
**Severity:** Critical
**File:** `src/main/java/com/synload/framework/http/HTTPRouting.java`
**Lines:** 369-387

## Description

Path traversal protection is incomplete. The `..` check only applies in limited cases. An attacker could use encoded characters, absolute paths, or URIs with more segments to bypass and read arbitrary files from the filesystem. File paths are constructed from user input without canonicalization.

## Action

Canonicalize paths and validate they remain within the document root. Use `File.getCanonicalPath()` and check prefix.
