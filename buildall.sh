#!/bin/sh

error() {
    echo "There were errors..."
}

CLASSPATH=build.~:lib/swing-layout-1.0.1.jar
SOURCES_DIR=src
BUILD_DIR=build.~
LIBRARY_PATH=lib
JARFILE=hfsx.jar

echo "Removing all class files..."
rm -r $BUILD_DIR
mkdir $BUILD_DIR

echo "Building..."
javac -cp $CLASSPATH -sourcepath $SOURCES_DIR -d $BUILD_DIR -Xlint:unchecked $SOURCES_DIR/org/catacombae/hfsexplorer/types/*.java
javac -cp $CLASSPATH -sourcepath $SOURCES_DIR -d $BUILD_DIR -Xlint:unchecked $SOURCES_DIR/org/catacombae/hfsexplorer/*.java
JAVAC_EXIT_CODE=$?
if [ "$JAVAC_EXIT_CODE" != 0 ]; then
    error
else
    echo "Building jar-file..."
    if [ ! -d "$LIBRARY_PATH" ]; then # if not exists $LIBRARY_PATH...
	echo "Making library path"
    	mkdir $LIBRARY_PATH
    fi
    jar cf $LIBRARY_PATH/$JARFILE -C $BUILD_DIR .
    if [ "$?" == 0 ]; then
	echo "Done!"
    else
	error
    fi
fi    
