#!/bin/sh

BASEDIR="`dirname "$0"`"
if [ $? -ne 0 ]; then
    echo "WARNING: No dirname utility found!"
    echo "         Script will only work if invoked from its parent directory."
    BASEDIR="."
fi

LIB="${BASEDIR}/dist/lib"

java -cp "${BASEDIR}/targets/jparted/lib/jparted.jar":"${LIB}/hfsx_dmglib.jar":"${LIB}/swing-layout-1.0.3.jar":"${LIB}/apache-ant-1.7.0-bzip2.jar":"${LIB}/iharder-base64.jar":"${LIB}/catacombae_io.jar" org.catacombae.jparted.app.Main "$@"
