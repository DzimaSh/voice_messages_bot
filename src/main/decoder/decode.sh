#!/bin/sh

if [ "$#" -ne 3 ]; then
    echo "Usage: ./decode.sh <decoder_file_name> <model_name> <voice_file_path>"
    exit 1
fi

py $1 $2 $3

exit 0

