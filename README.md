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








Install docker, docker-machine, docker-compose, VirtualBox


Setup CAS Locally steps:
mvn clean install, JDK 7
update /etc/host file a) 127.0.0.1    devcas.infusiontest.com
run ./bootstrap from root
run cas server using "mvn tomcat6:run-war"


Modify cas database: Add to user table e.g. INSERT INTO cas.user VALUES (1, 1, 'Chad', 'Cotter', null, null, 'chad.cotter@infusionsoft.com');
Modify cas database: Add to user_authority e.g. INSERT INTO cas.user_authority VALUES (1,2);
login https://devcas.infusiontest.com:7443

Need to set password for CAS, click "Forgot Password" link. The recover code will be in the database (SELECT * FROM cas.user). Enter recover code and set new password.
(optional) Add cas role for content publishing: ROLE_CAS_LISTING_PUBLISHER_MARKETPLACE
Go to flagship database. Run Select * from Contact. Make sure that you have an admin username that matches the cas username above (the username must be an email address. If it doesn't match, update Flagship database to match cas username).
Run flagship: mvn tomcat6:run -pl webapp -P cas
check roles and make sure have cas publishing role: https://infusionsoft.infusiontest.com:8443/app/authentication/whoAmI.jsp

Make sure the JDK has the valid certificates. If not, install the certs.

------------------------------------------
Linux script:
fix_certs.sh
-----------------------------------------
#! /bin/sh

echo "Java home is:" $JAVA_HOME

if [ $1 ]
then
    password=$1
else
    echo "Usage $0 <keystore password> (default is 'changeit')"
    exit 1
fi

wget https://certs.godaddy.com/repository/gdroot-g2.crt
wget https://certs.godaddy.com/repository/gdig2.crt

sudo $JAVA_HOME/bin/keytool -import -alias cross -file ./gdroot-g2.crt -storepass $password -trustcacerts -keystore $JAVA_HOME/jre/lib/security/cacerts
sudo $JAVA_HOME/bin/keytool -import -alias root -file ./gdig2.crt -storepass $password -trustcacerts -keystore $JAVA_HOME/jre/lib/security/cacerts

rm gdroot-g2.crt
rm gdig2.crt
-----------------------------------------
