@echo off
setlocal
pushd "%~dp0"

set SOURCEFILES=..\src\*.java ..\src\org\catacombae\hfsexplorer\gui\*.java
rem set CLASSPATH=..\lib\filedrop.jar;..\lib\swing

rmdir /s /q doc.~
mkdir doc.~
cd doc.~
javadoc -private -link http://java.sun.com/j2se/1.5.0/docs/api/ %SOURCEFILES%
cd ..
echo Javadoc constructed in directory doc.~.
popd
endlocal