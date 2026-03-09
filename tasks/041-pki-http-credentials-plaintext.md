# Task 041: PKI sends credentials over plaintext HTTP

**Category:** Security Vulnerability
**Severity:** Critical
**File:** `src/main/java/com/synload/framework/security/PKI.java`
**Lines:** 60-63

## Description

`"http://"+server.get("address")+"/auth.php"` sends username and password as unencrypted form parameters over plaintext HTTP. Credentials are exposed to any MITM attacker.

## Action

Use HTTPS for all credential transmission. Validate server certificates.
