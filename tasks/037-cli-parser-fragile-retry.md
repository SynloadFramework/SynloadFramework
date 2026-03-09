# Task 037: CLIParser fragile error-message parsing retry loop

**Category:** Code Smell
**Severity:** Medium
**File:** `src/main/java/com/synload/framework/CLIParser.java`
**Lines:** 28-48

## Description

The retry loop (up to 20 iterations) parses exception error messages via regex to determine which argument was unrecognized, then removes it via string replacement. This is fragile, breaks with library version changes, and can corrupt valid arguments if the option text appears as a substring.

## Action

Use Apache Commons CLI's built-in option to ignore unrecognized options, or pre-filter arguments.
