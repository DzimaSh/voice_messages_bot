@echo off

REM Check if both arguments are provided
if "%~2"=="" (
    echo Usage: decoder.bat ^<arg1^> ^<arg2^>
    exit /b 1
)

py decoder.py %1 %2
exit /b 0

