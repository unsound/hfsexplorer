#!/bin/sh

error() {
    decrement_buildnumber
    echo "There were errors..."
}

jobCompleted() {
    echo "Done!"
}

JAVAC_CMD="${JAVA_HOME:+${JAVA_HOME}/bin/}javac"
JAVAC_DEFAULT_ARGS="-target 1.5 -source 1.5 -encoding iso-8859-1"
CLASSPATH=build.~:lib/AppleJavaExtensions.jar:dist/lib/swing-layout-1.0.3.jar:dist/lib/hfsx_dmglib.jar:lib/java_awt_Desktop.jar
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

echo "javac command: ${JAVAC_CMD}"

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
    "${JAVAC_CMD}" $JAVAC_DEFAULT_ARGS -cp $CLASSPATH -sourcepath $SOURCES_DIR -d $BUILD_DIR -Xlint:unchecked $SOURCES_DIR/org/catacombae/hfsexplorer/*.java
    return $?
}

buildhfsx_partitioning() {
    echo "Building org.catacombae.hfsexplorer.partitioning..."
    "${JAVAC_CMD}" $JAVAC_DEFAULT_ARGS -cp $CLASSPATH -sourcepath $SOURCES_DIR -d $BUILD_DIR -Xlint:unchecked $SOURCES_DIR/org/catacombae/hfsexplorer/partitioning/*.java
    return $?
}

buildhfsx_gui() {
    echo "Building org.catacombae.hfsexplorer.gui..."
    "${JAVAC_CMD}" $JAVAC_DEFAULT_ARGS -cp $CLASSPATH -sourcepath $SOURCES_DIR -d $BUILD_DIR -Xlint:unchecked $SOURCES_DIR/org/catacombae/hfsexplorer/gui/*.java
    return $?
}

buildhfsx_testcode() {
    echo "Building org.catacombae.hfsexplorer.testcode..."
    "${JAVAC_CMD}" $JAVAC_DEFAULT_ARGS -cp $CLASSPATH -sourcepath $SOURCES_DIR -d $BUILD_DIR -Xlint:unchecked $SOURCES_DIR/org/catacombae/hfsexplorer/testcode/*.java
    return $?
}

buildhfsx_types() {
    echo "Building org.catacombae.hfsexplorer.types..."
    "${JAVAC_CMD}" $JAVAC_DEFAULT_ARGS -cp $CLASSPATH -sourcepath $SOURCES_DIR -d $BUILD_DIR -Xlint:unchecked $SOURCES_DIR/org/catacombae/hfsexplorer/types/*.java
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
