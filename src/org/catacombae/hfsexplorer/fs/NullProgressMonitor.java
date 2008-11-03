/*-
 * Copyright (C) Erik Larsson
 *
 * All rights reserved.
 */
package org.catacombae.hfsexplorer.fs;

/**
 *
 * @author Erik Larsson
 */
public class NullProgressMonitor implements ProgressMonitor {

    private static final NullProgressMonitor INSTANCE = new NullProgressMonitor();
    
    public static NullProgressMonitor getInstance() { return INSTANCE; }
    
    protected NullProgressMonitor() {}
    
    @Override
    public void signalCancel() {}

    @Override
    public boolean cancelSignaled() { return false; }

    @Override
    public void confirmCancel() {}

    @Override
    public void addDataProgress(long dataSize) {}

}
