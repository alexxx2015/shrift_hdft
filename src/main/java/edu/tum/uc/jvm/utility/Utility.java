package edu.tum.uc.jvm.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.restfb.Parameter;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.java.names.JavaName;
import de.tum.in.i22.uc.cm.datatypes.java.names.SourceSinkName;
import de.tum.in.i22.uc.cm.factories.IMessageFactory;
import de.tum.in.i22.uc.cm.factories.MessageFactoryCreator;
import edu.tum.uc.jvm.MyUcTransformer;
import edu.tum.uc.jvm.UcCommunicator;
import edu.tum.uc.jvm.UcTransformer;
import edu.tum.uc.jvm.utility.analysis.Flow;
import edu.tum.uc.jvm.utility.analysis.SinkSource;
import edu.tum.uc.jvm.utility.analysis.StaticAnalysis;
import edu.tum.uc.jvm.utility.analysis.StaticAnalysis.NODETYPE;

public class Utility {

	public final static String notSpecified = "-1";

	private static List<String[]> BLACKLIST;
	private static List<String[]> WHITELIST;

	public static List<Field> getAllFields(Object obj) {
		ArrayList<Field> _return = new ArrayList<Field>();
		Class clazz = obj.getClass();
		while (clazz != null && clazz != Object.class) {
			for (Field f : clazz.getDeclaredFields())
				_return.add(f);
			clazz = clazz.getSuperclass();
		}
		return _return;
	}

