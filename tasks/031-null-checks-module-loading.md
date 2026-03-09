# Task 031: Missing null checks in module loading

**Category:** Bug
**Severity:** High
**Files:**
- `src/main/java/com/synload/framework/modules/ModuleLoader.java` (lines 106, 145, 328, 364, 387, 402)
- `src/main/java/com/synload/framework/modules/CheckNewJar.java` (lines 40-41, 48, 52)

## Description

`folder.listFiles()` can return null (NPE on `.length`). `register()` can return null (NPE on `[0]`). `ZipEntry.getSize()` can return -1 (NegativeArraySizeException). Methods annotated `@Event` with zero parameters cause `ArrayIndexOutOfBoundsException` on `getParameterTypes()[0]`.

## Action

Add null/bounds checks before accessing arrays and dereferencing potentially null values.
