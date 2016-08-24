CAS Gradle Overlay
============================
Generic CAS gradle war overlay to exercise the latest versions of CAS. This overlay could be freely used as a starting template for local 
CAS gradle war overlays. 

## Versions

* CAS 5.0.0

## Requirements

* JDK 1.8+

## Configuration

The `etc` directory contains the configuration files that are copied to `/etc/cas/config`  automatically.

## Deployment

```bash
./gradlew[.bat] clean build && java -jar cas/build/libs/cas.war
```
