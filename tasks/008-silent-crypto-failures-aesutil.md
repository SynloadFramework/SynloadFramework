# Task 008: Silent cryptographic failures in AesUtil

**Category:** Security Vulnerability / Bug
**Severity:** Critical
**File:** `src/main/java/com/synload/framework/ws/AesUtil.java`
**Lines:** 36-38, 85-89

## Description

All encryption/decryption errors are silently swallowed (empty catch blocks), returning `null`. Callers don't check for null, leading to NullPointerExceptions. Swallowing `BadPaddingException` silently can mask padding oracle attacks. The `Cipher` instance is also not thread-safe (line 29).

## Action

Propagate exceptions or handle them meaningfully. Never silently swallow crypto errors. Consider using AES-GCM instead of CBC mode.
