# Task 012: Excessive upload and form size limits enable DoS

**Category:** Security Vulnerability
**Severity:** High
**File:** `src/main/java/com/synload/framework/http/HTTPHandler.java`
**Lines:** 12-13, 16

## Description

`MULTI_PART_CONFIG` allows uploads up to ~900 MB. `setMaxFormContentSize(2000000000)` allows ~2 GB form bodies. These limits enable denial-of-service attacks through memory/disk exhaustion. WebSocket message limits (100 MB) in `WebsocketHandler.java` (lines 13-14) have the same issue.

## Action

Set reasonable limits (e.g., 10-50 MB) and make them configurable.
