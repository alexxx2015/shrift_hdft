package edu.tum.uc.jvm.asm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

import edu.tum.uc.jvm.UcTransformer;
import edu.tum.uc.jvm.utility.ConfigProperties;
import edu.tum.uc.jvm.utility.Mnemonic;
import edu.tum.uc.jvm.utility.Utility;
import edu.tum.uc.jvm.utility.analysis.CreationSite;
import edu.tum.uc.jvm.utility.analysis.SinkSource;
import edu.tum.uc.jvm.utility.analysis.StaticAnalysis;

public class MyMethodVisitor extends MethodVisitor {
	private String methodName;
	private String className;
	private MethodNode methNode;
	private String description;
	private ArrayList<Properties> chopNodes;
	private ClassWriter cv;

	protected MyMethodVisitor(int p_api, MethodVisitor p_mv, int p_access,
			String p_name, String p_desc, String p_signature,
			String p_className, MethodNode p_methNode,
			ArrayList<Properties> p_chopNodes, ClassWriter cv) {
		super(p_api, p_mv);

		this.methodName = p_name;
		this.className = p_className;
		this.methNode = p_methNode;
		this.description = p_desc;
		this.cv = cv;

		this.chopNodes = p_chopNodes;

		if (((this.methNode.access & Opcodes.ACC_STATIC) != Opcodes.ACC_STATIC)
				&& ((this.methNode.access & Opcodes.ACC_ABSTRACT) != Opcodes.ACC_ABSTRACT)) {
		}

		// System.out.println("METHODVISITOR: "+this.className+", "+this.methodName+", "+this.methNode.access+", "+this.isInstance+", "+this.methNode.name);
		// if(this.className.contains("DefaultSystemMessagesProvider") &&
		// this.methodName.equals("get"))
		// this.isInstance = false;
	}

	// Return full name of a method, i.e. classname + methodname
	private String getFullName() {
		return this.className.replace("/", ".") + "." + this.methodName;
	}

	// Return the local variable node of a method by index
	private LocalVariableNode getLocVarByIdx(int p_indx) {
		LocalVariableNode lvn = null;
		Iterator<?> locVarIt = this.methNode.localVariables.iterator();
		while (locVarIt.hasNext()) {
			lvn = (LocalVariableNode) locVarIt.next();
			// System.out.println("UCAPT GLVBI: "+this.methodName+", "+this.methNode.name+", "+lvn.name+", "+lvn.index);
			if (lvn.index == p_indx) {
				break;
			}
		}
		return lvn;
	}

	// Returns true if label is within a chop
	private boolean checkChopNode(Label label) {
		boolean _return = false;
		if ((this.chopNodes != null) && (this.chopNodes.size() > 0)) {
			Iterator<Properties> it = this.chopNodes.iterator();
			while (it.hasNext()) {
				Properties p = it.next();
				int offset = label.getOffset();
				String byteCodeIndex = p.getProperty("byteCodeIndex");
				if ((byteCodeIndex != null)
						&& (Integer.parseInt(byteCodeIndex) == offset)) {
					_return = true;
				}
			}
		}
		return _return;
	}

	private List<CreationSite> checkContextNode(Label label) {
		List<CreationSite> _return = StaticAnalysis
				.getCreationSiteByLabel(this.className.replace("/", ".") + "."
						+ this.methodName, label);
		return _return;
	}

