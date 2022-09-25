#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

# Generate protobuf code
SCHEMAS_DIR=${SCHEMAS_DIR:-${DIR}/../schemas}

OUT_DIR=$(readlink -f ${DIR}/proto)
mkdir -p OUT_DIR

${SCHEMAS_DIR}/build.sh python ipc ${OUT_DIR}

# Install virtualenv requirements if needed
VENV_DIR=${DIR}/venv
if [ ! -d ${VENV_DIR} ] then
  python3 -m venv ${VENV_DIR}
fi

source VENV_DIR/bin/activate
pip install -r ${DIR}/requirements.txt
