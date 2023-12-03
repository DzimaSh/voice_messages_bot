@echo off

REM Check if both arguments are provided
if "%~3"=="" (
    echo Usage: decode.bat ^<decoder_file_name^> ^<model_name^> ^<voice_file_path^> [--encode | --no-encode] [--language=LANGUAGE]
    exit /b 1
)

REM Check if language is provided
if not "%~4"=="" (
    set encode_flag_arg=%4
) else (
    set encode_flag_arg=
)

REM Check if encode_flag is provided, else set it to false
if not "%~6"=="" (
    set language_arg=%~5=%~6
) else (
    set language_arg=
)

py %1 %2 %3 %language_arg% %encode_flag_arg%

exit /b 0
