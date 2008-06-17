/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib.ps.apm;

import java.io.PrintStream;
import org.catacombae.jparted.lib.ps.Partition;
import org.catacombae.jparted.lib.ps.PartitionType;

/**
 *
 * @author erik
 */
public class APMPartition extends Partition {
    
    @Override
    public long getStartOffset() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getLength() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PartitionType getType() {
        throw new UnsupportedOperationException("Not supported yet.");
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
