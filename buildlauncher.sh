#!/bin/bash

TOOLCHAIN_PREFIX="i686-w64-mingw32-"
LAUNCHER_SRC="`pwd`/src/win32/launcher"
OUTDIR="`pwd`/dist/bin"
OUTFILE="hfsexplorer.exe"
BUILD_DIR="`pwd`/build.~"

error () {
    echo There were errors...
    exit 1
}

if test -z "$JAVA_HOME"; then
    echo "Please set the JAVA_HOME environment variable before executing this script."
    error
fi

if test "$1" = "console"; then
    BUILDTYPE=console
elif test "$1" = "windows"; then
    BUILDTYPE=windows
else
    echo "You must specify console or windows application..."
    error
fi

echo "Cleaning build dir..."
if test -d "${BUILD_DIR}"; then
    rm -r "${BUILD_DIR}"
fi
if test -d "${BUILD_DIR}"; then
    echo "Could not clean build dir!"
    error
fi
mkdir "${BUILD_DIR}"

echo "Compiling resources..."
${TOOLCHAIN_PREFIX}windres -I doc/dmg_iconsource "${LAUNCHER_SRC}"/launcher.rc "${BUILD_DIR}"/launcher_res.o
if test $? -ne 0; then error; fi

echo "Compiling launcher.cpp..."
${TOOLCHAIN_PREFIX}g++ -g -Wall -D_JNI_IMPLEMENTATION_ "-I${JAVA_HOME}/include" "-I${JAVA_HOME}/include/win32" -c "${LAUNCHER_SRC}/launcher.cpp" -o "${BUILD_DIR}/launcher.o"
if test $? -ne 0; then error; fi

echo "Building ${BUILDTYPE} app..."
${TOOLCHAIN_PREFIX}g++ -static-libgcc -static-libstdc++ -g -m${BUILDTYPE} -Wall -D_JNI_IMPLEMENTATION_ -Wl,--kill-at "${BUILD_DIR}/launcher_res.o" "${BUILD_DIR}/launcher.o" -o "${OUTDIR}/${OUTFILE}"
if test $? -ne 0; then error; fi
echo "Done!"
