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
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.factories.IMessageFactory;
import de.tum.in.i22.uc.cm.factories.MessageFactoryCreator;
import edu.tum.uc.jvm.UcCommunicator;
import edu.tum.uc.jvm.UcTransformer;
import edu.tum.uc.jvm.utility.analysis.Flow;
import edu.tum.uc.jvm.utility.analysis.SinkSource;
import edu.tum.uc.jvm.utility.analysis.StaticAnalysis;
import edu.tum.uc.jvm.utility.analysis.StaticAnalysis.NODETYPE;

public class Utility {

	public final static String notSpecified = "-1";

	private static List<String[]> BLACKLIST;

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
					if ((parameterTypes == null)
							|| (parameterTypes.length == 0)) {
						mv.visitTypeInsn(Opcodes.NEW,
								clazzname.replace(".", "/"));// retT.toString());
						mv.visitInsn(Opcodes.DUP);
						mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
								clazzname.replace(".", "/"), "<init>", "()V",
								false);
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

	public static synchronized boolean checkInvocationOnStack(String p_class,
			String p_method) {
		boolean _return = false;

		for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
			// System.out.println("STACKTRACE: "+ste.getClassName()+"/"+ste.getMethodName());

			if (ste.getClassName().toLowerCase()
					.contains(p_class.toLowerCase())
					&& ste.getMethodName().toLowerCase()
							.contains(p_method.toLowerCase())
					&& !ste.getClassName().equals(Utility.notSpecified)
					&& !ste.getMethodName().equals(Utility.notSpecified)) {
				_return = true;
				break;
			} else if (ste.getClassName().toLowerCase()
					.contains(p_class.toLowerCase())
					&& !ste.getClassName().equals(Utility.notSpecified)) {
				_return = true;
				break;
			} else if (ste.getMethodName().toLowerCase()
					.contains(p_method.toLowerCase())
					&& !ste.getMethodName().equals(Utility.notSpecified)) {
				_return = true;
				break;
			}
		}

