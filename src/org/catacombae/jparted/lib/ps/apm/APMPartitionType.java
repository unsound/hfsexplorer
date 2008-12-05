/*-
 * Copyright (C) 2008 Erik Larsson
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
