#!/bin/sh

BASEDIR="`dirname "$0"`"
if [ $? -ne 0 ]; then
    echo "WARNING: No dirname utility found!"
    echo "         Script will only work if invoked from its parent directory."
    BASEDIR="."
fi

"${BASEDIR}/dist/bin/hfsx.sh" "$@"
