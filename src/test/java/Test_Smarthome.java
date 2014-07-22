import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import edu.tum.uc.jvm.asm.MyClassAdapter;
import edu.tum.uc.jvm.asm.MyClassWriter;
import edu.tum.uc.jvm.utility.ConfigProperties;

public class Test_Smarthome extends AbstractTest{

	@Before
	public void init() throws Exception{
		init("/uc_smarthome.config");
	}

	/**
	 * This test requires an established connection to a remote smart home, see /home/alex/connect.sh
	 * 
	 * @author alex
	 * 
	 */
//	@Test
	public void testInstrumentation() {
		try {
			// Class clazz = Class.forName("UcJavaPepTest.MyTest");
			// File f = new
			// File("src/test/resources/classes/org/pec/uc/energy/PecUcEnergyUi.class");v
			// URL policy = getClass().getResource("/uc.config");
			// File policyFile = new File(policy.getFile());
			// URL url = getClass().getResource("/test/TestProgram.class");
			// File f = new File(url.getFile());
			// FileInputStream fin = new FileInputStream(f);
			// byte[] b = new byte[(int)f.length()];
			// fin.read(b);

			Class<?> clazz = test.MyTest2.class;
			String className = clazz.getName().replace(".",
					System.getProperty("file.separator"))
					+ ".class";
			InputStream is = clazz.getClassLoader().getResourceAsStream(
					className);
			byte[] b = IOUtils.toByteArray(is);

			// MyClassReader cr = new MyClassReader(b);
			ClassReader cr = new ClassReader(b);
			ClassNode cn = new ClassNode();
			cr.accept(cn, 0);

			MyClassWriter cw = new MyClassWriter(cr, ClassWriter.COMPUTE_MAXS
					| ClassWriter.COMPUTE_FRAMES);
			// ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS
			// |ClassWriter.COMPUTE_FRAMES);
			// ClassWriter.COMPUTE_FRAMES);
			ClassVisitor cv = new MyClassAdapter(Opcodes.ASM5, cw, cn);
			cr.accept(cv, ClassReader.EXPAND_FRAMES);

			String s = ConfigProperties
					.getProperty(ConfigProperties.PROPERTIES.INSTREMENTED_CLASS_PATH
							.toString());
			if ((s != null) && !s.equals("")) {
				try {
					File f = new File(s + cr.getClassName().replace("/", "_")
							+ "_2.class");
					if (!f.exists()) {
						f.createNewFile();
					}
					DataOutputStream dos = new DataOutputStream(
							new FileOutputStream(f));
					dos.write(cw.toByteArray());
					DataOutputStream dos2 = new DataOutputStream(
							new FileOutputStream(s
									+ cr.getClassName().replace("/", "_")
									+ "_1.class"));
					dos2.write(cr.b);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("INSTRUMENTED CLASS " + className);
			}

			ClassLoader parent = this.getClass().getClassLoader();
			MyClassLoader mcl = new MyClassLoader(parent);

			Class<?> myTest2Clazz = mcl
					.define("test.MyTest2", cw.toByteArray());
			Object obj = myTest2Clazz.newInstance();
			test.TestIntf myTest2 = (test.TestIntf) obj;
			myTest2.runtest();
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
		}
	}
}