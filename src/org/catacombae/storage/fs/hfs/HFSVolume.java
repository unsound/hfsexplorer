/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.storage.fs.hfs;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import org.catacombae.hfsexplorer.fs.ProgressMonitor;
import org.catacombae.hfsexplorer.io.ForkFilter;
import org.catacombae.hfsexplorer.io.ReadableBlockCachingStream;
import org.catacombae.hfsexplorer.io.ReadableRandomAccessSubstream;
import org.catacombae.hfsexplorer.io.SynchronizedReadableRandomAccess;
import org.catacombae.hfsexplorer.io.SynchronizedReadableRandomAccessStream;
import org.catacombae.hfsexplorer.types.hfscommon.CommonBTHeaderRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonBTNodeDescriptor;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFile;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFileRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFolderRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogIndexNode;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogKey;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogLeafNode;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogLeafRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogNodeID;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogNodeID.ReservedID;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogString;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSExtentDescriptor;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSExtentKey;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSExtentLeafRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSForkData;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSForkType;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSVolumeHeader;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.jparted.lib.DataLocator;

/**
 *
 * @author erik
 */
public abstract class HFSVolume {
    final HFSOperations ops;

    private final CatalogFile catalogFile;
    private final ExtentsOverflowFile extentsOverflowFile;

    HFSVolume(HFSOperations ops, ReadableRandomAccessStream hfsFile, long fsOffset, boolean cachingEnabled) {
        this.sourceStream = hfsFile;
        this.hfsStream = new SynchronizedReadableRandomAccessStream(sourceStream);
        this.hfsFile = hfsStream;
        this.fsOffset = fsOffset;
        this.physicalBlockSize = 512; // This seems to be a built in assumption of HFSish file systems.

        if(cachingEnabled)
            enableFileSystemCaching();

        this.ops = ops;

        this.catalogFile = new CatalogFile(this);
        this.extentsOverflowFile = new ExtentsOverflowFile(this);
    }

    public static HFSVolume open(DataLocator loc, boolean writable) {
        throw new UnsupportedOperationException();
    }

    public static HFSVolume openHFS(DataLocator loc, boolean writable) {
        throw new UnsupportedOperationException();
    }

    public static HFSVolume openHFSPlus(DataLocator loc, boolean writable) {
        throw new UnsupportedOperationException();
    }

    public static HFSVolume openHFSX(DataLocator loc, boolean writable) {
        throw new UnsupportedOperationException();
    }
    
    public static HFSVolume openHFSWrappedHFSPlus(DataLocator loc,
            boolean writable) {
        throw new UnsupportedOperationException();
    }

    //public abstract VolumeHeader getVolumeHeader();
    public abstract CatalogFile getCatalogFile();
    public abstract ExtentsOverflowFile getExtentsOverflowFile();
    public abstract AllocationFile getAllocationFile();

    public abstract boolean hasAttributesFile();
    public abstract boolean hasJournal();
    public abstract boolean hasHotFilesFile();

    public abstract AttributesFile getAttributesFile();
    public abstract Journal getJournal();
    public abstract HotFilesFile getHotFilesFile();

    /**
     * Closes the volume and flushes any data not yet committed to disk (if
     * opened in writable mode).
     */
    public abstract void close();

    /*
     * The idea is to make few assumptions about static data in the file system.
     * No operations should be cached, since it would provide an inaccurate view
     * of a live file system.
     *
     * I don't know why I'm doing it this way... It would probably limit no one
     * if I just assumed exclusive access, but I can write another class for
     * that, providing better performance.
     *
     * Note: It has been shown that this approach has actually made it possible
     * to track changes in a live filesystem, for example when opening
     * /dev/disk0s2 in superuser mode in OS X.
     *
     * 2007-09-18: We now assume block size to be static. This can't possibly be
     *             a problem... ever. Variable staticBlockSize contains the
     *             block size.
     */

