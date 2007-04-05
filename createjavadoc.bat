@echo off
setlocal
pushd "%~dp0"

set SOURCEFILES=..\src\org\catacombae\hfsexplorer\*.java ..\src\org\catacombae\hfsexplorer\apm\*.java ..\src\org\catacombae\hfsexplorer\gui\*.java ..\src\org\catacombae\hfsexplorer\testcode\*.java ..\src\org\catacombae\hfsexplorer\types\*.java ..\src\org\catacombae\hfsexplorer\win32\*.java
set CLASSPATH=..\lib\swing-layout-1.0.1.jar;..\lib\hfsx_dmglib.jar

rmdir /s /q doc.~
mkdir doc.~
cd doc.~
javadoc -private -link http://java.sun.com/j2se/1.5.0/docs/api/ %SOURCEFILES%
cd ..
echo Javadoc constructed in directory doc.~.
popd
endlocal