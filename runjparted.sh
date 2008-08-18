#!/bin/sh
LIB=dist/lib
java -cp $LIB/jparted.jar:$LIB/hfsx_dmglib.jar:$LIB/swing-layout-1.0.3.jar:$LIB/apache-ant-1.7.0-bzip2.jar:$LIB/iharder-base64.jar:$LIB/catacombae_io.jar org.catacombae.jparted.app.Main $1 $2 $3 $4 $5 $6 $7 $8 $9
