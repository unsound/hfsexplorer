package org.catacombae.hfsexplorer;

/* Designed to mimic a subset of RandomAccessFile. (But without the IOExceptions... we throw RuntimeExceptions instead) */

public interface WritableLowLevelFile extends LowLevelFile {
    public void write(byte[] b);
    public void write(byte[] b, int off, int len);
    public void write(int b);
}