	public void visitVarInsn(int p_opcode, int p_var) {
		Label label = this.getCurrentLabel();
		if (this.checkChopNode(label)) {

			String varInsStr = this.getFullName();
			// System.out.println("UCAPT LOADVAR 1: "+p_var+", "+p_opcode);
			LocalVariableNode lvn = this.getLocVarByIdx(p_var);
			if (lvn != null) {
				varInsStr += "/" + lvn.name;
			} else {
				varInsStr += "/" + p_var;
			}
			// System.out.println("UCAPT LOADVAR 2: "+varInsStr);
			// Label lab = this.insertChecks(mv, Disassembler.class.getName(),
			// null, null);

			if ((p_opcode >= Opcodes.ILOAD) && (p_opcode <= Opcodes.ALOAD)) {
				mv.visitLdcInsn(varInsStr + ":" + p_opcode);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "loadVar",
						"(Ljava/lang/String;)V", false);
			} else if ((p_opcode >= Opcodes.ISTORE)
					&& (p_opcode <= Opcodes.ASTORE)) {
				mv.visitLdcInsn(varInsStr + ":" + p_opcode);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "storeVar",
						"(Ljava/lang/String;)V", false);
			}
			// mv.visitLabel(lab);

		}
		mv.visitVarInsn(p_opcode, p_var);
	}

	public void visitInsn(int p_opcode) {
		Label label = this.getCurrentLabel();
		if (this.checkChopNode(label)) {
			String ucaHAStr = this.getFullName();
			// Label lab = this.insertChecks(mv, Disassembler.class.getName(),
			// null, null);

			if ((p_opcode >= Opcodes.ACONST_NULL)
					&& (p_opcode <= Opcodes.SIPUSH)) {
				ucaHAStr += "/" + Mnemonic.OPCODE[p_opcode];
				mv.visitLdcInsn(ucaHAStr + ":" + p_opcode);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "constLoad",
						"(Ljava/lang/String;)V", false);
			} else if (p_opcode == Opcodes.POP) {
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "popInstr", "()V", false);
			} else if (((p_opcode >= Opcodes.IADD) && (p_opcode <= Opcodes.DREM))
					|| ((p_opcode >= Opcodes.ISHL) && (p_opcode <= Opcodes.LXOR))) {
				mv.visitLdcInsn(ucaHAStr + ":" + p_opcode);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "arithInstr",
						"(Ljava/lang/String;)V", false);
			} else if (p_opcode == Opcodes.ARRAYLENGTH) {
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "lengthArray", "()V", false);
			} else if ((p_opcode >= Opcodes.IALOAD)
					&& (p_opcode <= Opcodes.SALOAD)) {
				// ucaHAStr += "/"+Mnemonic.OPCODE[p_opcode];
				mv.visitLdcInsn(ucaHAStr + ":" + p_opcode);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "loadArrayVar",
						"(Ljava/lang/String;)V", false);
			} else if ((p_opcode >= Opcodes.IASTORE)
					&& (p_opcode <= Opcodes.SASTORE)) {
				// ucaHAStr += "/"+Mnemonic.OPCODE[p_opcode];
				mv.visitLdcInsn(ucaHAStr + ":" + p_opcode);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "storeArrayVar",
						"(Ljava/lang/String;)V", false);
			} else if (p_opcode == Opcodes.SWAP) {
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "swapInstr", "()V", false);
			} else if (p_opcode == Opcodes.DUP) {
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "dupInstr", "()V", false);
			} else if (p_opcode == Opcodes.DUP_X1) {
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "dupX1Instr", "()V", false);
			} else if (p_opcode == Opcodes.DUP_X2) {
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "dupX2Instr", "()V", false);
			} else if (p_opcode == Opcodes.DUP2) {
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "dup2Instr", "()V", false);
			} else if (p_opcode == Opcodes.DUP2_X1) {
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "dup2X1Instr", "()V", false);
			} else if (p_opcode == Opcodes.DUP2_X2) {
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "dup2X2Instr", "()V", false);
			}
			// mv.visitLabel(lab);
		}

		mv.visitInsn(p_opcode);
	}

	public void visitFieldInsn(int p_opcode, String p_owner, String p_name,
			String p_desc) {
		Label label = this.getCurrentLabel();
		if (this.checkChopNode(label)) {
			String ldcInsn = p_owner + "/" + p_name + ":" + p_desc;
			switch (p_opcode) {
			case Opcodes.GETFIELD:
				mv.visitLdcInsn(ldcInsn);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "getField",
						"(Ljava/lang/String;)V", false);
				break;
			case Opcodes.GETSTATIC:
				mv.visitLdcInsn(ldcInsn);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "getStaticField",
						"(Ljava/lang/String;)V", false);
				break;
			case Opcodes.PUTFIELD:
				mv.visitLdcInsn(ldcInsn);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "putField",
						"(Ljava/lang/String;)V", false);
				break;
			case Opcodes.PUTSTATIC:
				mv.visitLdcInsn(ldcInsn);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "putStaticField",
						"(Ljava/lang/String;)V", false);
				break;
			default:
				mv.visitInsn(Opcodes.POP);
			}
			// mv.visitLabel(lab);
		}

		mv.visitFieldInsn(p_opcode, p_owner, p_name, p_desc);
	}

	public void visitJumpInsn(int p_opcode, Label p_label) {
		Label label = this.getCurrentLabel();
		if (this.checkChopNode(label)) {
			// InsnList il = this.methNode.instructions;
			// ListIterator<AbstractInsnNode> ilIt = il.iterator();
			// while(ilIt.hasNext()){
			// AbstractInsnNode ain = ilIt.next();
			// System.out.println(ain.getClass().getName());
			// }

			if (((p_opcode >= Opcodes.IFEQ) && (p_opcode <= Opcodes.IF_ACMPNE))
					|| (p_opcode == Opcodes.IFNULL)
					|| (p_opcode == Opcodes.IFNONNULL)) {

				String jumpStr = this.getFullName() + "/"
						+ Mnemonic.OPCODE[p_opcode] + ":" + p_opcode;
				try {
					jumpStr += ":" + p_label.getOffset();
				} catch (Exception e) {
				}

				mv.visitLdcInsn(jumpStr);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "ifInstr",
						"(Ljava/lang/String;)V", false);
			}
		}

		mv.visitJumpInsn(p_opcode, p_label);
	}

	public void visitIntInsn(int p_opcode, int p_operand) {
		Label label = this.getCurrentLabel();
		if (this.checkChopNode(label)) {
			// Label lab = this.insertChecks(mv, Disassembler.class.getName(),
			// null, null);
			String ucaHAStr;
			if (p_opcode == Opcodes.NEWARRAY) {
				ucaHAStr = this.getFullName() + ":" + p_operand + ":"
						+ p_opcode;
				mv.visitLdcInsn(ucaHAStr);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "newArray",
						"(Ljava/lang/String;)V", false);
			} else if ((p_opcode >= Opcodes.ACONST_NULL)
					&& (p_opcode <= Opcodes.SIPUSH)) {
				ucaHAStr = this.getFullName() + "/" + Mnemonic.OPCODE[p_opcode]
						+ "_" + p_operand + ":" + p_opcode;
				mv.visitLdcInsn(ucaHAStr);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "constLoad",
						"(Ljava/lang/String;)V", false);
			}
			// mv.visitLabel(lab);
		}
		mv.visitIntInsn(p_opcode, p_operand);
	}

	public void visitTypeInsn(int p_opcode, String p_type) {
		Label label = this.getCurrentLabel();
		if (this.checkChopNode(label)) {
			// Label lab = this.insertChecks(mv, Disassembler.class.getName(),
			// null, null);
			String ucaHAStr = "";
			if (p_opcode == Opcodes.ANEWARRAY) {
				ucaHAStr = this.getFullName() + ":" + p_type + ":" + p_opcode;
				mv.visitLdcInsn(ucaHAStr);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "newArray",
						"(Ljava/lang/String;)V", false);
			} else if (p_opcode == Opcodes.NEW) {
				ucaHAStr = this.getFullName() + ":" + p_type + ":" + p_opcode;
				mv.visitLdcInsn(ucaHAStr);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "newInstr",
						"(Ljava/lang/String;)V", false);
			}
			// mv.visitLabel(lab);
		}
		mv.visitTypeInsn(p_opcode, p_type);

		if (p_opcode == Opcodes.NEW) {
			List<CreationSite> creationSite = checkContextNode(label);
			if (creationSite.size() > 0) {
				for (int i = 0; i < creationSite.size(); i++) {
					CreationSite cs = creationSite.get(i);
					cs.setType(p_type.replace("/", "."));
					mv.visitLdcInsn(cs.getId() + ":" + cs.getLocation() + ":"
							+ cs.getOffset() + ":" + p_type);
					mv.visitMethodInsn(Opcodes.INVOKESTATIC,
							UcTransformer.HOOKMETHOD, "pushCreationSiteScope",
							"(Ljava/lang/String;)V", false);
				}
			}
		}
	}

	public void visitMultiANewArrayInsn(String p_desc, int p_dim) {
		Label label = this.getCurrentLabel();
		if (this.checkChopNode(label)) {
			// Label lab = this.insertChecks(mv, Disassembler.class.getName(),
			// null, null);

			String ucaHAStr = this.getFullName() + "::"
					+ Opcodes.MULTIANEWARRAY;
			mv.visitLdcInsn(ucaHAStr);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, UcTransformer.HOOKMETHOD,
					"newArray", "(Ljava/lang/String;)V", false);
			// mv.visitLabel(lab);
		}

		mv.visitMultiANewArrayInsn(p_desc, p_dim);
	}

	public void visitLdcInsn(Object cst) {
		Label label = this.getCurrentLabel();
		if (this.checkChopNode(label)) {

			String ucaHAStr = this.getFullName() + ":"
					+ cst.toString().replace(":", "&#58;");
			// Label lab = this.insertChecks(mv, Disassembler.class.getName(),
			// null, null);
			mv.visitLdcInsn(ucaHAStr);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, UcTransformer.HOOKMETHOD,
					"ldcConstLoad", "(Ljava/lang/String;)V", false);
			// mv.visitLabel(lab);
			// mv.visitLdcInsn(cst);
		}
		mv.visitLdcInsn(cst);
	}

	public void visitMethodInsn(int p_opcode, String p_owner, String p_name,
			String p_desc) {
		// Skip all constructor invocation
		if (p_name.equals("<init>")) {
			mv.visitMethodInsn(p_opcode, p_owner, p_name, p_desc, false);
			
			Label lab = this.getCurrentLabel();
			List<CreationSite> creationSites = StaticAnalysis
					.getCreationSiteByLocation(this.className.replace("/", ".")
							+ "." + this.methodName, lab.getOffset(), p_owner.replace("/", "."));
			if (creationSites.size() > 0) {
				CreationSite cs = creationSites.get(0);
				mv.visitInsn(Opcodes.DUP);
				mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Object");
				mv.visitLdcInsn(cs.getId() + ":" + cs.getLocation() + ":"
						+ cs.getOffset() + ":" + p_owner);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, UcTransformer.HOOKMETHOD, "popCreationSiteScope", "(Ljava/lang/Object;Ljava/lang/String;)V", false);
			} 
			return;
		}

		String e = ConfigProperties
				.getProperty(ConfigProperties.PROPERTIES.ENFORCEMENT.toString());
		boolean enforcement = new Boolean(e);

		Label label = this.getCurrentLabel();
		String invokerFQN = this.className.replace("/", ".") + "."
				+ this.methodName + this.description;
		// Check if method invocation belongs to the set of sinks or sources
		List<SinkSource> sors = StaticAnalysis.getType(invokerFQN,
				label.getOffset());
		
		//System.out.println("METHODVISITOR: "+invokerFQN+", "+label.getOffset()+", "+sors.size()+", "+p_owner+"."+p_name);
		
		if (sors.size() == 0) {
			if (this.checkChopNode(label)) {
				// Label lab = this.insertChecks(mv,
				// Disassembler.class.getName(), null, null);
				mv.visitLdcInsn(invokerFQN);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "logMethodInvoked",
						"(Ljava/lang/String;)V", false);
			}

			boolean isIntf = false;
			if (p_opcode == Opcodes.INVOKEINTERFACE) {
				isIntf = true;
			}
			mv.visitMethodInsn(p_opcode, p_owner, p_name, p_desc, isIntf);
			return;
		}
		SinkSource sinkSource= null;
		Iterator<SinkSource> it = sors.iterator();
		while (it.hasNext()) {
			sinkSource = it.next();
		}

		final String delim = UcTransformer.STRDELIM;
		invokerFQN = this.getFullName() + delim + this.description + delim
				+ p_owner.replace("/", ".") + "." + p_name + delim + p_desc
				+ delim + label.getOffset() + delim + p_opcode + delim
				+ sinkSource.getId() + delim + sinkSource.getContextAsString();

		if (p_opcode != Opcodes.INVOKESTATIC) {
			String d_desc = Utility.createHelperMethod(p_opcode, p_owner,	p_name, p_desc, cv, this.className, sors);
			Boolean b = new Boolean(
					ConfigProperties
							.getProperty(ConfigProperties.PROPERTIES.TIMER_T2
									.toString()));
			if (b) {
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "timerT2Start", "()V", false);
			}
			
			mv.visitLdcInsn(invokerFQN);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, this.className.replace(".", "/"), p_name, d_desc, false);

