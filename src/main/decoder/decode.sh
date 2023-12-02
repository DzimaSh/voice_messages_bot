#!/bin/sh

if [ "$#" -ne 2 ]; then
    echo "Usage: ./decoder.sh <arg1> <arg2>"
    exit 1
fi

py ./decoder.py $1 $2

exit 0

