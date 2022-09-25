#!/bin/bash

# Strip sound from MP4 to make it processable by OpenCV

ffmpeg -i $1 -an -c copy $1_ns.mp4
