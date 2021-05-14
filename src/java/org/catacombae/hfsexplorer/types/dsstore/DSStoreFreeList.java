package org.catacombae.hfsexplorer.types.dsstore;

import java.io.PrintStream;
import org.catacombae.csjc.DynamicStruct;
import org.catacombae.csjc.PrintableStruct;
import org.catacombae.util.Util;

public class DSStoreFreeList implements DynamicStruct, PrintableStruct {
    public static class Bucket {
        int bucketEntryCount;
        int[] bucketEntries;
    }

    private final Bucket[] buckets = new Bucket[32];
    private int size = 0;

    public DSStoreFreeList(byte[] data, int offset) {
        int index = 0;
        for(int i = 0; i < 32; ++i) {
            buckets[i] = new Bucket();
            buckets[i].bucketEntryCount = Util.readIntBE(data, offset + index);
            index += 4;

            buckets[i].bucketEntries = new int[buckets[i].bucketEntryCount];
            for(int j = 0; j < buckets[i].bucketEntryCount; ++j) {
                buckets[i].bucketEntries[j] =
                        Util.readIntBE(data, offset + index);
                index += 4;
            }
        }
        size = index;
    }

    public int maxSize() {
        return Integer.MAX_VALUE; // I guess...
    }

    public int occupiedSize() {
        return size;
    }

    public int length() {
        return occupiedSize();
    }

    /**  */
    public final long[] getBucketOffsets(int bucket) {
        return Util.unsign(getRawBucketOffsets(bucket));
    }

    public final int[] getRawBucketOffsets(int bucket) {
        return Util.arrayCopy(this.buckets[bucket].bucketEntries,
                new int[this.buckets[bucket].bucketEntries.length]);
    }

    public void printFields(PrintStream ps, String prefix) {
        for(int i = 0; i < this.buckets.length; ++i) {
            ps.print(prefix + " bucket[" + i + "]: [");
            for(int j = 0; j < this.buckets[i].bucketEntryCount; ++j) {
                ps.print(prefix + ((j > 0) ? " " : "") +
                        this.buckets[i].bucketEntries[j]);
            }
            ps.println("]");
        }
    }

    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + "DSStoreFreeList:");
        printFields(ps, prefix);
    }

    public byte[] getBytes() {
        byte[] result = new byte[length()];
        getBytes(result, 0);
        return result;
    }

    public int getBytes(byte[] result, int offset) {
        final int startOffset = offset;

        for(int i = 0; i < this.buckets.length; ++i) {
            Util.arrayPutBE(result, offset, this.buckets[i].bucketEntryCount);
            offset += 4;

            for(int j = 0; j < this.buckets[i].bucketEntryCount; ++j) {
                Util.arrayPutBE(result, offset,
                        this.buckets[i].bucketEntries[j]);
                offset += 4;
            }
        }

        return offset - startOffset;
    }
}
