# Task 054: Duplicate @WSEvent name in PKI

**Category:** Bug
**Severity:** Medium
**File:** `src/main/java/com/synload/framework/security/PKI.java`
**Lines:** 97, 131

## Description

Both `receiveClientPub` and `receiveClientAcknowledge` use `name = "ReceiveClientPublicKey"`. This likely causes only one to be registered, or undefined routing behavior.

## Action

Give each handler a unique event name.
