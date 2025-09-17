@ECHO OFF
:: Gradle startup script for Windows

SET DIR=%~dp0
java -jar "%DIR%\gradle\wrapper\gradle-wrapper.jar" %*
