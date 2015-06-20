package test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Scanner;

public class TestProgram10 implements TestIntf {

	@Override
	public void runtest() {
		try {
			main(new String[] {});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	private static void moveData(byte[] inbuf, byte[] outbuf, int x, String dummyParam) throws IOException {
		x = inc(x);
		x *= x;
		
		DataMover dataMover = new DataMover();
		dataMover.inbuf = inbuf;
		dataMover.outbuf = outbuf;
		dataMover.x = inbuf[1];
		dataMover.staticx = inbuf[2];
		dataMover.moveData();
	}
	private static int inc(int x){
		x = x ^ x;
		x = x | x;
		x = x & x;
		x = x << 4;
		x = -x;
		return ++x;
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		//Scanner s = new Scanner(new BufferedReader(new InputStreamReader(System.in)));
		//int x = s.nextInt();
		
		FileInputStream fis1 = new FileInputStream("target/test-classes/foo1.txt");
		FileInputStream fis2 = new FileInputStream("target/test-classes/foo2.txt");
		FileOutputStream fos1 = new FileOutputStream("out1.txt");
		FileOutputStream fos2 = new FileOutputStream("out2.txt");
		byte[] inbuffer1 = new byte[1000];
		byte[] inbuffer2 = new byte[1000];
		byte[] outbuffer1 = new byte[1000];
		byte[] outbuffer2 = new byte[1000];
		
		fis1.read(inbuffer1);
		fis2.read(inbuffer2);
		
		/*short[] sa = new short[10];
		sa[1] = (short)inbuffer1[3];
		inbuffer2[4] = (byte)sa[1];
		
		int i = inbuffer2[10] % 1000;*/
		int x = (byte)inbuffer1[1];
		
		moveData(inbuffer1, outbuffer1, x, ",");
		moveData(inbuffer2, outbuffer2, x, "");
		//outbuffer1[1] = inbuffer2[4];
		fos1.write(outbuffer1);
		fos2.write(outbuffer2);
	}
}
