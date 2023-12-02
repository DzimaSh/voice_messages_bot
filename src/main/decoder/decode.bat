@echo off

REM Check if both arguments are provided
if "%~3"=="" (
    echo Usage: decode.bat ^<decoder_file_name^> ^<model_name^> ^<voice_file_path^>
    exit /b 1
)

py %1 %2 %3

exit /b 0
