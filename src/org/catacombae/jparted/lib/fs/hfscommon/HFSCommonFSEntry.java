/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib.fs.hfscommon;

import org.catacombae.util.Util;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogAttributes;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSFinderInfo;
import org.catacombae.jparted.lib.fs.BasicFSEntry;
import org.catacombae.jparted.lib.fs.FSFork;
import org.catacombae.jparted.lib.fs.FSForkType;

/**
 *
 * @author erik
 */
public abstract class HFSCommonFSEntry extends BasicFSEntry {

    protected final HFSCommonFileSystemHandler fsHandler;
    protected final CommonHFSCatalogAttributes catalogAttributes;
    private FSFork finderInfoFork = null;
    private boolean finderInfoForkLoaded = false;

    protected HFSCommonFSEntry(HFSCommonFileSystemHandler parentFileSystem,
            CommonHFSCatalogAttributes catalogAttributes) {
        super(parentFileSystem);

        this.fsHandler = parentFileSystem;
        this.catalogAttributes = catalogAttributes;
    }

    HFSCommonFileSystemHandler getFileSystemHandler() {
        return fsHandler;
    }

    @Override
    public FSFork[] getAllForks() {
        FSFork fork = getFinderInfoFork();
        if(fork != null)
            return new FSFork[] { fork };
        else
            return new FSFork[0];
    }

    @Override
    public FSFork getForkByType(FSForkType type) {

        if(type == FSForkType.MACOS_FINDERINFO)
            return getFinderInfoFork();
        else
            return null;
    }

    @Override
    public long getCombinedLength() {
        
        FSFork fork = getFinderInfoFork();
        if(fork != null)
            return finderInfoFork.getLength();
        else
            return 0;
    }

    public FSFork getFinderInfoFork() {
        if(!finderInfoForkLoaded) {
            CommonHFSFinderInfo finderInfo = catalogAttributes.getFinderInfo();
            byte[] finderInfoBytes = finderInfo.getBytes();

            if(!Util.zeroed(finderInfoBytes)) {
                finderInfoFork = new HFSCommonFinderInfoFork(finderInfo);
            }
            else
                finderInfoFork = null;
            finderInfoForkLoaded = true;
        }

        return finderInfoFork;
    }
}
