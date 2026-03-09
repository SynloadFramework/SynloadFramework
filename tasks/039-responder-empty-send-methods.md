# Task 039: Responder abstract class has empty send() methods

**Category:** Code Smell
**Severity:** Medium
**File:** `src/main/java/com/synload/framework/modules/Responder.java`
**Lines:** 12-14

## Description

All three `send()` methods have empty bodies. These should be `abstract` methods (the class is already abstract). Subclasses that forget to override will silently do nothing.

## Action

Make the send methods abstract, or throw `UnsupportedOperationException`.
