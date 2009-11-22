#!/bin/bash

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

echo "Setting execute permissions for shell scripts..."
chmod a+x $TEMPDIR/*.sh
chmod a+x $TEMPDIR/dist/*.sh

echo "Building zip file..."
cd $TEMPDIR
rm ../releases/${ZIPFILE}
zip -9 -r ../releases/${ZIPFILE} *
cd ..

echo "Done! Zip file generated in releases/${ZIPFILE}"
