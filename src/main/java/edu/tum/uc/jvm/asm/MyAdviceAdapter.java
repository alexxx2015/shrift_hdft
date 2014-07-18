package edu.tum.uc.jvm.asm;

import java.util.Iterator;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

import edu.tum.uc.jvm.UcException;
import edu.tum.uc.jvm.UcTransformer;
import edu.tum.uc.jvm.utility.Utility;


public class MyAdviceAdapter extends AdviceAdapter {
	private String methodName;
	private String className;
	private MethodNode methNode;
	private String signature;
	private String description;
	
	private boolean isInstance;
	
	private Label tcStart, tcEnd, tcHandler;

	protected MyAdviceAdapter(int p_api, MethodVisitor p_mv, int p_access, String p_name, String p_desc, String p_signature, String p_className, MethodNode p_methNode) {
		super(p_api, p_mv, p_access, p_name, p_desc);
		
		this.methodName = p_name;
		this.className = p_className;
		this.methNode = p_methNode;
		this.signature = p_signature;
		this.description = p_desc;
		
		this.isInstance = false;
		
		if (this.methodName.equals("<init>")){
			this.isInstance = false;
		}
		else if( ((p_access & Opcodes.ACC_STATIC) != Opcodes.ACC_STATIC)
				&& ((p_access & Opcodes.ACC_ABSTRACT) != Opcodes.ACC_ABSTRACT)){
			this.isInstance = true;
		}
//		System.out.println(this.className+", "+this.methodName+", "+this.methNode.access+", "+this.isInstance);
//		if(this.className.contains("DefaultSystemMessagesProvider") && this.methodName.equals("get"))
//			this.isInstance = false;
	}
	
	protected void _onMethodEnter(){
		this.tcStart = new Label();
		this.tcEnd = new Label();
		this.tcHandler = new Label();
		
		this.mv.visitTryCatchBlock(this.tcStart, this.tcEnd, this.tcHandler, "edu/tum/uc/jvm/UcException");
		this.mv.visitLabel(this.tcStart);
	}
	
	protected void _onMethodExit(int opcode){		
		this.mv.visitLabel(this.tcEnd);
		Label end = new Label();
		this.mv.visitJumpInsn(Opcodes.GOTO, end);
		this.mv.visitLabel(this.tcHandler);
		this.mv.visitVarInsn(Opcodes.ASTORE, 1);
		this.mv.visitVarInsn(Opcodes.ALOAD, 1);
		this.mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, UcException.class.getName().replace(".", "/"), "printStackTrace", "()V", false);
		this.mv.visitLabel(end);
	}	
	
	private String getFullName(){
		return this.className+"/"+this.methodName;
	}
	
	private Label insertChecks(MethodVisitor p_mv, String p_class, String p_method){
		if(p_mv == null){
			p_mv = mv;
		}
		if(p_class == null){
			p_class = Utility.notSpecified;
		} 
		if(p_method == null){
			p_method = Utility.notSpecified;
		}
		
//		p_mv.visitLdcInsn(p_class);
//		p_mv.visitLdcInsn(p_method);
//		p_mv.visitMethodInsn(Opcodes.INVOKESTATIC, ucaUtility.class.getName(), "checkInvocationOnStack", "(Ljava/lang/String;Ljava/lang/String;)Z");
		p_mv.visitFieldInsn(Opcodes.GETSTATIC, UcTransformer.HOOKMETHOD, "ucaUsed", "Z");
		Label lab = new Label();
		p_mv.visitJumpInsn(Opcodes.IFNE, lab);			
		return lab;
	}
}

