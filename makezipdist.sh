#!/bin/sh
TEMPDIR=disttemp.~

echo "Cleaning temp dir..."
rm -r $TEMPDIR
mkdir $TEMPDIR

echo "Copying files..."
cp -r dist/* $TEMPDIR 

echo "Setting execute permissions for shell scripts..."
chmod a+x $TEMPDIR/*.sh

echo "Removing CVS directories..."
rm -r $TEMPDIR/CVS
rm -r $TEMPDIR/lib/CVS
rm -r $TEMPDIR/doc/CVS
rm -r $TEMPDIR/doc/html/CVS
rm -r $TEMPDIR/doc/html/img/CVS

echo "Building zip file..."
cd $TEMPDIR
rm ../releases/current.zip
zip -9 -r ../releases/current.zip *
cd ..

echo "Done! Zip file generated in releases/current.zip"
