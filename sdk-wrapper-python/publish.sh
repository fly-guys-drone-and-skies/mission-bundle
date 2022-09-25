#!/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
VERSION=`cat ${SCRIPT_DIR}/version.txt`

# TODO: maybe an interactive option for which SDK implementation (support non-parrot)
docker build ${SCRIPT_DIR} -f deployment/parrot/Dockerfile -t sdk-wrapper-parrot:${VERSION}

