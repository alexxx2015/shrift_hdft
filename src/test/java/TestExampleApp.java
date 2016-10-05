import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.IllegalClassFormatException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import edu.tum.uc.jvm.MyUcTransformerOpt;
import edu.tum.uc.jvm.utility.StatisticsWriter;

public class TestExampleApp extends AbstractTest {

	private boolean runCode = true;

	@Before
	public void init() throws Exception {
		// this.startPdpServer=true;
		// init("/uc-config/uc-myjzip.config");
		init("/uc-config/uc-exampleapp.config");
		// runCode = false;
	}

	@Test
	public void testInstrumentation() {
		try {
			Logger.getRootLogger().setLevel(Level.OFF);
			List<Class<?>> clazzes = new LinkedList<Class<?>>();
			clazzes.add(types.Utility.class);
			clazzes.add(sap.PPLClient.class);
			clazzes.add(sap.ExampleApp.class);
			test.TestIntf myTest = null;

			ClassLoader parent = this.getClass().getClassLoader();
			MyClassLoader mcl = new MyClassLoader(parent);
			
			// Load and instrument JZip's bytecode
			for (Class<?> clazz : clazzes) {
				// Class<?> clazz = sap.ExampleApp.class;
				String className = clazz.getName().replace(".", System.getProperty("file.separator")) + ".class";
				InputStream is = clazz.getClassLoader().getResourceAsStream(className);
				byte[] raw_bytecode = IOUtils.toByteArray(is);

				MyUcTransformerOpt u = new MyUcTransformerOpt();
				byte[] instrumented_bytecode = u.transform(null, className, null, null, raw_bytecode);


				// Reload instrumented JZip class and execute test run: zip a
				// bunch of file in the resource folder 'toBeZippedFiles'
				Class<?> reloadClass = mcl.define(clazz.getName().replace("/", "."), instrumented_bytecode);
				Object obj = reloadClass.newInstance();
				if(obj.getClass().getName().equals("sap.ExampleApp"))
				  myTest = (test.TestIntf) obj;
			}
			if(myTest != null && runCode)
				myTest.runtest();
			StatisticsWriter.clear();
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