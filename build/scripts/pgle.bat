@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  pgle startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and PGLE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args
if "%@eval[2+2]" == "4" goto 4NT_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*
goto execute

:4NT_args
@rem Get arguments from the 4NT Shell from JP Software
set CMD_LINE_ARGS=%$

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\pgle-1.0-SNAPSHOT.jar;%APP_HOME%\lib\slf4j-api-1.7.12.jar;%APP_HOME%\lib\spring-context-4.0.3.RELEASE.jar;%APP_HOME%\lib\movile-res-commons-1.0.39.jar;%APP_HOME%\lib\spring-aop-4.0.3.RELEASE.jar;%APP_HOME%\lib\spring-expression-4.0.3.RELEASE.jar;%APP_HOME%\lib\lettuce-4.1.2.Final.jar;%APP_HOME%\lib\cassandra-driver-core-2.1.7.1.jar;%APP_HOME%\lib\snakeyaml-1.13.jar;%APP_HOME%\lib\guava-19.0.jar;%APP_HOME%\lib\commons-lang-2.6.jar;%APP_HOME%\lib\logback-core-1.1.3.jar;%APP_HOME%\lib\logback-classic-1.1.3.jar;%APP_HOME%\lib\spring-jdbc-4.1.6.RELEASE.jar;%APP_HOME%\lib\aopalliance-1.0.jar;%APP_HOME%\lib\rxjava-1.1.0.jar;%APP_HOME%\lib\netty-common-4.0.34.Final.jar;%APP_HOME%\lib\netty-transport-4.0.34.Final.jar;%APP_HOME%\lib\netty-handler-4.0.34.Final.jar;%APP_HOME%\lib\commons-pool2-2.4.2.jar;%APP_HOME%\lib\LatencyUtils-2.0.3.jar;%APP_HOME%\lib\metrics-core-3.0.2.jar;%APP_HOME%\lib\spring-tx-4.1.6.RELEASE.jar;%APP_HOME%\lib\netty-buffer-4.0.34.Final.jar;%APP_HOME%\lib\HdrHistogram-2.1.8.jar;%APP_HOME%\lib\jackson-databind-2.6.0.jar;%APP_HOME%\lib\netty-codec-4.0.34.Final.jar;%APP_HOME%\lib\spring-beans-4.1.6.RELEASE.jar;%APP_HOME%\lib\spring-core-4.1.6.RELEASE.jar;%APP_HOME%\lib\jackson-annotations-2.6.0.jar;%APP_HOME%\lib\jackson-core-2.6.0.jar;%APP_HOME%\lib\commons-logging-1.2.jar

@rem Execute pgle
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %PGLE_OPTS%  -classpath "%CLASSPATH%" com.movile.pgle.Coordinator %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable PGLE_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%PGLE_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
