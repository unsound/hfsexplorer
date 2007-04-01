#!/bin/sh

error() {
    echo "There were errors..."
}

jobCompleted() {
    echo "Done!"
}

CLASSPATH=build.~:lib/swing-layout-1.0.1.jar
SOURCES_DIR=src
BUILD_DIR=build.~
LIBRARY_PATH=lib
JARFILE=hfsx.jar
RESOURCE_SRC_DIR=resource
RESOURCE_DST_DIR=$BUILD_DIR/res

removeclassfiles() {
    echo "Removing all class files..."
    rm -r $BUILD_DIR
    mkdir $BUILD_DIR
    return $?
}

copyresources() {
    echo "Copying resources to classpath..."
    mkdir $RESOURCE_DST_DIR
    cp $RESOURCE_SRC_DIR/*.png $RESOURCE_DST_DIR
    #copy /y \\.\\"%RESOURCE_SRC_DIR%\*.png" "%RESOURCE_DST_DIR%" > NUL:
}

buildhfsx() {
    echo "Building org.catacombae.hfsexplorer..."
    javac -cp $CLASSPATH -sourcepath $SOURCES_DIR -d $BUILD_DIR -Xlint:unchecked $SOURCES_DIR/org/catacombae/hfsexplorer/*.java
    return $?
}

buildhfsx_partitioning() {
    echo "Building org.catacombae.hfsexplorer.partitioning..."
    javac -cp $CLASSPATH -sourcepath $SOURCES_DIR -d $BUILD_DIR -Xlint:unchecked $SOURCES_DIR/org/catacombae/hfsexplorer/partitioning/*.java
    return $?
}

buildhfsx_gui() {
    echo "Building org.catacombae.hfsexplorer.gui..."
    javac -cp $CLASSPATH -sourcepath $SOURCES_DIR -d $BUILD_DIR -Xlint:unchecked $SOURCES_DIR/org/catacombae/hfsexplorer/gui/*.java
    return $?
}

buildhfsx_testcode() {
    echo "Building org.catacombae.hfsexplorer.testcode..."
    javac -cp $CLASSPATH -sourcepath $SOURCES_DIR -d $BUILD_DIR -Xlint:unchecked $SOURCES_DIR/org/catacombae/hfsexplorer/testcode/*.java
    return $?
}

buildhfsx_types() {
    echo "Building org.catacombae.hfsexplorer.types..."
    javac -cp $CLASSPATH -sourcepath $SOURCES_DIR -d $BUILD_DIR -Xlint:unchecked $SOURCES_DIR/org/catacombae/hfsexplorer/types/*.java
    return $?
}

buildjar() {
    echo "Building jar-file..."
    if [ ! -d "$LIBRARY_PATH" ]; then # if not exists $LIBRARY_PATH...
	echo "Making library path"
    	mkdir $LIBRARY_PATH
    fi
    jar cf $LIBRARY_PATH/$JARFILE -C $BUILD_DIR .
    return $?
}

main() {
    removeclassfiles
    if [ "$?" == 0 ]; then
	copyresources
	if [ "$?" == 0 ]; then
	    buildhfsx
	    if [ "$?" == 0 ]; then
		buildhfsx_partitioning
		if [ "$?" == 0 ]; then
		    buildhfsx_gui
		    if [ "$?" == 0 ]; then
			buildhfsx_testcode
			if [ "$?" == 0 ]; then
			    buildhfsx_types
			    if [ "$?" == 0 ]; then
				buildjar
				if [ "$?" == 0 ]; then
				    jobCompleted
				else
				    error
				fi
			    else
				error
			    fi
			else
			    error
			fi
		    else
			error
		    fi
		else
		    error
		fi
	    else
		error
	    fi
	else
	    error
	fi
    else
	error
    fi
}

#entry point
main
