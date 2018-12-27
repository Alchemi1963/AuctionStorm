@echo off
start /wait cmd /C mvn dependency:get -Dartifact=org.apache.maven.plugins:maven-downloader-plugin:1.0
start /wait cmd /C mvn dependency:copy-dependencies
start /wait cmd /C mvn dependency:go-offline
start /wait cmd /C mvn clean install

start /wait cmd /C mvn ant:ant
start /wait cmd /C mvn eclipse:eclipse