# Task 043: PKI regenerates keys after client key exchange

**Category:** Bug
**Severity:** High
**File:** `src/main/java/com/synload/framework/security/PKI.java`
**Lines:** 121

## Description

After successfully exchanging keys with the client, the server calls `this.generateKeys()` which regenerates its own key pair. The client still holds the old server public key, causing all subsequent encrypted communication to fail.

## Action

Remove the key regeneration call after key exchange, or re-send the new public key to the client.
