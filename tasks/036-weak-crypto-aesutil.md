# Task 036: Weak cryptographic choices in AesUtil

**Category:** Security Vulnerability
**Severity:** Medium
**File:** `src/main/java/com/synload/framework/ws/AesUtil.java`
**Lines:** 35, 99

## Description

Uses `AES/CBC/PKCS5Padding` which is vulnerable to padding oracle attacks. Uses `PBKDF2WithHmacSHA1` for key derivation - SHA-1 is considered weak. No minimum iteration count is enforced.

## Action

Switch to AES-GCM for authenticated encryption. Use PBKDF2WithHmacSHA256 with a minimum iteration count.
