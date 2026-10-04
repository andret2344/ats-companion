<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Inspection on `@Mapper` used in a non-`@Argument` method, with a quick fix removing the annotation.

### Changed

- Minimal supported IDE version is now 2024.1.

### Fixed

- Gutter navigation icons are no longer displayed inside method bodies (e.g. lambdas) and on non-`@Argument` methods.

## [0.3.1]

### Fixed

- Invalid type mapper inspection no longer reports parameters annotated with `@Mapper`.

## [0.3.0]

atsArguments compatibility version: **0.1.3**

### Added

- Completions for argument and type mappers.
- Completion for argument completer.
- Validations for argument and type mappers.
- Validation for argument completer.

### Fixed

- Intentions generating fallback methods now work correctly.
- Some random errors are now less likely.

## [0.2.0]

atsArguments compatibility version: **0.1.3**

### Added

- Implicit usage provider, now methods annotated with `@Argument` won't be unused anymore.
- Fallback method verifier, now those methods must have exactly one String parameter.
- Inspection on `@Mapper` with primitive or complex type with no `@Mapper`.
- Basic gutter navigation. The icon is kinda ok!
- Constructor verifier.

## [0.1.0]

atsArguments compatibility version: **0.1.3**

### Changed

- Null comparison inspection when using ExecutorType.
- Argument fallback method generation intention.

### Added

- Type fallback method generation intention.

## [0.0.1]

atsArguments compatibility version: **0.1.1.2**

### Added

- Inspections:
    - Abstract class annotated with `@BaseCommand`.
    - Static modifier in `@Argument` method.
    - Use of array in `@Argument` method's parameter list.
    - `@Fallback` method name must match the `@Argument` method name.
    - Instance checking of sender against executorType.
    - Non-public visibility of `@Argument` method.
    - Missing `@BaseCommand` annotation in class extending AnnotatedCommandExecutor.
    - Missing superclass in class annotated with `@BaseCommand`.
    - Position must be positive lower or equal to parameters count.
    - `@Argument` method should not return a primitive.
- Fallback method generation intention.
- Completion of the position parameter in `@Argument` annotation.
