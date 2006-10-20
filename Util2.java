import java.io.*;

public class Util2 {
    public static String toASCIIString(byte[] data) {
	try {
	    return new String(data, "US-ASCII");
	} catch(Exception e) {
	    return null;
	}
    }
    public static String toASCIIString(short i) {
	try {
	    return new String(Util.toByteArrayBE(i), "US-ASCII");
	} catch(Exception e) {
	    return null;
	}
    }
    public static String toASCIIString(int i) {
	try {
	    return new String(Util.toByteArrayBE(i), "US-ASCII");
	} catch(Exception e) {
	    return null;
	}
    }

    public static short[] readShortArrayBE(byte[] b) {
	short[] result = new short[b.length/2];
	for(int i = 0; i < result.length; ++i)
	    result[i] = Util.readShortBE(b, i*2);
	return result;
    }
    public static int[] readIntArrayBE(byte[] b) {
	int[] result = new int[b.length/4];
	for(int i = 0; i < result.length; ++i)
	    result[i] = Util.readIntBE(b, i*4);
	return result;
    }

    public static byte[] fillBuffer(InputStream is, byte[] buffer) throws IOException {
	DataInputStream dis = new DataInputStream(is);
	dis.readFully(buffer);
	return buffer;
    }
	
}
