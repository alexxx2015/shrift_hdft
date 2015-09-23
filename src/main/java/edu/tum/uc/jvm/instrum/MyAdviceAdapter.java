package edu.tum.uc.jvm.instrum;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

import edu.tum.uc.jvm.MyUcTransformer;
import edu.tum.uc.jvm.UcTransformer;
import edu.tum.uc.jvm.utility.ConfigProperties;

public class MyAdviceAdapter extends AdviceAdapter {
    private String methodName;
    private String descriptor;
    private String className;
    private String fqName;

    protected MyAdviceAdapter(int p_api, MethodVisitor p_mv, int p_access, String p_name, String p_desc,
	    String p_signature, String p_className) {
	super(p_api, p_mv, p_access, p_name, p_desc);

	this.methodName = p_name;
	this.descriptor = p_desc;
	this.className = p_className;
	this.fqName = this.className.replace("/", ".") + "|" + this.methodName + this.descriptor;
    }

    protected void onMethodExit(int opcode) {
	if (this.methodName.equals("main")) {
	    mv.visitLdcInsn(fqName);
	    mv.visitMethodInsn(Opcodes.INVOKESTATIC, MyUcTransformer.DELEGATECLASSNAME, "mainMethodReturned",
		    "(Ljava/lang/String;)V", false);
	}
    }
}
