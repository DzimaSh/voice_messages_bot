@echo off

REM Check if both arguments are provided
if "%~3"=="" (
    echo Usage: decode.bat ^<decoder_file_name^> ^<model_name^> ^<voice_file_path^> [language] [encode_flag]
    exit /b 1
)

REM Check if language is provided, else set it to empty string
if not "%~4"=="" (
    set language=%4
) else (
    set language=""
)

REM Check if encode_flag is provided, else set it to false
if not "%~5"=="" (
    set encode_flag=%5
) else (
    set encode_flag=false
)

py %1 %2 %3 %language% %encode_flag%

exit /b 0
