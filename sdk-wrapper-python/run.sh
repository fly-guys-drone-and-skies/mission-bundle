#!/bin/bash

# ./run.sh 30000 192.168.54.1

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

export PYTHONPATH="${SCRIPT_DIR}/lib:${SCRIPT_DIR}/proto:${SCRIPT_DIR}/test:${PYTHONPATH}"

python3 app.py --bind-host 127.0.0.1 --bind-port $1 --drone-type parrot-anafi --drone-host $2



