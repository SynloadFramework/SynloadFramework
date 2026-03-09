# Task 004: Single shared JDBC connection is not thread-safe

**Category:** Bug / Thread Safety
**Severity:** Critical
**File:** `src/main/java/com/synload/framework/SynloadFramework.java`
**Line:** 65

## Description

`public static Connection sql` stores a single JDBC connection as a public static field. This connection is used from multiple threads concurrently (HTTP requests, WebSocket handlers), which will corrupt state and produce unpredictable results. JDBC connections are not thread-safe.

## Action

Implement a connection pool (e.g., HikariCP) or use `ThreadLocal<Connection>`.