/*
 * 
	
	protected void _onMethodEnter(){
		String methodEnterStr = this.getFullName()+":"+this.description+":";
		Type[] methType = Type.getArgumentTypes(this.methodDesc);
		LocalVariableNode lvn;
		int iStart = 1, iEnd= iStart + methType.length; 
		if((this.methodAccess & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC){
			iStart--;
			iEnd--;
		}
//		if((this.methNode.exceptions != null) && (this.methNode.exceptions.size() > 0)){
//			iStart += this.methNode.exceptions.size();
//			iEnd += this.methNode.exceptions.size();
//		}
		if(iEnd > iStart){
			if((this.methNode.localVariables != null) && (this.methNode.localVariables.size() > 0)){
				Iterator locVarIt = this.methNode.localVariables.iterator();
				while(locVarIt.hasNext()){
					lvn = (LocalVariableNode)locVarIt.next();
//					System.out.println("UCAPREM: "+this.className+"."+this.methNode.name+", "+lvn.name+", "+lvn.index);
					if((lvn.index >= iStart) && (lvn.index < iEnd)){

						if((this.methodAccess & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC){
							methodEnterStr += lvn.name+"#"+lvn.index+"|";
						} else {
							methodEnterStr += lvn.name+"#"+(lvn.index-1)+"|";
						}
					}
				}
			}
			else{
				for(;iStart < iEnd; iStart++){
					methodEnterStr += "#"+iStart+"|";
				}
			}
//			for(;iStart < iEnd; iStart++){
//				try{
//					if((this.methNode.localVariables != null) && (this.methNode.localVariables.size() > 0)){
//						lvn = (LocalVariableNode)this.methNode.localVariables.get(iStart);
//						methodEnterStr += lvn.name+"#"+lvn.index+"|";
////						System.out.println(this.className+", "+this.methodName+", "+methodEnterStr+", "+this.methNode.name);
//					}else{
//						methodEnterStr += "#"+iStart+"|";
//					}
//				}catch(IndexOutOfBoundsException e){}
//			}
		}			
		
//		Label elseLab = this.insertChecks(mv, ucaHA.class.getName(), null, null);
//		mv.visitLdcInsn(this.methodAccess+":"+this.getFullName()+":"+this.className+"/"+this.methodName+":"+this.signature);
//		if((this.methodAccess & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC){
		if(this.isInstance == false){
			mv.visitLdcInsn(methodEnterStr);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, UcTransformer.HOOKMETHOD, "methodEntered", "(Ljava/lang/String;)Z", false);
		} else {
			mv.visitVarInsn(Opcodes.ALOAD, 0);
//			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Object.class.getName().replace(".", "/"), "hashCode", "()I");
			mv.visitLdcInsn(methodEnterStr);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, UcTransformer.HOOKMETHOD, "methodEntered", "(Ljava/lang/Object;Ljava/lang/String;)Z", false);
		}
		mv.visitInsn(Opcodes.POP);		
	}
protected void REM_onMethodEnter(){
		Type[] argT = Type.getArgumentTypes(this.description);
		Type retT = Type.getReturnType(this.description);
		Type retTOri = Type.getReturnType(this.description);
		
		String methodEnterStr = this.methodAccess+":"+this.getFullName()+":"+this.description+":";
		Type[] methType = Type.getArgumentTypes(this.methodDesc);
		LocalVariableNode lvn;
		int iStart = 1, iEnd= iStart + methType.length; 
		if((this.methodAccess & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC){
			iStart--;
			iEnd--;
		}
		if((this.methNode.exceptions != null) && (this.methNode.exceptions.size() > 0)){
			iStart += this.methNode.exceptions.size();
			iEnd += this.methNode.exceptions.size();
		}
		if(iEnd > iStart){
			for(;iStart < iEnd; iStart++){
				try{
					if((this.methNode.localVariables != null) && (this.methNode.localVariables.size() > 0)){
						lvn = (LocalVariableNode)this.methNode.localVariables.get(iStart);
						methodEnterStr += lvn.name+"#"+lvn.index+"|";
//						System.out.println(this.className+", "+this.methodName+", "+methodEnterStr+", "+this.methNode.name);
					}else{
						methodEnterStr += "#"+iStart+"|";
					}
				}catch(IndexOutOfBoundsException e){}
			}
		}
		
//		Label elseLab = this.insertChecks(mv, ucaHA.class.getName(), null, null);
//		mv.visitLdcInsn(this.methodAccess+":"+this.getFullName()+":"+this.className+"/"+this.methodName+":"+this.signature);
		mv.visitLdcInsn(methodEnterStr);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, UcTransformer.HOOKMETHOD, "methodEntered", "(Ljava/lang/String;)Z", false);
//		mv.visitInsn(Opcodes.POP);
		
		Label elseLab = new Label();
		mv.visitJumpInsn(Opcodes.IFNE, elseLab);			
//		Add a return frame on the stack
		if(retT.getSort() == Type.VOID){
			mv.visitInsn(Opcodes.RETURN);
		}
		else{
			int isArray = 0;
			while(retT.getSort() == Type.ARRAY){
				isArray++;
				retT = retT.getElementType();
			}
			
			if(isArray > 0){
				for(int i = 0; i < isArray; i++){
					mv.visitInsn(Opcodes.ICONST_1);
				}
				
				if(isArray > 1){
					mv.visitMultiANewArrayInsn(retTOri.toString(), isArray);
				}else{
					if(retT.getSort() == Type.OBJECT){
//						System.out.println(this.className+","+this.methodName+","+retT.toString()+", "+isArray);
						mv.visitTypeInsn(Opcodes.ANEWARRAY, retT.toString());
					}
					else{
						int retType;				
						switch(retT.getSort()){
							case Type.BOOLEAN: retType = Opcodes.T_BOOLEAN;break;
							case Type.CHAR: retType = Opcodes.T_CHAR; break;
							case Type.FLOAT: retType = Opcodes.T_FLOAT; break;
							case Type.DOUBLE: retType = Opcodes.T_DOUBLE; break;
							case Type.BYTE: retType = Opcodes.T_BYTE; break;
							case Type.SHORT: retType = Opcodes.T_SHORT; break;
							case Type.INT: retType = Opcodes.T_INT; break;
							case Type.LONG: retType = Opcodes.T_LONG; break;
							default: retType = Opcodes.T_CHAR;
						}
						mv.visitIntInsn(Opcodes.NEWARRAY, retType);
					}
				}
				mv.visitInsn(Opcodes.ARETURN);
			}else{
				if(retT.getSort() == Type.OBJECT){
//					mv.visitTypeInsn(Opcodes.NEW, "java/lang/Object");//retT.toString());
//					mv.visitInsn(Opcodes.DUP);
//					mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
//					mv.visitInsn(Opcodes.ARETURN);
					mv.visitInsn(Opcodes.ACONST_NULL);
					mv.visitInsn(Opcodes.ARETURN);
				}
				else{
					switch(retT.getSort()){
						case Type.BOOLEAN: 	
						case Type.INT: 		mv.visitInsn(Opcodes.ICONST_0);
											mv.visitInsn(Opcodes.IRETURN);
											break;
						case Type.CHAR:
						case Type.BYTE:
						case Type.SHORT: 	mv.visitIntInsn(Opcodes.BIPUSH, 0);
											mv.visitInsn(Opcodes.IRETURN);
											break;
						case Type.FLOAT: 	mv.visitInsn(Opcodes.FCONST_0);
											mv.visitInsn(Opcodes.FRETURN);
											break;
						case Type.DOUBLE: 	mv.visitInsn(Opcodes.DCONST_0);
											mv.visitInsn(Opcodes.DRETURN);
											break;
						case Type.LONG: 	mv.visitInsn(Opcodes.LCONST_0);
											mv.visitInsn(Opcodes.LRETURN);
											break;
					}
				}
			}
		}
		mv.visitLabel(elseLab);			
	}
	
	protected void REM_onMethodExit(int p_opcode){
		mv.visitLdcInsn(p_opcode+":"+this.getFullName()+":"+this.className+"/"+this.methodName+":"+this.description);	
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, UcTransformer.HOOKMETHOD, "methodExit", "(Ljava/lang/String;)V", false);
		
	}
	
	protected void VonMethodEnter(){
		String methodEnterStr = this.getFullName();//+"/"+this.methodAccess;
		Type methType = Type.getType(this.methodDesc);
		LocalVariableNode lvn;
		int iStart = 1, iEnd= iStart + methType.getArgumentTypes().length; 
		if((this.methodAccess & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC){
			iStart--;
			iEnd--;
		}
		if(iEnd > iStart){
			for(;iStart < iEnd; iStart++){
				try{
					if(this.methNode.localVariables != null){
						lvn = (LocalVariableNode)this.methNode.localVariables.get(iStart);
						methodEnterStr += "/"+lvn.index+":"+lvn.name;
					}
				}catch(IndexOutOfBoundsException e){}
			}

//			Label lab = this.insertCheck(mv, ucaHA.class.getName(), "methodEntered");
			
			mv.visitLdcInsn(methodEnterStr);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, UcTransformer.HOOKMETHOD, "methodEntered", "(Ljava/lang/String;)V", false);
//			mv.visitLabel(lab);
		}
	}
	
	protected void VonMethodExit(int p_opcode){
		String methodExitStr = this.getFullName();
		Type methType = Type.getReturnType(this.methodDesc);
		LocalVariableNode lvn;
		if(this.className.toLowerCase().contains("helloworld"))
		if(methType != null){
			methodExitStr += "/"+methType.getSize()+":"+methType.getDescriptor();
		}

//		Label lab = this.insertCheck(mv, ucaHA.class.getName(), "methodExit");
		
		mv.visitLdcInsn(methodExitStr);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, UcTransformer.HOOKMETHOD, "methodExit", "(Ljava/lang/String;)V", false);	
//		mv.visitLabel(lab);		
	}
*/
