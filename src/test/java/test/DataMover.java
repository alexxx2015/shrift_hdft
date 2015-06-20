package test;

public class DataMover {
	public byte[] inbuf;
	public byte[] outbuf;
	public int x;
	public static int staticx;
	
	public void moveData() {
		for (int i = 0; i < Math.min(inbuf.length, outbuf.length); i++) {
			outbuf[i] = inbuf[i];
		}
		outbuf[0] = (byte)x;
		outbuf[1] = (byte)staticx;
	}
}
