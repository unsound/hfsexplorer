#!/bin/sh

pushd dist > /dev/null
./runfsb.sh "$@"
popd > /dev/null
