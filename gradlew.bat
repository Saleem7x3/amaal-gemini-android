@if "%DEBUG%" == "" @echo off
set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_HOME=%DIRNAME%
set GRADLE_OPTS=-Xmx1024m -Dfile.encoding=UTF-8
set CLASSPATH=%APP_HOME%gradle\wrapper\gradle-wrapper.jar
if not exist "%CLASSPATH%" (
    echo Error: gradle-wrapper.jar not found.
    echo Please open this project in Android Studio to auto-generate it.
    goto end
)
java %GRADLE_OPTS% -jar "%CLASSPATH%" %*
:end