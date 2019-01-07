@echo off
mvn dependency:resolve dependency:copy-dependencies dependency:sources -o clean install -o ant:ant -o eclipse:eclipse
