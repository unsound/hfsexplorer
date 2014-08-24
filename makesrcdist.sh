#!/bin/bash

GIT_CLEAN_OUTPUT="$(git clean -n -d)"
if [ $? -ne 0 ]; then
    echo "Error value returned from \"git clean -n -d\". Cannot check for untracked files."
    exit 1
fi

if [ ! -z "${GIT_CLEAN_OUTPUT}" ]; then
    echo "Source tree is not clean. Please stash all untracked files:"
    echo "${GIT_CLEAN_OUTPUT}" | while read GIT_CLEAN_LINE; do echo "    $(echo "${GIT_CLEAN_LINE}" | sed 's/^Would remove //g')"; done
    exit 1
fi

GIT_STATUS_OUTPUT="$(git status -s)"
if [ $? -ne 0 ]; then
    echo "Error value returned from \"git status -s\". Cannot check for modifications."
    exit 1
fi

if [ ! -z "${GIT_STATUS_OUTPUT}" ]; then
    echo "Source tree is not clean. Please stash all modifications:"
    echo "${GIT_STATUS_OUTPUT}" | while read GIT_STATUS_LINE; do echo "    ${GIT_STATUS_LINE}"; done
    exit 1
fi

if [ -f "dist/lib/hfsx.jar" ]; then
    echo "Found an hfsx.jar binary in \"dist/lib\". This should not be present when building the source distribution."
    exit 1
fi

ZIPFILE=hfsexplorer-current-src.zip

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

TEMPDIR=srcdisttemp.~

echo "Cleaning temp dir..."
rm -r $TEMPDIR
mkdir $TEMPDIR

echo "Copying files..."
cp * $TEMPDIR 
cp -r dist $TEMPDIR 
cp -r lib $TEMPDIR 
cp -r resource $TEMPDIR 
cp -r src $TEMPDIR 
cp -r src.META-INF $TEMPDIR 
cp -r src.win32 $TEMPDIR 

echo "Removing CVS directories..."
recursiveRmdir "^CVS$" "$TEMPDIR"
echo "Removing emacs backup files (*~)..."
recursiveRmdir ".*~$" "$TEMPDIR"
echo "Removing emacs temporary files (#*#)..."
recursiveRmdir "^#.*#$" "$TEMPDIR"
echo "Removing Thumbs.db files..."
recursiveRmdir "^Thumbs\.db$" "$TEMPDIR"
echo "Removing .DS_Store files..."
recursiveRmdir "^\.DS_Store$" "$TEMPDIR"
echo "Removing .cvsignore files..."
recursiveRmdir "^\.cvsignore$" "$TEMPDIR"
echo "Removing dist build scripts..."
rm -v "${TEMPDIR}/makebindist.sh"
rm -v "${TEMPDIR}/makesrcdist.sh"

echo "Setting execute permissions for shell scripts..."
chmod a+x $TEMPDIR/*.sh
chmod a+x $TEMPDIR/dist/*.sh

echo "Building zip file..."
cd $TEMPDIR
if [ ! -d ../releases ]; then
    mkdir ../releases
fi
rm -fv ../releases/${ZIPFILE}
zip -9 -r ../releases/${ZIPFILE} *
cd ..

echo "Done! Zip file generated in releases/${ZIPFILE}"
