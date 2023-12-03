@echo off

REM Check if both arguments are provided
if "%~3"=="" (
    echo Usage: decode.bat ^<decoder_file_name^> ^<model_name^> ^<voice_file_path^> [--language=LANGUAGE] [--encode | --no-encode]
    exit /b 1
)

REM Check if language is provided
if not "%~4"=="" (
    set language_arg=%4
) else (
    set language_arg=
)

REM Check if encode_flag is provided, else set it to false
if not "%~5"=="" (
    set encode_flag_arg=%5
) else (
    set encode_flag_arg=
)

py %1 %2 %3 %language_arg% %encode_flag_arg%

exit /b 0
