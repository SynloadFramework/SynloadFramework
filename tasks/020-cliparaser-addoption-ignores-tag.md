# Task 020: CLIParser.addOption() ignores its tag parameter

**Category:** Bug
**Severity:** Critical
**File:** `src/main/java/com/synload/framework/CLIParser.java`
**Line:** 51

## Description

`addOption()` accepts a `tag` parameter but hardcodes `"t"` as the option name: `options.addOption("t", hasArg, description)`. The `tag` parameter is completely ignored, so every call overwrites the same `"t"` option.

## Action

Replace `"t"` with the `tag` parameter.
