/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib.ps.apm;

import org.catacombae.jparted.lib.ps.PartitionType;

/**
 *
 * @author erik
 */
public enum APMPartitionType {
    // Partition type
    APPLE_PARTITION_MAP("Apple_partition_map", PartitionType.APPLE_PARTITION_MAP),
    APPLE_DRIVER("Apple_Driver", PartitionType.APPLE_DRIVER),
    APPLE_DRIVER43("Apple_Driver43", PartitionType.APPLE_DRIVER),
    APPLE_MFS("Apple_MFS", PartitionType.APPLE_MFS),
    /** Since the Apple_HFS type can mean HFS or HFS+, we bind it to a container type */
    APPLE_HFS("Apple_HFS", PartitionType.APPLE_HFS_CONTAINER),
    APPLE_HFSX("Apple_HFSX", PartitionType.APPLE_HFSX),
    APPLE_UNIX_SVR2("Apple_Unix_SVR2", PartitionType.APPLE_UNIX_SVR2),
    APPLE_PRODOS("Apple_PRODOS", PartitionType.APPLE_PRODOS),
    APPLE_FREE("Apple_Free", PartitionType.EMPTY),
    APPLE_SCRATCH("Apple_Scratch", PartitionType.EMPTY);

    private final String apmName;
    private final PartitionType generalType;
    
    private APMPartitionType(String apmName) {
	this(apmName, null);
    }
    private APMPartitionType(String apmName, PartitionType generalType) {
        this.apmName = apmName;
	this.generalType = generalType;
    }
    
    public String getAPMName() {
	return apmName;
    }

    public PartitionType getGeneralType() {
	return generalType;
    }
 }
