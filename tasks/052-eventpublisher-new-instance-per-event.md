# Task 052: EventPublisher creates new handler instance per event

**Category:** Bug / Performance Issue
**Severity:** High
**File:** `src/main/java/com/synload/eventsystem/EventPublisher.java`
**Lines:** 50, 69

## Description

`trigger.getHostClass().newInstance()` creates a new handler class instance for every event invocation. Handler state is never preserved, singleton patterns are broken, and it adds unnecessary object creation. `Class.newInstance()` is also deprecated since Java 9. Unsafe casts at lines 53, 56 can throw `ClassCastException`.

## Action

Cache handler instances or use a proper dependency injection mechanism.
