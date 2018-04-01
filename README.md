CAS Gradle Overlay
============================
Generic CAS gradle war overlay to exercise the latest versions of CAS. This overlay could be freely
used as a starting template for local CAS gradle war overlays.

## Versions

* CAS `5.2.x`

## Requirements

* JDK 1.8.x

## Getting started

First make sure that you have both git and Java 8 installed. Apereo
CAS will not work with Java 9.

```shell
$ git clone git@github.com:apereo/cas-gradle-overlay-template.git
$ sudo ./build.sh gencert
$ sudo ./build.sh run
$ xdg-open https://localhost:8443/cas
```

A CAS login page should now, after accepting the self signed HTPPS
certificate, be visible in your browser.

## Configuration

The `etc` directory contains the configuration files that are copied to `/etc/cas/config`  automatically.

## Adding Modules

CAS modules may be specified under the `dependencies` block of the [CAS subproject](cas/build.gradle):

```gradle
dependencies {
    compile "org.apereo.cas:cas-server-webapp-tomcat:${project.'cas.version'}@war"
    compile "org.apereo.cas:cas-server-some-module:${project.'cas.version'}"
    ...
}
```

Study material:

- https://docs.gradle.org/current/userguide/artifact_dependencies_tutorial.html
- https://docs.gradle.org/current/userguide/dependency_management.html

## Build

To see what commands are available to the build script, run:

```bash
./build.sh help
```

To package the final web application, run:

```bash
./build.sh package
```

To update `SNAPSHOT` versions run:

```bash
./build.sh package --refresh-dependencies
```

### Clear Gradle Cache

If you need to, on Linux/Unix systems, you can delete all the existing artifacts (artifacts and metadata)
Gradle has downloaded using:

```bash
# Only do this when absolutely necessary!
rm -rf $HOME/.gradle/caches/
```

Same strategy applies to Windows too, provided you switch `$HOME` to its equivalent in the above command.

### Build Tasks

To see what commands are available in the build, use:

```bash
 ./gradlew[.bat] tasks
```

### Project Dependencies

To see where certain dependencies come from in the build:

```bash
# Show the surrounding 2 before/after lines once a match is found
 ./gradlew[.bat] allDependencies | grep -A 2 -B 2 xyz
```

Or:

```bash
./gradlew[.bat] allDependenciesInsight --configuration [compile|runtime] --dependency xyz
```

## Deployment

- Create a keystore file `thekeystore` under `/etc/cas` on Linux. Use `c:/etc/cas` on Windows.
- Use the password `changeit` for both the keystore and the key/certificate entries.
- Ensure the keystore is loaded up with keys and certificates of the server, by adding the following to `./etc/cas/config/cas.properties`:

```properties
server.ssl.keyStore=file:/etc/cas/thekeystore
server.ssl.keyStorePassword=changeit
server.ssl.keyPassword=changeit
```

On a successful deployment via the following methods, CAS will be available at:

* `http://cas.server.name:8080/cas`
* `https://cas.server.name:8443/cas`

## Executable WAR

Run the CAS web application as an executable WAR.

```bash
./build.sh run
```

## Spring Boot

Run the CAS web application as an executable WAR via Spring Boot. This is most useful during development and testing.

```bash
./build.sh bootrun
```

### Warning!

Be careful with this method of deployment. `bootRun` is not designed to work with already executable WAR artifacts such that CAS server web application. YMMV. Today, uses of this mode ONLY work when there is **NO OTHER** dependency added to the build script and the `cas-server-webapp` is the only present module. See [this issue](https://github.com/apereo/cas/issues/2334) and [this issue](https://github.com/spring-projects/spring-boot/issues/8320) for more info.

### External

Deploy resultant `cas/build/libs/cas.war` to a servlet container of choice.

## Troubleshooting

You can also run the CAS server in `DEBUG` mode to step into the code via an IDE that is able to connect to the port `5005`.

```bash
./build.sh debug
```

To setup a development environment for either eclipse or IDEA:

```bash
# ./gradlew[.bat] eclipse
# ./gradlew[.bat] idea
```

The above tasks help to setup a project for your development environment. If you find that something has gone wrong, you can always start anew by using the following:

```bash
# ./gradlew[.bat] cleanEclipse
# ./gradlew[.bat] cleanIdea
```


## Explode WAR

You may explode/unzip the generated CAS web application if you wish to peek into the artifact
to examine dependencies, configuration files and such that are merged as part of the overlay build process.

```bash
./gradlew[.bat] explodeWar
```


## Command Line Shell

Invokes the CAS Command Line Shell. For a list of commands either use no arguments or use -h. To enter the interactive shell use -sh.

```bash
./build.sh cli
```
