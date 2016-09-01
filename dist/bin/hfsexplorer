#!/bin/sh

BASEDIR="`dirname "$0"`"
if [ $? -ne 0 ]; then
    echo "WARNING: No dirname utility found!"
    echo "         Script will only work if invoked from its parent directory."
    BASEDIR="."
fi

LIB="${BASEDIR}/../lib"

UNAME=`uname`
if [ "${UNAME}" = "Darwin" ]; then
    DOCK_NAME="-Xdock:name=HFSExplorer"
    DOCK_ICON="-Xdock:icon=${BASEDIR}/../res/icon.png"
fi
java ${DOCK_NAME:+"${DOCK_NAME}"} ${DOCK_ICON:+"${DOCK_ICON}"} -cp "$LIB/hfsx.jar" org.catacombae.hfsexplorer.FileSystemBrowserWindow "$@"