//			mv.visitMethodInsn(Opcodes.INVOKESTATIC, UcTransformer.HOOKMETHOD,
//					"methodInvoked", "(Ljava/lang/String;)Z", false);
//			mv.visitInsn(Opcodes.POP);
//			mv.visitMethodInsn(p_opcode, p_owner, p_name, p_desc, false);
			if (b) {
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "timerT2Stop", "()V", false);
			}
			
		} else {
			mv.visitLdcInsn(invokerFQN);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, UcTransformer.HOOKMETHOD,
					"methodInvoked", "(Ljava/lang/String;)Z", false);

			if (enforcement) {
				// mv.visitMethodInsn(p_opcode, p_owner, p_name, p_desc, false);
				// if(true)
				// return;

				Label elseLab = new Label();
				mv.visitJumpInsn(Opcodes.IFNE, elseLab);

				Type[] argT = Type.getArgumentTypes(p_desc);
				Type retT = Type.getReturnType(p_desc);
				if (argT.length > 0) {
					for (int k = 0; k < argT.length; k++) {
						// System.out.println(p_owner+", "+p_name+", "+argT.length);
						mv.visitInsn(Opcodes.POP);
					}
				}

				// Remove a reference frame if method invocation is type of
				// INVOKEVIRTUAL
				if (p_opcode != Opcodes.INVOKESTATIC) {
					mv.visitInsn(Opcodes.POP);
				}

				// Add a return frame on the stack
				if (retT.getSort() != Type.VOID) {
					int isArray = 0;

					while (retT.getSort() == Type.ARRAY) {
						isArray++;
						retT = retT.getElementType();
					}

					if (isArray > 0) {
						if (retT.getSort() == Type.OBJECT) {
							// mv.visitTypeInsn(Opcodes.ANEWARRAY,
							// retT.toString());
							mv.visitInsn(Opcodes.ACONST_NULL);
							mv.visitTypeInsn(Opcodes.CHECKCAST,
									"[Ljava/lang/Object;");
						} else {
							int retType;
							switch (retT.getSort()) {
							case Type.BOOLEAN:
								retType = Opcodes.T_BOOLEAN;
								break;
							case Type.CHAR:
								retType = Opcodes.T_CHAR;
								break;
							case Type.FLOAT:
								retType = Opcodes.T_FLOAT;
								break;
							case Type.DOUBLE:
								retType = Opcodes.T_DOUBLE;
								break;
							case Type.BYTE:
								retType = Opcodes.T_BYTE;
								break;
							case Type.SHORT:
								retType = Opcodes.T_SHORT;
								break;
							case Type.INT:
								retType = Opcodes.T_INT;
								break;
							case Type.LONG:
								retType = Opcodes.T_LONG;
								break;
							default:
								retType = Opcodes.T_CHAR;
							}
							mv.visitIntInsn(Opcodes.NEWARRAY, retType);
						}
					} else {
						if (retT.getSort() == Type.OBJECT) {
							Utility.createNewInstruction(mv,
									retT.getClassName());
						} else {
							switch (retT.getSort()) {
							case Type.BOOLEAN:
								mv.visitInsn(Opcodes.ICONST_0);
								break;
							case Type.CHAR:
								mv.visitIntInsn(Opcodes.BIPUSH, 0);
								break;
							case Type.FLOAT:
								mv.visitLdcInsn(1.0f);
								break;
							case Type.DOUBLE:
								mv.visitLdcInsn(1.0d);
								break;
							case Type.BYTE:
								mv.visitInsn(Opcodes.ICONST_0);
								break;
							case Type.SHORT:
								mv.visitIntInsn(Opcodes.SIPUSH, 0);
								break;
							case Type.INT:
								mv.visitIntInsn(Opcodes.SIPUSH, 0);
								break;
							case Type.LONG:
								mv.visitLdcInsn(1l);
								break;
							}
						}
					}
				}
				// mv.visitMethodInsn(p_opcode, p_owner, p_name, p_desc,false);

				Label endLab = new Label();
				mv.visitJumpInsn(Opcodes.GOTO, endLab);
				mv.visitLabel(elseLab);

				mv.visitMethodInsn(p_opcode, p_owner, p_name, p_desc, false);
				mv.visitLdcInsn(invokerFQN);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "methodExited",
						"(Ljava/lang/String;)V", false);

				// Maybe some return values
				// this.insertChecks(mv, Disassembler.class.getName(), null,
				// endLab);
				// if(this.isInstance == false){
				// mv.visitLdcInsn(this.getFullName()+":"+p_owner+"/"+p_name+":"+p_desc+":"+p_opcode);
				// mv.visitMethodInsn(Opcodes.INVOKESTATIC,
				// UcTransformer.HOOKMETHOD,
				// "methodExited", "(Ljava/lang/String;)V", false);
				// } else {
				// mv.visitVarInsn(Opcodes.ALOAD, 0);
				// // mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
				// Object.class.getName().replace(".", "/"), "hashCode", "()I");
				// mv.visitLdcInsn(this.getFullName()+":"+p_owner+"/"+p_name+":"+p_desc+":"+p_opcode);
				// mv.visitMethodInsn(Opcodes.INVOKESTATIC,
				// UcTransformer.HOOKMETHOD,
				// "methodExited", "(Ljava/lang/Object;Ljava/lang/String;)V",
				// false);
				// }
				mv.visitLabel(endLab);
			} else {
				mv.visitInsn(Opcodes.POP);
				mv.visitMethodInsn(p_opcode, p_owner, p_name, p_desc, false);

				mv.visitLdcInsn(invokerFQN);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "methodExited",
						"(Ljava/lang/String;)V", false);
			}
		}
		// mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
		// "Ljava/io/PrintStream;");
		// mv.visitLdcInsn("ADDED: "+invokerFQN);
		// mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
		// "println", "(Ljava/lang/String;)V",false);
	}
}

