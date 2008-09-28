/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib.fs.hfs;

import org.catacombae.hfsexplorer.fs.ImplHFSFileSystemView;
import org.catacombae.jparted.lib.DataLocator;
import org.catacombae.jparted.lib.fs.hfscommon.HFSCommonFileSystemHandler;

/**
 *
 * @author erik
 */
public class HFSFileSystemHandler extends HFSCommonFileSystemHandler {

    public HFSFileSystemHandler(DataLocator fsLocator, boolean useCaching, String encodingName) {
        super(new ImplHFSFileSystemView(fsLocator.createReadOnlyFile(), 0, useCaching, encodingName),
                false);
    }
}
