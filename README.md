CAS 4.2.x Gradle Overlay
============================

## Versions
```bash
4.2.x
```

## Requirements
* JDK 1.7+

## Configuration

The `etc` directory contains the configuration files that need to be copied to `/etc/cas` 
and configured to satisfy local deployment environment configuration needs.

## Build

```bash
./gradlew[.bat] clean build
```

To produce an exploded war directory (convenient during development, etc.)

```bash
./gradlew[.bat] clean build explodeWar
```

## Deployment

Deploy resultant `cas/build/libs/cas.war` to a Servlet container of choice.
