/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib.ps.container.hfs;

import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.io.LowLevelFile;
import org.catacombae.jparted.lib.DataLocator;
import org.catacombae.jparted.lib.fs.FileSystemMajorType;
import org.catacombae.jparted.lib.ps.PartitionSystemType;
import org.catacombae.jparted.lib.ps.container.ContainerHandler;
import org.catacombae.jparted.lib.ps.container.ContainerType;

/**
 *
 * @author erik
 */
public class HFSContainerHandler extends ContainerHandler {
    private static final short SIGNATURE_HFS =      (short)0x4244; // ASCII: 'BD'
    private static final short SIGNATURE_HFS_PLUS = (short)0x482B; // ASCII: 'H+'
    private static final short SIGNATURE_HFSX =     (short)0x4858; // ASCII: 'HX'

    private DataLocator partitionData;
    
    public HFSContainerHandler(DataLocator partitionData) {
        this.partitionData = partitionData;
    }
    
    @Override
    public boolean containsFileSystem() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean containsPartitionSystem() {
        return false;
    }

    @Override
    public boolean containsContainer() {
        return false;
    }

    /**
     * The file system type of an HFS container will be one of HFS or HFS+. If
     * no signature at all can be found, null is returned and the file system
     * type is undetermined.
     */
    @Override
    public FileSystemMajorType detectFileSystemType() {
        LowLevelFile bitstream = partitionData.createReadOnlyFile();
        bitstream.seek(1024);
        byte[] signatureData = new byte[2];
        bitstream.readFully(signatureData);
        short signature = Util.readShortBE(signatureData);
        switch(signature) {
            case SIGNATURE_HFS:

                try {
                    bitstream.seek(1024 + 124);
                    bitstream.readFully(signatureData);
                    short embeddedSignature = Util.readShortBE(signatureData);
                    if(embeddedSignature == SIGNATURE_HFS_PLUS) {
                        return FileSystemMajorType.APPLE_HFS_PLUS;
                    } else {
                        return FileSystemMajorType.APPLE_HFS;
                    }
                } catch(Exception e) {
                    return FileSystemMajorType.APPLE_HFS;
                }
            case SIGNATURE_HFS_PLUS:
                return FileSystemMajorType.APPLE_HFS_PLUS;
            case SIGNATURE_HFSX:
                return FileSystemMajorType.APPLE_HFSX;
        }
        return null;
    }

    @Override
    public PartitionSystemType detectPartitionSystemType() {
        throw new UnsupportedOperationException("An HFS container does not contain partition systems.");
    }

    @Override
    public ContainerType detectContainerType() {
        throw new UnsupportedOperationException("An HFS container does not contain other containers.");
    }

}
