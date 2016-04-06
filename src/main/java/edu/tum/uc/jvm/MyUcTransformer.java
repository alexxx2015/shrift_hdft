package edu.tum.uc.jvm;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import edu.tum.uc.jvm.instrum.MyClassAdapter;
import edu.tum.uc.jvm.instrum.MyClassReader;
import edu.tum.uc.jvm.instrum.MyClassWriter;
import edu.tum.uc.jvm.instrum.InstrumDelegate;
import edu.tum.uc.jvm.utility.ConfigProperties;
import edu.tum.uc.jvm.utility.EventRepository;
import edu.tum.uc.jvm.utility.StatisticsWriter;
import edu.tum.uc.jvm.utility.Utility;

public class MyUcTransformer implements ClassFileTransformer {

	private static TomcatClassLoader myClassLoader;

	private static ProtectionDomain myProtDom;

	public static final String DELEGATECLASS = InstrumDelegate.class
			.getName().replace(".", "/");

	// true if running instrumentation in a webservice
	private boolean instrument_webservice;

	{
		// Initialize pdp communication
//		UcCommunicator.getInstance().initPDP();

		// Populate PIP
		// Utility.populatePip(ConfigProperties.getProperty(ConfigProperties.PROPERTIES.ANALYSIS_REPORT));

		// Create for all sinks and sources a corresponding object in the event
		// repository
		EventRepository.createEventObjects(ConfigProperties
				.getProperty(ConfigProperties.PROPERTIES.ANALYSIS_REPORT));
	}

	public MyUcTransformer() {
		this(false);
	}

	public MyUcTransformer(boolean p_instrument_webservice) {
		this.instrument_webservice = p_instrument_webservice;
	}

	// Run instruction: java -javaagent:uc4jvm.jar -Djava.security.manager
	// -Djava.security.policy=mypolicy.txt -Xverify:none helloWorld

	private void setClassLoader(ClassLoader p_myClassLoader) {
		MyUcTransformer.myClassLoader = (TomcatClassLoader) p_myClassLoader;
	}

	public static ClassLoader getMyClassLoader() {
		return MyUcTransformer.myClassLoader;
	}

	private void setProtectionDomain(ProtectionDomain p_protectionDomain) {
		MyUcTransformer.myProtDom = p_protectionDomain;
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
		//System.out.println("[MyUcTransformer]: Calling tranform ...");
		if (this.instrument_webservice) {
			this.setClassLoader(loader);
			this.setProtectionDomain(protectionDomain);
		}
		
		//System.out.println("[MyUcTransformer]: Trying to instrument class: " + className);
		//Only instrument whitelisted classes and they are not allowed to be in the blacklist
		if(!Utility.isWhitelisted(className)){
			if (Utility.isBlacklisted(className)) {
				return classfileBuffer;
			}
			return classfileBuffer;
		}
		
//		System.out.println("[MyUcTransformer]: Will instrument class: " + className);

		String statistic = ConfigProperties
				.getProperty(ConfigProperties.PROPERTIES.STATISTICS);
		// log time for instrumentation
		long start_instrumentation = 0;
		if (!"".equals(statistic)) {
			start_instrumentation = System.nanoTime();
		}

		MyClassReader cr = new MyClassReader(classfileBuffer);
		ClassNode cn = new ClassNode();
		cr.accept(cn, 0);

		MyClassWriter cw = new MyClassWriter(cr, ClassWriter.COMPUTE_MAXS
				| ClassWriter.COMPUTE_FRAMES);// ClassWriter cw = new
												// ClassWriter(cr,
												// ClassWriter.COMPUTE_MAXS |
												// ClassWriter.COMPUTE_FRAMES);
		ClassVisitor cv = new MyClassAdapter(Opcodes.ASM5, cw, cn);
		cr.accept(cv, ClassReader.EXPAND_FRAMES);

		if (!"".equals(statistic)) {
			StatisticsWriter.logInstrumentation(cn, cw.toByteArray(),
					System.nanoTime() - start_instrumentation);
		}

		// Dump instrumented bytecode if INSTRUMENTED_CLASS_PATH is set in
		// configuration file
		String s = ConfigProperties
				.getProperty(ConfigProperties.PROPERTIES.INSTRUMENTED_CLASS_PATH);
		if ((s != null) && !s.equals("")) {
			try {
				File f = new File(s + cr.getClassName().replace("/", "_")
						+ ".class");
//				System.out.println("DUMPED: "+f.getName());
				if (!f.getParentFile().exists()) {
				    f.getParentFile().mkdirs();
				}
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

		// Add class name to list of instrumented classes in the instrumentation
		// delegate
		InstrumDelegate.addInstrumentedClassName(cr.getClassName());

		// return the instrumented class
		return cw.toByteArray();
	}
}

// try {
// Method method = ClassLoader.class.getDeclaredMethod(
// "findLoadedClass", new Class[] { String.class });
// method.setAccessible(true);
//
// ClassLoader cl = ClassLoader.getSystemClassLoader();
// Object clazz = method.invoke(cl,
// "edu.tum.uc.jvm.utility.analysis.StaticAnalysis");
// if (clazz == null) {
// }
// } catch (SecurityException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// } catch (IllegalArgumentException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// } catch (IllegalAccessException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// } catch (InvocationTargetException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// } catch (NoSuchMethodException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