    /**
     * Debug variable which is mainly used to ... ??!
     */
    public static volatile long fileReadOffset = 0;

    /** Internal class. */
    private abstract class InitProcedure {

        public final CommonHFSVolumeHeader header;
        public final ReadableRandomAccessStream forkFilterFile;
        public final CommonBTNodeDescriptor btnd;
        public final CommonBTHeaderRecord bthr;

        public InitProcedure() {
            this.header = ops.getVolumeHeader();
            //header.print(System.err, "    ");
            this.forkFilterFile = getForkFilterFile(header);

            forkFilterFile.seek(0);
            //byte[] nodeDescriptorData = new byte[14];
            //if(forkFilterFile.read(nodeDescriptorData) != nodeDescriptorData.length)
            //	System.out.println("ERROR: Did not read nodeDescriptor completely.");
            this.btnd = ops.readNodeDescriptor(forkFilterFile);
            this.bthr = ops.readHeaderRecord(forkFilterFile);
            //byte[] headerRec = new byte[BTHeaderRec.length()];
            //forkFilterFile.readFully(headerRec);
            //this.bthr = new BTHeaderRec(headerRec, 0);

        }

        protected abstract ReadableRandomAccessStream getForkFilterFile(CommonHFSVolumeHeader header);
    }

    /** Internal class. */
    private class CatalogInitProcedure extends InitProcedure {

        public final ReadableRandomAccessStream catalogFile;

        public CatalogInitProcedure() {
            this.catalogFile = forkFilterFile;
        }

        @Override
        protected ReadableRandomAccessStream getForkFilterFile(CommonHFSVolumeHeader header) {
            //if(catalogCache != null)
            //    return catalogCache;
            CommonHFSExtentDescriptor[] allCatalogFileDescriptors =
                    getAllDataExtentDescriptors(ops.getCommonHFSCatalogNodeID(ReservedID.CATALOG_FILE),
                    header.getCatalogFile());
            return new ForkFilter(header.getCatalogFile(), allCatalogFileDescriptors,
                    new ReadableRandomAccessSubstream(hfsFile), fsOffset, header.getAllocationBlockSize(),
                    header.getAllocationBlockStart()*physicalBlockSize);
        }
    }

    /** Internal class. */
    private class ExtentsInitProcedure extends InitProcedure {
        public final ReadableRandomAccessStream extentsFile;

        public ExtentsInitProcedure() {
            this.extentsFile = forkFilterFile;
        }

        @Override
        protected ReadableRandomAccessStream getForkFilterFile(CommonHFSVolumeHeader header) {
            return new ForkFilter(header.getExtentsOverflowFile(),
                    header.getExtentsOverflowFile().getBasicExtents(),
                    new ReadableRandomAccessSubstream(hfsFile), fsOffset, header.getAllocationBlockSize(),
                    header.getAllocationBlockStart()*physicalBlockSize);
        }
    }

    protected static interface CatalogOperations {
        public CommonHFSCatalogIndexNode newCatalogIndexNode(byte[] data,
                int offset, int nodeSize, CommonBTHeaderRecord bthr);

        public CommonHFSCatalogKey newCatalogKey(CommonHFSCatalogNodeID nodeID,
                CommonHFSCatalogString searchString, CommonBTHeaderRecord bthr);

        public CommonHFSCatalogLeafNode newCatalogLeafNode(byte[] data,
                int offset, int nodeSize, CommonBTHeaderRecord bthr);

        public CommonHFSCatalogLeafRecord newCatalogLeafRecord(byte[] data,
                int offset, CommonBTHeaderRecord bthr);
    }

    protected volatile SynchronizedReadableRandomAccess hfsFile;
    private volatile SynchronizedReadableRandomAccessStream hfsStream;
    //private final SynchronizedReadableRandomAccessStream backingFile;
    private final ReadableRandomAccessStream sourceStream;
    protected final long fsOffset;
    protected final int physicalBlockSize;

