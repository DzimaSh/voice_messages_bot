#!/bin/sh

if [ "$#" -lt 3 ]; then
    echo "Usage: ./decode.sh <decoder_file_name> <model_name> <voice_file_path> [language] [encode_flag]"
    exit 1
fi

# Check if language is provided, else set it to empty string
if [ -n "$4" ]; then
    language=$4
else
    language=""
fi

# Check if encode_flag is provided, else set it to false
if [ -n "$5" ]; then
    encode_flag=$5
else
    encode_flag=false
fi

py $1 $2 $3 $language $encode_flag

exit 0


