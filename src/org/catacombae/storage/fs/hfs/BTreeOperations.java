/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.storage.fs.hfs;

import org.catacombae.io.Readable;
import org.catacombae.hfsexplorer.types.hfscommon.CommonBTHeaderNode;
import org.catacombae.hfsexplorer.types.hfscommon.CommonBTHeaderRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonBTNodeDescriptor;

/**
 *
 * @author erik
 */
public interface BTreeOperations {
    public CommonBTHeaderNode createCommonBTHeaderNode(byte[] currentNodeData,
            int offset, int nodeSize);
    public CommonBTNodeDescriptor readNodeDescriptor(Readable rd);
    public CommonBTHeaderRecord readHeaderRecord(Readable rd);
    public CommonBTNodeDescriptor createCommonBTNodeDescriptor(
            byte[] currentNodeData, int offset);

}