    // Variables for reading cached files.
    //private ReadableBlockCachingStream catalogCache = null;

    /*
    */

    /*
    public boolean isFileSystemCachingEnabled() {
        ReadableRandomAccessStream currentSourceStream = hfsStream.getSourceStream();
        return currentSourceStream != this.sourceStream &&
                currentSourceStream instanceof ReadableBlockCachingStream;
    }
    */

    public void enableFileSystemCaching() {
        enableFileSystemCaching(256 * 1024, 64); // 64 pages of 256 KiB each is the default setting
    }

    public void enableFileSystemCaching(int blockSize, int blocksInCache) {
        hfsStream = new SynchronizedReadableRandomAccessStream(
                new ReadableBlockCachingStream(sourceStream, blockSize, blocksInCache));
        hfsFile = hfsStream;
    }

    public void disableFileSystemCaching() {
        hfsStream = new SynchronizedReadableRandomAccessStream(sourceStream);
        hfsFile = hfsStream;
    }

    /**
     * Returns the underlying stream, serving the view with HFS+ file system
     * data.
     * @return the underlying stream.
     */
    /*
    public ReadableRandomAccessStream getStream() {
        return hfsFile;
    }
     * */


    /*
    public long extractDataForkToStream(CommonHFSCatalogLeafRecord fileRecord, OutputStream os) throws IOException {
	return extractDataForkToStream(fileRecord, os, NullProgressMonitor.getInstance());
    }
    */
    public long extractDataForkToStream(CommonHFSCatalogLeafRecord fileRecord, OutputStream os,
					ProgressMonitor pm) throws IOException {
	// = fileRecord.getData();
	if(fileRecord instanceof CommonHFSCatalogFileRecord) {
	    CommonHFSCatalogFile catFile = ((CommonHFSCatalogFileRecord)fileRecord).getData();
	    CommonHFSForkData dataFork = catFile.getDataFork();
	    return extractForkToStream(dataFork, getAllDataExtentDescriptors(fileRecord), os, pm);
	}
	else
	    throw new IllegalArgumentException("fileRecord.getData() it not of type RECORD_TYPE_FILE");
    }
    /*
    public long exdtractResourceForkToStream(CommonHFSCatalogLeafRecord fileRecord, OutputStream os) throws IOException {
	return extractResourceForkToStream(fileRecord, os, NullProgressMonitor.getInstance());
    }*/
    public long extractResourceForkToStream(CommonHFSCatalogLeafRecord fileRecord, OutputStream os,
					    ProgressMonitor pm) throws IOException {
	//CommonHFSCatalogLeafRecordData recData = fileRecord.getData();
	if(fileRecord instanceof CommonHFSCatalogFileRecord) {
	    CommonHFSCatalogFile catFile = ((CommonHFSCatalogFileRecord)fileRecord).getData();
	    CommonHFSForkData resFork = catFile.getResourceFork();
	    return extractForkToStream(resFork, getAllResourceExtentDescriptors(fileRecord), os, pm);
	}
	else
	    throw new IllegalArgumentException("fileRecord.getData() it not of type RECORD_TYPE_FILE");
    }
    /*
    public long extractForkToStream(CommonHFSForkData forkData, CommonHFSExtentDescriptor[] extentDescriptors,
				    OutputStream os) throws IOException {
	return extractForkToStream(forkData, extentDescriptors, os, NullProgressMonitor.getInstance());
    }
    */
    public long extractForkToStream(CommonHFSForkData forkData,
            CommonHFSExtentDescriptor[] extentDescriptors, OutputStream os,
            ProgressMonitor pm) throws IOException {
    CommonHFSVolumeHeader header = ops.getVolumeHeader();
	ForkFilter forkFilter = new ForkFilter(forkData, extentDescriptors,
                new ReadableRandomAccessSubstream(hfsFile), fsOffset,
                header.getAllocationBlockSize(),
                header.getAllocationBlockStart()*physicalBlockSize);
	long bytesToRead = forkData.getLogicalSize();
	byte[] buffer = new byte[4096];
	while(bytesToRead > 0) {
	    if(pm.cancelSignaled()) break;
	    //System.out.print("forkFilter.read([].length=" + buffer.length + ", 0, " + (bytesToRead < buffer.length ? (int)bytesToRead : buffer.length) + "...");
	    int bytesRead = forkFilter.read(buffer, 0, (bytesToRead < buffer.length ? (int)bytesToRead : buffer.length));
	    //System.out.println("done. bytesRead = " + bytesRead);
	    if(bytesRead < 0)
		break;
	    else {
		pm.addDataProgress(bytesRead);
		os.write(buffer, 0, bytesRead);
		bytesToRead -= bytesRead;
	    }
	}
	return forkData.getLogicalSize()-bytesToRead;
    }

