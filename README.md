infusionsoft-cas
================

The full installation instructions can be found here: https://wiki.infusionsoft.com/display/dev/Running+CAS+in+a+Development+Environment


# Mac Setup
## - For more advanced functionality reference the full instructions above
1. Download and install Java 6 (Yes, Java 6)
    - https://support.apple.com/kb/dl1572?locale=en_US
2. Install the Certificates of Authenticity for Java 6
    - Switch $JAVA_HOME to Java 6
    - Download the .sh script found here: https://wiki.infusionsoft.com/display/dev/JDK+Certificate+Store
    - Run the sh script from terminal, pass the default password of 'changeit' into the command eg:
    ```sh 
        sh fix_certs.sh changeit
    ```
3. Download Maven 3.0.5
    - https://archive.apache.org/dist/maven/maven-3/3.0.5/binaries/
    - Extract the file into 
    ```sh
        /usr/local/installs/maven 
    ```
    - Create a symlink for Maven 3.0.5
    ```sh
        ln -s /usr/local/installs/maven/apache-maven-3.0.5 cas
    ```
    
4. Create a DB schema for 'cas' in your database, make sure the default charset is latin1
    ```sh
        mysql createdb -u eric -p cas
    ```
    - The default password is: eric5425
5. Add a hosts file entry for cas
    - Inside of ```/etc/hosts``` add 
    ```sh
        127.0.0.1   devcas.infusiontest.com
    ```
6. Checkout CAS to ```~/Development/infusionsoft-cas/```
		
	```sh
	  git clone git@github.com:infusionsoft/infusionsoft-cas.git
	```
7. Build CAS using Java 6 and Maven 3.0.5 ```Inside of ~/Development/infusionsoft-cas/``` run:
    
  ```sh
      JAVA_HOME=`/usr/libexec/java_home -v 1.6` M2_HOME=/usr/local/installs/maven/cas MAVEN_HOME=/usr/local/installs/maven/cas/bin mvn clean install
  ```
8. Run CAS using Java 6 and Maven 3.0.5 ```Inside of ~/Development/infusionsoft-cas/``` run:
    
  ```sh
      JAVA_HOME=`/usr/libexec/java_home -v 1.6` M2_HOME=/usr/local/installs/maven/cas MAVEN_HOME=/usr/local/installs/maven/cas/bin mvn tomcat:run
  ```
9. Make sure that the 'success' column of cas.schema_version in your cas db is all 1's
10. Install Core for CAS ```Inside of ~/Development/infusionsoft-core/``` run:
  
  ```sh
      mvn clean install -P cas
  ```
11. Run Core for CAS ```Inside of ~/Development/infusionsoft-core/``` run:
    
  ```sh
      mvn tomcat6:run -pl webapp -P cas
  ```
12. You should now be able to access CAS through the same URL you go to for your Core app, which will then redirect you to https://devcas.infusiontest.com:7443/ ```If you go straight to this URL your app may throw security errors and not work!```    

# Note
You can alias the commands above by adding them to your ```.bashrc``` file like so: ```Modify file paths to suit your setup```

```sh
        alias installCas="JAVA_HOME=`/usr/libexec/java_home -v 1.6` M2_HOME=/usr/local/installs/maven/cas MAVEN_HOME=/usr/local/installs/maven/cas/bin mvn clean install"
        alias runCas="JAVA_HOME=`/usr/libexec/java_home -v 1.6` M2_HOME=/usr/local/installs/maven/cas MAVEN_HOME=/usr/local/installs/maven/cas/bin mvn tomcat:run"
        alias installCoreForCas="mvn clean install -P cas"
        alias runCoreForCas="mvn tomcat6:run -pl webapp -P cas"
```
