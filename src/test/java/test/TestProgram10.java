package test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestProgram10 implements TestIntf {

	public void runtest() {
		try {
			main(new String[] {});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void moveData(byte[] inbuf, byte[] outbuf, int x) throws IOException {
		x = inc(x);
		x *= x;
		moveDataHelp(inbuf, outbuf, x);
	}
	
	/*private int testMethod(int arg1, double arg2, String arg3) {
		return 5;
	}*/
	
	private static int inc(int x){
		return x+3;
	}

	private static void moveDataHelp(byte[] inbuf, byte[] outbuf, int x) throws IOException {		
		System.arraycopy(inbuf, 0, outbuf, 0, Math.min(inbuf.length, outbuf.length));
		outbuf[0] = (byte)x;
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		//Scanner s = new Scanner(new BufferedReader(new InputStreamReader(System.in)));
		//int x = s.nextInt();
		
		FileInputStream fis1 = new FileInputStream("target/test-classes/foo.txt");
		FileInputStream fis2 = new FileInputStream("target/test-classes/foo2.txt");
		FileOutputStream fos1 = new FileOutputStream("target/test-classes/bar.txt");
		FileOutputStream fos2 = new FileOutputStream("target/test-classes/bar2.txt");
		byte[] inbuffer1 = new byte[1000];
		byte[] inbuffer2 = new byte[1000];
		byte[] outbuffer1 = new byte[1000];
		byte[] outbuffer2 = new byte[1000];
		fis1.read(inbuffer1);
		fis2.read(inbuffer2);
		
		/*TestProgram10 tp = new TestProgram10();
		tp.testMethod(5, 3.4, "aString");*/
		
		int x = (byte)inbuffer1[1];
		
		moveData(inbuffer1, outbuffer1, x);
		moveData(inbuffer2, outbuffer2, x);
		fos1.write(outbuffer1);
		fos2.write(outbuffer2);
	}

}
