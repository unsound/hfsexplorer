@echo off
:begin
setlocal
pushd %~dp0

set LIBRARY_PATH=%~dp0lib
set JARFILE=hfsx.jar
set CLASSPATH="%~dp0build.~";"%~dp0lib\swing-layout-1.0.1.jar";"%~dp0lib\hfsx_dmglib.jar":"%~dp0lib\MRJToolkitStubs.zip"
set SOURCES_DIR=%~dp0src
set BUILD_DIR=%~dp0build.~
set RESOURCE_SRC_DIR=%~dp0resource
set RESOURCE_DST_DIR=%BUILD_DIR%\res

echo Removing all class files...
if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
mkdir "%BUILD_DIR%"

echo Copying resources to classpath...
mkdir "%RESOURCE_DST_DIR%"
copy /y \\.\\"%RESOURCE_SRC_DIR%\*.png" "%RESOURCE_DST_DIR%" > NUL:

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
if not exist "%LIBRARY_PATH%" mkdir "%LIBRARY_PATH%"
jar cvf "%LIBRARY_PATH%\%JARFILE%" -C "%BUILD_DIR%" . >NUL:
if "%ERRORLEVEL%"=="0" (echo Done!) else echo Problems while building jar-file...

goto end

:error
echo There were errors...
goto end

:end
popd
endlocal