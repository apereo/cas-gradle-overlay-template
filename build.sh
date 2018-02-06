#!/bin/bash


function copy() {
	echo -e "Creating configuration directory under /etc/cas"
	mkdir -p /etc/cas/config

	echo -e "Copying configuration files from etc/cas to /etc/cas"
	cp -rfv etc/cas/* /etc/cas
}

function help() {
	echo "Usage: build.sh [copy|clean|package|run|debug|bootrun|gencert]"
	echo "	copy: Copy config from ./etc/cas/config to /etc/cas/config"
	echo "	clean: Clean build directory"
	echo "	package: Clean and build CAS war, also call copy"
	echo "	run: Build and run cas.war via Spring Boot (java -jar target/cas.war)"
	echo "	runalone: Build and run cas.war on its own (target/cas.war)"
	echo "	debug: Run CAS.war and listen for Java debugger on port 5000"
	echo "	bootrun: Run with the Spring Boot plugin, doesn't work with multiple dependencies"
	echo "	gencert: Create keystore with SSL certificate in location where CAS looks by default"
	echo "	command: Run the CAS command line shell and pass commands"
}

function clean() {
	./gradlew clean "$@"
}

function package() {
	./gradlew clean build "$@"
	copy
}

function bootrun() {
	./gradlew clean build bootRun "$@"
}

function debug() {
	package && java -Xdebug -Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=n -jar target/cas.war
}

function run() {
	package && java -jar target/cas.war
}

function runalone() {
	package && chmod +x target/cas.war && target/cas.war
}

function gencert() {
	if [[ ! -d /etc/cas ]] ; then 
		copy
	fi
	which keytool
	if [[ $? -ne 0 ]] ; then
	    echo Error: Java JDK \'keytool\' is not installed or is not in the path
	    exit 1
	fi
	# override DNAME and CERT_SUBJ_ALT_NAMES before calling or use dummy values
	DNAME="${DNAME:-CN=cas.example.org,OU=Example,OU=Org,C=US}"
	CERT_SUBJ_ALT_NAMES="${CERT_SUBJ_ALT_NAMES:-dns:example.org,dns:localhost,ip:127.0.0.1}"
	echo "Generating keystore for CAS with DN ${DNAME}"
	keytool -genkeypair -alias cas -keyalg RSA -keypass changeit -storepass changeit -keystore /etc/cas/thekeystore -dname ${DNAME} -ext SAN=${CERT_SUBJ_ALT_NAMES}
	keytool -exportcert -alias cas -storepass changeit -keystore /etc/cas/thekeystore -file /etc/cas/cas.cer
}

function cli() {
	
	CAS_VERSION=$(./gradlew casVersion --quiet)
	# echo "CAS version: $CAS_VERSION"
	JAR_FILE_NAME="cas-server-support-shell-${CAS_VERSION}.jar"
	# echo "JAR name: $JAR_FILE_NAME"
	JAR_PATH="org/apereo/cas/cas-server-support-shell/${CAS_VERSION}/${JAR_FILE_NAME}"
	# echo "JAR path: $JAR_PATH"

	JAR_FILE_LOCAL="$HOME/.m2/repository/$JAR_PATH";
	# echo "Local JAR file path: $JAR_FILE_LOCAL";
	if [ -f "$JAR_FILE_LOCAL" ]; then
		# echo "Using JAR file locally at $JAR_FILE_LOCAL"
		java -jar $JAR_FILE_LOCAL "$@"
		exit 0;
	fi

	COMMAND_FILE="./target/${JAR_FILE_NAME}"
	if [ ! -f "$COMMAND_FILE" ]; then
		mkdir -p ./target
		wget "https://repo1.maven.org/maven2/${JAR_PATH}" -P ./target
		java -jar $COMMAND_FILE "$@"
		exit 0;
	fi

}

if [ $# -eq 0 ]; then
    echo -e "No commands provided. Defaulting to [run]\n"
    run
    exit 0
fi


case "$1" in
"copy")
    copy 
    ;;
"clean")
	shift
    clean "$@"
    ;;   
"package")
	shift
    package "$@"
    ;;
"bootrun")
	shift
    bootrun "$@"
    ;;
"debug")
    debug "$@"
    ;;
"run")
    run "$@"
    ;;
"runalone")
    runalone "$@"
    ;;
"gencert")
    gencert "$@"
    ;;
"cli")
    shift
    cli "$@"
    ;;
*)
    help
    ;;
esac