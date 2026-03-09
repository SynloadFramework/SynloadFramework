# Task 023: New thread created per WebSocket message

**Category:** Performance Issue
**Severity:** Medium
**File:** `src/main/java/com/synload/framework/ws/WSHandler.java`
**Line:** 262

## Description

A new `Thread` is created for every incoming WebSocket message (`new Thread(new HandleRequest(...))`). Under high load, this causes excessive thread creation overhead and potential resource exhaustion. A new `ObjectMapper` is also created per message (line 252), which is expensive and unnecessary.

## Action

Use a thread pool (e.g., `ExecutorService`). Make `ObjectMapper` a shared static instance.
