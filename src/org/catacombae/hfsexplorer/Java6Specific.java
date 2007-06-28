package org.catacombae.hfsexplorer;

import java.io.File;
import java.io.IOException;
import java.awt.Desktop;

public class Java6Specific {
    public static boolean canOpen() {
	return Desktop.getDesktop().isSupported(Desktop.Action.OPEN);
    }
    public static void openFile(File f) throws IOException {
	Desktop.getDesktop().open(f);
    }
}