		return _return;
	}

	static Map<String, String> METHODS = new HashMap<String, String>();

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
	public static String extractFileDescriptor(Object obj) {
		String fileDescriptor = "", handle = "";
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
							Iterator<java.lang.reflect.Field> it2 = attrs2
									.iterator();
							while (it2.hasNext()) {
								field = it2.next();
								// Find attribute "fd" -> FileDescriptor of lock
								if ("fd".equals(field.getName().toLowerCase())) {
									field.setAccessible(true);
									Object fd = field.get(lock);
									if (fd instanceof FileDescriptor) {
										java.util.List<java.lang.reflect.Field> attrs3 = getAllFields(fd);
										Iterator<java.lang.reflect.Field> it3 = attrs3
												.iterator();
										// Find attribue "int fd" -> contains
										// the file descriptor id from os
										while (it3.hasNext()) {
											field = it3.next();
											if ("fd".equals(field.getName()
													.toLowerCase())) {
												field.setAccessible(true);
												Object fdOs = field.get(fd);
												if (fdOs instanceof Integer) {
													// System.out.println("Mirror fdOs: "+fdOs);
													fileDescriptor = String
															.valueOf(fdOs);
												}
											}
											if ("handle".equals(field.getName()
													.toLowerCase())
													&& osSystem.toLowerCase()
															.contains("window")) {
												field.setAccessible(true);
												Object handleOs = field.get(fd);
												if (handleOs instanceof Long) {
													handle = String
															.valueOf(handleOs);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			} else if ((obj instanceof FileOutputStream)
					|| (obj instanceof FileInputStream)) {
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
							Iterator<java.lang.reflect.Field> it3 = attrs3
									.iterator();
							// Find attribue "int fd" -> contains
							// the file descriptor id from os
							while (it3.hasNext()) {
								field = it3.next();
								if ("fd".equals(field.getName().toLowerCase())) {
									field.setAccessible(true);
									Object fdOs = field.get(fd);
									if (fdOs instanceof Integer) {
										// System.out.println("Mirror fdOs: "+fdOs);
										fileDescriptor = String.valueOf(fdOs);
									}
								}

								if ("handle".equals(field.getName()
										.toLowerCase())
										&& osSystem.toLowerCase().contains(
												"window")) {
									field.setAccessible(true);
									Object handleOs = field.get(fd);
									if (handleOs instanceof Long) {
										handle = String.valueOf(handleOs);
									}
								}
							}
						}
					}
				}
			} else if ((obj instanceof FilterOutputStream)
					|| (obj instanceof FilterInputStream)) {
				List<java.lang.reflect.Field> attrs = getAllFields(obj);
				Iterator<java.lang.reflect.Field> it = attrs.iterator();
				while (it.hasNext()) {
					java.lang.reflect.Field field = it.next();
					// Find attribute "lock" -> FileOutputStream
					if ("out".equals(field.getName().toLowerCase())) {
						field.setAccessible(true);
						Object out = field.get(obj);
						if (out instanceof FileOutputStream) {
							java.util.List<java.lang.reflect.Field> attrs2 = getAllFields(out);
							Iterator<java.lang.reflect.Field> it2 = attrs2
									.iterator();
							while (it2.hasNext()) {
								field = it2.next();
								// Find attribute "fd" -> FileDescriptor of lock
								if ("fd".equals(field.getName().toLowerCase())) {
									field.setAccessible(true);
									Object fd = field.get(out);
									if (fd instanceof FileDescriptor) {
										java.util.List<java.lang.reflect.Field> attrs3 = getAllFields(fd);
										Iterator<java.lang.reflect.Field> it3 = attrs3
												.iterator();
										// Find attribue "int fd" -> contains
										// the file descriptor id from os
										while (it3.hasNext()) {
											field = it3.next();
											if ("fd".equals(field.getName()
													.toLowerCase())) {
												field.setAccessible(true);
												Object fdOs = field.get(fd);
												if (fdOs instanceof Integer) {
													// System.out.println("Mirror fdOs: "+fdOs);
													fileDescriptor = String
															.valueOf(fdOs);
												}
											}

											if ("handle".equals(field.getName()
													.toLowerCase())
													&& osSystem.toLowerCase()
															.contains("window")) {
												field.setAccessible(true);
												Object handleOs = field.get(fd);
												if (handleOs instanceof Long) {
													handle = String
															.valueOf(handleOs);
												}
											}
										}
									}
								}
							}
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

		if ("-1".equals(fileDescriptor) || "".equals(fileDescriptor)) {
			if (!"-1".equals(handle) && !"".equals(handle)) {
				fileDescriptor = handle;
			}
		}

		return fileDescriptor;
	}
	
	public static void main(String[] args) {
		Type[] argT = Type.getArgumentTypes("(JLjava/lang/Object;D)V");
		for(Type t: argT){
			if(Type.LONG_TYPE.getDescriptor().equals(t.getDescriptor()))
			System.out.println(t+", "+Type.LONG+", "+Type.LONG_TYPE);
		}
	}

	/**
	 * Creates a dummy method that extracts that invokes the method for file
	 * descriptor extraction
	 * 
	 * @param p_opcode
	 *            Java Bytecode opcode to invoke instruction
	 * @param p_owner
	 *            Owner class where of invoked method p_name
	 * @param p_name
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
	public static String createHelperMethod(int p_opcode, String p_owner,
			String p_name, String p_desc, ClassWriter cv, String classname,
			List<SinkSource> sors) {
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
			
			//Helper variable to store the correct parameter index within the local variable table
			desc.append("(");
			// desc.append("Ljava/lang/Object;");
			desc.append("L" + p_owner + ";");
			int paramIndex = 0;
			if (argT.length > 0) {
				for (Type t : argT) {
					desc.append(t.getDescriptor());
					paramIndex++;
					if(Type.DOUBLE == t.getSort() || Type.LONG == t.getSort()){
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

			if (p_name.equals("<init>")) {
				p_name = p_owner.replace("/", "_") + "_init";
			}
			String id = classname + "." + p_name + ":" + desc.toString();
			if (Utility.METHODS.containsKey(id)) {
				return desc.toString();
			} else {
				Utility.METHODS.put(id, id);
			}

			// MyClassReader cr = new MyClassReader(b);
			// ClassReader cr = new ClassReader(b);
			// ClassWriter cw = new ClassWriter(cv, ClassWriter.COMPUTE_MAXS |
			// ClassWriter.COMPUTE_FRAMES);

			MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC
					+ Opcodes.ACC_STATIC, p_name, desc.toString(), null, null);
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
			mv.visitVarInsn(Opcodes.ALOAD, 0);// ALOAD_0 -> the first
												// parameter is the
												// object
												// reference
												// }
			mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Object");
			mv.visitVarInsn(Opcodes.ALOAD, paramIndex);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, UcTransformer.HOOKMETHOD,
					"methodInvoked", "(Ljava/lang/Object;Ljava/lang/String;)Z",
					false);
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
;					i++;
				} else if (t.getSort() == Type.INT) {
					mv.visitVarInsn(Opcodes.ILOAD, i);
				}
				i++;
			}
			Boolean b = new Boolean(
					ConfigProperties
							.getProperty(ConfigProperties.PROPERTIES.TIMER_T4
									.toString()));
			if (b) {
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "timerT4Start", "()V", false);
			}
			
			mv.visitMethodInsn(p_opcode, p_owner, p_name, p_desc, false);
			
			if (b) {
				mv.visitMethodInsn(Opcodes.INVOKESTATIC,
						UcTransformer.HOOKMETHOD, "timerT4Stop", "()V", false);
			}

			mv.visitVarInsn(Opcodes.ALOAD, 0);// ALOAD_0 -> the first parameter
												// is the object reference
			mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Object");
			mv.visitVarInsn(Opcodes.ALOAD, paramIndex);// myArgT.length - 1);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, UcTransformer.HOOKMETHOD,
					"methodExited", "(Ljava/lang/Object;Ljava/lang/String;)V",
					false);

			// Add return
			if (retT.getSort() == Type.OBJECT || retT.getSort() == Type.ARRAY) {
				mv.visitInsn(Opcodes.ARETURN);
			} else if (retT.getSort() == Type.DOUBLE) {
				mv.visitInsn(Opcodes.DRETURN);
			} else if (retT.getSort() == Type.FLOAT) {
				mv.visitInsn(Opcodes.FRETURN);
			} else if (retT.getSort() == Type.LONG) {
				mv.visitInsn(Opcodes.LRETURN);
			} else if (retT.getSort() == Type.INT
					|| retT.getSort() == Type.BOOLEAN) {
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
		} catch (Exception e) {}

		return desc.toString();
	}

	private static String myrep(String p_replaceable) {
		if (p_replaceable != null) {
			if (!p_replaceable.contains(";")) {
				if (!p_replaceable.contains("java/lang/Object")
						&& !p_replaceable.contains("java/lang/Class")
						&& !p_replaceable.contains("sun/misc/Unsafe")) {
					if (p_replaceable.startsWith("apple/")) {
						p_replaceable = p_replaceable.replaceFirst("apple/",
								UcTransformer.PREFIX + "apple/");
					} else if (p_replaceable.startsWith("com/")) {
						p_replaceable = p_replaceable.replaceFirst("com/",
								UcTransformer.PREFIX + "com/");
					} else if (p_replaceable.startsWith("java/")) {
						p_replaceable = p_replaceable.replaceFirst("java/",
								UcTransformer.PREFIX + "java/");
					} else if (p_replaceable.startsWith("javax/")) {
						p_replaceable = p_replaceable.replaceFirst("javax/",
								UcTransformer.PREFIX + "javax/");
					} else if (p_replaceable.startsWith("org/")) {
						p_replaceable = p_replaceable.replaceFirst("org/",
								UcTransformer.PREFIX + "org/");
					} else if (p_replaceable.startsWith("sun/")) {
						p_replaceable = p_replaceable.replaceFirst("sun/",
								UcTransformer.PREFIX + "sun/");
					} else if (p_replaceable.startsWith("sunw/")) {
						p_replaceable = p_replaceable.replaceFirst("sunw/",
								UcTransformer.PREFIX + "sunw/");
					} else if (p_replaceable.startsWith("oracle/")) {
						p_replaceable = p_replaceable.replaceFirst("sunw/",
								UcTransformer.PREFIX + "oracle/");
					}
				}
			} else {
				String[] str = p_replaceable.split(";");
				StringBuilder sb = new StringBuilder();
				Map<String, Integer> occurence = new HashMap<String, Integer>();
				for (int i = 0; i < str.length; i++) {
					if (!str[i].contains("java/lang/Object")
							&& !str[i].contains("java/lang/Class")
							&& !str[i].contains("sun/misc/Unsafe")) {
						String minString = "";
						int minInt = 100000000;
						if ((str[i].lastIndexOf("apple/") < minInt)
								&& (str[i].lastIndexOf("apple/") != -1)) {
							minString = "apple/";
							minInt = str[i].lastIndexOf("apple/");
						}

						if ((str[i].lastIndexOf("com/") < minInt)
								&& (str[i].lastIndexOf("com/") != -1)) {
							minString = "com/";
							minInt = str[i].lastIndexOf("com/");
						}

						if ((str[i].lastIndexOf("java/") < minInt)
								&& (str[i].lastIndexOf("java/") != -1)) {
							minString = "java/";
							minInt = str[i].lastIndexOf("java/");
						}

						if ((str[i].lastIndexOf("javax/") < minInt)
								&& (str[i].lastIndexOf("javax/") != -1)) {
							minString = "javax/";
							minInt = str[i].lastIndexOf("javax/");
						}

						if ((str[i].lastIndexOf("org/") < minInt)
								&& (str[i].lastIndexOf("org/") != -1)) {
							minString = "org/";
							minInt = str[i].lastIndexOf("org/");
						}

						if ((str[i].lastIndexOf("sun/") < minInt)
								&& (str[i].lastIndexOf("sun/") != -1)) {
							minString = "sun/";
							minInt = str[i].lastIndexOf("sun/");
						}

						if ((str[i].lastIndexOf("sunw/") < minInt)
								&& (str[i].lastIndexOf("sunw/") != -1)) {
							minString = "sunw/";
							minInt = str[i].lastIndexOf("sunw/");
						}

						if ((str[i].lastIndexOf("oracle/") < minInt)
								&& (str[i].lastIndexOf("oracle/") != -1)) {
							minString = "oracle/";
							minInt = str[i].lastIndexOf("oracle/");
						}

						if (!minString.equals("")) {
							str[i] = str[i].replaceFirst(minString,
									UcTransformer.PREFIX + minString);
						}
					}

					sb.append(str[i]);
					if (str[i].contains("L" + UcTransformer.PREFIX)
							|| str[i].contains("java/lang/Object")
							|| str[i].contains("java/lang/Class")
							|| str[i].contains("sun/misc/Unsafe")) {
						sb.append(";");
					}
				}
				p_replaceable = sb.toString();
			}
		}
		return p_replaceable;
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

		IMessageFactory _messageFactory = MessageFactoryCreator
				.createMessageFactory();

		// String sep1 = Settings.getInstance().getJoanaDelimiter1();
		// String sep2 = Settings.getInstance().getJoanaDelimiter2();

		// Generate Sources
		JSONObject jsonReq = new JSONObject();
		JSONArray sources = new JSONArray();
		try {
			// param.put("PEP", "Java");
			// param.put("type", "source");
			// param.put("PID", pid);
			Iterator<SinkSource> it = StaticAnalysis.getSources().iterator();
			while (it.hasNext()) {
				JSONObject s = new JSONObject();
				SinkSource source = it.next();

				// Add id
				// param.put("id", source.getId());
				// currSource += "id" + sep1 + source.getId()+ sep1;
				s.put("id", source.getId());

				// Add location
				// param.put("location", source.getLocation());
				// currSource += "location" + sep1 + source.getLocation()+ sep1;
				s.put("location", source.getLocation());

				// Add offset
				// param.put("offset", String.valueOf(source.getOffset()));
				// currSource += "offset" + sep1 +
				// String.valueOf(source.getOffset()) + sep1;
				s.put("offset", source.getOffset());

				if (source.is_return()) {
					// param.put("parampos", "-1");
					// currSource += "parampos" + sep1 + "-1" + sep1;
					s.put("parampos", -1);
				} else if (source.getParam() != -1000) {
					// param.put("parampos", String.valueOf(source.getParam()));
					// currSource += "parampos" + sep1 +
					// String.valueOf(source.getParam()) + sep1;
					s.put("parampos", source.getParam());
				}

				// Add signature
				List<String> signatures = source.getPossibleSignatures();
				Iterator<String> sigIt = signatures.iterator();
				// String signature = "";
				JSONArray possibleSignature = new JSONArray();
				while (sigIt.hasNext()) {
					// signature += sigIt.next()+
					// Settings.getInstance().getJoanaDelimiter1();
					possibleSignature.add(sigIt.next());
				}
				// if (signature.length() > 0) {
				// signature = signature.substring(0, signature.length() - 1);
				// }
				// param.put("signature", signature);
				// currSource += "signature" + sep1 + signature+ sep1;
				s.put("signature", possibleSignature);

				// IEvent initEvent = _messageFactory.createActualEvent(
				// "JoanaInitInfoFlow", param);
				// ucom.sendInitPdpEvent(initEvent);
				// listOfSources += currSource + sep2;
				sources.add(s);
			}
		} catch (Exception e) {
			System.out.println("Error while pasrsing sources. ");
			e.printStackTrace();
		}
		jsonReq.put("listOfSources", sources);

		// String listOfSinks = "";
		// Generate Sinks
		JSONArray sinks = new JSONArray();
		try {
			// param.clear();
			// param.put("PEP", "Java");
			// param.put("type", "sink");
			// param.put("PID", pid);
			Iterator<SinkSource> it = StaticAnalysis.getSinks().iterator();
			while (it.hasNext()) {
				// String currSink = "";
				JSONObject sink = new JSONObject();
				SinkSource sinkSource = it.next();

				// Add id
				// param.put("id", source.getId());
				// currSink += "id" + sep1 + source.getId()+ sep1;
				sink.put("id", sinkSource.getId());

				// Add location
				// param.put("location", source.getLocation());
				// currSink += "location" + sep1 + source.getLocation() + sep1;
				sink.put("location", sinkSource.getLocation());

				// Add offset
				// param.put("offset", String.valueOf(source.getOffset()));
				// currSink += "offset" + sep1 +
				// String.valueOf(source.getOffset()) +sep1;
				sink.put("offset", sinkSource.getOffset());

				if (sinkSource.is_return()) {
					// param.put("parampos", "-1");
					// currSink += "parampos" + sep1 + "-1" + sep1;
					sink.put("parampos", -1);
				} else if (sinkSource.getParam() != -1000) {
					// param.put("parampos", String.valueOf(source.getParam()));
					// currSink += "parampos" +sep1 +
					// String.valueOf(source.getParam()) + sep1;
					sink.put("parampos", sinkSource.getParam());
				}

				// Add signature
				List<String> signatures = sinkSource.getPossibleSignatures();
				Iterator<String> sigIt = signatures.iterator();
				// String signature = "";
				JSONArray possibleSignatures = new JSONArray();
				while (sigIt.hasNext()) {
					// signature += sigIt.next() +
					// Settings.getInstance().getJoanaDelimiter1();
					possibleSignatures.add(sigIt.next());
				}
				// if (signature.length() > 0) {
				// signature = signature.substring(0, signature.length() - 1);
				// }
				// param.put("signature", signature);
				// currSink += "signature" + sep1 + signature + sep1;
				sink.put("signature", possibleSignatures);

				// IEvent initEvent = _messageFactory.createActualEvent(
				// "JoanaInitInfoFlow", param);
				// ucom.sendInitPdpEvent(initEvent);
				// listOfSinks += currSink + sep2;
				sinks.add(sink);
			}
		} catch (Exception e) {
			System.err.println("Error while pasrsing sinks. ");
			e.printStackTrace();
		}
		jsonReq.put("listOfSinks", sinks);

		// Generate Flow
		// param.clear();
		// param.put("type", "iflow");
		// param.put("PEP", "Java");
		// param.put("PID", pid);

		JSONArray flows = new JSONArray();
		// String listOfFlows = "";
		Iterator<Flow> flowIt = StaticAnalysis.getFlows().iterator();
		// int i = 0;
		while (flowIt.hasNext()) {
			JSONObject f = new JSONObject();
			// String currFlow="";
			Flow flow = flowIt.next();

			// String parampos = String.valueOf(sink.getParam());
			// if (sink.is_return()) {
			// parampos = "-1";
			// }
			// if (sink != null) {
			// //param.put("sink", sink.getLocation() + ":" + sink.getOffset()
			// // + ":" + parampos);
			// currFlow += sink.getId() + sep1;
			// }

			// String sources = "";
			JSONArray flowSources = new JSONArray();
			List<String> listOfSources = flow.getSource();
			if (listOfSources != null) {
				SinkSource sink = StaticAnalysis.getSinkSourceById(
						flow.getSink(), NODETYPE.SINK);

				f.put("sink", sink.getId());
				Iterator<String> sourceIt = flow.getSource().iterator();
				while (sourceIt.hasNext()) {
					String sourceId = sourceIt.next();
					SinkSource source = StaticAnalysis.getSinkSourceById(
							sourceId, NODETYPE.SOURCE);
					flowSources.add(sourceId);

					// parampos = String.valueOf(source.getParam());
					// if (source.is_return()) {
					// parampos = "-1";
					// }
					// sources += source.getLocation() + ":" +
					// source.getOffset()
					// + ":" + parampos
					// + Settings.getInstance().getJoanaInitDelimiter();
					// currFlow += source.getId()+sep1;
				}
				f.put("sources", flowSources);
				// if (sources.length() > 0) {
				// sources = sources.substring(0, sources.length() - 1);
				// }
				// param.put("source", sources);

				// IEvent initEvent = _messageFactory.createActualEvent(
				// "JoanaInitInfoFlow", param);
				// ucom.sendInitPdpEvent(initEvent);
				// listOfFlows += currFlow + sep2;
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

		IEvent initEvent = _messageFactory.createActualEvent(
				"JoanaInitInfoFlow", param);
		ucom.sendInitPdpEvent(initEvent);
	}

	public static boolean isBlackisted(String classname) {
		classname = classname.replace("/", ".");
		boolean _return = false;
		// Read blacklist file if not done yet
		if (BLACKLIST == null) {
			try {
				BLACKLIST = new LinkedList<String[]>();
				String filename = ConfigProperties
						.getProperty(ConfigProperties.PROPERTIES.BLACKLIST
								.toString());
				if (!"".equals(filename)) {
					File f = new File(filename);
					FileInputStream fis = new FileInputStream(f);
					BufferedReader br = new BufferedReader(
							new InputStreamReader(fis));
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
					if (classname.toLowerCase()
							.startsWith(cmp[1].toLowerCase()))
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
}
