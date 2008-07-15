#!/bin/sh

error() {
    decrement_buildnumber
    echo "There were errors..."
}

jobCompleted() {
    echo "Done!"
}

CLASSPATH=build.~:dist/lib/swing-layout-1.0.1.jar:dist/lib/hfsx_dmglib.jar:lib/java_awt_Desktop.jar
SOURCES_DIR=src
BUILD_DIR=build.~
LIBRARY_PATH=lib
JARFILE_DIR=dist/lib
JARFILE=hfsx.jar
RESOURCE_SRC_DIR=resource
RESOURCE_DST_DIR=$BUILD_DIR/res
BUILDENUM_CP=lib/buildenumerator.jar
METAINF_DIR=src.META-INF
MANIFEST_FILE=MANIFEST.MF

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

increment_buildnumber() {
    echo "Incrementing build number..."
    java -cp $BUILDENUM_CP BuildEnumerator $SOURCES_DIR/org/catacombae/hfsexplorer/BuildNumber.java 1
}

decrement_buildnumber() {
    echo "Decrementing build number..."
    java -cp $BUILDENUM_CP BuildEnumerator $SOURCES_DIR/org/catacombae/hfsexplorer/BuildNumber.java -1
}

buildhfsx() {
    echo "Building org.catacombae.hfsexplorer..."
    javac -target 1.5 -source 1.5 -cp $CLASSPATH -sourcepath $SOURCES_DIR -d $BUILD_DIR -Xlint:unchecked $SOURCES_DIR/org/catacombae/hfsexplorer/*.java
    return $?
}

buildhfsx_partitioning() {
    echo "Building org.catacombae.hfsexplorer.partitioning..."
    javac -target 1.5 -source 1.5 -cp $CLASSPATH -sourcepath $SOURCES_DIR -d $BUILD_DIR -Xlint:unchecked $SOURCES_DIR/org/catacombae/hfsexplorer/partitioning/*.java
    return $?
}

buildhfsx_gui() {
    echo "Building org.catacombae.hfsexplorer.gui..."
    javac -target 1.5 -source 1.5 -cp $CLASSPATH -sourcepath $SOURCES_DIR -d $BUILD_DIR -Xlint:unchecked $SOURCES_DIR/org/catacombae/hfsexplorer/gui/*.java
    return $?
}

buildhfsx_testcode() {
    echo "Building org.catacombae.hfsexplorer.testcode..."
    javac -target 1.5 -source 1.5 -cp $CLASSPATH -sourcepath $SOURCES_DIR -d $BUILD_DIR -Xlint:unchecked $SOURCES_DIR/org/catacombae/hfsexplorer/testcode/*.java
    return $?
}

buildhfsx_types() {
    echo "Building org.catacombae.hfsexplorer.types..."
    javac -target 1.5 -source 1.5 -cp $CLASSPATH -sourcepath $SOURCES_DIR -d $BUILD_DIR -Xlint:unchecked $SOURCES_DIR/org/catacombae/hfsexplorer/types/*.java
    return $?
}

buildjar() {
    echo "Building jar-file..."
    if [ ! -d "$JARFILE_DIR" ]; then # if not exists $LIBRARY_PATH...
	echo "Making library path"
    	mkdir $JARFILE_DIR
    fi
    jar cfm $JARFILE_DIR/$JARFILE $METAINF_DIR/$MANIFEST_FILE -C $BUILD_DIR .
    return $?
}

main() {
    increment_buildnumber
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
