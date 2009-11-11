/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.storage.fs.hfs;

import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSVolumeHeader;

/**
 *
 * @author erik
 */
public abstract class VolumeHeader {
    public abstract CommonHFSVolumeHeader getHeaderStruct();

}
