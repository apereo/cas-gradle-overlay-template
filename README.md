CAS Gradle Overlay
============================
Generic CAS gradle war overlay to exercise the latest versions of CAS. This overlay could be freely used as a starting template for local
CAS gradle war overlays.

## Versions

* CAS 4.2.x

## Requirements

* JDK 1.7+

## Configuration

The `etc` directory contains the configuration files that need to be copied to `/etc/cas`
and configured to satisfy local deployment environment configuration needs.

### Adding Modules

CAS modules may be specified under the `dependencies` block of the CAS subproject:

```gradle
dependencies {
    compile "org.apereo.cas:cas-server-webapp:${project.'cas.version'}@war"
    compile "org.apereo.cas:cas-server-some-module:${project.'cas.version'}"
    ...
}
```

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

Remember to start your container with the following variables set with `-D`:

```properties
cas.properties.config.location=file:/etc/cas/cas.properties
log4j.configurationFile=/etc/cas/log4j2.xml
```
