#!/bin/sh
LIB=lib

UNAME=`uname`
if [ "${UNAME}" = "Darwin" ]; then
    DOCK_NAME="-Xdock:name=HFSExplorer"
fi
java ${DOCK_NAME:+"${DOCK_NAME}"} -jar "$LIB/hfsx.jar" "$@"