    /**
     * Returns a stream from which the data fork of the specified file record
     * can be accessed.
     *
     * @return a stream from which the data fork of the specified file record
     * can be accessed.
     * @throws IllegalArgumentException if fileRecord is not a file record.
     */
    public ReadableRandomAccessStream getReadableDataForkStream(CommonHFSCatalogLeafRecord fileRecord) {
	if(fileRecord instanceof CommonHFSCatalogFileRecord) {
	    CommonHFSCatalogFile catFile = ((CommonHFSCatalogFileRecord)fileRecord).getData();
	    CommonHFSForkData fork = catFile.getDataFork();
	    return getReadableForkStream(fork, getAllDataExtentDescriptors(fileRecord));
	}
	else
	    throw new IllegalArgumentException("fileRecord.getData() it not of type RECORD_TYPE_FILE");
    }

    /**
     * Returns a stream from which the resource fork of the specified file record
     * can be accessed.
     *
     * @return a stream from which the resource fork of the specified file record
     * can be accessed.
     * @throws IllegalArgumentException if fileRecord is not a file record.
     */
    public ReadableRandomAccessStream getReadableResourceForkStream(CommonHFSCatalogLeafRecord fileRecord) {
	if(fileRecord instanceof CommonHFSCatalogFileRecord) {
	    CommonHFSCatalogFile catFile = ((CommonHFSCatalogFileRecord)fileRecord).getData();
	    CommonHFSForkData fork = catFile.getResourceFork();
	    return getReadableForkStream(fork, getAllResourceExtentDescriptors(fileRecord));
	}
	else
	    throw new IllegalArgumentException("fileRecord.getData() it not of type RECORD_TYPE_FILE");
    }

    private ReadableRandomAccessStream getReadableForkStream(CommonHFSForkData forkData,
            CommonHFSExtentDescriptor[] extentDescriptors) {
        CommonHFSVolumeHeader header = ops.getVolumeHeader();
        return new ForkFilter(forkData, extentDescriptors, new ReadableRandomAccessSubstream(hfsFile),
                fsOffset + fileReadOffset,
                header.getAllocationBlockSize(),
                header.getAllocationBlockStart() * physicalBlockSize);
    }

    private static String getDebugString(CommonHFSExtentKey key) {
        return key.getForkType() + ":" + key.getFileID().toLong() + ":" + key.getStartBlock();
    }

    public CommonHFSExtentDescriptor[] getAllExtents(CommonHFSCatalogLeafRecord requestFile,
            CommonHFSForkType forkType) {
	if(requestFile instanceof CommonHFSCatalogFileRecord) {
	    CommonHFSCatalogFile catFile = ((CommonHFSCatalogFileRecord)requestFile).getData();

	    CommonHFSForkData forkData;
	    if(forkType == CommonHFSForkType.DATA_FORK)
		forkData = catFile.getDataFork();
	    else if(forkType == CommonHFSForkType.RESOURCE_FORK)
		forkData = catFile.getResourceFork();
	    else
		throw new IllegalArgumentException("Illegal fork type!");
	    return getAllExtents(catFile.getFileID(), forkData, forkType);
	}
	else
	    throw new IllegalArgumentException("Not a file record!");
    }

