#!/bin/sh

BASEDIR="`dirname "$0"`"
if [ $? -ne 0 ]; then
    echo "WARNING: No dirname utility found!"
    echo "         Script will only work if invoked from its parent directory."
    BASEDIR="."
fi

cd "${BASEDIR}"

GIT_CLEAN_OUTPUT="$(git clean -n -d dist)"
if [ $? -ne 0 ]; then
    echo "Error value returned from \"git clean -n -d dist\". Cannot check for untracked files."
    exit 1
fi

if [ ! -z "${GIT_CLEAN_OUTPUT}" ]; then
    echo "Directory \"dist\" is not clean. Please stash all untracked files:"
    echo "${GIT_CLEAN_OUTPUT}" | while read GIT_CLEAN_LINE; do echo "    $(echo "${GIT_CLEAN_LINE}" | sed 's/^Would remove //g')"; done
    exit 1
fi

GIT_STATUS_OUTPUT="$(git status -s dist)"
if [ $? -ne 0 ]; then
    echo "Error value returned from \"git status -s dist\". Cannot check for modifications."
    exit 1
fi

if [ ! -z "${GIT_STATUS_OUTPUT}" ]; then
    echo "Directory \"dist\" is not clean. Please stash all modifications:"
    echo "${GIT_STATUS_OUTPUT}" | while read GIT_STATUS_LINE; do echo "    ${GIT_STATUS_LINE}"; done
    exit 1
fi

if [ ! -f "dist/lib/hfsx.jar" ]; then
    echo "No hfsx.jar binary in \"dist/lib\". Did you forget to build it?"
    exit 1
fi

ZIPFILE=hfsexplorer-current-bin.zip

match () {
    local SUBFILE="$1"
    local DIRNAME="$2"
    CMPRES=`expr "${SUBFILE}" : "${DIRNAME}"`
    RETVAL="$?"
    if [ "${RETVAL}" -eq 0 ]; then
	if [ -d "${SUBFILE}" ]; then
	    echo "`pwd`/${SUBFILE}/"
	    rm -r "${SUBFILE}"
	else
	    echo "`pwd`/${SUBFILE}"
	    rm "${SUBFILE}"
	fi
    elif [ "${RETVAL}" -eq 1 ]; then
	if [ -d "${SUBFILE}" ]; then
	    recursiveRmdir "${DIRNAME}" "${SUBFILE}"
	fi
    fi
}

recursiveRmdir () {
    local DIRNAME="$1"
    #local DIRNAMELEN="${#DIRNAME}"
    shift 1
    while [ ! $# -eq 0 ]; do
	local FILE="$1"
	shift 1
	
	if [ -d "${FILE}" ]; then
	    
	    pushd "${FILE}" > /dev/null
	    
	    for SUBFILE in .*; do
		if [ ! "${SUBFILE}" = ".." ] && [ ! "${SUBFILE}" = "." ]; then
		    match "${SUBFILE}" "${DIRNAME}"
		fi
	    done		    
	    
	    for SUBFILE in *; do
		if [ ! "${SUBFILE}" = "*" ]; then
		    match "${SUBFILE}" "${DIRNAME}"
		fi
	    done
	    
	    popd > /dev/null
	else
	    match "${FILE}" "${DIRNAME}"
	fi
    done
}

TEMPDIR=disttemp.~

echo "Cleaning temp dir..."
rm -r $TEMPDIR
mkdir $TEMPDIR

echo "Copying files..."
cp -r dist/* $TEMPDIR 

echo "Removing CVS directories..."
recursiveRmdir "^CVS$" "$TEMPDIR"

echo "Building zip file..."
cd $TEMPDIR
if [ ! -d "../releases" ]; then
    mkdir "../releases"
fi
rm "../releases/${ZIPFILE}"
zip -9 -r "../releases/${ZIPFILE}" *
cd ..

echo "Done! Zip file generated at releases/${ZIPFILE}"
