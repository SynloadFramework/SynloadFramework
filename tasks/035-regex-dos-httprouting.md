# Task 035: Regex DoS risk in HTTPRouting route matching

**Category:** Security Vulnerability
**Severity:** Medium
**File:** `src/main/java/com/synload/framework/http/HTTPRouting.java`
**Line:** 338

## Description

`target.matches(path)` uses route keys as regex patterns. If route keys contain complex patterns, user-supplied URL targets could trigger catastrophic backtracking (ReDoS).

## Action

Use literal string matching or pre-compile and validate regex patterns.
