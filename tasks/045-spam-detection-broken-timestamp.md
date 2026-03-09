# Task 045: SpamDetection timestamps broken by modulo 1000

**Category:** Bug
**Severity:** Critical
**File:** `src/main/java/com/synload/framework/security/SpamDetection.java`
**Lines:** 19, 35

## Description

`System.currentTimeMillis() % 1000` yields only 0-999 (sub-second portion), making all time comparisons nonsensical. Timestamps wrap every second, so rate limiting is completely broken. Additionally, when under the limit, the current timestamp is never added to the access list (line 29), so requests are never actually accumulated.

## Action

Remove `% 1000`. Add timestamp recording for all requests, not just new identifiers.
