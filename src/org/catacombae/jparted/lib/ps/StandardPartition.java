/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib.ps;

import java.io.PrintStream;

/**
 *
 * @author erik
 */
public class StandardPartition extends Partition {
    private final long startOffset;
    private final long length;
    private final PartitionType type;
    
    public StandardPartition(long startOffset, long length, PartitionType type) {
        this.startOffset = startOffset;
        this.length = length;
        this.type = type;
               
    }
    
    @Override
    public long getStartOffset() {
        return startOffset;
    }

    @Override
    public long getLength() {
        return length;
    }

    @Override
    public PartitionType getType() {
        return type;
    }

    @Override
    public void printFields(PrintStream ps, String prefix) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void print(PrintStream ps, String prefix) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