    public CommonHFSExtentDescriptor[] getAllExtents(CommonHFSCatalogNodeID fileID,
            CommonHFSForkData forkData, CommonHFSForkType forkType) {
        if(fileID == null)
            throw new IllegalArgumentException("fileID == null");
        if(forkData == null)
            throw new IllegalArgumentException("forkData == null");
        if(forkType == null)
            throw new IllegalArgumentException("forkType == null");

        CommonHFSExtentDescriptor[] result;
        long allocationBlockSize = ops.getVolumeHeader().getAllocationBlockSize();

        long basicExtentsBlockCount = 0;
        {
            CommonHFSExtentDescriptor[] basicExtents = forkData.getBasicExtents();
            for(int i = 0; i < basicExtents.length; ++i)
                basicExtentsBlockCount += basicExtents[i].getBlockCount();
        }

        if(basicExtentsBlockCount * allocationBlockSize >= forkData.getLogicalSize()) {
            result = forkData.getBasicExtents();
        }
        else {
            //System.err.println("Reading overflow extent for file " + fileID.toString());
            LinkedList<CommonHFSExtentDescriptor> resultList = new LinkedList<CommonHFSExtentDescriptor>();
            for(CommonHFSExtentDescriptor descriptor : forkData.getBasicExtents())
                resultList.add(descriptor);
            long totalBlockCount = basicExtentsBlockCount;

            while(totalBlockCount * allocationBlockSize < forkData.getLogicalSize()) {
                CommonHFSExtentKey extentKey =
                        ops.createCommonHFSExtentKey(forkType, fileID, (int) totalBlockCount);

                CommonHFSExtentLeafRecord currentRecord = extentsOverflowFile.getOverflowExtent(extentKey);
                if(currentRecord == null) {
                    System.err.println("ERROR: currentRecord == null!!");
                    System.err.print(  "       extentKey");
                    if(extentKey != null) {
                        System.err.println(":");
                        extentKey.print(System.err, "         ");
                    }
                    else
                        System.err.println(" == null!!");
                }
                CommonHFSExtentDescriptor[] currentRecordData = currentRecord.getRecordData();
                for(CommonHFSExtentDescriptor cur : currentRecordData) {
                    resultList.add(cur);
                    totalBlockCount += cur.getBlockCount();
                }
            }
            //System.err.println("  Finished reading extents... (currentblock: " + currentBlock + " total: " + forkData.getTotalBlocks() + ")");

            result = resultList.toArray(new CommonHFSExtentDescriptor[resultList.size()]);
        }
        return result;
    }

    public CommonHFSExtentDescriptor[] getAllExtentDescriptors(CommonHFSCatalogLeafRecord requestFile,
            CommonHFSForkType forkType) {
	return getAllExtentDescriptors(getAllExtents(requestFile, forkType));
    }

    public CommonHFSExtentDescriptor[] getAllExtentDescriptors(CommonHFSCatalogNodeID fileID,
            CommonHFSForkData forkData, CommonHFSForkType forkType) {
	return getAllExtentDescriptors(getAllExtents(fileID, forkData, forkType));
    }

    protected CommonHFSExtentDescriptor[] getAllExtentDescriptors(
            CommonHFSExtentDescriptor[] descriptors) {
        LinkedList<CommonHFSExtentDescriptor> descTmp = new LinkedList<CommonHFSExtentDescriptor>();
        for(CommonHFSExtentDescriptor desc : descriptors) {
            if(desc.getStartBlock() == 0 && desc.getBlockCount() == 0) {
                break;
            } else {
                descTmp.addLast(desc);
            }
        }

	return descTmp.toArray(new CommonHFSExtentDescriptor[descTmp.size()]);
    }

