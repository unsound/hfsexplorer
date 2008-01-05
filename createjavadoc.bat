@echo off
setlocal
pushd "%~dp0"

REM Sub-base path
set SBP=..\src\org\catacombae

REM Base path
set BP=%SBP%\hfsexplorer

set SOURCEFILES=%BP%\*.java %BP%\gui\*.java %BP%\partitioning\*.java %BP%\testcode\*.java %BP%\types\*.java %BP%\types\hfs\*.java %BP%\win32\*.java %BP%\io\*.java %SBP%\csjc\*.java
set CLASSPATH=..\lib\swing-layout-1.0.1.jar;..\lib\hfsx_dmglib.jar

rmdir /s /q doc.~
mkdir doc.~
cd doc.~
javadoc -private -link http://java.sun.com/j2se/1.5.0/docs/api/ %SOURCEFILES%
cd ..
echo Javadoc constructed in directory doc.~.
popd
endlocal