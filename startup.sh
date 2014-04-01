#!/bin/sh
echo Starting APDPlat ...
cd APDPlat_Core
mvn clean install
cd ..
cd APDPlat_Module
mvn clean install
cd ..
cd APDPlat_Web
mvn clean
export MAVEN_OPTS="-Xms256m -Xmx1000m -XX:MaxNewSize=256m -XX:MaxPermSize=256m"
mvn jetty:run-war