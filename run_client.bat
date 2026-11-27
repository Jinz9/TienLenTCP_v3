@echo off
set JFX_PATH=C:\javafx-sdk-25\lib
set CLASSPATH=target/classes

echo Running ClientFX...
java --module-path "%JFX_PATH%" --add-modules javafx.controls,javafx.graphics,javafx.media -cp "%CLASSPATH%" tienlen.client.ClientFX
pause
