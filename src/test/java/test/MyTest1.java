package test;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class MyTest1 {

		public void read() throws Exception {
			final FileInputStream fis = new FileInputStream("foo.txt");
			final FileOutputStream fos = new FileOutputStream("bar.txt");

			final byte[] buf = new byte[1000];
			int z = System.in.read();
			fis.read(buf); // source
			fos.write(buf); // sink
			System.out.println(z);
		}
	}