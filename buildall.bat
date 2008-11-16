@echo off

:begin
setlocal
pushd %~dp0

set SOURCES_DIR=%~dp0src
set BUILDENUM_CP="%~dp0lib\buildenumerator.jar"

echo Incrementing build number...
java -cp %BUILDENUM_CP% BuildEnumerator "%SOURCES_DIR%\org\catacombae\hfsexplorer\BuildNumber.java" 1

echo Building with ant...
call ant build-all
if not "%ERRORLEVEL%"=="0" goto error

goto end

:error
echo There were errors...
echo Decrementing build number...
java -cp %BUILDENUM_CP% BuildEnumerator "%SOURCES_DIR%\org\catacombae\hfsexplorer\BuildNumber.java" -1
goto end

:end
popd
endlocal