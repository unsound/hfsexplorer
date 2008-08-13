/*-
 * Copyright (C) Erik Larsson
 *
 * All rights reserved.
 */
package org.catacombae.hfsexplorer.unfinished;

/**
 * A HFSExplorer exception that should result in a pretty error message.
 * 
 * @author Erik Larsson
 */
public class ErrorMessageException extends RuntimeException {
    public ErrorMessageException(String errorMessage) {
        super(errorMessage);
    }
}
