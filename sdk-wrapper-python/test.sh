#!/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

export PYTHONPATH="${SCRIPT_DIR}/lib:${SCRIPT_DIR}/proto:${SCRIPT_DIR}/test:${PYTHONPATH}"

pytest test
