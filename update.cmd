@echo off
start /wait cmd /C mvn dependency:resolve
start /wait cmd /C mvn dependency:copy-dependencies
start /wait cmd /C mvn dependency:go-offline
start /wait cmd /C mvn clean install

start /wait cmd /C mvn -o ant:ant
start /wait cmd /C mvn -o eclipse:eclipse