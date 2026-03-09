# Task 042: Bare RSA cipher without proper padding specification

**Category:** Security Vulnerability
**Severity:** Critical
**File:** `src/main/java/com/synload/framework/security/PKI.java`
**Lines:** 153, 158

## Description

`Cipher.getInstance("RSA")` defaults to provider-dependent padding (usually PKCS1). This is vulnerable to padding oracle attacks. Additionally, data is split into 50-byte chunks encrypted independently (ECB mode for RSA), leaking information when identical plaintext chunks produce identical ciphertext.

## Action

Use `RSA/ECB/OAEPWithSHA-256AndMGF1Padding`. Implement hybrid encryption (RSA+AES) instead of chunked RSA.
