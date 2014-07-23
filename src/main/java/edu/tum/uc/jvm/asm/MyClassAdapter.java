package edu.tum.uc.jvm.asm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.JSRInlinerAdapter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import edu.tum.uc.jvm.utility.ConfigProperties;
import edu.tum.uc.jvm.utility.Utility;
import edu.tum.uc.jvm.utility.analysis.StaticAnalysis;

public class MyClassAdapter extends ClassVisitor {
	private String className;
	private ClassNode cn;
	private ClassWriter classWriter;

	// public MyClassAdapter(int p_api) {
	// super(p_api);
	// }
	// public MyClassAdapter(int p_api, ClassVisitor p_cv) {
	// super(p_api, p_cv);
	// }

	public MyClassAdapter(int p_api, ClassVisitor p_cv, ClassNode p_cn) {
		super(p_api, p_cv);
		this.cn = p_cn;
		this.className = this.cn.name;
		this.classWriter = (ClassWriter) p_cv;

		try {
			Method method = ClassLoader.class.getDeclaredMethod(
					"findLoadedClass", new Class[] { String.class });
			method.setAccessible(true);

			ClassLoader cl = ClassLoader.getSystemClassLoader();
			Object clazz = method.invoke(cl,
					"edu.tum.uc.jvm.utility.analysis.StaticAnalysis");
			if (clazz == null) {
				Utility.populatePip(ConfigProperties
						.getProperty(ConfigProperties.PROPERTIES.ANALYSIS_REPORT
								.toString()));
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public MethodVisitor visitMethod(int p_access, String p_name,
			String p_desc, String p_signature, String[] p_exceptions) {
		// System.out.println("VisitMethod: "+this.className+"/"+p_name+", "+Integer.toBinaryString(p_access));
		// Determine current method node
		// if(p_name.equals("hashCode")){
		// // return cv.visitMethod(p_access, p_name, p_desc, p_signature,
		// p_exceptions);
		// return null;
		// }
		// else{

		// Find corresponding method node, contains all instructions as a list
		Iterator<?> methIt = cn.methods.iterator();
		MethodNode methNode = null, tmpNode;
		while (methIt.hasNext()) {
			tmpNode = (MethodNode) methIt.next();
			if (tmpNode.name.toLowerCase().equals(p_name.toLowerCase())
					&& tmpNode.desc.equals(p_desc)
					&& (tmpNode.access == p_access)) {
				// System.out.println("UCAPREM VISITMETH: "+this.className+", "+p_name+", "+tmpNode.name);
				// if(tmpNode.localVariables != null){
				// for(int u = 0; u < tmpNode.localVariables.size(); u++)
				// System.out.println("UCAPREM VISITMETH LOCVAR: "+this.className+", "+p_name+", "+((LocalVariableNode)tmpNode.localVariables.get(u)).name+", "+((LocalVariableNode)tmpNode.localVariables.get(u)).index);
				// }
				methNode = tmpNode;
				break;
			}
		}

		// find corresponding chops
		String k = this.className.replace("/", ".") + "." + p_name + p_desc;
		ArrayList<Properties> chopNodes = StaticAnalysis.getChop(k);
		if (chopNodes != null) {
			chopNodes.get(0);
		}
		MethodVisitor mv = cv.visitMethod(p_access, p_name, p_desc,
				p_signature, p_exceptions);
		// if(p_name.equals("hashCode")){
		// System.out.println("METH NOT INSTR: "+this.className+", "+p_name);
		// return mv;
		// }
		// else
		// if(methNode != null){
		// System.out.println("LOC: "+this.className+", "+methNode.name+", "+methNode.instructions.size());
		// }
		if ((p_access & Opcodes.ACC_NATIVE) != Opcodes.ACC_NATIVE) {
			Boolean instrumentation = true;
			String s = ConfigProperties
					.getProperty(ConfigProperties.PROPERTIES.INSTRUMENTATION
							.toString());
			if (s != null) {
				instrumentation = new Boolean(s);
			}
			if (instrumentation) {
				JSRInlinerAdapter ja = new JSRInlinerAdapter(mv, p_access,
						p_name, p_desc, p_signature, p_exceptions);
				AdviceAdapter aa = new MyAdviceAdapter(Opcodes.ASM4, ja,
						p_access, p_name, p_desc, p_signature, this.className,
						methNode);
				mv = new MyMethodVisitor(Opcodes.ASM4, aa, p_access, p_name,
						p_desc, p_signature, this.className, methNode,
						chopNodes, this.classWriter);
			} else {
				mv = new MyAdviceAdapter(Opcodes.ASM4, mv, p_access, p_name,
						p_desc, p_signature, this.className, methNode);
			}
		}
		return mv;
		// }
	}
	// public void visit(int p_version, int p_access, String p_name, String
	// p_signature, String p_superName, String[] p_interfaces){
	// cv.visit(p_version, p_access, p_name, p_signature, p_superName,
	// p_interfaces);
	// // this.className = p_name;
	// }
	// public FieldVisitor visitField(int p_access, String p_name, String
	// p_desc, String p_signature, Object p_value){
	// if(p_desc != null){
	// Type t = Type.getType(p_desc);
	// while(t.getSort() == Type.ARRAY)
	// t = t.getElementType();
	// if( ((t.getSort() == Type.OBJECT) || ((t.getSort() == Type.ARRAY) &&
	// (t.getElementType().getSort() == Type.OBJECT)))
	// && !t.getClassName().contains("java.lang.Object") &&
	// ucaPremainTransformer.checkClassExist(t.getClassName()) ){
	// p_desc = ucaPremainTransformer.myrep(p_desc);
	// }
	// }
	//
	// if(p_signature != null){
	// // p_signature = MyASM.myrep(p_signature);
	// }
	//
	//
	// return super.visitField(p_access, p_name, p_desc, p_signature, p_value);
	// }
	// public MethodVisitor visitMethod(int p_access, String p_name, String
	// p_desc, String p_signature, String[] p_exceptions){
	//
	// return cv.visitMethod(p_access, p_name, p_desc, p_signature,
	// p_exceptions);
	// }
}
