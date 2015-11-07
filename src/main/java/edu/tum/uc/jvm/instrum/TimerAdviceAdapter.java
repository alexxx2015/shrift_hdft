package edu.tum.uc.jvm.instrum;

import java.io.PrintStream;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import edu.tum.uc.jvm.MyUcTransformer;
import edu.tum.uc.jvm.utility.Utility;

/**
 * This class is responsible to insert delegate calls for starting and stopping method timers based on the method name.
 * 
 * @author vladi
 *
 */
public class TimerAdviceAdapter extends AdviceAdapter {

    /**
     * The method name.
     */
    private String methodName;
    /**
     * The name of the class this method belongs to.
     */
    private String className;
    /**
     * The descriptor of the method.
     */
    private String descriptor;
    /**
     * The fully qualified name of this method consisting of the class and method names and the descriptor.
     */
    private String fqName;
    /**
     * The name of the superclass of this method's class.
     */
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

    /**
     * Called at the beginning of the method or after super class class call in the constructor. Inserts a
     * StartMethodTimer call on the delegate if the method is eligible.
     */
    protected void onMethodEnter() {
	if (shouldAddTimer()) {
	    mv.visitLdcInsn(fqName);
	    mv.visitMethodInsn(Opcodes.INVOKESTATIC, MyUcTransformer.DELEGATECLASS, "startMethodTimer",
		    "(Ljava/lang/String;)V", false);
	}
    }

    /**
     * Called before explicit exit from the method using either return or throw. Inserts a delegate call of
     * DumpStatistics if the method is called "main". Inserts a StopMethodTimer call on the delegate if the method is
     * eligible.
     * 
     * @param opcode
     *            A return opcode like IRETURN.
     */
    protected void onMethodExit(int opcode) {
	if (shouldAddTimer()) {
	    mv.visitLdcInsn(fqName);
	    mv.visitMethodInsn(Opcodes.INVOKESTATIC, MyUcTransformer.DELEGATECLASS, "stopMethodTimer",
		    "(Ljava/lang/String;)V", false);
	}

	if (this.methodName.equals("main")) {
	    mv.visitMethodInsn(Opcodes.INVOKESTATIC, MyUcTransformer.DELEGATECLASS, "dumpStatistics", "()V", false);
	}
    }

    /**
     * Checks whether this method is Servlet.doGet(), Servlet.doPost() or JZip.start().
     * 
     * @return A boolean value indicating that a timer should be added.
     */
    private boolean shouldAddTimer() {
	return (this.superClassName.contains("Servlet") && (this.methodName.equals("doPost") || this.methodName
		.equals("doGet")))
		|| (this.className.contains("JZip") && this.methodName.equals("start"));
    }
}
