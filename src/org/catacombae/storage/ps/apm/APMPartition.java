/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib.ps.apm;

import org.catacombae.jparted.lib.ps.PartitionType;
import org.catacombae.jparted.lib.ps.StandardPartition;

/**
 *
 * @author erik
 */
public class APMPartition extends StandardPartition {
    private final String name;
    
    public APMPartition(long startOffset, long length, PartitionType type,
            String name) {
        super(startOffset, length, type);

        this.name = name;
    }

    public String getName() {
        return name;
    }
}
