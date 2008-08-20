#!/bin/sh

CLASSPATH="dist/lib/hfsx.jar":"dist/lib/swing-layout-1.0.3.jar":"dist/lib/hfsx_dmglib.jar":"lib/apache-ant-1.7.0-bzip2.jar":"lib/iharder-base64.jar":"dist/lib/catacombae_io.jar"
java -cp $CLASSPATH org.catacombae.hfsexplorer.$1 ${2:+"${2}"} ${3:+"$3"} ${4:+"$4"} ${5:+"$5"} ${6:+"$6"} ${7:+"$7"} ${8:+"$8"} ${9:+"$9"}
