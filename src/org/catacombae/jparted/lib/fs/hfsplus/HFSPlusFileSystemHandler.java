/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib.fs.hfsplus;

import org.catacombae.jparted.lib.fs.hfscommon.HFSCommonFileSystemHandler;
import org.catacombae.hfsexplorer.fs.ImplHFSPlusFileSystemView;
import org.catacombae.jparted.lib.DataLocator;

/**
 *
 * @author erik
 */
public class HFSPlusFileSystemHandler extends HFSCommonFileSystemHandler {
    public HFSPlusFileSystemHandler(DataLocator fsLocator, boolean useCaching,
            boolean iDoUnicodeFileNameComposition) {
        super(new ImplHFSPlusFileSystemView(fsLocator.createReadOnlyFile(), 0,
                    useCaching), iDoUnicodeFileNameComposition);
    }
}
