CAS 4.2.x Gradle Overlay
============================

## Versions

```bash
CAS 4.2.x
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

### Embedded Jetty

- Create a Java keystore under `/etc/cas/jetty`
- Import your CAS server certificate inside this keystore.
- Edit your `~/.gradle/gradle.properties` to include:

```properties
jettySslKeyStorePath=/etc/cas/jetty/thekeystore
jettySslTrustStorePath=/etc/cas/jetty/thekeystore
jettySslTrustStorePassword=changeit
jettySslKeyStorePassword=changeit
```

Then run:

```bash
./gradlew[.bat] clean build jettyRunWar 
```

CAS will be available at:

- http://cas.server.name:8080/cas
- https://cas.server.name:8443/cas

If you do not specify a keystore configuration, CAS will simply run on port `8080`.

### External

Deploy resultant `cas/build/libs/cas.war` to a Servlet container of choice.
