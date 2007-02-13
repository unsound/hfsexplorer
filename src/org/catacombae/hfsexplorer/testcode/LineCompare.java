package org.catacombae.hfsexplorer.testcode;

import java.io.*;

/**
 * Application that compares two text files for equality, after removing empty empty lines, and ignoring
 * different line endings.
 */
public class LineCompare {
    public static void main(String[] args) throws IOException {
	BufferedReader file1 = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
	BufferedReader file2 = new BufferedReader(new InputStreamReader(new FileInputStream(args[1])));
	long lineNumber = 0;
	String line1 = "", line2 = "";
	while(!(line1 == null && line2 == null)) {
	    if(line1 == null) {
		System.out.println("File 1 ended at line " + lineNumber + ". Files are not equal.");
		System.exit(1);
	    }
	    else if(line2 == null) {
		System.out.println("File 2 ended at line " + lineNumber + ". Files are not equal.");
		System.exit(1);
	    }
	    else if(!line1.equals(line2)) {
		System.out.println("Files differ at line " + lineNumber + ":");
		System.out.println("File 1:");
		System.out.println(line1);
		System.out.println("File 2:");
		System.out.println(line2);
		System.exit(1);
	    }

	    ++lineNumber;
	    line1 = "";
	    while(line1 != null && line1.trim().equals(""))
		line1 = file1.readLine();
	    line2 = "";
	    while(line2 != null && line2.trim().equals(""))
		line2 = file2.readLine();
	}
	System.out.println("Files are equal (ignoring empty lines and different line endings).");
    }
}