# Task 001: SQL Injection in Model._related()

**Category:** Security Vulnerability
**Severity:** Critical
**File:** `src/main/java/com/synload/framework/sql/Model.java`
**Line:** 265-266

## Description

The `_related` method interpolates field values directly into a SQL `IN (...)` clause without parameterization. If an attacker can control the contents of the HasMany field, they can inject arbitrary SQL.

## Action

Replace string concatenation with parameterized queries using `PreparedStatement`.
