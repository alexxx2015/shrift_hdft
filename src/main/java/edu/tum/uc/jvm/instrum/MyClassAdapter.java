package edu.tum.uc.jvm.instrum;

import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.JSRInlinerAdapter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import edu.tum.uc.jvm.utility.ConfigProperties;
import edu.tum.uc.jvm.utility.analysis.Flow.Chop;
import edu.tum.uc.jvm.utility.analysis.StaticAnalysis;

public class MyClassAdapter extends ClassVisitor {
	private String className;
	private ClassNode classNode;
	private ClassWriter classWriter;

	public MyClassAdapter(int p_api, ClassVisitor p_cv, ClassNode p_cn) {
		super(p_api, p_cv);
		this.classNode = p_cn;
		this.className = this.classNode.name;
		this.classWriter = (ClassWriter) p_cv;
	}
	
	@Override
	public MethodVisitor visitMethod(int p_access, String p_name,
			String p_desc, String p_signature, String[] p_exceptions) {
		
		// In the class node, find method node of same name and descriptor as the one of this method call
		Iterator<?> methodIterator = classNode.methods.iterator();
		MethodNode methodNode = null, tmpNode;
		while (methodIterator.hasNext()) {
			tmpNode = (MethodNode) methodIterator.next();
			if (tmpNode.name.toLowerCase().equals(p_name.toLowerCase())
					&& tmpNode.desc.equals(p_desc)
					&& (tmpNode.access == p_access)) {
				methodNode = tmpNode;
				break;
			}
		}

		// Get chop nodes list from analysis report
		String k = this.className.replace("/", ".") + "." + p_name + p_desc;
		List<Chop> chopNodes = StaticAnalysis.getChop(k);
		if(chopNodes.size() > 0){
			chopNodes.get(0);
		}

		// Forward the method to next ClassVisitor in chain
		MethodVisitor mv = cv.visitMethod(p_access, p_name, p_desc,
				p_signature, p_exceptions);

		// If the method is not native, switch in the instrumenting MyMethodVisitor
		// We cannot instrument native machine code
		if ((p_access & Opcodes.ACC_NATIVE) != Opcodes.ACC_NATIVE) {
			JSRInlinerAdapter ja = new JSRInlinerAdapter(mv, p_access,
					p_name, p_desc, p_signature, p_exceptions);
			// this one removes JSR instructions and inlines the referenced subroutines
			mv = new MyMethodVisitor(Opcodes.ASM4, ja, p_access, p_name,
					p_desc, p_signature, this.className, methodNode,
					chopNodes, this.classWriter);
		}
		return mv;
	}
}
