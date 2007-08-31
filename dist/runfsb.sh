#!/bin/sh
java -cp lib/hfsx.jar:lib/hfsx_dmglib.jar:lib/swing-layout-1.0.1.jar:lib/apache-ant-1.7.0-bzip2.jar:lib/iharder-base64.jar org.catacombae.hfsexplorer.FileSystemBrowserWindow $1 $2 $3 $4