// //Return the local variable node of a method by index
// private LocalVariableNode getLocVarByIdx(int p_indx){
// LocalVariableNode lvn = null;
// Iterator<?> locVarIt = this.methNode.localVariables.iterator();
// while(locVarIt.hasNext()){
// lvn = (LocalVariableNode)locVarIt.next();
// //
// System.out.println("UCAPT GLVBI: "+this.methodName+", "+this.methNode.name+", "+lvn.name+", "+lvn.index);
// if(lvn.index == p_indx){
// break;
// }
// }
// return lvn;
// }
//
// //Return the local variable node of a method by index
// private LocalVariableNode getLocVarByName(String p_name){
// LocalVariableNode lvn = null;
// Iterator<?> locVarIt = this.methNode.localVariables.iterator();
// while(locVarIt.hasNext()){
// lvn = (LocalVariableNode)locVarIt.next();
// if(lvn.name.equals(p_name)){
// break;
// }
// }
// return lvn;
// }
//
// private Label insertChecks(MethodVisitor p_mv, String p_class, String
// p_method, Label p_lab){
// if(p_mv == null){
// p_mv = mv;
// }
// if(p_class == null){
// p_class = Utility.notSpecified;
// }
// if(p_method == null){
// p_method = Utility.notSpecified;
// }
// if(p_lab == null){
// p_lab = new Label();
// }
// // p_mv.visitLdcInsn(p_class);
// // p_mv.visitLdcInsn(p_method);
// // p_mv.visitMethodInsn(Opcodes.INVOKESTATIC, ucaUtility.class.getName(),
// "checkInvocationOnStack", "(Ljava/lang/String;Ljava/lang/String;)Z");
// // p_mv.visitFieldInsn(Opcodes.GETSTATIC, Disassembler.class.getName(),
// "ucaUsed", "Z");
//
// p_mv.visitMethodInsn(Opcodes.INVOKESTATIC, UcTransformer.HOOKMETHOD,
// "getUcaUsed", "()Z");
// p_mv.visitJumpInsn(Opcodes.IFNE, p_lab);
// return p_lab;
// }
// public void visitLocalVariable(String p_name, String p_desc, String
// p_signature, Label p_start, Label p_end, int p_index){
// if(this.className.toLowerCase().contains("helloworld")){
// System.out.println("LV "+this.methodName+", "+p_name+", "+p_desc+", "+p_signature+", "+p_start.toString()+", "+p_end.toString()+", "+p_index);
// }
// }