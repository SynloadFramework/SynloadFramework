# Task 026: randomString() uses length as radix causing NumberFormatException

**Category:** Bug
**Severity:** High
**File:** `src/main/java/com/synload/framework/SynloadFramework.java`
**Lines:** 274-277

## Description

`randomString(int length)` passes `length` as the radix to `BigInteger.toString(radix)`. Valid radix range is 2-36. Passing values outside this range (e.g., 50) throws `NumberFormatException`. The parameter name is misleading.

## Action

Use a fixed radix (e.g., 36) and truncate/pad the result to the desired length.
