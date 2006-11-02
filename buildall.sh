#!/bin/sh

CLASSPATH=build.~:lib/swing-layout-1.0.1-stripped.jar
SOURCES_DIR=src
BUILD_DIR=build.~

echo "Removing all class files..."
rm -r $BUILD_DIR
mkdir $BUILD_DIR

echo "Building..."
javac -cp $CLASSPATH -sourcepath $SOURCES_DIR -d $BUILD_DIR -Xlint:unchecked $SOURCES_DIR/*.java
javac -cp $CLASSPATH -sourcepath $SOURCES_DIR -d $BUILD_DIR -Xlint:unchecked $SOURCES_DIR/org/catacombae/hfsexplorer/gui/*.java

echo "Done!"
