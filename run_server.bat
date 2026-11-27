@echo off
set JFX_PATH=C:\javafx-sdk-25\lib
set MYSQL_JAR=C:\Users\ADMIN\.m2\repository\com\mysql\mysql-connector-j\8.2.0\mysql-connector-j-8.2.0.jar
set CLASSPATH=target/classes;%MYSQL_JAR%

echo Running ServerUI...
java --module-path "%JFX_PATH%" --add-modules javafx.controls,javafx.graphics -cp "%CLASSPATH%" tienlen.server.ServerUI
pause
