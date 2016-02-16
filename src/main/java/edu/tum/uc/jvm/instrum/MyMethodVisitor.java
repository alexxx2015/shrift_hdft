package edu.tum.uc.jvm.instrum;

import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import edu.tum.uc.jvm.MyUcTransformer;
import edu.tum.uc.jvm.utility.analysis.Flow;
import edu.tum.uc.jvm.utility.analysis.Flow.Chop;
import edu.tum.uc.jvm.utility.eval.JavaEventName;

/**
 * This class is responsible for intercepting bytecode events generated by ASM while traversing a method during
 * instrumentation. Here happens most of the instrumentation which inserts calls to <code>InstrumDelegate</code> for
 * tracking event generation and event timers.
 * 
 * @author vladi
 *
 */
public class MyMethodVisitor extends MethodVisitor {

    /**
     * The method name.
     */
    private String methodName;
    /**
     * The name of the class this method belongs to.
     */
    private String className;
    private String superName;
    /**
     * The access flags of the method.
     */
    private int accessFlags;
    /**
     * The fully qualified name of this method consisting of the class and method names and the descriptor.
     */
    private String fqName;
    /**
     * The descriptor of the method.
     */
    private String descriptor;
    /**
     * The list of chop nodes being located in this method.
     */
    private List<Chop> chopNodes;
    /**
     * The class writer being the end of the class-as-event-chain-processing.
     */
    private ClassWriter cv;

    protected MyMethodVisitor(int p_api, MethodVisitor p_mv, int p_access, String p_name, String p_desc,
	    String p_signature, String p_className, List<Chop> p_chopNodes, ClassWriter cv, String p_superName) {
	super(p_api, p_mv);

	this.methodName = p_name;
	this.className = p_className;
	this.superName = p_superName;
	this.accessFlags = p_access;

	this.descriptor = p_desc;
	this.cv = cv;

	this.fqName = this.className.replace("/", ".") + "|" + this.methodName + this.descriptor;
	this.chopNodes = p_chopNodes;
    }

