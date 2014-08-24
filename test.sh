#!/bin/sh

BASEDIR="`dirname "$0"`"
if [ $? -ne 0 ]; then
    echo "WARNING: No dirname utility found!"
    echo "         Script will only work if invoked from its parent directory."
    BASEDIR="."
fi

CLASSPATH="${BASEDIR}/dist/lib/hfsx.jar":"${BASEDIR}/dist/lib/swing-layout-1.0.3.jar":"${BASEDIR}/dist/lib/hfsx_dmglib.jar":"${BASEDIR}/dist/lib/apache-ant-1.7.0-bzip2.jar":"${BASEDIR}/dist/lib/iharder-base64.jar":"${BASEDIR}/dist/lib/catacombae_io.jar"
java -cp "${CLASSPATH}" org.catacombae.hfsexplorer.$1 ${2:+"${2}"} ${3:+"$3"} ${4:+"$4"} ${5:+"$5"} ${6:+"$6"} ${7:+"$7"} ${8:+"$8"} ${9:+"$9"}
