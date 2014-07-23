package edu.tum.uc.jvm;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Date;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import edu.tum.uc.jvm.asm.MyClassAdapter;
import edu.tum.uc.jvm.asm.MyClassReader;
import edu.tum.uc.jvm.asm.MyClassWriter;
import edu.tum.uc.jvm.utility.ConfigProperties;
import edu.tum.uc.jvm.utility.StatisticsWriter;
import edu.tum.uc.jvm.utility.Utility;

public class UcTransformer implements ClassFileTransformer {

	private static TomcatClassLoader myClassLoader;
	private static ProtectionDomain myProtDom;

	public static final String PREFIX = "my/";

	public static final String HOOKMETHOD = MirrorStack.class.getName()
			.replace(".", "/");
	public static final String STRDELIM = ":";

	public static final String MY_REPO = "/Users/ladmin/Documents/workspace/edu.tum.www22.example/bin/MyRepo/my";// "/Users/ladmin/MyRepo/my";

	private static boolean byTomcat = false;

	public UcTransformer() {
	}

	public UcTransformer(boolean p_byTomcat) {
		UcTransformer.byTomcat = p_byTomcat;
	}

	// Run instruction: java -javaagent:uc4jvm.jar -Djava.security.manager
	// -Djava.security.policy=mypolicy.txt -Xverify:none helloWorld

	private void setClassLoader(ClassLoader p_myClassLoader) {
		UcTransformer.myClassLoader = (TomcatClassLoader) p_myClassLoader;
	}

	public static ClassLoader getMyClassLoader() {
		return UcTransformer.myClassLoader;
	}

	private void setProtectionDomain(ProtectionDomain p_protectionDomain) {
		UcTransformer.myProtDom = p_protectionDomain;
	}

	// private static boolean checkClassExist(String p_className){
	// File f = new File(UcTransformer.MY_REPO+"/"+p_className.replace(".",
	// "/")+".class");
	// return f.exists();
	// }
	

	@Override
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		
		if (UcTransformer.byTomcat) {
			this.setClassLoader(loader);
			this.setProtectionDomain(protectionDomain);
		}
		
		if(Utility.isBlackisted(className)){
			return null;
		}
		
		//For statstic purposes
		Date start = new Date();

		MyClassReader cr = new MyClassReader(classfileBuffer);
		ClassNode cn = new ClassNode();
		cr.accept(cn, 0);

		MyClassWriter cw = new MyClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		// ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS |
		// ClassWriter.COMPUTE_FRAMES);
		ClassVisitor cv = new MyClassAdapter(Opcodes.ASM5, cw, cn);
		cr.accept(cv, ClassReader.EXPAND_FRAMES);
		
		//For statistic purposes
		Date end = new Date();
		
		String statistic = ConfigProperties.getProperty(ConfigProperties.PROPERTIES.STATISTICS.toString());
		if(!"".equals(statistic)){
			StatisticsWriter.logInstrumentation(cn, cw.toByteArray(), end.getTime() - start.getTime());
		}
		
		//Dump instrumented bytecode if INSTRUMENTED_CLASS_PATH is set in configuration file
		String s = ConfigProperties
				.getProperty(ConfigProperties.PROPERTIES.INSTREMENTED_CLASS_PATH
						.toString());
		if ((s != null) && !s.equals("")) {
			try {
				File f = new File(s + cr.getClassName().replace("/", "_")+".class");
				if (!f.exists()) {
					f.createNewFile();
				}
				DataOutputStream dos = new DataOutputStream(
						new FileOutputStream(f));
				dos.write(cw.toByteArray());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return cw.toByteArray();
	}
}