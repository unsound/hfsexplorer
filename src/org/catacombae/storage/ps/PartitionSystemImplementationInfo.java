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

package org.catacombae.jparted.lib.ps;

/**
 *
 * @author erik
 */
public class PartitionSystemImplementationInfo {
    private String partitionSystemName;
    private String implementationName;
    private String implementationVersion;
    private String author;
    
    public PartitionSystemImplementationInfo(String partitionSystemName,
            String implementationName, String implementationVersion,
            String author) {
        this.partitionSystemName = partitionSystemName;
        this.implementationName = implementationName;
        this.implementationVersion = implementationVersion;
        this.author = author;
    }
    
    public String getPartitionSystemName() {
        return partitionSystemName;
    }
    
    public String getImplementationName() {
        return implementationName;
    }
    
    public String getImplementationVersion() {
        return implementationVersion;
    }
    
    public String getAuthor() {
        return author;
    }
}
