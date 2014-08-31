/*-
 * Copyright (C) 2009 Erik Larsson
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

package org.catacombae.storage.fs.hfscommon;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.catacombae.hfs.AttributesFile;
import org.catacombae.hfs.types.hfscommon.CommonHFSAttributesLeafRecord;
import org.catacombae.util.Util;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogAttributes;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogNodeID;
import org.catacombae.hfs.types.hfscommon.CommonHFSFinderInfo;
import org.catacombae.storage.fs.BasicFSEntry;
import org.catacombae.storage.fs.FSFork;
import org.catacombae.storage.fs.FSForkType;
import org.catacombae.util.Util.Pair;

/**
 *
 * @author erik
 */
public abstract class HFSCommonFSEntry extends BasicFSEntry {

    protected final HFSCommonFileSystemHandler fsHandler;
    protected final CommonHFSCatalogAttributes catalogAttributes;
    private FSFork finderInfoFork = null;
    private boolean finderInfoForkLoaded = false;
    LinkedList<FSFork> attributeForkList = null;

    protected HFSCommonFSEntry(HFSCommonFileSystemHandler parentFileSystem,
            CommonHFSCatalogAttributes catalogAttributes) {
        super(parentFileSystem);

        this.fsHandler = parentFileSystem;
        this.catalogAttributes = catalogAttributes;
    }

    private synchronized List<FSFork> getAttributeForks() {
        if(attributeForkList == null) {
            LinkedList<FSFork> forkList = new LinkedList<FSFork>();

            AttributesFile attributesFile =
                    fsHandler.getFSView().getAttributesFile();
            if(attributesFile != null) {
                LinkedList<Pair<char[],
                        LinkedList<CommonHFSAttributesLeafRecord>>>
                        attributeBucketList =
                        new LinkedList<Pair<char[],
                        LinkedList<CommonHFSAttributesLeafRecord>>>();

                for(CommonHFSAttributesLeafRecord attributeRecord :
                        attributesFile.listAttributeRecords(getCatalogNodeID()))
                {
                    Pair<char[], LinkedList<CommonHFSAttributesLeafRecord>> p;

                    if(!attributeBucketList.isEmpty() &&
                           (p = attributeBucketList.getLast()) != null)
                    {
                        if(Arrays.equals(p.getA(),
                                attributeRecord.getKey().getAttrName()))
                        {
                            p.getB().addLast(attributeRecord);
                        }
                    }
                    else {
                        LinkedList<CommonHFSAttributesLeafRecord> bucket =
                                new LinkedList<CommonHFSAttributesLeafRecord>();
                        bucket.add(attributeRecord);

                        p = new Pair<char[],
                                LinkedList<CommonHFSAttributesLeafRecord>>(
                                attributeRecord.getKey().getAttrName(), bucket);
                        attributeBucketList.add(p);
                    }
                }

                for(Pair<char[], LinkedList<CommonHFSAttributesLeafRecord>> p :
                        attributeBucketList)
                {
                    LinkedList<CommonHFSAttributesLeafRecord> recordList =
                            p.getB();

                    forkList.add(new HFSCommonAttributeFork(this,
                            recordList.toArray(
                            new CommonHFSAttributesLeafRecord[recordList.
                            size()])));
                }
            }

            attributeForkList = forkList;
        }

        return attributeForkList;
    }

    HFSCommonFileSystemHandler getFileSystemHandler() {
        return fsHandler;
    }

    /* @Override */
    public FSFork[] getAllForks() {
        LinkedList<FSFork> forkList = new LinkedList<FSFork>();

        FSFork fork = getFinderInfoFork();
        if(fork != null)
            forkList.add(fork);

        FSFork resourceFork = getResourceFork();
        if(resourceFork != null) {
            forkList.add(resourceFork);
        }

        forkList.addAll(getAttributeForks());

        return forkList.toArray(new FSFork[forkList.size()]);
    }

    /* @Override */
    public FSFork getForkByType(FSForkType type) {

        if(type == FSForkType.MACOS_FINDERINFO)
            return getFinderInfoFork();
        else
            return null;
    }

    /* @Override */
    public long getCombinedLength() {

        FSFork fork = getFinderInfoFork();
        if(fork != null)
            return finderInfoFork.getLength();
        else
            return 0;
    }

    protected abstract CommonHFSCatalogNodeID getCatalogNodeID();

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

    protected abstract FSFork getResourceFork();
}
