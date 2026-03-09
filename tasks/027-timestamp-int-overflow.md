# Task 027: Timestamp cast to int will overflow in 2038

**Category:** Bug
**Severity:** Medium
**File:** `src/main/java/com/synload/framework/SynloadFramework.java`
**Lines:** 300-302

## Description

`getTimestamp()` casts `long` to `int`. This will overflow (Y2K38 problem) in January 2038.

## Action

Change return type to `long`.
