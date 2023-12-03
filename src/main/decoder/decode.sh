#!/bin/sh

# Check if both arguments are provided
if [ "$#" -lt 3 ]; then
    echo "Usage: ./decode.sh <decoder_file_name> <model_name> <voice_file_path> [--language=LANGUAGE] [--encode | --no-encode]"
    exit 1
fi

# Check if language is provided
if [ -n "$4" ]; then
    language_arg=$4
else
    language_arg=""
fi

# Check if encode_flag is provided, else set it to false
if [ -n "$5" ]; then
    encode_flag_arg=$5
else
    encode_flag_arg=""
fi

py $1 $2 $3 $language_arg $encode_flag_arg

exit 0
