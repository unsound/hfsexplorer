#!/bin/sh

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

echo "Setting execute permissions for shell scripts..."
chmod a+x $TEMPDIR/*.sh

echo "Removing CVS directories..."
recursiveRmdir "^CVS$" "$TEMPDIR"

echo "Building zip file..."
cd $TEMPDIR
rm ../releases/current-bin.zip
zip -9 -r ../releases/current-bin.zip *
cd ..

echo "Done! Zip file generated in releases/current-bin.zip"
