import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.ProtectionDomain;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import edu.tum.uc.jvm.MyUcTransformer;
import edu.tum.uc.jvm.UcCommunicator;
import edu.tum.uc.jvm.UcTransformer;
import edu.tum.uc.jvm.utility.ConfigProperties;
import edu.tum.uc.jvm.utility.StatisticsWriter;

public class MyTestJzip extends AbstractTest {

	@Before
	public void init() throws Exception {
		init("/uc-myjzip.config");
	}

	@Test
	public void testInstrumentation() {
		try {			
			//Load and instrument JZip's bytecode
			Class<?> clazz = jzip.JZip.class;
			String className = clazz.getName().replace(".",
					System.getProperty("file.separator"))
					+ ".class";			
			InputStream is = clazz.getClassLoader().getResourceAsStream(
					className);
			byte[] raw_bytecode = IOUtils.toByteArray(is);

			MyUcTransformer u = new MyUcTransformer();
			byte[] instrumented_bytecode = u.transform(null, className, null,
					null, raw_bytecode);
			
			FileOutputStream fos = new FileOutputStream("target/test-classes/jzip/JZipInstrd.class");
			fos.write(instrumented_bytecode);

			ClassLoader parent = this.getClass().getClassLoader();
			MyClassLoader mcl = new MyClassLoader(parent);
			
			
			
			//Reload instrumented JZip class and execute test run: zip a bunch of file in the resource folder 'toBeZippedFiles'
			Class<?> reloadClass = mcl.define("jzip.JZip",
					instrumented_bytecode);
			Object obj = reloadClass.newInstance();
			test.TestIntf myTest2 = (test.TestIntf) obj;

			for (int i = 0; i < 1; i++) {
				String instruction = "";
				URL url = this.getClass().getResource("/toBeZippedFiles");
				if (url != null) {
					File f = new File(url.getFile());
					// File f = new File("/home/alex/xlayerpip.zip");
					instruction = "zip " + f.getParent()
							+ "/toBeZipped.zip "+ f.getAbsolutePath() + "/";
					// instruction = "zip /home/alex/toBeZipped.zip "// + f.getAbsolutePath();
				}

				instruction += "\n exit";
				InputStream is2 = System.in;
				System.setIn(new ByteArrayInputStream(instruction.getBytes()));
				myTest2.runtest();
				System.setIn(is2);
				StatisticsWriter.clear();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalClassFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}