FROM ubuntu:18.04

RUN useradd -ms /bin/bash app

# Install package dependencies
RUN apt update && apt install -y \
    python3 \
    python3-pip \
    repo

# Force specific version of clang for SDK usage
# https://forum.developer.parrot.com/t/libclang-11-not-found/11230/3
RUN python3 -m pip install \
    clang==6.0.0.2


WORKDIR /work/sdk
RUN chown -R app /work/sdk

# Clone SDK for usage
RUN repo init -u https://github.com/Parrot-Developers/groundsdk-manifest.git && repo sync
# Install SDK dependencies
RUN DEBIAN_FRONTEND="noninteractive" /work/sdk/products/olympe/linux/env/postinst
# Build SDK
USER app
RUN yes | /work/sdk/build.sh -p olympe-linux -A all final -j
USER root
# Remove temporary dependencies
RUN apt purge --autoremove -y \
    repo
USER app

RUN /bin/bash -c "source /work/sdk/products/olympe/linux/env/shell"


# Add wrapper
COPY requirements.txt /
RUN pip3 install -r /requirements.txt

COPY lib /work/src/lib
COPY proto /work/src/proto

CMD python3 /work/src/lib/app.py
