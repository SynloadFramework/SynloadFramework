# Task 017: Missing space before DEFAULT in SQL generation

**Category:** Bug
**Severity:** High
**File:** `src/main/java/com/synload/framework/sql/SQLRegistry.java`
**Lines:** 42, 138

## Description

`sql += "DEFAULT '" + cd.getDefaultV() + "'";` is missing a leading space. The generated SQL becomes `...NOT NULLDEFAULT 'value'`, which is a syntax error.

## Action

Add a leading space: `" DEFAULT '"`.
