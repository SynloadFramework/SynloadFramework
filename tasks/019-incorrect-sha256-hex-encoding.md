# Task 019: Incorrect SHA-256 hex encoding

**Category:** Bug
**Severity:** High
**File:** `src/main/java/com/synload/framework/modules/ModuleLoader.java`
**Lines:** 184-185

## Description

`Integer.toHexString(0xFF & mdbytes[i])` produces a single hex character for values 0x00-0x0F (e.g., byte 0x0A becomes "a" instead of "0a"). This produces inconsistent-length hashes that can collide.

## Action

Use `String.format("%02x", mdbytes[i])` for consistent two-character hex encoding.
