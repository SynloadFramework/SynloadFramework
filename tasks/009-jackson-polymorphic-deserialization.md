# Task 009: Jackson polymorphic deserialization RCE vulnerability

**Category:** Security Vulnerability
**Severity:** High
**Files:**
- `src/main/java/com/synload/framework/handlers/Request.java` (line 10)
- `src/main/java/com/synload/framework/handlers/Response.java` (line 15)

## Description

`@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)` enables polymorphic deserialization. This is a well-known Jackson deserialization attack vector that allows remote code execution if an attacker can control the JSON input.

## Action

Remove `@JsonTypeInfo` or restrict allowed subtypes with `@JsonSubTypes`. Enable Jackson's `DefaultTyping` safeguards.
