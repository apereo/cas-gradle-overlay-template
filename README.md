CAS Gradle Overlay
============================
Generic CAS gradle war overlay to exercise the latest versions of CAS. This overlay could be freely
used as a starting template for local CAS gradle war overlays.

## Versions

* CAS 5.0.x

## Requirements

* JDK 1.8+

## Configuration

The `etc` directory contains the configuration files that are copied to `/etc/cas/config`  automatically.

## Adding Modules

CAS modules may be specified under the `dependencies` block of the [CAS subproject](cas/build.gradle):

```gradle
dependencies {
    compile "org.apereo.cas:cas-server-webapp:${project.'cas.version'}@war"
    compile "org.apereo.cas:cas-server-some-module:${project.'cas.version'}"
    ...
}
```

Study material:

- https://docs.gradle.org/current/userguide/artifact_dependencies_tutorial.html
- https://docs.gradle.org/current/userguide/dependency_management.html


## Build

```bash
./gradlew[.bat] clean build
```

Or faster builds on subsequent attempts once modules/dependencies are resolved:

```bash
./gradlew[.bat] clean build --parallel --offline
```

If you are on a `SNAPSHOT` version, you can force redownloads of modules/dependencies:

```bash
 ./gradlew[.bat] clean build --parallel --refresh-dependencies
```

If you need to, on Linux/Unix systems, you can delete all the existing artifacts (artifacts and metadata)
Gradle has downloaded using:

```bash
# Only do this when absolutely necessary!
rm -rf $HOME/.gradle/caches/
```

Same strategy applies to Windows too, provided you switch `$HOME` to its equivalent in the above command.

To see what commands are available in the build, use:

```bash
 ./gradlew[.bat] tasks
```

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

### Warning!

Be careful with this method of deployment. `bootRun` is not designed to work with already executable WAR artifacts such that CAS server web application. YMMV. Today, uses of this mode ONLY work when there is **NO OTHER** dependency added to the build script and the `cas-server-webapp` is the only present module. See [this issue](https://github.com/apereo/cas/issues/2334) and [this issue](https://github.com/spring-projects/spring-boot/issues/8320) for more info.

## External

Deploy resultant `cas/build/libs/cas.war` to a servlet container of choice.
