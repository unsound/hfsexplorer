@echo off
:begin
setlocal
pushd %~dp0

set LIBRARY_PATH=%~dp0lib
set JARFILE_PATH=%~dp0build\lib
set JARFILENAME=hfsx.jar
set CLASSPATH="%~dp0build.~";"%~dp0dist\lib\swing-layout-1.0.1.jar";"%~dp0dist\lib\hfsx_dmglib.jar";"%~dp0lib\AppleJavaExtensions.jar";"%~dp0lib\java_awt_Desktop.jar"
set SOURCES_DIR=%~dp0src
set BUILD_DIR=%~dp0build.~
set RESOURCE_SRC_DIR=%~dp0resource
set RESOURCE_DST_DIR=%BUILD_DIR%\res
set BUILDENUM_CP="%~dp0lib\buildenumerator.jar"

echo Removing all class files...
if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
mkdir "%BUILD_DIR%"

echo Copying resources to classpath...
mkdir "%RESOURCE_DST_DIR%"
copy /y \\.\\"%RESOURCE_SRC_DIR%\*.png" "%RESOURCE_DST_DIR%" > NUL:

echo Incrementing build number...
java -cp %BUILDENUM_CP% BuildEnumerator "%SOURCES_DIR%\org\catacombae\hfsexplorer\BuildNumber.java" 1

REM Found out the trick for wildcard expansion of
REM spaced file names! (enclosed within "") Just 
REM prepend \\.\\ (\\.\ really, but last \ needed for ")

echo Compiling org.catacombae.hfsexplorer...
javac -cp %CLASSPATH% -sourcepath "%SOURCES_DIR%" -d "%BUILD_DIR%" -Xlint:unchecked \\.\\"%SOURCES_DIR%\org\catacombae\hfsexplorer\*.java"
set JAVAC_EXIT_CODE=%ERRORLEVEL%
if not "%JAVAC_EXIT_CODE%"=="0" goto error

echo Compiling org.catacombae.hfsexplorer.gui...
javac -cp %CLASSPATH% -sourcepath "%SOURCES_DIR%" -d "%BUILD_DIR%" -Xlint:unchecked \\.\\"%SOURCES_DIR%\org\catacombae\hfsexplorer\gui\*.java"
set JAVAC_EXIT_CODE=%ERRORLEVEL%
if not "%JAVAC_EXIT_CODE%"=="0" goto error

echo Compiling org.catacombae.hfsexplorer.partitioning...
javac -cp %CLASSPATH% -sourcepath "%SOURCES_DIR%" -d "%BUILD_DIR%" -Xlint:unchecked \\.\\"%SOURCES_DIR%\org\catacombae\hfsexplorer\partitioning\*.java"
set JAVAC_EXIT_CODE=%ERRORLEVEL%
if not "%JAVAC_EXIT_CODE%"=="0" goto error

echo Compiling org.catacombae.hfsexplorer.testcode...
javac -cp %CLASSPATH% -sourcepath "%SOURCES_DIR%" -d "%BUILD_DIR%" -Xlint:unchecked \\.\\"%SOURCES_DIR%\org\catacombae\hfsexplorer\testcode\*.java"
set JAVAC_EXIT_CODE=%ERRORLEVEL%
if not "%JAVAC_EXIT_CODE%"=="0" goto error

echo Compiling org.catacombae.hfsexplorer.types...
javac -cp %CLASSPATH% -sourcepath "%SOURCES_DIR%" -d "%BUILD_DIR%" -Xlint:unchecked \\.\\"%SOURCES_DIR%\org\catacombae\hfsexplorer\types\*.java"
set JAVAC_EXIT_CODE=%ERRORLEVEL%
if not "%JAVAC_EXIT_CODE%"=="0" goto error

echo Compiling org.catacombae.hfsexplorer.win32...
javac -cp %CLASSPATH% -sourcepath "%SOURCES_DIR%" -d "%BUILD_DIR%" -Xlint:unchecked \\.\\"%SOURCES_DIR%\org\catacombae\hfsexplorer\win32\*.java"
set JAVAC_EXIT_CODE=%ERRORLEVEL%
if not "%JAVAC_EXIT_CODE%"=="0" goto error

echo Building jar-file...
if not exist "%JARFILE_PATH%" mkdir "%JARFILE_PATH%"
jar cvf "%JARFILE_PATH%\%JARFILENAME%" -C "%BUILD_DIR%" . >NUL:
if "%ERRORLEVEL%"=="0" (echo Done!) else echo Problems while building jar-file...

goto end

:error
echo There were errors...
echo Decrementing build number...
java -cp %BUILDENUM_CP% BuildEnumerator "%SOURCES_DIR%\org\catacombae\hfsexplorer\BuildNumber.java" -1
goto end

:end
popd
endlocal