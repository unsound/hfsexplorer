package org.catacombae.hfsexplorer;

public class UDIFRecognizer {
    private static final int SIGNATURE = 0x6B6F6C79; // in ASCII this reads 'koly' as a fourcc
    public static boolean isUDIF(LowLevelFile llf) {
	llf.seek(llf.length()-512);
	byte[] signature = new byte[4];
	llf.readFully(signature);
	int sigAsInt = Util.readIntBE(signature);
	return sigAsInt == SIGNATURE;
    }
}
