# Task 050: ServerTalk accepts connections with no authentication or TLS

**Category:** Security Vulnerability
**Severity:** High
**File:** `src/main/java/com/synload/talksystem/ServerTalk.java`
**Lines:** 23-29

## Description

Every accepted socket is immediately wrapped in a `Client` with the server's `serverTalkKey`. There is no IP filtering, TLS, or authentication handshake. New threads are created per connection with no pool or limit, enabling DoS. The `ServerSocket` is never closed (resource leak).

## Action

Add TLS support, authentication handshake, IP filtering, and connection limits via a thread pool.
