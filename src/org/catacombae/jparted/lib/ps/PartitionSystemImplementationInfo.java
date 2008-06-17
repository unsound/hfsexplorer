/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
