# Task 002: SQL Injection via QuerySet limit field

**Category:** Security Vulnerability
**Severity:** Critical
**File:** `src/main/java/com/synload/framework/sql/QuerySet.java`
**Line:** 78

## Description

`limit` is a raw `String` concatenated directly into SQL: `sql += " LIMIT " + limit;`. A caller passing user-controlled input can inject arbitrary SQL. The `order` field (line 75) has the same issue.

## Action

Change `limit` to an integer type. Validate/sanitize `order` field values.
