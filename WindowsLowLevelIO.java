import java.io.*;

public class WindowsLowLevelIO {
    private byte[] fileHandle;
    static {
	try {
	    System.loadLibrary("llio");
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }
    public WindowsLowLevelIO(String filename) {
	fileHandle = open(filename);
	System.out.println("fileHandle: 0x" + Util.byteArrayToHexString(fileHandle));
    }
    public void seek(long pos) {
	if(fileHandle != null)
	    seek(pos, fileHandle);
	else
	    throw new RuntimeException("File closed!");
    }
    public int read() {
	byte[] oneByte = new byte[1];
	if(read(oneByte) == 1)
	    return oneByte[0] & 0xFF;
	else
	    return -1;
    }
    public int read(byte[] data) {
	return read(data, 0, data.length);
    }
    public int read(byte[] data, int pos, int len) {
	if(fileHandle != null)
	    return read(data, pos, len, fileHandle);
	else
	    throw new RuntimeException("File closed!");
    }
    
    public void readFully(byte[] data) {
	readFully(data, 0, data.length);
    }

    public void readFully(byte[] data, int offset, int length) {
	int bytesRead = 0;
	while(bytesRead < length) {
	    int curBytesRead = read(data, bytesRead, length-bytesRead);
	    if(curBytesRead > 0) bytesRead += curBytesRead;
	    else 
		throw new RuntimeException("Couldn't read the entire length.");
	}
    }

    public void close() {
	if(fileHandle != null) {
	    close(fileHandle);
	    fileHandle = null;
	}
	else
	    throw new RuntimeException("File closed!");
    }

    public void ejectMedia() {
	if(fileHandle != null)
	    ejectMedia(fileHandle);
	else
	    throw new RuntimeException("File closed!");
    }
    public void loadMedia() {
	if(fileHandle != null)
	    loadMedia(fileHandle);
	else
	    throw new RuntimeException("File closed!");
    }
    
    private static native byte[] open(String filename);
    private static native void seek(long pos, byte[] handle);
    private static native int read(byte[] data, int pos, int len, byte[] handle);
    private static native void close(byte[] handle);
    private static native void ejectMedia(byte[] handle);
    private static native void loadMedia(byte[] handle);

    public static void main(String[] args) {
	BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
	WindowsLowLevelIO wllio1 = new WindowsLowLevelIO(args[0]);
	
	try {
	    if(args[1].equals("testread")) {
		// When reading directly from block devices, the buffer must be a multiple of the sector size of the device. Also, reading must start at a value dividable by the sector size. Calling DeviceIoControl with IOCTL_DISK_GET_DRIVE_GEOMETRY_EX will get the drive geometry for the device.
		System.out.println("Seeking to 1024...");
		wllio1.seek(1024);
		byte[] buf = new byte[4096];
		System.out.println("Reading " + buf.length + " bytes from file: ");
		int bytesRead = wllio1.read(buf);
		System.out.println(" Bytes read: " + bytesRead);
		System.out.println(" As hex:    0x" + Util.byteArrayToHexString(buf));
		System.out.println(" As string: \"" + new String(buf, "US-ASCII") + "\"");
	    }
	    else if(args[1].equals("eject")) {
		System.out.print("Press any key to eject media...");
		stdin.readLine();
		wllio1.ejectMedia();
		System.out.print("Press any key to load media...");
		stdin.readLine();
		wllio1.loadMedia();
	    }
	    else
		System.out.println("Nothing to do.");
	} catch(Exception e) { e.printStackTrace(); }
	wllio1.close();
    }
}
