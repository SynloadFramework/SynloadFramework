# Task 028: Config parsing with no null checks or validation

**Category:** Bug
**Severity:** High
**File:** `src/main/java/com/synload/framework/SynloadFramework.java`
**Lines:** 132, 140, 147, 150, 238, 433-447

## Description

`Integer.valueOf(prop.getProperty(...))` and `Long.valueOf(prop.getProperty(...))` throw `NumberFormatException` if the property is missing or malformed. `parsePubKeyServers` does no bounds checking on array after `split(",")`, risking `ArrayIndexOutOfBoundsException`. `eventShareServers` can be null causing NPE on `.equals("")`.

## Action

Add null checks, default values, and proper validation for all config properties.