    /**
     * Visits a zero operand instruction and adds one of the following instrumentations: For type conversion like I2L
     * and negation instructions like INEG a UnaryAssign delegate call is inserted. For shifting like LUSHR, binary
     * arithmetic like IMUL and binary logical instructions like XOR a BinaryAssign delegate call is inserted. For array
     * reading instructions like FALOAD a ReadArray delegate call is inserted. For array writing instructions like
     * DASTORE a WriteArray delegate call is inserted. For return instructions like IRETURN a PrepareMethodReturn
     * delegate call is inserted.
     * 
     * @param p_opcode
     *            The opcode of the instruction to be visited.
     */
    public void visitInsn(int p_opcode) {

	// get the chop node if there is one at the current bytecode offset
	Chop chopNode = checkChopNode(this.getCurrentLabel());

	// check for the chopnode to be present here and that it has the correct operation
	if (chopNode != null && chopNode.getOperation().equals(Flow.OP_ASSIGN)) {
	    if ((p_opcode >= Opcodes.I2L && p_opcode <= Opcodes.I2S)
		    || (p_opcode >= Opcodes.INEG && p_opcode <= Opcodes.DNEG)) {
		// t2t conversion, 133-147, unary
		// tneg, 116-119, unary

		// add call to start event creation timer
		mv.visitLdcInsn(JavaEventName.UNARY_ASSIGN);
		mv.visitLdcInsn(chopNode.getByteCodeIndex());
		mv.visitLdcInsn(chopNode.getOwnerMethod());
		mv.visitLdcInsn(chopNode.getLabel());
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, MyUcTransformer.DELEGATECLASSNAME, "startEventTimer",
			"(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;)V", false);

		// Create a copy of the opcode argument and box it
		Type operandType = null;
		switch (p_opcode) {
		case Opcodes.I2L:
		case Opcodes.I2B:
		case Opcodes.I2C:
		case Opcodes.I2D:
		case Opcodes.I2F:
		case Opcodes.I2S:
		case Opcodes.INEG:
		    operandType = Type.INT_TYPE;
		    mv.visitInsn(Opcodes.DUP);
		    break;
		case Opcodes.L2D:
		case Opcodes.L2F:
		case Opcodes.L2I:
		case Opcodes.LNEG:
		    operandType = Type.LONG_TYPE;
		    mv.visitInsn(Opcodes.DUP2);
		    break;
		case Opcodes.F2D:
		case Opcodes.F2I:
		case Opcodes.F2L:
		case Opcodes.FNEG:
		    operandType = Type.FLOAT_TYPE;
		    mv.visitInsn(Opcodes.DUP);
		    break;
		case Opcodes.D2F:
		case Opcodes.D2I:
		case Opcodes.D2L:
		case Opcodes.DNEG:
		    operandType = Type.DOUBLE_TYPE;
		    mv.visitInsn(Opcodes.DUP2);
		    break;
		}
		boxTopStackValue(mv, operandType);

		// Load parent object (or null if parent method is static)
		if ((accessFlags & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {
		    mv.visitInsn(Opcodes.ACONST_NULL);
		} else {
		    mv.visitVarInsn(Opcodes.ALOAD, 0);
		}

		// Load parent method name, chopnode label and invoke delegate method
		mv.visitLdcInsn(fqName);
		mv.visitLdcInsn(chopNode.getLabel());
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, MyUcTransformer.DELEGATECLASSNAME, "unaryAssign",
			"(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)V", false);

	    } else if ((p_opcode >= Opcodes.ISHL && p_opcode <= Opcodes.LUSHR)
		    || (p_opcode >= Opcodes.IADD && p_opcode <= Opcodes.DREM)
		    || (p_opcode >= Opcodes.IAND && p_opcode <= Opcodes.LXOR)) {
		// shift, 120-125, binary
		// arithmetic, 96-115, binary
		// logical, 126-131, binary

		// add call to start event creation timer
		mv.visitLdcInsn(JavaEventName.BINARY_ASSIGN);
		mv.visitLdcInsn(chopNode.getByteCodeIndex());
		mv.visitLdcInsn(chopNode.getOwnerMethod());
		mv.visitLdcInsn(chopNode.getLabel());
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, MyUcTransformer.DELEGATECLASSNAME, "startEventTimer",
			"(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;)V", false);

		// Create a copy of the two opcode arguments and box them
		switch (p_opcode) {
		case Opcodes.LSHL:
		case Opcodes.LSHR:
		case Opcodes.LUSHR:
		    visitDup3(mv);
		    boxTopStackValue(mv, Type.INT_TYPE);
		    visitSwap15(mv);
		    boxTopStackValue(mv, Type.LONG_TYPE);
		    mv.visitInsn(Opcodes.SWAP);
		    break;
		case Opcodes.ISHL:
		case Opcodes.ISHR:
		case Opcodes.IUSHR:
		case Opcodes.IADD:
		case Opcodes.ISUB:
		case Opcodes.IMUL:
		case Opcodes.IDIV:
		case Opcodes.IREM:
		case Opcodes.IAND:
		case Opcodes.IOR:
		case Opcodes.IXOR:
		    mv.visitInsn(Opcodes.DUP2);
		    boxTopStackValue(mv, Type.INT_TYPE);
		    mv.visitInsn(Opcodes.SWAP);
		    boxTopStackValue(mv, Type.INT_TYPE);
		    mv.visitInsn(Opcodes.SWAP);
		    break;
		case Opcodes.LADD:
		case Opcodes.LSUB:
		case Opcodes.LMUL:
		case Opcodes.LDIV:
		case Opcodes.LREM:
		case Opcodes.LAND:
		case Opcodes.LOR:
		case Opcodes.LXOR:
		    visitDup4(mv);
		    boxTopStackValue(mv, Type.LONG_TYPE);
		    visitSwap15(mv);
		    boxTopStackValue(mv, Type.LONG_TYPE);
		    mv.visitInsn(Opcodes.SWAP);
		    break;
		case Opcodes.FADD:
		case Opcodes.FSUB:
		case Opcodes.FMUL:
		case Opcodes.FDIV:
		case Opcodes.FREM:
		    visitDup4(mv);
		    boxTopStackValue(mv, Type.FLOAT_TYPE);
		    visitSwap15(mv);
		    boxTopStackValue(mv, Type.FLOAT_TYPE);
		    mv.visitInsn(Opcodes.SWAP);
		    break;
		case Opcodes.DADD:
		case Opcodes.DSUB:
		case Opcodes.DMUL:
		case Opcodes.DDIV:
		case Opcodes.DREM:
		    visitDup4(mv);
		    boxTopStackValue(mv, Type.DOUBLE_TYPE);
		    visitSwap15(mv);
		    boxTopStackValue(mv, Type.DOUBLE_TYPE);
		    mv.visitInsn(Opcodes.SWAP);
		    break;
		}

		// Load parent object (or null if parent method is static)
		if ((accessFlags & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {
		    mv.visitInsn(Opcodes.ACONST_NULL);
		} else {
		    mv.visitVarInsn(Opcodes.ALOAD, 0);
		}

		// Load parent method name, chopnode label and invoke delegate method
		mv.visitLdcInsn(fqName);
		mv.visitLdcInsn(chopNode.getLabel());
		mv.visitMethodInsn(
			Opcodes.INVOKESTATIC,
			MyUcTransformer.DELEGATECLASSNAME,
			"binaryAssign",
			"(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)V",
			false);
	    }
	} else if (chopNode != null && chopNode.getOperation().equals(Flow.OP_REFERENCE)) {
	    if ((p_opcode >= Opcodes.IALOAD && p_opcode <= Opcodes.SALOAD)) {
		// load from array

		// add call to start event creation timer
		mv.visitLdcInsn(JavaEventName.READ_ARRAY);
		mv.visitLdcInsn(chopNode.getByteCodeIndex());
		mv.visitLdcInsn(chopNode.getOwnerMethod());
		mv.visitLdcInsn(chopNode.getLabel());
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, MyUcTransformer.DELEGATECLASSNAME, "startEventTimer",
			"(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;)V", false);

		// Create a copy of the array and the index
		mv.visitInsn(Opcodes.DUP2);

		// Load object or value from array cell
		// Copy array + index, then load cell content
		mv.visitInsn(Opcodes.DUP2);
		mv.visitInsn(p_opcode);

		// Box primitive value if it is one
		Type arrayType = null;
		if (p_opcode != Opcodes.AALOAD) {
		    if (p_opcode == Opcodes.IALOAD) {
			arrayType = Type.INT_TYPE;
		    } else if (p_opcode == Opcodes.BALOAD) {
			arrayType = Type.BYTE_TYPE;
		    } else if (p_opcode == Opcodes.CALOAD) {
			arrayType = Type.CHAR_TYPE;
		    } else if (p_opcode == Opcodes.SALOAD) {
			arrayType = Type.SHORT_TYPE;
		    } else if (p_opcode == Opcodes.LALOAD) {
			arrayType = Type.LONG_TYPE;
		    } else if (p_opcode == Opcodes.FALOAD) {
			arrayType = Type.FLOAT_TYPE;
		    } else if (p_opcode == Opcodes.DALOAD) {
			arrayType = Type.DOUBLE_TYPE;
		    }
		    boxTopStackValue(mv, arrayType);
		}

		// Load parent object (or null if parent method is static)
		if ((accessFlags & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {
		    mv.visitInsn(Opcodes.ACONST_NULL);
		} else {
		    mv.visitVarInsn(Opcodes.ALOAD, 0);
		}

		// Load parent method name, chopnode label and invoke delegate method
		mv.visitLdcInsn(fqName);
		mv.visitLdcInsn(chopNode.getLabel());
		mv.visitMethodInsn(
			Opcodes.INVOKESTATIC,
			MyUcTransformer.DELEGATECLASSNAME,
			"readArray",
			"(Ljava/lang/Object;ILjava/lang/Object;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)V",
			false);
	    }
	} else if (chopNode != null && chopNode.getOperation().equals(Flow.OP_MODIFY)) {
	    if (p_opcode >= Opcodes.IASTORE && p_opcode <= Opcodes.SASTORE) {
		// write into array

		// add call to start event creation timer
		mv.visitLdcInsn(JavaEventName.WRITE_ARRAY);
		mv.visitLdcInsn(chopNode.getByteCodeIndex());
		mv.visitLdcInsn(chopNode.getOwnerMethod());
		mv.visitLdcInsn(chopNode.getLabel());
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, MyUcTransformer.DELEGATECLASSNAME, "startEventTimer",
			"(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;)V", false);

		// duplicate value, index, array & box value if primitive
		Type arrayType = null;
		if (p_opcode == Opcodes.AASTORE) {
		    visitDup3(mv);
		} else {
		    if (p_opcode == Opcodes.IASTORE) {
			arrayType = Type.INT_TYPE;
			visitDup3(mv);
		    } else if (p_opcode == Opcodes.BASTORE) {
			arrayType = Type.BYTE_TYPE;
			visitDup3(mv);
		    } else if (p_opcode == Opcodes.CASTORE) {
			arrayType = Type.CHAR_TYPE;
			visitDup3(mv);
		    } else if (p_opcode == Opcodes.SASTORE) {
			arrayType = Type.SHORT_TYPE;
			visitDup3(mv);
		    } else if (p_opcode == Opcodes.LASTORE) {
			arrayType = Type.LONG_TYPE;
			visitDup4(mv);
		    } else if (p_opcode == Opcodes.FASTORE) {
			arrayType = Type.FLOAT_TYPE;
			visitDup4(mv);
		    } else if (p_opcode == Opcodes.DASTORE) {
			arrayType = Type.DOUBLE_TYPE;
			visitDup4(mv);
		    }
		}
		boxTopStackValue(mv, arrayType);

		// Load parent object (or null if parent method is static)
		if ((accessFlags & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {
		    mv.visitInsn(Opcodes.ACONST_NULL);
		} else {
		    mv.visitVarInsn(Opcodes.ALOAD, 0);
		}

		// Load parent method name, chopnode label and invoke delegate method
		mv.visitLdcInsn(fqName);
		mv.visitLdcInsn(chopNode.getLabel());
		mv.visitMethodInsn(
			Opcodes.INVOKESTATIC,
			MyUcTransformer.DELEGATECLASSNAME,
			"writeArray",
			"(Ljava/lang/Object;ILjava/lang/Object;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)V",
			false);
	    }
	} else if (chopNode != null && chopNode.getOperation().equals(Flow.OP_COMPOUND)) {
	    if (p_opcode >= Opcodes.IRETURN && p_opcode <= Opcodes.ARETURN) {
		// return operations
		// handling void return not needed here (there are no chopnodes for this because of missing data
		// propagation)

		// add call to start event creation timer
		mv.visitLdcInsn(JavaEventName.PREPARE_METHOD_RETURN);
		mv.visitLdcInsn(chopNode.getByteCodeIndex());
		mv.visitLdcInsn(chopNode.getOwnerMethod());
		mv.visitLdcInsn(chopNode.getLabel());
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, MyUcTransformer.DELEGATECLASSNAME, "startEventTimer",
			"(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;)V", false);

		// duplicate top stack value (return value)
		Type retType = Type.getReturnType(descriptor);
		if (retType.getSize() == 2) {
		    mv.visitInsn(Opcodes.DUP2);
		} else {
		    mv.visitInsn(Opcodes.DUP);
		}

		boxTopStackValue(mv, retType);

		// Load parent object (or null if parent method is static)
		if ((accessFlags & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {
		    mv.visitInsn(Opcodes.ACONST_NULL);
		} else {
		    mv.visitVarInsn(Opcodes.ALOAD, 0);
		}

		// Load parent method name, chopnode label and invoke delegate method
		mv.visitLdcInsn(fqName);
		mv.visitLdcInsn(chopNode.getLabel());
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, MyUcTransformer.DELEGATECLASSNAME, "prepareMethodReturn",
			"(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)V", false);
	    }
	}
	// insert original bytecode operation
	mv.visitInsn(p_opcode);
    }

    /**
     * Visits an IINC instruction and instruments it with a BinaryAssign delegate call (incrementation can be viewed as
     * adding the value and the amount to increment it together).
     * 
     * 
     * @param p_var
     *            The index of the local variable to be incremented.
     * @param p_inc
     *            The amount to increment the local variable by.
     */
    public void visitIincInsn(int p_var, int p_inc) {
	// opcode is IINC
	// binary

	// get the chop node if there is one at the current bytecode offset
	Chop chopNode = checkChopNode(this.getCurrentLabel());

	// check for the chopnode to be present here and that it has the correct operation
	if (chopNode != null && chopNode.getOperation().equals(Flow.OP_ASSIGN)) {

	    // add call to start event creation timer
	    mv.visitLdcInsn(JavaEventName.BINARY_ASSIGN);
	    mv.visitLdcInsn(chopNode.getByteCodeIndex());
	    mv.visitLdcInsn(chopNode.getOwnerMethod());
	    mv.visitLdcInsn(chopNode.getLabel());
	    mv.visitMethodInsn(Opcodes.INVOKESTATIC, MyUcTransformer.DELEGATECLASSNAME, "startEventTimer",
		    "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;)V", false);

	    // Load opcode operands explicitly on stack and box em
	    mv.visitVarInsn(Opcodes.ILOAD, p_var);
	    boxTopStackValue(mv, Type.INT_TYPE);
	    mv.visitLdcInsn(p_inc);
	    boxTopStackValue(mv, Type.INT_TYPE);

	    // Load parent object (or null if parent method is static)
	    if ((accessFlags & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {
		mv.visitInsn(Opcodes.ACONST_NULL);
	    } else {
		mv.visitVarInsn(Opcodes.ALOAD, 0);
	    }

	    // Load parent method name, chopnode label and invoke delegate method
	    mv.visitLdcInsn(fqName);
	    mv.visitLdcInsn(chopNode.getLabel());
	    mv.visitMethodInsn(Opcodes.INVOKESTATIC, MyUcTransformer.DELEGATECLASSNAME, "binaryAssign",
		    "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)V",
		    false);
	}
	// insert original bytecode operation
	mv.visitIincInsn(p_var, p_inc);
    }

    /**
     * Visits a type instruction (one that takes an internal name of a class as parameter). The only instruction falling
     * in this category and occuring in chopnode operations is CHECKCAST, being instrumented with a UnaryAssign delegate
     * call.
     * 
     * 
     * @param p_opcode
     *            The the opcode of the type instruction to be visited.
     * @param p_type
     *            An internal name of an object or array class.
     */
    public void visitTypeInsn(int p_opcode, String p_type) {
	// get the chop node if there is one at the current bytecode offset
	Chop chopNode = checkChopNode(this.getCurrentLabel());

	// check for the chopnode to be present here and that it has the correct operation
	if (chopNode != null && chopNode.getOperation().equals(Flow.OP_ASSIGN)) {
	    if (p_opcode == Opcodes.CHECKCAST) {
		// unary

		// add call to start event creation timer
		mv.visitLdcInsn(JavaEventName.UNARY_ASSIGN);
		mv.visitLdcInsn(chopNode.getByteCodeIndex());
		mv.visitLdcInsn(chopNode.getOwnerMethod());
		mv.visitLdcInsn(chopNode.getLabel());
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, MyUcTransformer.DELEGATECLASSNAME, "startEventTimer",
			"(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;)V", false);

		// Create a copy of the opcode argument (already reference type)
		mv.visitInsn(Opcodes.DUP);

		// Load parent object (or null if parent method is static)
		if ((accessFlags & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {
		    mv.visitInsn(Opcodes.ACONST_NULL);
		} else {
		    mv.visitVarInsn(Opcodes.ALOAD, 0);
		}

		// Load parent method name, chopnode label and invoke delegate method
		mv.visitLdcInsn(fqName);
		mv.visitLdcInsn(chopNode.getLabel());
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, MyUcTransformer.DELEGATECLASSNAME, "unaryAssign",
			"(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)V", false);
	    }
	}
	// insert original bytecode operation
	mv.visitTypeInsn(p_opcode, p_type);
    }

    /**
     * Visits a field instruction (one that loads or stores the content of a field). Instructions reading a field are
     * instrumented with a ReadField delegate call while the ones writing into a field make a WriteField call after
     * instrumentation.
     * 
     * 
     * @param p_opcode
     *            The the opcode of the type instruction to be visited.
     * @param p_owner
     *            An internal name of the field owner's class.
     * @param p_name
     *            The name of the field.
     * @param p_desc
     *            The descriptor of the field.
     */
    public void visitFieldInsn(int p_opcode, String p_owner, String p_name, String p_desc) {
	// get the chop node if there is one at the current bytecode offset
	Chop chopNode = checkChopNode(this.getCurrentLabel());

	// check for the chopnode to be present here and that it has the correct operation
	if (chopNode != null && chopNode.getOperation().equals(Flow.OP_REFERENCE)
		&& (p_opcode == Opcodes.GETSTATIC || p_opcode == Opcodes.GETFIELD)) {

	    // add call to start event creation timer
	    mv.visitLdcInsn(JavaEventName.READ_FIELD);
	    mv.visitLdcInsn(chopNode.getByteCodeIndex());
	    mv.visitLdcInsn(chopNode.getOwnerMethod());
	    mv.visitLdcInsn(chopNode.getLabel());
	    mv.visitMethodInsn(Opcodes.INVOKESTATIC, MyUcTransformer.DELEGATECLASSNAME, "startEventTimer",
		    "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;)V", false);

	    // Create a copy of the opcode argument (field owner object)
	    // (or load null to fill fieldOwner parameter in delegate method)
	    if (p_opcode == Opcodes.GETSTATIC) {
		mv.visitInsn(Opcodes.ACONST_NULL);
	    } else {
		mv.visitInsn(Opcodes.DUP);
	    }

	    // Perform original opcode to get field contents
	    mv.visitFieldInsn(p_opcode, p_owner, p_name, p_desc);

	    // put a copy of the field contents underneath owner/null
	    if (Type.getType(p_desc).getSize() == 2) {
		mv.visitInsn(Opcodes.DUP2_X1);
	    } else {
		mv.visitInsn(Opcodes.DUP_X1);
	    }

	    // Wrap field content
	    boxTopStackValue(mv, Type.getType(p_desc));

	    // field owner class
	    mv.visitLdcInsn(p_owner.replace("/", "."));
	    // field name
	    mv.visitLdcInsn(p_name);

	    // Load parent object (or null if parent method is static)
	    if ((accessFlags & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {
		mv.visitInsn(Opcodes.ACONST_NULL);
	    } else {
		mv.visitVarInsn(Opcodes.ALOAD, 0);
	    }

	    // Load parent method name, chopnode label and invoke delegate method
	    mv.visitLdcInsn(fqName);
	    mv.visitLdcInsn(chopNode.getLabel());
	    mv.visitMethodInsn(Opcodes.INVOKESTATIC, MyUcTransformer.DELEGATECLASSNAME, "readField",
		    "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;"
			    + "Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)V", false);
	} else if (chopNode != null && chopNode.getOperation().equals(Flow.OP_MODIFY)
		&& (p_opcode == Opcodes.PUTSTATIC || p_opcode == Opcodes.PUTFIELD)) {

	    // add call to start event creation timer
	    mv.visitLdcInsn(JavaEventName.WRITE_FIELD);
	    mv.visitLdcInsn(chopNode.getByteCodeIndex());
	    mv.visitLdcInsn(chopNode.getOwnerMethod());
	    mv.visitLdcInsn(chopNode.getLabel());
	    mv.visitMethodInsn(Opcodes.INVOKESTATIC, MyUcTransformer.DELEGATECLASSNAME, "startEventTimer",
		    "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;)V", false);

	    // true if value has type long, double or float
	    Type valueType = Type.getType(p_desc);
	    boolean valueIsBig = valueType.getSize() == 2;

	    // Create a copy of value and the field owner object
	    // (or load null to fill fieldOwner parameter in delegate method)
	    // then box the value

	    if (p_opcode == Opcodes.PUTSTATIC) {
		// duplicate value, then push "null" underneath it
		if (valueIsBig) {
		    mv.visitInsn(Opcodes.DUP2);
		    mv.visitInsn(Opcodes.ACONST_NULL);
		    visitSwap15(mv);
		} else {
		    mv.visitInsn(Opcodes.DUP);
		    mv.visitInsn(Opcodes.ACONST_NULL);
		    mv.visitInsn(Opcodes.SWAP);
		}
	    } else {
		if (valueIsBig) {
		    visitDup3(mv);
		} else {
		    mv.visitInsn(Opcodes.DUP2);
		}
	    }
	    boxTopStackValue(mv, valueType);

	    // field owner class
	    mv.visitLdcInsn(p_owner.replace("/", "."));
	    // field name
	    mv.visitLdcInsn(p_name);

	    // Load parent object (or null if parent method is static)
	    if ((accessFlags & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {
		mv.visitInsn(Opcodes.ACONST_NULL);
	    } else {
		mv.visitVarInsn(Opcodes.ALOAD, 0);
	    }

	    // Load parent method name, chopnode label and invoke delegate method
	    mv.visitLdcInsn(fqName);
	    mv.visitLdcInsn(chopNode.getLabel());
	    mv.visitMethodInsn(Opcodes.INVOKESTATIC, MyUcTransformer.DELEGATECLASSNAME, "writeField",
		    "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;"
			    + "Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)V", false);

	    // invoke original opcode
	    mv.visitFieldInsn(p_opcode, p_owner, p_name, p_desc);
	} else {
	    // do nothing
	    mv.visitFieldInsn(p_opcode, p_owner, p_name, p_desc);
	}

    }

    /**
     * Visits a method instruction. All method calls (being on a chopnode) are replaced with a call to a wrapper method
     * which takes the original arguments and puts them into an array which is being handed to a
     * CallInstanceMethod/CallStaticMethod delegate call. Afterwards the original method is invoked, followed by an
     * InstanceMethodReturned/StaticMethodReturned delegate call.
     * 
     * @param p_opcode
     *            The opcode of the method instruction to be visited.
     * @param p_owner
     *            The internal name of the method's owner class.
     * @param p_name
     *            The method name.
     * @param p_desc
     *            The method descriptor.
     */
    public void visitMethodInsn(int p_opcode, String p_owner, String p_name, String p_desc) {
    	boolean isSuperConstructor = false;
    	if(this.superName != null && !"".equals(this.superName) && p_owner.equals(this.superName)){
    		isSuperConstructor = p_opcode == Opcodes.INVOKESPECIAL && p_name.equals("<init>");
    	}
    	//Do not instrument super-constructor invocations
    	if(isSuperConstructor){
    		mv.visitMethodInsn(p_opcode, p_owner, p_name, p_desc, p_opcode == Opcodes.INVOKEINTERFACE);
    		return;
    	}

	// get the chop node if there is one at the current bytecode offset
	Chop chopNode = checkChopNode(this.getCurrentLabel());


	boolean isConstructor = p_opcode == Opcodes.INVOKESPECIAL && p_name.equals("<init>");
	boolean isPublicInstanceMethod = p_opcode == Opcodes.INVOKEVIRTUAL;
	boolean isPrivateInstanceMethod = p_opcode == Opcodes.INVOKESPECIAL && !p_name.equals("<init>");
	boolean isInterfaceMethod = p_opcode == Opcodes.INVOKEINTERFACE;

	boolean isInstanceOrInterfaceMethod = isPublicInstanceMethod || isPrivateInstanceMethod || isInterfaceMethod;
	// check for the chopnode to be present here and that it has the correct operation
	if (chopNode != null && chopNode.getOperation().equals(Flow.OP_CALL) && chopNode.getLabel().contains(p_name)) {
	    StringBuilder desc = new StringBuilder();
	    // Generate new method signature
	    Type[] argT = Type.getArgumentTypes(p_desc);

	    // Create method descriptor of the to be invoked method
	    desc.append("(");

	    // Helper variable to track the parameter index of the LAST extra argument within the local variable table
	    // Increments appropriately when a double or long is encountered
	    int paramIndex = 0;
	    int startArgIndex = 0;

	    // If instance method, add original method caller as first argument
	    // Constructor -> leave uninitialized objects on stack
	    if (isInstanceOrInterfaceMethod) {
		desc.append("L" + p_owner + ";");
		paramIndex++;
		startArgIndex++;
	    }

	    // Add all arguments of the original methods
	    if (argT.length > 0) {
		for (Type t : argT) {
		    desc.append(t.getDescriptor());
		    paramIndex++;
		    if (Type.DOUBLE == t.getSort() || Type.LONG == t.getSort()) {
			paramIndex++;
		    }
		}
	    }

	    // Parameter for chop label
	    desc.append("Ljava/lang/String;");
	    int chopLabelIndex = paramIndex;
	    paramIndex++;

	    // Parameter for parent object
	    desc.append("Ljava/lang/Object;");
	    int parentObjectIndex = paramIndex;
	    paramIndex++;

	    // Parameter for bytecode offset
	    desc.append("I");
	    int offsetIndex = paramIndex;
	    paramIndex++;

	    desc.append(")");

	    // Return type
	    Type retT = Type.getReturnType(p_desc);
	    // Wrapped constructor returns object of constructor's class
	    if (isConstructor) {
		desc.append("L" + p_owner + ";");
	    } else if (retT != null) {
		desc.append(retT.getDescriptor());
	    }

	    Type[] myArgT = Type.getArgumentTypes(desc.toString());

	    String wrapperMethodName;
	    if (isConstructor) {
		wrapperMethodName = "newInit" + p_owner.replace("/", ""); // make it more unique :D
	    } else {
		wrapperMethodName = p_name;
	    }

	    String id = className + "." + wrapperMethodName + ":" + desc.toString();
	    if (InstrumDelegate.HelperMethods.contains(id)) {
		// skip adding method
	    } else {
		// add method to class
		InstrumDelegate.HelperMethods.add(id);

		// Create a new asm-method instance
		MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, wrapperMethodName, desc
			.toString(), null, null);
		mv.visitCode();

		int arrayIndex = paramIndex;

		// Create new Object array to fit all arguments
		mv.visitLdcInsn(argT.length);
		mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");

		// Store it after all method params
		mv.visitVarInsn(Opcodes.ASTORE, arrayIndex);

		// Load params on stack and put them into the array, converting primitive types
		int i = startArgIndex; // local variable index counter
		int j = 0; // array entry counter
		for (Type argType : argT) {
		    mv.visitVarInsn(Opcodes.ALOAD, arrayIndex);
		    mv.visitLdcInsn(j);
		    if (argType.getSort() == Type.OBJECT) {
			mv.visitVarInsn(Opcodes.ALOAD, i);
		    } else if (argType.getSort() == Type.ARRAY) {
			mv.visitVarInsn(Opcodes.ALOAD, i);
		    } else {
			if (argType.getSort() == Type.DOUBLE) {
			    mv.visitVarInsn(Opcodes.DLOAD, i);
			    i++;
			} else if (argType.getSort() == Type.FLOAT) {
			    mv.visitVarInsn(Opcodes.FLOAD, i);
			} else if (argType.getSort() == Type.LONG) {
			    mv.visitVarInsn(Opcodes.LLOAD, i);
			    i++;
			} else if (argType.getSort() == Type.INT) {
			    mv.visitVarInsn(Opcodes.ILOAD, i);
			} else if (argType.getSort() == Type.CHAR) {
			    mv.visitVarInsn(Opcodes.ILOAD, i);
			} else if (argType.getSort() == Type.BYTE) {
			    mv.visitVarInsn(Opcodes.ILOAD, i);
			} else if (argType.getSort() == Type.BOOLEAN) {
			    mv.visitVarInsn(Opcodes.ILOAD, i);
			} else if (argType.getSort() == Type.SHORT) {
			    mv.visitVarInsn(Opcodes.ILOAD, i);
			}
			boxTopStackValue(mv, argType);
		    }

		    mv.visitInsn(Opcodes.AASTORE);
		    i++;
		    j++;
		}

		// Load delegate method arguments
		mv.visitLdcInsn(fqName);
		mv.visitVarInsn(Opcodes.ALOAD, chopLabelIndex);
		mv.visitLdcInsn(p_owner.replace("/", ".") + "|" + p_name + p_desc);
		mv.visitVarInsn(Opcodes.ALOAD, arrayIndex);
		mv.visitVarInsn(Opcodes.ALOAD, parentObjectIndex);
		// Invoke delegate method
		if (isInstanceOrInterfaceMethod || isConstructor) {
		    // load caller object (if its there, it came after chop label)
		    if (isInstanceOrInterfaceMethod) {
			mv.visitVarInsn(Opcodes.ALOAD, 0);
		    } else {
			mv.visitInsn(Opcodes.ACONST_NULL);
		    }
		    mv.visitMethodInsn(
			    Opcodes.INVOKESTATIC,
			    MyUcTransformer.DELEGATECLASSNAME,
			    "instanceMethodInvoked",
			    "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V",
			    false);
		} else {
		    mv.visitMethodInsn(
			    Opcodes.INVOKESTATIC,
			    MyUcTransformer.DELEGATECLASSNAME,
			    "staticMethodInvoked",
			    "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;Ljava/lang/Object;)V",
			    false);
		}

		// Load original method arguments
		if (isInstanceOrInterfaceMethod) {
		    mv.visitVarInsn(Opcodes.ALOAD, 0);
		} else if (isConstructor) {
		    mv.visitTypeInsn(Opcodes.NEW, p_owner);
		    mv.visitInsn(Opcodes.DUP);
		}
		i = startArgIndex;
		for (Type argType : argT) {
		    if (argType.getSort() == Type.OBJECT) {
			mv.visitVarInsn(Opcodes.ALOAD, i);
		    } else if (argType.getSort() == Type.ARRAY) {
			mv.visitVarInsn(Opcodes.ALOAD, i);
		    } else if (argType.getSort() == Type.DOUBLE) {
			mv.visitVarInsn(Opcodes.DLOAD, i);
			i++;
		    } else if (argType.getSort() == Type.FLOAT) {
			mv.visitVarInsn(Opcodes.FLOAD, i);
		    } else if (argType.getSort() == Type.LONG) {
			mv.visitVarInsn(Opcodes.LLOAD, i);
			i++;
		    } else if ((argType.getSort() == Type.INT) || (argType.getSort() == Type.CHAR)
			    || (argType.getSort() == Type.BOOLEAN)) {
			mv.visitVarInsn(Opcodes.ILOAD, i);
		    }
		    i++;
		}
		// Invoke original method
		mv.visitMethodInsn(p_opcode, p_owner, p_name, p_desc, p_opcode == Opcodes.INVOKEINTERFACE);

		// add call to start event creation timer
		if (isInstanceOrInterfaceMethod || isConstructor) {
		    mv.visitLdcInsn(JavaEventName.RETURN_INSTANCE_METHOD);
		} else {
		    mv.visitLdcInsn(JavaEventName.RETURN_STATIC_METHOD);
		}
		mv.visitLdcInsn(chopNode.getByteCodeIndex());
		mv.visitLdcInsn(chopNode.getOwnerMethod());
		mv.visitLdcInsn(chopNode.getLabel());
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, MyUcTransformer.DELEGATECLASSNAME, "startEventTimer",
			"(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;)V", false);

		// Duplicate return value (or if constructor, the unintialized pointer duped before)
		// to call return event delegate method with it (and wrap it)
		// If original method returns void, push a null value

		// true if value has type long, double or float
		if (isConstructor) {
		    mv.visitInsn(Opcodes.DUP);
		    mv.visitVarInsn(Opcodes.ASTORE, paramIndex);
		    mv.visitInsn(Opcodes.ACONST_NULL);
		} else if (retT.getSort() != Type.VOID) {
		    boolean retValueIsBig = retT.getSize() == 2;
		    if (retValueIsBig) {
			mv.visitInsn(Opcodes.DUP2);
		    } else {
			mv.visitInsn(Opcodes.DUP);
		    }
		    boxTopStackValue(mv, retT);
		} else {
		    mv.visitInsn(Opcodes.ACONST_NULL);
		}

		// Load number of (original) method arguments
		mv.visitLdcInsn(argT.length);

		// Load bytecode offset of the method call
		mv.visitVarInsn(Opcodes.ILOAD, offsetIndex);

		// Load delegate method arguments
		mv.visitLdcInsn(fqName);
		mv.visitVarInsn(Opcodes.ALOAD, chopLabelIndex);
		mv.visitLdcInsn(p_owner.replace("/", ".") + "|" + p_name + p_desc);
		mv.visitVarInsn(Opcodes.ALOAD, parentObjectIndex);
		// Invoke delegate method
		if (isInstanceOrInterfaceMethod || isConstructor) {
		    if (isInstanceOrInterfaceMethod) {
			// load caller object (if its there, it came after chop label)
			mv.visitVarInsn(Opcodes.ALOAD, 0);
		    } else {
			mv.visitVarInsn(Opcodes.ALOAD, paramIndex);
		    }
		    mv.visitMethodInsn(
			    Opcodes.INVOKESTATIC,
			    MyUcTransformer.DELEGATECLASSNAME,
			    "instanceMethodReturned",
			    "(Ljava/lang/Object;IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V",
			    false);
		} else {
		    mv.visitMethodInsn(
			    Opcodes.INVOKESTATIC,
			    MyUcTransformer.DELEGATECLASSNAME,
			    "staticMethodReturned",
			    "(Ljava/lang/Object;IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V",
			    false);
		}

		// Return what original method returns
		if (isConstructor || retT.getSort() == Type.OBJECT || retT.getSort() == Type.ARRAY) {
		    mv.visitInsn(Opcodes.ARETURN);
		} else if (retT.getSort() == Type.DOUBLE) {
		    mv.visitInsn(Opcodes.DRETURN);
		} else if (retT.getSort() == Type.FLOAT) {
		    mv.visitInsn(Opcodes.FRETURN);
		} else if (retT.getSort() == Type.LONG) {
		    mv.visitInsn(Opcodes.LRETURN);
		} else if (retT.getSort() == Type.INT || retT.getSort() == Type.BOOLEAN || retT.getSort() == Type.BYTE
			|| retT.getSort() == Type.SHORT || retT.getSort() == Type.CHAR) {
		    mv.visitInsn(Opcodes.IRETURN);
		} else {
		    mv.visitInsn(Opcodes.RETURN);
		}

		// Finish
		mv.visitMaxs(myArgT.length + 2, myArgT.length + 1);
		mv.visitEnd();
	    }

	    // add call to start event creation timer
	    if (isInstanceOrInterfaceMethod || isConstructor) {
		mv.visitLdcInsn(JavaEventName.CALL_INSTANCE_METHOD);
	    } else {
		mv.visitLdcInsn(JavaEventName.CALL_STATIC_METHOD);
	    }
	    mv.visitLdcInsn(chopNode.getByteCodeIndex());
	    mv.visitLdcInsn(chopNode.getOwnerMethod());
	    mv.visitLdcInsn(chopNode.getLabel());
	    mv.visitMethodInsn(Opcodes.INVOKESTATIC, MyUcTransformer.DELEGATECLASSNAME, "startEventTimer",
		    "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;)V", false);

	    // Load chopnode label
	    mv.visitLdcInsn(chopNode.getLabel());
	    // Load parent object (or null if parent method is static)
	    if ((accessFlags & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {
		mv.visitInsn(Opcodes.ACONST_NULL);
	    } else {
		mv.visitVarInsn(Opcodes.ALOAD, 0);
	    }
	    mv.visitLdcInsn(this.getCurrentLabel().getOffset());
	    // Invoke wrapper method
	    mv.visitMethodInsn(Opcodes.INVOKESTATIC, className, wrapperMethodName, desc.toString(), false);

	    // constructor -> throw away the two uninitiaziled instances created before wrapper was called
	    if (isConstructor) {
		mv.visitInsn(Opcodes.SWAP);
		mv.visitInsn(Opcodes.POP);
		mv.visitInsn(Opcodes.SWAP);
		mv.visitInsn(Opcodes.POP);
	    }
	} else {
	    // do nothing
	    mv.visitMethodInsn(p_opcode, p_owner, p_name, p_desc, p_opcode == Opcodes.INVOKEINTERFACE);
	}
    }

    /**
     * Visits the maximum stack size and the maximum number of local variables of the method.
     * 
     * @param maxStack
     *            The maximum stack size of the method.
     * @param maxLocals
     *            The maximum number of local variables of the method.
     */
    public void visitMaxs(int maxStack, int maxLocals) {
	mv.visitMaxs(maxStack, maxLocals);
    }

    /**
     * Checks if chopnode exisits at the current location specified by the given label containing a bytecode offset and
     * returns it.
     * 
     * @param label
     *            The label with the bytecode index.
     * @return A chopnode if one is found, otherwise null
     */
    private Chop checkChopNode(Label label) {
	// iterate through chopnode list and compare bytecode offsets
	if ((this.chopNodes != null) && (this.chopNodes.size() > 0)) {
	    Iterator<Chop> it = this.chopNodes.iterator();
	    while (it.hasNext()) {
		Chop c = it.next();
		int offset = label.getOffset();
		int byteCodeIndex = c.getByteCodeIndex();
		if ((byteCodeIndex != 0) && (offset == byteCodeIndex)) {
		    return c;
		}
	    }
	}
	return null;
    }

    /**
     * Adds a call to the constructor of a primitive value boxing class like Double or Integer based on the given type
     * to the given method visitor.
     * 
     * @param p_mv A method visitor where to add the bytecode instruction.
     * @param p_valuetype The type of the value to be boxed.
     */
    private void boxTopStackValue(MethodVisitor p_mv, Type p_valuetype) {
	if (p_valuetype == null)
	    return;
	int typeType = p_valuetype.getSort();
	if (typeType == Type.DOUBLE) {
	    p_mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
	} else if (typeType == Type.FLOAT) {
	    p_mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
	} else if (typeType == Type.LONG) {
	    p_mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
	} else if (typeType == Type.INT) {
	    p_mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
	} else if (typeType == Type.CHAR) {
	    p_mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;",
		    false);
	} else if (typeType == Type.BYTE) {
	    p_mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
	} else if (typeType == Type.BOOLEAN) {
	    p_mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
	} else if (typeType == Type.SHORT) {
	    p_mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
	}
    }

    /**
     * Adds instructions to duplicate the top three stack values.
     * @param mv The method visitor where to add the bytecode instructions.
     */
    private void visitDup3(MethodVisitor mv) {
	mv.visitInsn(Opcodes.DUP2_X1);
	mv.visitInsn(Opcodes.POP2);
	mv.visitInsn(Opcodes.DUP);
	mv.visitInsn(Opcodes.DUP2_X2);
	mv.visitInsn(Opcodes.POP2);
	mv.visitInsn(Opcodes.DUP2_X1);
    }

    /**
     * Adds instructions to duplicate the top four stack values.
     * @param mv The method visitor where to add the bytecode instructions.
     */
    private void visitDup4(MethodVisitor mv) {
	visitSwap2(mv);
	mv.visitInsn(Opcodes.DUP2_X2);
	visitSwap2(mv);
	mv.visitInsn(Opcodes.DUP2_X2);
    }

    /**
     * Adds instructions to swap two double-slot values like two longs
     * @param mv The method visitor where to add the bytecode instructions.
     */
    private void visitSwap2(MethodVisitor mv) {
	mv.visitInsn(Opcodes.DUP2_X2);
	mv.visitInsn(Opcodes.POP2);
    }

    /**
     * Adds instructions to swap one single and one double slot values. The single slot value has to be on top.
     * @param mv The method visitor where to add the bytecode instructions.
     */
    private void visitSwap15(MethodVisitor mv) {
	mv.visitInsn(Opcodes.DUP_X2);
	mv.visitInsn(Opcodes.POP);
    }

}