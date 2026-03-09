# Task 049: Non-cryptographic Random used for AES salt/IV generation

**Category:** Security Vulnerability
**Severity:** Critical
**File:** `src/main/java/com/synload/talksystem/Client.java`
**Line:** 294

## Description

`new Random()` is used to generate AES salt and IV values. An attacker who knows the seed can predict all salts and IVs, breaking the encryption.

## Action

Replace with `java.security.SecureRandom`.
