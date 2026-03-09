# Task 011: Insecure random number generation in WSHandler

**Category:** Security Vulnerability
**Severity:** High
**File:** `src/main/java/com/synload/framework/ws/WSHandler.java`
**Lines:** 221-228

## Description

`getRandomHexString` uses `java.util.Random` instead of `java.security.SecureRandom`. The output is predictable and exploitable if used for session tokens, encryption keys, or security-sensitive identifiers.

## Action

Replace `java.util.Random` with `java.security.SecureRandom`.
