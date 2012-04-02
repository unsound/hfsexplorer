#!/bin/sh

rm -rf .build/ && mkdir .build && javac -d .build -classpath src src/javax/swing/table/TableRowSorter.java && jar cvf ../lib/Java6Stubs.jar -C .build javax
