/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types.hfscommon;

import java.io.PrintStream;
import org.catacombae.csjc.DynamicStruct;
import org.catacombae.csjc.PrintableStruct;

/**
 *
 * @author erik
 */
public abstract class CommonBTKey implements Comparable<CommonBTKey>, DynamicStruct, PrintableStruct {
    //public abstract int getMaxLength();
    //public abstract int length();
    
    public int compareTo(CommonBTKey btk) {
	byte[] thisData = getBytes();
	byte[] thatData = btk.getBytes();
	for(int i = 0; i < Math.min(thisData.length, thatData.length); ++i) {
	    if(thisData[i] < thatData[i])
		return -1;
	    else if(thisData[i] > thatData[i])
		return 1;
	}
	if(thisData.length < thatData.length)
	    return -1;
	else if(thisData.length > thatData.length)
	    return 1;
	else
	    return 0;
    }
    
    public abstract byte[] getBytes();
}