	public static void createNewInstruction(MethodVisitor mv, String clazzname) {
		try {
			Class<?> clazz = Class.forName(clazzname);
			Constructor[] constr = clazz.getConstructors();
			boolean added = false;
			if ((constr != null) && (constr.length > 0)) {
				for (Constructor<?> c : constr) {
					Class[] parameterTypes = c.getParameterTypes();
					if ((parameterTypes == null) || (parameterTypes.length == 0)) {
						mv.visitTypeInsn(Opcodes.NEW, clazzname.replace(".", "/"));// retT.toString());
						mv.visitInsn(Opcodes.DUP);
						mv.visitMethodInsn(Opcodes.INVOKESPECIAL, clazzname.replace(".", "/"), "<init>", "()V", false);
						added = true;
						break;
					}
				}
				if (!added) {
					mv.visitInsn(Opcodes.ACONST_NULL);
					mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Object");
				}
			} else {
				mv.visitInsn(Opcodes.ACONST_NULL);
				mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Object");
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static synchronized boolean checkInvocationOnStack(String p_class, String p_method) {
		boolean _return = false;

		for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
			// System.out.println("STACKTRACE:
			// "+ste.getClassName()+"/"+ste.getMethodName());

			if (ste.getClassName().toLowerCase().contains(p_class.toLowerCase())
					&& ste.getMethodName().toLowerCase().contains(p_method.toLowerCase())
					&& !ste.getClassName().equals(Utility.notSpecified)
					&& !ste.getMethodName().equals(Utility.notSpecified)) {
				_return = true;
				break;
			} else if (ste.getClassName().toLowerCase().contains(p_class.toLowerCase())
					&& !ste.getClassName().equals(Utility.notSpecified)) {
				_return = true;
				break;
			} else if (ste.getMethodName().toLowerCase().contains(p_method.toLowerCase())
					&& !ste.getMethodName().equals(Utility.notSpecified)) {
				_return = true;
				break;
			}
		}

		return _return;
	}

	static Map<String, String> METHODS = new HashMap<String, String>();

	public static final String STRDELIM = ":";

	private void printStr2File(String p_str) {
		try {
			File f = new File("/Users/ladmin/LOGTRANS.txt");
			FileWriter fo = new FileWriter(f, true);
			fo.append(p_str);
			fo.append(System.getProperty("line.separator"));
			fo.flush();
			fo.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Extracts the file descriptor from object parameter obj
	 * 
	 * @param obj
	 * @return
	 */
	public static Map<String,String> extractFileDescriptor(Object obj) {
		Map<String,String> _return = new HashMap<String,String>();
//		String fileDescriptor = "", handle = "";
		String osSystem = System.getProperty("os.name");
		try {
			if ((obj instanceof Writer) || (obj instanceof Reader)) {
				List<java.lang.reflect.Field> attrs = getAllFields(obj);
				Iterator<java.lang.reflect.Field> it = attrs.iterator();
				while (it.hasNext()) {
					java.lang.reflect.Field field = it.next();
					// Find attribute "lock" -> FileOutputStream
					if ("lock".equals(field.getName().toLowerCase())) {
						field.setAccessible(true);
						Object lock = field.get(obj);
						if (lock instanceof FileOutputStream) {
							java.util.List<java.lang.reflect.Field> attrs2 = getAllFields(lock);
							Iterator<java.lang.reflect.Field> it2 = attrs2.iterator();
							while (it2.hasNext()) {
								field = it2.next();
								// Find attribute "fd" -> FileDescriptor of lock
								if ("fd".equals(field.getName().toLowerCase())) {
									field.setAccessible(true);
									Object fd = field.get(lock);
									if (fd instanceof FileDescriptor) {
										java.util.List<java.lang.reflect.Field> attrs3 = getAllFields(fd);
										Iterator<java.lang.reflect.Field> it3 = attrs3.iterator();
										// Find attribue "int fd" -> contains
										// the file descriptor id from os
										while (it3.hasNext()) {
											field = it3.next();
											if ("fd".equals(field.getName().toLowerCase())) {
												field.setAccessible(true);
												Object fdOs = field.get(fd);
												if (fdOs instanceof Integer) {
													// System.out.println("Mirror
													// fdOs: "+fdOs);
													_return.put("fd", String.valueOf(fdOs));
//													fileDescriptor = String.valueOf(fdOs);
												}
											}
											if ("handle".equals(field.getName().toLowerCase())
													&& osSystem.toLowerCase().contains("window")) {
												field.setAccessible(true);
												Object handleOs = field.get(fd);
												if (handleOs instanceof Long) {
													_return.put("handle", String.valueOf(handleOs));
//													handle = String.valueOf(handleOs);
												}
											}
										}
									}
								}
							}
						}
						else if (lock instanceof Reader){
							_return = extractFileDescriptor(lock);
						}
					}
				}
			} else if ((obj instanceof FileOutputStream) || (obj instanceof FileInputStream)) {
				java.util.List<java.lang.reflect.Field> attrs2 = getAllFields(obj);
				Iterator<java.lang.reflect.Field> it2 = attrs2.iterator();
				while (it2.hasNext()) {
					java.lang.reflect.Field field = it2.next();
					// Find attribute "fd" -> FileDescriptor of lock
					if ("fd".equals(field.getName().toLowerCase())) {
						field.setAccessible(true);
						Object fd = field.get(obj);
						if (fd instanceof FileDescriptor) {
							java.util.List<java.lang.reflect.Field> attrs3 = getAllFields(fd);
							Iterator<java.lang.reflect.Field> it3 = attrs3.iterator();
							// Find attribue "int fd" -> contains
							// the file descriptor id from os
							while (it3.hasNext()) {
								field = it3.next();
								if ("fd".equals(field.getName().toLowerCase())) {
									field.setAccessible(true);
									Object fdOs = field.get(fd);
									if (fdOs instanceof Integer) {
										// System.out.println("Mirror fdOs:
										// "+fdOs);
										_return.put("fd", String.valueOf(fdOs));
//										fileDescriptor = String.valueOf(fdOs);
									}
								}

								if ("handle".equals(field.getName().toLowerCase())
										&& osSystem.toLowerCase().contains("window")) {
									field.setAccessible(true);
									Object handleOs = field.get(fd);
									if (handleOs instanceof Long) {
										_return.put("handle", String.valueOf(handleOs));
//										handle = String.valueOf(handleOs);
									}
								}
							}
						}
					}
				}
			} else if ((obj instanceof FilterOutputStream) || (obj instanceof FilterInputStream)) {
				List<java.lang.reflect.Field> attrs = getAllFields(obj);
				Iterator<java.lang.reflect.Field> it = attrs.iterator();
				while (it.hasNext()) {
					java.lang.reflect.Field field = it.next();
					// Find attribute "lock" -> FileOutputStream
					if ("out".equals(field.getName().toLowerCase()) || "in".equals(field.getName().toLowerCase())) {
						field.setAccessible(true);
						Object out = field.get(obj);
						if ((out instanceof FileOutputStream) || (out instanceof FileInputStream)) {
							java.util.List<java.lang.reflect.Field> attrs2 = getAllFields(out);
							Iterator<java.lang.reflect.Field> it2 = attrs2.iterator();
							while (it2.hasNext()) {
								field = it2.next();
								// Find attribute "fd" -> FileDescriptor of lock
								if ("fd".equals(field.getName().toLowerCase())) {
									field.setAccessible(true);
									Object fd = field.get(out);
									if (fd instanceof FileDescriptor) {
										java.util.List<java.lang.reflect.Field> attrs3 = getAllFields(fd);
										Iterator<java.lang.reflect.Field> it3 = attrs3.iterator();
										// Find attribue "int fd" -> contains
										// the file descriptor id from os
										while (it3.hasNext()) {
											field = it3.next();
											if ("fd".equals(field.getName().toLowerCase())) {
												field.setAccessible(true);
												Object fdOs = field.get(fd);
												if (fdOs instanceof Integer) {
													// System.out.println("Mirror
													// fdOs: "+fdOs);
													_return.put("fd", String.valueOf(fdOs));
//													fileDescriptor = String.valueOf(fdOs);
												}
											}

											if ("handle".equals(field.getName().toLowerCase())
													&& osSystem.toLowerCase().contains("window")) {
												field.setAccessible(true);
												Object handleOs = field.get(fd);
												if (handleOs instanceof Long) {
													_return.put("handle", String.valueOf(handleOs));
//													handle = String.valueOf(handleOs);
												}
											}
										}
									}
								}
								else if ("path".equals(field.getName().toLowerCase())) {
									field.setAccessible(true);
									Object path = field.get(out);
									if (path instanceof String) {
										_return.put("path", String.valueOf(path));
//										handle = String.valueOf(handleOs);
									}
								}
							}
						}
					}
				}
			} else if (obj instanceof File){
				List<java.lang.reflect.Field> attrs = getAllFields(obj);
				Iterator<java.lang.reflect.Field> it = attrs.iterator();
				while (it.hasNext()) {
					java.lang.reflect.Field field = it.next();
					// Find attribute "lock" -> FileOutputStream
					if ("path".equals(field.getName().toLowerCase())) {
						field.setAccessible(true);
						Object path = field.get(obj);
						if(path instanceof String){
							_return.put("path", String.valueOf(path));
						}
					}
				}
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("FileDescriptor: "+fileDescriptor);

		if ("-1".equals(_return.get("fd")) || "".equals(_return.get("fd"))) {
			if (!"-1".equals(_return.get("handle")) && !"".equals(_return.get("handle"))) {
				_return.put("fd", _return.get("handle"));
			}
		}

		return _return;
	}

	public static String[] createSourceWrapper(int p_opcode, String p_ownerclass, String p_ownermethod,
			String p_descownermethod, ClassWriter cv, String p_parentclass, List<SinkSource> p_sources) {
	    boolean isConstructor = p_opcode == Opcodes.INVOKESPECIAL && p_ownermethod.equals("<init>");
	    boolean isStatic = p_opcode == Opcodes.INVOKESTATIC;
	    String[] _return = new String[2];
		StringBuilder wrapperMethodDesc = new StringBuilder();
		try {
			// ==> Preprocessing, create required method description, indizes,
			// Create method description of the to be invoked methods
			wrapperMethodDesc.append("(");
			int argIndex = -1;
			if(!isConstructor && !isStatic){
				wrapperMethodDesc.append("L" + p_ownerclass + ";");// append("Ljava/lang/Object;");
				++argIndex;
			}			// count the number of parameters the wrapper method has, (0)=object,(1...n-1)=parameters,(n)=source-id

			// Helper variable to store the correct parameter index within the
			// local variable table
			int paramStartIndex = 0;
			Type[] argT = Type.getArgumentTypes(p_descownermethod);
			if (argT.length > 0) {
				paramStartIndex = argIndex+1;
				for (Type t : argT) {
					wrapperMethodDesc.append(t.getDescriptor());
					argIndex++;
					if (Type.DOUBLE == t.getSort() || Type.LONG == t.getSort()) {
						argIndex++;
					}
				}
			}
			// Parent-object index
			wrapperMethodDesc.append("Ljava/lang/Object;");
			int parentObjIndex = ++argIndex;

			// Parent-method index
			wrapperMethodDesc.append("Ljava/lang/String;");
			int parentMethodIndex = ++argIndex;

			// sourceindex
			wrapperMethodDesc.append("Ljava/lang/String;");
			int sinksourceIndex = ++argIndex;

			// chopLabel index
			wrapperMethodDesc.append("Ljava/lang/String;");
			int chopLabelIndex = ++argIndex;

			wrapperMethodDesc.append(")");

			Type retT = Type.getReturnType(p_descownermethod);
			if (isConstructor) {
				wrapperMethodDesc.append("L" + p_ownerclass + ";");
			} else if(retT != null) {
				wrapperMethodDesc.append(retT.getDescriptor());
			}

			// Constructors are renamed
			String wrapperMethodName = p_ownermethod;
			if (isConstructor) {
				wrapperMethodName = p_ownerclass.replace("/", "_") + "_init";
			}
			_return[0] = wrapperMethodName;
			_return[1] = wrapperMethodDesc.toString();

			// Check if method invocation was already wrapped
			String id = p_parentclass + "." + wrapperMethodName + ":" + wrapperMethodDesc.toString();
			if (Utility.METHODS.containsKey(id)) {
				return _return;
			} else {
				Utility.METHODS.put(id, id);
			}

			// ==> Create wrapper method content
			MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, wrapperMethodName,
					wrapperMethodDesc.toString(), null, null);
			mv.visitCode();

			//--> Load all parameters into an array
			mv.visitLdcInsn(argT.length);
			mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
			// Store it after all method params
			int paramArrayIndex = ++argIndex;
			mv.visitVarInsn(Opcodes.ASTORE, paramArrayIndex);
			int i = paramStartIndex; // local variable index counter
			int j = 0; // array entry counter
			for (Type argType : argT) {
				mv.visitVarInsn(Opcodes.ALOAD, paramArrayIndex);
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
			//<-- Load all parameters into an array

			//--> Execute the original method
			if(isConstructor){
			    mv.visitTypeInsn(Opcodes.NEW, p_ownerclass);
			    mv.visitInsn(Opcodes.DUP);
			}else if (!isStatic){
				mv.visitVarInsn(Opcodes.ALOAD, 0);
			}
			i = paramStartIndex;
			for (Type t : argT) {
				if (t.getSort() == Type.OBJECT) {
					mv.visitVarInsn(Opcodes.ALOAD, i);
				} else if (t.getSort() == Type.ARRAY) {
					mv.visitVarInsn(Opcodes.ALOAD, i);
				} else if (t.getSort() == Type.DOUBLE) {
					mv.visitVarInsn(Opcodes.DLOAD, i);
					i++;
				} else if (t.getSort() == Type.FLOAT) {
					mv.visitVarInsn(Opcodes.FLOAD, i);
				} else if (t.getSort() == Type.LONG) {
					mv.visitVarInsn(Opcodes.LLOAD, i);
					i++;
				} else if ((t.getSort() == Type.INT) || (t.getSort() == Type.CHAR)) {
					mv.visitVarInsn(Opcodes.ILOAD, i);
				}
				i++;
			}
			// Timer4: log native method execution
			// Boolean timer4 = new
			// Boolean(ConfigProperties.getProperty(ConfigProperties.PROPERTIES.TIMER_T4));
			// if (timer4) {
			// mv.visitMethodInsn(Opcodes.INVOKESTATIC,
			// UcTransformer.HOOKMETHOD, "timerT4Start", "()V", false);
			// }

			mv.visitMethodInsn(p_opcode, p_ownerclass, p_ownermethod, p_descownermethod, p_opcode == Opcodes.INVOKEINTERFACE);
			//<-- Execute the original method
			
//			int constructorIndex = -1;
//			if(isConstructor){
//				constructorIndex = ++argIndex;
//			    mv.visitVarInsn(Opcodes.ASTORE, constructorIndex);
//			}
			
			// if (timer4) {
			// mv.visitVarInsn(Opcodes.ALOAD, paramIndex);// myArgT.length -
			// // 1);
			// mv.visitMethodInsn(Opcodes.INVOKESTATIC,
			// UcTransformer.HOOKMETHOD, "timerT4Stop",
			// "(Ljava/lang/String;)V", false);
			// }
			
			//Send event to pdp
			if(p_sources.size() > 0){
				for(SinkSource s : p_sources){
					if(s.is_return()){
						if (isConstructor) {
							mv.visitInsn(Opcodes.DUP);
						} else if (retT.getSort() != Type.VOID){
							boolean retValueIsBig = retT.getSize() == 2;
							if (retValueIsBig) {
								mv.visitInsn(Opcodes.DUP2);
							} else {
								mv.visitInsn(Opcodes.DUP);
							}
							Utility.boxTopStackValue(mv, retT);
						}else {
							mv.visitInsn(Opcodes.ACONST_NULL);
						}
					}
					else{
						int param = s.getParam();
						if(param > 0 && retT.getSize() > 0){
							i = paramStartIndex + param - 1;
							Type t = argT[param-1];
							if (t.getSort() == Type.OBJECT) {
								mv.visitVarInsn(Opcodes.ALOAD, i);
							} else if (t.getSort() == Type.ARRAY) {
								mv.visitVarInsn(Opcodes.ALOAD, i);
							} else if (t.getSort() == Type.DOUBLE) {
								mv.visitVarInsn(Opcodes.DLOAD, i);
							} else if (t.getSort() == Type.FLOAT) {
								mv.visitVarInsn(Opcodes.FLOAD, i);
							} else if (t.getSort() == Type.LONG) {
								mv.visitVarInsn(Opcodes.LLOAD, i);
							} else if ((t.getSort() == Type.INT) || (t.getSort() == Type.CHAR)) {
								mv.visitVarInsn(Opcodes.ILOAD, i);
							}
							break;
						} else{
							mv.visitInsn(Opcodes.ACONST_NULL);
						}
					}
				}
			} else{
				mv.visitInsn(Opcodes.ACONST_NULL);
			}

			if(isConstructor || isStatic)
				mv.visitInsn(Opcodes.ACONST_NULL);
			else
				mv.visitVarInsn(Opcodes.ALOAD, 0);// first parameter is the ownerobject
			mv.visitLdcInsn(p_ownerclass.replace("/", "."));
			mv.visitLdcInsn(p_ownermethod);
			mv.visitVarInsn(Opcodes.ALOAD, parentObjIndex);// Load parentObj ref
			// mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Object");
			mv.visitLdcInsn(p_parentclass.replace("/", ".")); // Load parent class name
			mv.visitVarInsn(Opcodes.ALOAD, parentMethodIndex); // Load parent method name
			mv.visitVarInsn(Opcodes.ALOAD, sinksourceIndex); // Load sinksource-ids
			mv.visitVarInsn(Opcodes.ALOAD, chopLabelIndex); // Load choplabel onto the stack
			mv.visitVarInsn(Opcodes.ALOAD, paramArrayIndex);// Load method params array
			
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, MyUcTransformer.DELEGATECLASS, "sourceInvoked",
					"(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Z",
					false);
			mv.visitInsn(Opcodes.POP);

			// Add return
			if (retT.getSort() == Type.OBJECT || retT.getSort() == Type.ARRAY || isConstructor) {
				mv.visitInsn(Opcodes.ARETURN);
			} else if (retT.getSort() == Type.DOUBLE) {
				mv.visitInsn(Opcodes.DRETURN);
			} else if (retT.getSort() == Type.FLOAT) {
				mv.visitInsn(Opcodes.FRETURN);
			} else if (retT.getSort() == Type.LONG) {
				mv.visitInsn(Opcodes.LRETURN);
			} else if (retT.getSort() == Type.INT || retT.getSort() == Type.BOOLEAN) {
				mv.visitInsn(Opcodes.IRETURN);
			} else {
				mv.visitInsn(Opcodes.RETURN);
			}

			Type[] myArgT = Type.getArgumentTypes(wrapperMethodDesc.toString());
			mv.visitMaxs(myArgT.length+6, myArgT.length+5);
			mv.visitEnd();
//			cv.visitEnd();
		} catch (Exception e) {
		}
		return _return;
	}
	
	public static String[] createSinkWrapper(int p_opcode, String p_ownerclass, String p_ownermethod,
		String p_descownermethod, ClassWriter cv, String p_parentclass, List<SinkSource> p_sinks) {
		String[] _return = new String[2];
	    boolean isConstructor = p_opcode == Opcodes.INVOKESPECIAL && p_ownermethod.equals("<init>");
	    boolean isStatic = p_opcode == Opcodes.INVOKESTATIC;
		StringBuilder wrapperMethodDesc = new StringBuilder();
		try {
			//Preprocessing, create required method description, indizes, etc
			// Create method description of the to be invoked methods
			wrapperMethodDesc.append("(");
			int argIndex = -1;
			if(!isConstructor && !isStatic){
				wrapperMethodDesc.append("L" + p_ownerclass + ";");// append("Ljava/lang/Object;");
				++argIndex;
			}
			// count the number of parameters the wrapper method has,

			// Helper variable to store the correct parameter index within the
			// local variable table
			int paramStartIndex = 0;
			Type[] argT = Type.getArgumentTypes(p_descownermethod);
			if (argT.length > 0) {
				paramStartIndex = argIndex+1;
				for (Type t : argT) {
					wrapperMethodDesc.append(t.getDescriptor());
					if (Type.DOUBLE == t.getSort() || Type.LONG == t.getSort()) {
						argIndex++;
					}
					++argIndex;
				}
			}

			// Parent-object index
			wrapperMethodDesc.append("Ljava/lang/Object;");
			int parentObjIndex = ++argIndex;

			// Parent-method index
			wrapperMethodDesc.append("Ljava/lang/String;");
			int parentMethodIndex = ++argIndex;

			// sinksourceindex
			wrapperMethodDesc.append("Ljava/lang/String;");
			int sinksourceIndex = ++argIndex;

			// chopLabel index
			wrapperMethodDesc.append("Ljava/lang/String;");
			int chopLabelIndex = ++argIndex;

			wrapperMethodDesc.append(")");

			Type retT = Type.getReturnType(p_descownermethod);
			if (isConstructor) {
				wrapperMethodDesc.append("L" + p_ownerclass + ";");
			} else if(retT != null) {
				wrapperMethodDesc.append(retT.getDescriptor());
			}

			// Constructors are renamed
			String wrapperMethodName = p_ownermethod;
			if (isConstructor) {
				wrapperMethodName = p_ownerclass.replace("/", "_") + "_init";
			}
			_return[0] = wrapperMethodName;
			_return[1] = wrapperMethodDesc.toString();

			// Check if method invocation was already wrapped
			String id = p_parentclass + "." + wrapperMethodName + ":" + wrapperMethodDesc.toString();
			if (Utility.METHODS.containsKey(id)) {
				return _return;
			} else {
				Utility.METHODS.put(id, id);
			}

			// ==> Create wrapper method content
			MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, wrapperMethodName,
					wrapperMethodDesc.toString(), null, null);
			mv.visitCode();

			// Create array to fit all arguments
			mv.visitLdcInsn(argT.length);
			mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
			// Store it after all method params
			int paramArrayIndex = ++argIndex;
			mv.visitVarInsn(Opcodes.ASTORE, paramArrayIndex);

			int i = paramStartIndex; // local variable index counter
			int j = 0; // array entry counter
			for (Type argType : argT) {
				mv.visitVarInsn(Opcodes.ALOAD, paramArrayIndex);
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

			// Send an event to the pdp
			if(isConstructor){
				mv.visitInsn(Opcodes.ACONST_NULL);
			}
			else if(!isStatic){
				mv.visitVarInsn(Opcodes.ALOAD, 0);// first parameter is the ownerobject 
			}
			mv.visitLdcInsn(p_ownerclass.replace("/", "."));
			mv.visitLdcInsn(p_ownermethod);
			mv.visitVarInsn(Opcodes.ALOAD, paramArrayIndex);//Load method params
			mv.visitVarInsn(Opcodes.ALOAD, parentObjIndex);// Load parentObj ref
			mv.visitLdcInsn(p_parentclass); // Load parent class name
			mv.visitVarInsn(Opcodes.ALOAD, parentMethodIndex); // Load parent method name
			mv.visitVarInsn(Opcodes.ALOAD, sinksourceIndex); // Load sinksource-ids
			mv.visitVarInsn(Opcodes.ALOAD, chopLabelIndex);

			mv.visitMethodInsn(Opcodes.INVOKESTATIC, MyUcTransformer.DELEGATECLASS, "sinkInvoked",
					"(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z",
					false);
			mv.visitInsn(Opcodes.POP);

			// Preprocessing, to execute the original method
			if(isConstructor){
			    mv.visitTypeInsn(Opcodes.NEW, p_ownerclass);
			    mv.visitInsn(Opcodes.DUP);
			}else if (!isStatic){
				mv.visitVarInsn(Opcodes.ALOAD, 0);
			}
			
			i = paramStartIndex;
			for (Type t : argT) {
				if (t.getSort() == Type.OBJECT) {
					mv.visitVarInsn(Opcodes.ALOAD, i);
				} else if (t.getSort() == Type.ARRAY) {
					mv.visitVarInsn(Opcodes.ALOAD, i);
				} else if (t.getSort() == Type.DOUBLE) {
					mv.visitVarInsn(Opcodes.DLOAD, i);
					i++;
				} else if (t.getSort() == Type.FLOAT) {
					mv.visitVarInsn(Opcodes.FLOAD, i);
				} else if (t.getSort() == Type.LONG) {
					mv.visitVarInsn(Opcodes.LLOAD, i);
					i++;
				} else if ((t.getSort() == Type.INT) || (t.getSort() == Type.CHAR)) {
					mv.visitVarInsn(Opcodes.ILOAD, i);
				}
				i++;
			}
			// Timer4: log native method execution
			// Boolean timer4 = new
			// Boolean(ConfigProperties.getProperty(ConfigProperties.PROPERTIES.TIMER_T4));
			// if (timer4) {
			// mv.visitMethodInsn(Opcodes.INVOKESTATIC,
			// UcTransformer.HOOKMETHOD, "timerT4Start", "()V", false);
			// }

			// Execute original method
			mv.visitMethodInsn(p_opcode, p_ownerclass, p_ownermethod, p_descownermethod, p_opcode == Opcodes.INVOKEINTERFACE);

			// if (timer4) {
			// mv.visitVarInsn(Opcodes.ALOAD, paramIndex);// myArgT.length -
			// // 1);
			// mv.visitMethodInsn(Opcodes.INVOKESTATIC,
			// UcTransformer.HOOKMETHOD, "timerT4Stop",
			// "(Ljava/lang/String;)V", false);
			// }

			// Add return
			if (retT.getSort() == Type.OBJECT || retT.getSort() == Type.ARRAY || isConstructor) {
				mv.visitInsn(Opcodes.ARETURN);
			} else if (retT.getSort() == Type.DOUBLE) {
				mv.visitInsn(Opcodes.DRETURN);
			} else if (retT.getSort() == Type.FLOAT) {
				mv.visitInsn(Opcodes.FRETURN);
			} else if (retT.getSort() == Type.LONG) {
				mv.visitInsn(Opcodes.LRETURN);
			} else if (retT.getSort() == Type.INT || retT.getSort() == Type.BOOLEAN) {
				mv.visitInsn(Opcodes.IRETURN);
			} else {
				mv.visitInsn(Opcodes.RETURN);
			}

			Type[] myArgT = Type.getArgumentTypes(wrapperMethodDesc.toString());
			mv.visitMaxs(myArgT.length+5, myArgT.length+5);
			mv.visitEnd();
		} catch (Exception e) {
		}

		return _return;
	}
	
	public static Parameter[] addSinkSourceParam(Parameter[] p_param, SourceSinkName.Type p_sinksource, String p_sinksourceId){
		Parameter[] _return = new Parameter[p_param.length+1];
		System.arraycopy(p_param, 0, _return, 0, p_param.length);
		_return[_return.length-1] = Parameter.with(p_sinksource.name(), p_sinksourceId);
		return _return;
	}

	/**
	 * Creates a dummy method that extracts the method for file descriptor
	 * 
	 * @param p_opcode
	 *            Java Bytecode opcode to invoke instruction
	 * @param p_owner_classname
	 *            Owner class where of invoked method p_name
	 * @param p_owner_methodname
	 *            Method name of invoked method that has to be replaced
	 * @param p_desc
	 *            Java bytecode method signature
	 * @param cv
	 *            ClassVisitor
	 * @param classname
	 *            The classname where the method is invoked
	 * @param sors
	 *            Contains sink or source specification
	 * @return
	 */
	public static String createASMHelperMethod(int p_opcode, String p_owner_classname, String p_owner_methodname,
			String p_desc, ClassWriter cv, String classname, List<SinkSource> sors) {
		StringBuilder desc = new StringBuilder();
		try {
			// Class<?> clazz = DummyHelperClass.class;
			// String className = clazz.getName().replace(".",
			// System.getProperty("file.separator")) + ".class";
			// InputStream is =
			// clazz.getClassLoader().getResourceAsStream(className);
			// byte[] b = IOUtils.toByteArray(is);

			// Generate new method signature
			Type[] argT = Type.getArgumentTypes(p_desc);

			// Create method description of the to be invoked methods
			desc.append("(");
			// desc.append("Ljava/lang/Object;");
			desc.append("L" + p_owner_classname + ";");

			// Helper variable to store the correct parameter index within the
			// local variable table
			int paramIndex = 0;
			if (argT.length > 0) {
				for (Type t : argT) {
					desc.append(t.getDescriptor());
					paramIndex++;
					if (Type.DOUBLE == t.getSort() || Type.LONG == t.getSort()) {
						paramIndex++;
					}
				}
			}
			desc.append("Ljava/lang/String;");
			paramIndex++;
			desc.append(")");
			Type retT = Type.getReturnType(p_desc);
			if (retT != null) {
				desc.append(retT.getDescriptor());
			}
			Type[] myArgT = Type.getArgumentTypes(desc.toString());

			if (p_owner_methodname.equals("<init>")) {
				// p_owner_methodname = "Lpackage_class_init"
				p_owner_methodname = p_owner_classname.replace("/", "_") + "_init";
			}
			String id = classname + "." + p_owner_methodname + ":" + desc.toString();
			if (Utility.METHODS.containsKey(id)) {
				return desc.toString();
			} else {
				Utility.METHODS.put(id, id);
			}

			// MyClassReader cr = new MyClassReader(b);
			// ClassReader cr = new ClassReader(b);
			// ClassWriter cw = new ClassWriter(cv, ClassWriter.COMPUTE_MAXS |
			// ClassWriter.COMPUTE_FRAMES);

			// Create a new asm-method instance
			MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, p_owner_methodname,
					desc.toString(), null, null);
			mv.visitCode();

			// Set<Integer> ks = sors.keySet();
			// Iterator<Integer> ksIt = ks.iterator();
			// while (ksIt.hasNext()) {
			// int parameter = ksIt.next();
			// if (sors.get(parameter).equals(StaticAnalysis.nodeType.SOURCE)) {
			// if (myArgT[parameter].getSort() == Type.OBJECT) {
			// mv.visitVarInsn(Opcodes.ALOAD, parameter);
			// } else if (myArgT[parameter].getSort() == Type.ARRAY) {
			// mv.visitVarInsn(Opcodes.ALOAD, parameter);
			// } else if (myArgT[parameter].getSort() == Type.DOUBLE) {
			// mv.visitVarInsn(Opcodes.DLOAD, parameter);
			// } else if (myArgT[parameter].getSort() == Type.FLOAT) {
			// mv.visitVarInsn(Opcodes.FLOAD, parameter);
			// } else if (myArgT[parameter].getSort() == Type.LONG) {
			// mv.visitVarInsn(Opcodes.LLOAD, parameter);
			// } else if (myArgT[parameter].getSort() == Type.INT) {
			// mv.visitVarInsn(Opcodes.ILOAD, parameter);
			// }
			// } else {
			mv.visitVarInsn(Opcodes.ALOAD, 0);// ALOAD_0 -> the first parameter
												// is the object reference }
			mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Object");
			mv.visitVarInsn(Opcodes.ALOAD, paramIndex);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, UcTransformer.HOOKMETHOD, "methodInvoked",
					"(Ljava/lang/Object;Ljava/lang/String;)Z", false);
			mv.visitInsn(Opcodes.POP);
			// }

			// mv.visitVarInsn(Opcodes.ALOAD, 0);
			// mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object",
			// "getClass", "()Ljava/lang/Class;",false);
			// mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class",
			// "toString", "()Ljava/lang/String;",false);
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			int i = 1;
			for (Type t : argT) {
				if (t.getSort() == Type.OBJECT) {
					mv.visitVarInsn(Opcodes.ALOAD, i);
				} else if (t.getSort() == Type.ARRAY) {
					mv.visitVarInsn(Opcodes.ALOAD, i);
				} else if (t.getSort() == Type.DOUBLE) {
					mv.visitVarInsn(Opcodes.DLOAD, i);
					i++;
				} else if (t.getSort() == Type.FLOAT) {
					mv.visitVarInsn(Opcodes.FLOAD, i);
				} else if (t.getSort() == Type.LONG) {
					mv.visitVarInsn(Opcodes.LLOAD, i);
					;
					i++;
				} else if ((t.getSort() == Type.INT) || (t.getSort() == Type.CHAR)) {
					mv.visitVarInsn(Opcodes.ILOAD, i);
				}
				i++;
			}
			Boolean timer4 = new Boolean(ConfigProperties.getProperty(ConfigProperties.PROPERTIES.TIMER_T4));
			if (timer4) {
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, UcTransformer.HOOKMETHOD, "timerT4Start", "()V", false);
			}

			mv.visitMethodInsn(p_opcode, p_owner_classname, p_owner_methodname, p_desc, false);

			if (timer4) {
				mv.visitVarInsn(Opcodes.ALOAD, paramIndex);// myArgT.length -
															// 1);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, UcTransformer.HOOKMETHOD, "timerT4Stop",
						"(Ljava/lang/String;)V", false);
			}

			// mv.visitVarInsn(Opcodes.ALOAD, 0);// ALOAD_0 -> the first
			// parameter is the object reference
			// mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Object");
			// mv.visitVarInsn(Opcodes.ALOAD, paramIndex);// myArgT.length - 1);
			// mv.visitMethodInsn(Opcodes.INVOKESTATIC,
			// UcTransformer.HOOKMETHOD,"methodExited",
			// "(Ljava/lang/Object;Ljava/lang/String;)V",false);

			// Add return
			if (retT.getSort() == Type.OBJECT || retT.getSort() == Type.ARRAY) {
				mv.visitInsn(Opcodes.ARETURN);
			} else if (retT.getSort() == Type.DOUBLE) {
				mv.visitInsn(Opcodes.DRETURN);
			} else if (retT.getSort() == Type.FLOAT) {
				mv.visitInsn(Opcodes.FRETURN);
			} else if (retT.getSort() == Type.LONG) {
				mv.visitInsn(Opcodes.LRETURN);
			} else if (retT.getSort() == Type.INT || retT.getSort() == Type.BOOLEAN) {
				mv.visitInsn(Opcodes.IRETURN);
			} else {
				mv.visitInsn(Opcodes.RETURN);
			}

			mv.visitMaxs(myArgT.length, myArgT.length);
			mv.visitEnd();
			cv.visitEnd();

			// cr.accept(cw, 0);
			// ClassLoader parent = DummyHelperClass.class.getClassLoader();
			// DummyHelperClassLoader mcl = new
			// DummyHelperClassLoader(parent.getParent());
			// Class<?> myTest2Clazz =
			// mcl.define("edu.tum.uc.jvm.utility.DummyHelperClass",
			// cw.toByteArray());

			// Method[] methods = ClassLoader.class.getDeclaredMethods();
			// for(Method m : methods){
			// if(m.getName().toLowerCase().equals("defineclass") &&
			// (m.getParameterTypes().length == 4)){
			// m.setAccessible(true);
			// m.invoke(parent,
			// "edu.tum.uc.jvm.utility.DummyHelperClass",cw.toByteArray(),0,cw.toByteArray().length);
			// }
			// }
			// scl.setAccessible(true);
			// scl.set(null, mcl);
			// Object obj = myTest2Clazz.newInstance();

			// File f = new
			// File("/home/alex/instrumented/DummyHelperClass.class");
			// if (!f.exists()) {
			// f.createNewFile();
			// }
			// DataOutputStream dos = new DataOutputStream(
			// new FileOutputStream(f));
			// dos.write(cw.toByteArray());
		} catch (Exception e) {
		}

		return desc.toString();
	}

	/**
	 * Populates the PIP, i.e. create all Sink and Source container and their
	 * aliases among each other
	 * 
	 * @param file
	 *            Filename that specifies the Joana analysis report
	 */

	public static void populatePip(String file) {
		UcCommunicator ucom = UcCommunicator.getInstance();
		StaticAnalysis.importXML(new File(file).getAbsolutePath());

		IMessageFactory _messageFactory = MessageFactoryCreator.createMessageFactory();

		// Generate Sources
		JSONObject jsonReq = new JSONObject();
		JSONArray sources = new JSONArray();
		try {
			Iterator<SinkSource> it = StaticAnalysis.getSources().iterator();
			while (it.hasNext()) {
				JSONObject s = new JSONObject();
				SinkSource source = it.next();

				// Add id
				s.put("id", source.getId());

				// Add location
				s.put("location", source.getLocation());

				// Add offset
				s.put("offset", source.getOffset());

				if (source.is_return()) {
					s.put("parampos", -1);
				} else if (source.getParam() != -1000) {
					s.put("parampos", source.getParam());
				}

				// Add signature
				List<String> signatures = source.getPossibleSignatures();
				Iterator<String> sigIt = signatures.iterator();
				// String signature = "";
				JSONArray possibleSignature = new JSONArray();
				while (sigIt.hasNext()) {
					possibleSignature.add(sigIt.next());
				}

				s.put("signature", possibleSignature);

				sources.add(s);
			}
		} catch (Exception e) {
			System.out.println("Error while pasrsing sources. ");
			e.printStackTrace();
		}
		jsonReq.put("listOfSources", sources);

		// Generate Sinks
		JSONArray sinks = new JSONArray();
		try {
			Iterator<SinkSource> it = StaticAnalysis.getSinks().iterator();
			while (it.hasNext()) {
				JSONObject sink = new JSONObject();
				SinkSource sinkSource = it.next();

				sink.put("id", sinkSource.getId());

				sink.put("location", sinkSource.getLocation());

				sink.put("offset", sinkSource.getOffset());

				if (sinkSource.is_return()) {
					sink.put("parampos", -1);
				} else if (sinkSource.getParam() != -1000) {
					sink.put("parampos", sinkSource.getParam());
				}

				// Add signature
				List<String> signatures = sinkSource.getPossibleSignatures();
				Iterator<String> sigIt = signatures.iterator();
				JSONArray possibleSignatures = new JSONArray();
				while (sigIt.hasNext()) {
					possibleSignatures.add(sigIt.next());
				}

				sink.put("signature", possibleSignatures);

				sinks.add(sink);
			}
		} catch (Exception e) {
			System.err.println("Error while pasrsing sinks. ");
			e.printStackTrace();
		}
		jsonReq.put("listOfSinks", sinks);

		JSONArray flows = new JSONArray();
		// String listOfFlows = "";
		Iterator<Flow> flowIt = StaticAnalysis.getFlows().iterator();

		while (flowIt.hasNext()) {
			JSONObject f = new JSONObject();

			Flow flow = flowIt.next();

			JSONArray flowSources = new JSONArray();
			List<String> listOfSources = flow.getSource();
			if (listOfSources != null) {
				SinkSource sink = StaticAnalysis.getSinkSourceById(flow.getSink(), NODETYPE.SINK);

				f.put("sink", sink.getId());
				Iterator<String> sourceIt = flow.getSource().iterator();
				while (sourceIt.hasNext()) {
					String sourceId = sourceIt.next();
					SinkSource source = StaticAnalysis.getSinkSourceById(sourceId, NODETYPE.SOURCE);
					flowSources.add(sourceId);
				}
				f.put("sources", flowSources);

				flows.add(f);
			}
		}
		jsonReq.put("listOfFlows", flows);

		// param.put("listOfSources", listOfSources);
		// param.put("listOfSinks", listOfSinks);
		// param.put("listOfFlows", listOfFlows);

		Map<String, String> param = new HashMap<String, String>();

		String runningVm = ManagementFactory.getRuntimeMXBean().getName();
		String[] runningVmComp = runningVm.split("@");
		String pid = "";
		if (runningVmComp.length > 0) {
			pid = runningVmComp[0];
		}
		param.put("PEP", "Java");
		param.put("PID", pid);
		param.put("REPORT", jsonReq.toJSONString());

		IEvent initEvent = _messageFactory.createActualEvent("JoanaInitInfoFlow", param);
		ucom.sendInitPdpEvent(initEvent);
	}

	public static boolean isWhitelisted(String classname) {
		classname = classname.replace("/", ".");
		boolean _return = false;
		// Read blacklist file if not done yet
		if (WHITELIST == null) {
			try {
				WHITELIST = new LinkedList<String[]>();
				String filename = ConfigProperties.getProperty(ConfigProperties.PROPERTIES.WHITELIST);
				if (!"".equals(filename)) {
					File f = new File(filename);
					FileInputStream fis = new FileInputStream(f);
					BufferedReader br = new BufferedReader(new InputStreamReader(fis));
					String line;
					while ((line = br.readLine()) != null) {
						String[] lineCmp = line.split(":");
						if (lineCmp.length == 2)
							WHITELIST.add(lineCmp);
					}
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (WHITELIST.size() > 0) {
			Iterator<String[]> it = WHITELIST.iterator();
			while (it.hasNext()) {
				String[] cmp = it.next();
				switch (cmp[0]) {
				case "contains":
					if (classname.toLowerCase().contains(cmp[1].toLowerCase()))
						_return = true;
					break;
				case "startswith":
					if (classname.toLowerCase().startsWith(cmp[1].toLowerCase()))
						_return = true;
					break;
				case "endswith":
					if (classname.toLowerCase().endsWith(cmp[1].toLowerCase()))
						_return = true;
					break;
				}
				if (_return)
					break;
			}
		}
		return _return;
	}

	public static boolean isBlacklisted(String classname) {
		classname = classname.replace("/", ".");
		boolean _return = false;
		// Read blacklist file if not done yet
		if (BLACKLIST == null) {
			try {
				BLACKLIST = new LinkedList<String[]>();
				String filename = ConfigProperties.getProperty(ConfigProperties.PROPERTIES.BLACKLIST);
				if (!"".equals(filename)) {
					File f = new File(filename);
					FileInputStream fis = new FileInputStream(f);
					BufferedReader br = new BufferedReader(new InputStreamReader(fis));
					String line;
					while ((line = br.readLine()) != null) {
						String[] lineCmp = line.split(":");
						if (lineCmp.length == 2)
							BLACKLIST.add(lineCmp);
					}
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (BLACKLIST.size() > 0) {
			Iterator<String[]> it = BLACKLIST.iterator();
			while (it.hasNext()) {
				String[] cmp = it.next();
				switch (cmp[0]) {
				case "contains":
					if (classname.toLowerCase().contains(cmp[1].toLowerCase()))
						_return = true;
					break;
				case "startswith":
					if (classname.toLowerCase().startsWith(cmp[1].toLowerCase()))
						_return = true;
					break;
				case "endswith":
					if (classname.toLowerCase().endsWith(cmp[1].toLowerCase()))
						_return = true;
					break;
				}
				if (_return)
					break;
			}
		}
		return _return;
	}

	public static boolean isWhitelisted_2(String classname) {
		classname = classname.replace("/", ".");
		boolean _return = false;
		// Read WHITELIST file if not done yet
		if (WHITELIST == null) {
			try {
				WHITELIST = new LinkedList<String[]>();
				String filename = ConfigProperties.getProperty(ConfigProperties.PROPERTIES.WHITELIST);
				if (!"".equals(filename)) {
					File f = new File(filename);
					FileInputStream fis = new FileInputStream(f);
					BufferedReader br = new BufferedReader(new InputStreamReader(fis));
					String line;
					while ((line = br.readLine()) != null) {
						String[] lineCmp = line.split(":");
						if (lineCmp.length == 2)
							WHITELIST.add(lineCmp);
					}
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (WHITELIST.size() > 0) {
			Iterator<String[]> it = WHITELIST.iterator();
			while (it.hasNext()) {
				String[] cmp = it.next();
				switch (cmp[0]) {
				case "contains":
					if (classname.toLowerCase().contains(cmp[1].toLowerCase()))
						_return = true;
					break;
				case "startswith":
					if (classname.toLowerCase().startsWith(cmp[1].toLowerCase()))
						_return = true;
					break;
				case "endswith":
					if (classname.toLowerCase().endsWith(cmp[1].toLowerCase()))
						_return = true;
					break;
				}
				if (_return)
					break;
			}
		}
		return _return;
	}

	/**
	 * 
	 * @return "Proc" + the PID
	 */
	public static String getPID() {
		String runningVm = ManagementFactory.getRuntimeMXBean().getName();
		String[] runningVmComp = runningVm.split("@");
		if (runningVmComp.length > 0) {
			return "Proc" + runningVmComp[0];
		}
		return "Proc-1";
	}

	public static String getThreadId() {
		return "Thread" + String.valueOf(Thread.currentThread().getId());
	}

	/**
	 * Adds a call to the constructor of a primitive value boxing class like
	 * Double or Integer based on the given type to the given method visitor.
	 * 
	 * @param p_mv
	 *            A method visitor where to add the bytecode instruction.
	 * @param p_valuetype
	 *            The type of the value to be boxed.
	 */
	public static void boxTopStackValue(MethodVisitor p_mv, Type p_valuetype) {
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
}

/*
 * public static final String PREFIX = "my/";
 * 
 * private static String myrep(String p_replaceable) { if (p_replaceable !=
 * null) { if (!p_replaceable.contains(";")) { if
 * (!p_replaceable.contains("java/lang/Object") &&
 * !p_replaceable.contains("java/lang/Class") &&
 * !p_replaceable.contains("sun/misc/Unsafe")) { if
 * (p_replaceable.startsWith("apple/")) { p_replaceable =
 * p_replaceable.replaceFirst("apple/", UcTransformer.PREFIX + "apple/"); } else
 * if (p_replaceable.startsWith("com/")) { p_replaceable =
 * p_replaceable.replaceFirst("com/", UcTransformer.PREFIX + "com/"); } else if
 * (p_replaceable.startsWith("java/")) { p_replaceable =
 * p_replaceable.replaceFirst("java/", UcTransformer.PREFIX + "java/"); } else
 * if (p_replaceable.startsWith("javax/")) { p_replaceable =
 * p_replaceable.replaceFirst("javax/", UcTransformer.PREFIX + "javax/"); } else
 * if (p_replaceable.startsWith("org/")) { p_replaceable =
 * p_replaceable.replaceFirst("org/", UcTransformer.PREFIX + "org/"); } else if
 * (p_replaceable.startsWith("sun/")) { p_replaceable =
 * p_replaceable.replaceFirst("sun/", UcTransformer.PREFIX + "sun/"); } else if
 * (p_replaceable.startsWith("sunw/")) { p_replaceable =
 * p_replaceable.replaceFirst("sunw/", UcTransformer.PREFIX + "sunw/"); } else
 * if (p_replaceable.startsWith("oracle/")) { p_replaceable =
 * p_replaceable.replaceFirst("sunw/", UcTransformer.PREFIX + "oracle/"); } } }
 * else { String[] str = p_replaceable.split(";"); StringBuilder sb = new
 * StringBuilder(); Map<String, Integer> occurence = new HashMap<String,
 * Integer>(); for (int i = 0; i < str.length; i++) { if
 * (!str[i].contains("java/lang/Object") && !str[i].contains("java/lang/Class")
 * && !str[i].contains("sun/misc/Unsafe")) { String minString = ""; int minInt =
 * 100000000; if ((str[i].lastIndexOf("apple/") < minInt) &&
 * (str[i].lastIndexOf("apple/") != -1)) { minString = "apple/"; minInt =
 * str[i].lastIndexOf("apple/"); }
 * 
 * if ((str[i].lastIndexOf("com/") < minInt) && (str[i].lastIndexOf("com/") !=
 * -1)) { minString = "com/"; minInt = str[i].lastIndexOf("com/"); }
 * 
 * if ((str[i].lastIndexOf("java/") < minInt) && (str[i].lastIndexOf("java/") !=
 * -1)) { minString = "java/"; minInt = str[i].lastIndexOf("java/"); }
 * 
 * if ((str[i].lastIndexOf("javax/") < minInt) && (str[i].lastIndexOf("javax/")
 * != -1)) { minString = "javax/"; minInt = str[i].lastIndexOf("javax/"); }
 * 
 * if ((str[i].lastIndexOf("org/") < minInt) && (str[i].lastIndexOf("org/") !=
 * -1)) { minString = "org/"; minInt = str[i].lastIndexOf("org/"); }
 * 
 * if ((str[i].lastIndexOf("sun/") < minInt) && (str[i].lastIndexOf("sun/") !=
 * -1)) { minString = "sun/"; minInt = str[i].lastIndexOf("sun/"); }
 * 
 * if ((str[i].lastIndexOf("sunw/") < minInt) && (str[i].lastIndexOf("sunw/") !=
 * -1)) { minString = "sunw/"; minInt = str[i].lastIndexOf("sunw/"); }
 * 
 * if ((str[i].lastIndexOf("oracle/") < minInt) &&
 * (str[i].lastIndexOf("oracle/") != -1)) { minString = "oracle/"; minInt =
 * str[i].lastIndexOf("oracle/"); }
 * 
 * if (!minString.equals("")) { str[i] = str[i].replaceFirst(minString,
 * UcTransformer.PREFIX + minString); } }
 * 
 * sb.append(str[i]); if (str[i].contains("L" + UcTransformer.PREFIX) ||
 * str[i].contains("java/lang/Object") || str[i].contains("java/lang/Class") ||
 * str[i].contains("sun/misc/Unsafe")) { sb.append(";"); } } p_replaceable =
 * sb.toString(); } } return p_replaceable; }
 */
