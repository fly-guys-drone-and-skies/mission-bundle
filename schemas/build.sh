#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

# Fixes MinGW path conversion issues
export MSYS_NO_PATHCONV=1


if [ $# -ne 3 ]; then
    echo "Usage: $0 [language] [target] [output]"
    exit 1
fi

LANGUAGE=$1
TARGET=$2
OUTPUT=$3

# TODO: limit this so gRPC doesn't get generated
# Generate code from target protoc files via a container
# https://github.com/namely/docker-protoc
docker pull namely/protoc-all
docker run -v ${DIR}:/defs -v ${OUTPUT}:/tmp/out namely/protoc-all -d /defs/${TARGET} -l ${LANGUAGE} -o /tmp/out
