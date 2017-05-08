Infusionsoft CAS
================

# Initial Setup
For more advanced functionality reference https://wiki.infusionsoft.com/display/dev/Running+CAS+in+a+Development+Environment
1. Checkout CAS
		
	```sh
	  git clone git@github.com:infusionsoft/cas-gradle-overlay-template.git
	```
2. Download and install Java 8
3. Install the Certificates of Authenticity for Java 8

    Note this step is not technically needed to run CAS, but any service such as flagship connecting to the CAS API might get certificate errors if you skip this step.
    - Switch $JAVA_HOME to Java 8
    - Download the .sh script found here: https://wiki.infusionsoft.com/display/dev/JDK+Certificate+Store
    - Run the sh script from terminal, pass the default password of 'changeit' into the command eg:
    ```sh 
        sh fix_certs.sh changeit
    ```
4. Create a DB schema for 'cas' in your database, make sure the default charset is latin1, use workbench OR run
    ```sh
        mysql createdb -u eric -p cas
    ```
    The default password is: eric5425
5. Add a hosts file entry for CAS. Inside of `/etc/hosts` add: 
    ```sh
        127.0.0.1   devcas.infusiontest.com
    ```
6. Build CAS
    ```bash
    ./gradlew[.bat] clean build
    ```
7. Run CAS
    ```bash
    java -jar cas/build/libs/cas.war --cas.standalone.config=etc/cas/config
    ```
      - The first time you run CAS it should populate your cas schema
      - Make sure that the 'success' column of cas.schema_version in your cas DB is all 1's
8. Account Central
CAS depends on Account Central, see that project's README for how to build and run it.

# Run Flagship with CAS
1. With CAS and Account Central running, run Flagship using the `cas` profile. **Inside of your Core directory** run:
    ```sh
      mvn tomcat6:run -pl webapp -P cas
    ```
    Now when you access your flagship app, it will redirect you to `https://devcas.infusiontest.com:7443/` with a `service` URL parameter. _If you go straight to this URL without the service parameter, you will be logging into Account Central, not your app!_
2. Follow sign-up/registration link to create yourself a user, this will create a record in 'cas.user'. To link this user to the user in your local Core app's DB, place your cas.user.id into (localApp).User as the `GlobalUserId`.
   ```sql
   	UPDATE <localApp>.User SET GlobalUserId=<cas user id> WHERE id=<your local user id>;
   ```
3. Add permissions
    
    Add some rights to the cas.user_authority table, add as many of them as you'd like from the cas.authority table. 
    If you give yourself `ROLE_CAS_ADMIN` you can then use the account central UI to grant yourself additional roles.

Development Info
============================
Infusionsoft CAS is based on the generic CAS gradle war overlay.

* Version: CAS 5.1.x
* Requirement: JDK 1.8+

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
java -jar cas/build/libs/cas.war --cas.standalone.config=etc/cas/config
```

## Spring Boot

Run the CAS web application as an executable WAR via Spring Boot. This is most useful during development and testing.

```bash
./gradlew[.bat] bootRun
```

## External

Deploy resultant `cas/build/libs/cas.war` to a servlet container of choice.


