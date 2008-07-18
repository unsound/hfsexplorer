/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.fsframework;

/**
 *
 * @author Erik
 */
public class FSFileNotFoundException extends RuntimeException {
    public FSFileNotFoundException() {
	super();
    }
    public FSFileNotFoundException(String message) {
	super(message);
    }
    public FSFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public FSFileNotFoundException(Throwable cause) {
        super(cause);
    }
}
