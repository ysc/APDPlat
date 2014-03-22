@echo off
echo Starting APDPlat ...
cd APDPlat_Core
call mvn clean install
cd ..
cd APDPlat_Module
call mvn clean install
cd ..
cd APDPlat_Web
call mvn clean
set MAVEN_OPTS=-Xms256m -Xmx1000m -XX:MaxNewSize=256m -XX:MaxPermSize=256m
call mvn jetty:run-war