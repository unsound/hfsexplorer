#!/bin/sh

SOURCEFILES="../src/org/catacombae/hfsexplorer/*.java ../src/org/catacombae/hfsexplorer/gui/*.java ../src/org/catacombae/hfsexplorer/partitioning/*.java ../src/org/catacombae/hfsexplorer/testcode/*.java ../src/org/catacombae/hfsexplorer/types/*.java ../src/org/catacombae/hfsexplorer/win32/*.java"
CLASSPATH=../lib/swing-layout-1.0.1.jar:../lib/hfsx_dmglib.jar


rm -r ./doc.~
mkdir ./doc.~
cd doc.~
javadoc -private -classpath $CLASSPATH -link http://java.sun.com/j2se/1.5.0/docs/api/ $SOURCEFILES
cd ..
echo Javadoc constructed in directory doc.~.
