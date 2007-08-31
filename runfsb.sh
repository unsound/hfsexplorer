#!/bin/sh
LIB=dist/lib
java -cp $LIB/hfsx.jar:$LIB/hfsx_dmglib.jar:$LIB/swing-layout-1.0.1.jar:$LIB/apache-ant-1.7.0-bzip2.jar:$LIB/iharder-base64.jar org.catacombae.hfsexplorer.FileSystemBrowserWindow $1 $2 $3 $4 $5 $6 $7 $8 $9
