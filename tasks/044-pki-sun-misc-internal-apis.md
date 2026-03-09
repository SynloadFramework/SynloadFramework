# Task 044: PKI uses internal sun.misc APIs removed in Java 9+

**Category:** Code Smell / Portability
**Severity:** High
**File:** `src/main/java/com/synload/framework/security/PKI.java`
**Lines:** 43-45

## Description

`sun.misc.BASE64Decoder`, `sun.misc.BASE64Encoder`, and `sun.misc.IOUtils` are internal JDK APIs removed in modern Java versions. The SHA-512 digest (line 57) is also stored as `new String(byte[])` producing garbled output.

## Action

Replace with `java.util.Base64`. Hex-encode or Base64-encode the digest output.
