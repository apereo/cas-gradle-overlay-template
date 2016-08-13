infusionsoft-cas
================

The full installation instructions can be found here: https://wiki.infusionsoft.com/display/dev/Running+CAS+in+a+Development+Environment


# Mac setup to run CAS with Core
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
    - Switch $JAVA_HOME back to your original Java
3. Download Maven 3.0.5 (other versions may work)
    - https://archive.apache.org/dist/maven/maven-3/3.0.5/binaries/
    - Extract the file into 
    ```sh
        /usr/local/installs/maven 
    ```
    - Create a symlink for Maven 3.0.5
    ```sh
        ln -s /usr/local/installs/maven/apache-maven-3.0.5 cas
    ```
    
4. Create a DB schema for 'cas' in your database, make sure the default charset is latin1, use workbench OR run
    ```sh
        mysql createdb -u eric -p cas
    ```
    - The default password is: eric5425
5. Add a hosts file entry for CAS
    - Inside of ```/etc/hosts``` add 
    ```sh
        127.0.0.1   devcas.infusiontest.com
    ```
6. Checkout CAS
		
	```sh
	  git clone git@github.com:infusionsoft/infusionsoft-cas.git
	```
7. Build CAS using Java 6 and Maven 3.0.5. ```Inside of your CAS directory``` run:
    
  ```sh
      JAVA_HOME=`/usr/libexec/java_home -v 1.6` M2_HOME=/usr/local/installs/maven/cas mvn clean install
  ```
8. Run CAS using Java 6 and Maven 3.0.5. ```Inside of your CAS directory``` run:
    
  ```sh
      JAVA_HOME=`/usr/libexec/java_home -v 1.6` M2_HOME=/usr/local/installs/maven/cas mvn tomcat6:run-war
  ```
  - The first time you run CAS it should populate your cas schema
9. Install Core for CAS ```Inside of your Core directory``` run:
  
  ```sh
      mvn clean install -P cas
  ```
10. Run Core for CAS ```Inside of your Core directory``` run:
    
  ```sh
      mvn tomcat6:run -pl webapp -P cas
  ```
11. You should now be able to access CAS through the same URL you go to for your Core app, which will then redirect you to https://devcas.infusiontest.com:7443/ ```If you go straight to this URL your app may throw security errors and not work!```
12. Follow through the wizzard and create yourself a user, this will create a record in 'cas.user' link this user to the user in your local Core app's DB, place your cas.user.id into (localApp).User as the "CasGlobalId.
   
   ```sql
   	UPDATE <localApp>.User SET CasGlobalId=<cas user id> WHERE id=<your local user id>;
   ```
13. Make some tweaks to your cas schema
  - Make sure that the 'success' column of cas.schema_version in your cas DB is all 1's, 
  - Add some rights to the cas.user_autority table, there are 10 authorities found in the cas.authority table, add as many of them as you'd like.

# Note
You can alias the commands above by adding them to your ```.bashrc``` file like so:

```sh
	alias installCas="JAVA_HOME=`/usr/libexec/java_home -v 1.6` M2_HOME=/usr/local/installs/maven/cas mvn clean install"
	alias runCas="JAVA_HOME=`/usr/libexec/java_home -v 1.6` M2_HOME=/usr/local/installs/maven/cas mvn tomcat6:run-war"
	alias installCoreForCas="mvn clean install -P cas"
	alias runCoreForCas="mvn tomcat6:run -pl webapp -P cas"
```

#Congratulations, you've made it!









##Setup cas:

1. run ./bin/bootstrap 

2. add to /etc/hosts file: 127.0.0.1    devcas.infusiontest.com

3. mvn clean install

4. run cas server using "mvn tomcat6:run-war"

5. INSERT INTO cas.user VALUES (1, 1, 'your-first-name', 'your-last-name', null, null, 'youre-email')

6. INSERT INTO cas.user_authority VALUES (1,2);

7. Go to https://devcas.infusiontest.com:7443

8. Click 'Forgot Your Password?'

9. Get recovery code from the logs or from the database with: SELECT password_recovery_code FROM cas.user;

10. Enter recovery code and create new password 


##Configure Infusionsoft to use cas:

1. Update your infusionsoft User.GlobalUserId field: UPDATE User SET GlobalUserId=1 WHERE Id=~yourUserId~;

2. Compile and Run flagship with the cas profile: 
    mvn clean install -P cas
    mvn tomcat6:run -pl webapp -P cas
    
3. Login with cas crednetials

# You did it!