CAS Gradle Overlay
============================
Generic CAS gradle war overlay to exercise the latest versions of CAS. This overlay could be freely used as a starting template for local CAS gradle war overlays. 

## Versions

* CAS 5.0.0

## Requirements

* JDK 1.8+

## Configuration

The `etc` directory contains the configuration files that are copied to `/etc/cas/config`  automatically.

## Build

```bash
./gradlew[.bat] clean build
```

## Deployment

- Create a keystore file `thekeystore` under `/etc/cas`. Use the password `changeit` for both the keystore and the key/certificate entries.
- Ensure the keystore is loaded up with keys and certificates of the server.

On a successful deployment via the following methods, CAS will be available at:

* `http://cas.server.name:8080/cas`
* `https://cas.server.name:8443/cas`

## Executable WAR

Run the CAS web application as an executable WAR.

```bash
java -jar cas/build/libs/cas.war 
```

## Spring Boot

Run the CAS web application as an executable WAR via Spring Boot. This is most useful during development and testing.

```bash
./gradlew[.bat] bootRun
```

## External

Deploy resultant `cas/build/libs/cas.war` to a servlet container of choice.