#!/bin/sh

JAVAC_CMD="${JAVA_HOME:+${JAVA_HOME}/bin/}javac"
SOURCES_DIR=src
BUILDENUM_CP=lib/buildenumerator.jar

echo "javac command: ${JAVAC_CMD}"

error() {
    decrement_buildnumber
    echo "There were errors..."
}

jobCompleted() {
    echo "Done!"
}

increment_buildnumber() {
    echo "Incrementing build number..."
    java -cp $BUILDENUM_CP BuildEnumerator $SOURCES_DIR/org/catacombae/hfsexplorer/BuildNumber.java 1
}

decrement_buildnumber() {
    echo "Decrementing build number..."
    java -cp $BUILDENUM_CP BuildEnumerator $SOURCES_DIR/org/catacombae/hfsexplorer/BuildNumber.java -1
}

antbuild() {
    echo "Building with ant..."
    ant build-all
    return $?
}

main() {
    increment_buildnumber
    antbuild
    if [ "$?" -eq 0 ]; then
	jobCompleted
	return 0
    else
	error
	return -1
    fi
}

#entry point
main