    public CommonHFSExtentDescriptor[] getAllDataExtentDescriptors(
            CommonHFSCatalogNodeID fileID, CommonHFSForkData forkData) {
	return getAllExtentDescriptors(fileID, forkData, CommonHFSForkType.DATA_FORK);
    }

    public CommonHFSExtentDescriptor[] getAllDataExtentDescriptors(
            CommonHFSCatalogLeafRecord requestFile) {
	return getAllExtentDescriptors(requestFile, CommonHFSForkType.DATA_FORK);
    }

    public CommonHFSExtentDescriptor[] getAllResourceExtentDescriptors(
            CommonHFSCatalogNodeID fileID, CommonHFSForkData forkData) {
	return getAllExtentDescriptors(fileID, forkData, CommonHFSForkType.RESOURCE_FORK);
    }

    public CommonHFSExtentDescriptor[] getAllResourceExtentDescriptors(
            CommonHFSCatalogLeafRecord requestFile) {
	return getAllExtentDescriptors(requestFile, CommonHFSForkType.RESOURCE_FORK);
    }

    // Utility methods





    /*
    private static HFSPlusCatalogLeafRecord findRecordID(HFSPlusCatalogLeafNode leafNode, HFSCatalogNodeID nodeID) {
	HFSPlusCatalogLeafRecord[] records = leafNode.getLeafRecords();
	for(int i = 0; i < records.length; ++i) {
	    HFSPlusCatalogLeafRecord curRec = records[i];
	    HFSPlusCatalogLeafRecordData curRecData = curRec.getData();
	    if(curRecData instanceof HFSPlusCatalogFile &&
	       ((HFSPlusCatalogFile)curRecData).getFileID().toInt() == nodeID.toInt()) {
		return (HFSPlusCatalogFile)curRecData;
	    }
	}
	return null;
    }
    */

    protected long calculateDataForkSizeRecursive(CommonHFSCatalogLeafRecord[] recs) {
	return calculateForkSizeRecursive(recs, false);
    }
    protected long calculateDataForkSizeRecursive(CommonHFSCatalogLeafRecord rec) {
	return calculateForkSizeRecursive(rec, false);
    }
    protected long calculateResourceForkSizeRecursive(CommonHFSCatalogLeafRecord[] recs) {
	return calculateForkSizeRecursive(recs, true);
    }
    protected long calculateResourceForkSizeRecursive(CommonHFSCatalogLeafRecord rec) {
	return calculateForkSizeRecursive(rec, true);
    }
    /** Calculates the complete size of the trees represented by <code>recs</code>. */
    protected long calculateForkSizeRecursive(CommonHFSCatalogLeafRecord[] recs, boolean resourceFork) {
	long totalSize = 0;
	for(CommonHFSCatalogLeafRecord rec : recs)
	    totalSize += calculateForkSizeRecursive(rec, resourceFork);
	return totalSize;
    }
    /** Calculates the complete size of the tree represented by <code>rec</code>. */
    protected long calculateForkSizeRecursive(CommonHFSCatalogLeafRecord rec, boolean resourceFork) {
	if(rec instanceof CommonHFSCatalogFileRecord) {
	    if(!resourceFork)
		return ((CommonHFSCatalogFileRecord)rec).getData().getDataFork().getLogicalSize();
	    else
		return ((CommonHFSCatalogFileRecord)rec).getData().getResourceFork().getLogicalSize();
	}
	else if(rec instanceof CommonHFSCatalogFolderRecord) {
	    CommonHFSCatalogNodeID requestedID =
                    ((CommonHFSCatalogFolderRecord)rec).getData().getFolderID();
	    CommonHFSCatalogLeafRecord[] contents = catalogFile.listRecords(requestedID);
	    long totalSize = 0;
	    for(CommonHFSCatalogLeafRecord outRec : contents) {
		totalSize += calculateForkSizeRecursive(outRec, resourceFork);
	    }
	    return totalSize;
	}
	else
	    return 0;
    }
}
