# Task 046: Java deserialization of untrusted network data (RCE)

**Category:** Security Vulnerability
**Severity:** Critical
**File:** `src/main/java/com/synload/talksystem/ExecuteRead.java`
**Lines:** 70, 84, 132

## Description

`in.readObject()` deserializes arbitrary Java objects from the network with no class whitelist or deserialization filter. This is a well-known remote code execution vector. Additionally, `dIn.readInt()` (line 84) reads a length from the network with no upper bound, allowing OOM DoS. `Class.forName()` (line 132) loads classes by name from untrusted input.

## Action

Implement deserialization filters (Java 9+ `ObjectInputFilter`). Validate and cap the length field. Whitelist allowed classes.
