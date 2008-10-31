/*-
 * Copyright (C) 2006-2007 Erik Larsson
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catacombae.hfsexplorer;

import java.io.*;
import java.util.Hashtable;

public class SimpleDictionaryParser {
    private static final String SPD_HEADER = "!SimpleDictionary";
    private Hashtable<String, String> dictionaryTable = new Hashtable<String, String>();
    
    public SimpleDictionaryParser(InputStream is) {
	try {
	    BufferedReader buf = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	    String firstLine = buf.readLine();
	    int colonIndex = firstLine.indexOf(":");
	    String spdTag = firstLine.substring(0, firstLine.indexOf(":"));
	    String spdVersion = firstLine.substring(firstLine.indexOf(":")+1).trim();
	    //System.err.println("spdTag: \"" + spdTag + "\" spdVersion: \"" + spdVersion + "\"");
	    if(!(spdTag.equals(SPD_HEADER) && spdVersion.equals("1.0")))
		throw new RuntimeException("Invalid SimpleDictionary data.");
	    
	    String currentLine = buf.readLine();
	    while(currentLine != null) {
		colonIndex = currentLine.indexOf(":");
		String key = currentLine.substring(0, currentLine.indexOf(":"));
		String value = currentLine.substring(currentLine.indexOf(":")+1).trim();
		//System.err.println("key: \"" + key + "\" value: \"" + value + "\"");
		dictionaryTable.put(key, value);
		currentLine = buf.readLine();
	    }
	} catch(IOException ioe) { throw new RuntimeException(ioe); }
    }
    
    public String getValue(String key) { return dictionaryTable.get(key); }
    
    /*
    public static void main(String[] args) {
	new SimpleDictionaryParser(System.in);
    }
    */
}
