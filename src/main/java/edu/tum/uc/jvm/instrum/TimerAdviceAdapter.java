package edu.tum.uc.jvm.instrum;

import java.io.PrintStream;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import edu.tum.uc.jvm.MyUcTransformer;
import edu.tum.uc.jvm.utility.Utility;

public class TimerAdviceAdapter extends AdviceAdapter {
    private String methodName;
    private String descriptor;
    private String className;
    private String fqName;
    private String superClassName;

    protected TimerAdviceAdapter(int p_api, MethodVisitor p_mv, int p_access, String p_name, String p_desc,
	    String p_signature, String p_className, String p_superClassName) {
	super(p_api, p_mv, p_access, p_name, p_desc);

	this.methodName = p_name;
	this.descriptor = p_desc;
	this.className = p_className;
	this.superClassName = p_superClassName;
	this.fqName = this.className.replace("/", ".") + "|" + this.methodName + this.descriptor;
    }

    protected void onMethodEnter() {
	if (shouldAddTimer()) {
	    mv.visitLdcInsn(fqName);
	    mv.visitMethodInsn(Opcodes.INVOKESTATIC, MyUcTransformer.DELEGATECLASSNAME, "startMethodTimer", "(Ljava/lang/String;)V", false);
	    /*mv.visitFieldInsn(Opcodes.GETSTATIC, Type.getInternalName(System.class), "out", "Ljava/io/PrintStream;");
	    mv.visitLdcInsn("STARTED " + Utility.getThreadId() + "|" + fqName);
	    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(PrintStream.class), "println", "(Ljava/lang/String;)V", false);*/
	}
    }

    protected void onMethodExit(int opcode) {
	if (shouldAddTimer()) {
	    mv.visitLdcInsn(fqName);
	    mv.visitMethodInsn(Opcodes.INVOKESTATIC, MyUcTransformer.DELEGATECLASSNAME, "stopMethodTimer", "(Ljava/lang/String;)V", false);
	    /*mv.visitFieldInsn(Opcodes.GETSTATIC, Type.getInternalName(System.class), "out", "Ljava/io/PrintStream;");
	    mv.visitLdcInsn("STOPPED " + Utility.getThreadId() + "|" + fqName);
	    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(PrintStream.class), "println", "(Ljava/lang/String;)V", false);*/
	}
	
	if (this.methodName.equals("main")) {
	    mv.visitMethodInsn(Opcodes.INVOKESTATIC, MyUcTransformer.DELEGATECLASSNAME, "dumpStatistics", "()V", false);
	}
    }
    
    private boolean shouldAddTimer() {
	return (this.superClassName.contains("Servlet") && (this.methodName.equals("doPost") || this.methodName.equals("doGet")))
		|| (this.className.contains("JZip") && this.methodName.equals("start"));
    }
}
