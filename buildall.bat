@echo off
set CLASSPATH="%~dp0\build.~";"%~dp0\lib\swing-layout-1.0.1-stripped.jar"
set SOURCES_DIR=%~dp0\src
set BUILD_DIR=%~dp0\build.~

REM Found out the trick for wildcard expansion of
REM spaced file names! (enclosed within "") Just 
REM prepend \\.\\ (\\.\ really, but last \ needed for ")

javac -cp %CLASSPATH% -sourcepath "%SOURCES_DIR%" -d "%BUILD_DIR%" -Xlint:unchecked \\.\\"%SOURCES_DIR%\*.java"
javac -cp %CLASSPATH% -sourcepath "%SOURCES_DIR%" -d "%BUILD_DIR%" -Xlint:unchecked \\.\\"%SOURCES_DIR%\org\catacombae\hfsexplorer\gui\*.java"
