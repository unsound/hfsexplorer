#!/bin/sh

error() {
    echo "There were errors..."
}

jobCompleted() {
    echo "Done!"
}

JAVAC_CMD="${JAVA_HOME:+${JAVA_HOME}/bin/}javac"
JAVAC_DEFAULT_ARGS="-target 1.5 -source 1.5 -encoding iso-8859-1"
CLASSPATH=build.~:dist/lib/catacombae_io.jar:dist/lib/swing-layout-1.0.3.jar:dist/lib/hfsx_dmglib.jar:lib/java_awt_Desktop.jar
SOURCES_DIR=src
BUILD_DIR=build.~
LIBRARY_PATH=lib
JARFILE_DIR=dist/lib
JARFILE=jparted.jar
RESOURCE_SRC_DIR=resource
RESOURCE_DST_DIR=$BUILD_DIR/res
BUILDENUM_CP=lib/buildenumerator.jar

removeclassfiles() {
    echo "Removing all class files..."
    rm -r $BUILD_DIR
    mkdir $BUILD_DIR
    return $?
}

buildjparted() {
    echo "Building org.catacombae.jparted.app..."
    "${JAVAC_CMD}" $JAVAC_DEFAULT_ARGS -cp $CLASSPATH -sourcepath $SOURCES_DIR -d $BUILD_DIR -Xlint:unchecked $SOURCES_DIR/org/catacombae/jparted/app/*.java
    return $?
}

buildjar() {
    echo "Building jar-file..."
    if [ ! -d "$JARFILE_DIR" ]; then # if not exists $LIBRARY_PATH...
	echo "Making library path"
    	mkdir $JARFILE_DIR
    fi
    jar cf $JARFILE_DIR/$JARFILE -C $BUILD_DIR .
    return $?
}

main() {
    removeclassfiles
    if [ "$?" -eq 0 ]; then
	buildjparted
	if [ "$?" -eq 0 ]; then
	    buildjar
	    if [ "$?" -eq 0 ]; then
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
}

#entry point
main
