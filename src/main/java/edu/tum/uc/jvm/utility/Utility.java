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
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.restfb.Parameter;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.java.names.JavaName;
import de.tum.in.i22.uc.cm.datatypes.java.names.SourceSinkName;
import de.tum.in.i22.uc.cm.factories.IMessageFactory;
import de.tum.in.i22.uc.cm.factories.MessageFactoryCreator;
import edu.tum.uc.jvm.UcCommunicator;
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
		Class<?> clazz = obj.getClass();
		while (clazz != null && clazz != Object.class) {
			for (Field f : clazz.getDeclaredFields())
				_return.add(f);
			clazz = clazz.getSuperclass();
		}
		return _return;
	}
	
	public static boolean hasQtyInfo(String classname, String methodname){
		boolean _return = false;
		if(classname.toLowerCase().contains("string") && methodname.toLowerCase().contains("append"))
			_return = true;
		if(classname.toLowerCase().contains("string") && methodname.toLowerCase().contains("replace"))
			_return = true;
		if(classname.toLowerCase().contains("string") && methodname.toLowerCase().contains("split"))//TODO
			_return = true;
		if(classname.toLowerCase().contains("string") && methodname.toLowerCase().contains("subsequence"))//TODO
			_return = true;
		if(classname.toLowerCase().contains("string") && methodname.toLowerCase().contains("substring"))//TODO
			_return = true;
			
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

	public static final String STRDELIM = ":";


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

	public static Parameter[] addSinkSourceParam(Parameter[] p_param, SourceSinkName.Type p_sinksource, String p_sinksourceId){
		Parameter[] _return = new Parameter[p_param.length+1];
		System.arraycopy(p_param, 0, _return, 0, p_param.length);
		_return[_return.length-1] = Parameter.with(p_sinksource.name(), p_sinksourceId);
		return _return;
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

				if (source.isReturn()) {
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

				if (sinkSource.isReturn()) {
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
	 * @param valueType
	 *            The type of the value to be boxed.
	 */
	public static void boxTopStackValue(MethodVisitor p_mv, Type valueType) {
		if (valueType == null)
			return;
		int typeType = valueType.getSort();
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
	
	//Extracts the signature of a string, that is composed of methodname and methodsignature
	public static String extractMethodSignature(String method){
		String _return = method;
		int index = _return.indexOf("(");
		return _return.substring(index);
	}
	
	/**
     * returns the levenshtein distance of two strings<br>
     * Der Levenshtein-Algorithmus (auch Edit-Distanz genannt) errechnet die
     * Mindestanzahl von Editierungsoperationen, die notwendig sind, um eine
     * bestimmte Zeichenkette soweit abzudern, um eine andere bestimmte
     * Zeichenkette zu erhalten.<br>
     * Die wohl bekannteste Weise die Edit-Distanz zu berechnen erfolgt durch
     * den sogenannten Dynamic-Programming-Ansatz. Dabei wird eine Matrix
     * initialisiert, die fr jede (m, N)-Zelle die Levenshtein-Distanz
     * (levenshtein distance) zwischen dem m-Buchstabenprfix des einen Wortes
     * und des n-Prfix des anderen Wortes enthlt.<br>
     * Die Tabelle kann z.B. von der oberen linken Ecke zur untereren rechten
     * Ecke gefllt werden. Jeder Sprung horizontal oder vertikal entspricht
     * einer Editieroperation (Einfgen bzw. Lschen eines Zeichens) und
     * "kostet" einen bestimmte virtuellen Betrag.<br>
     * Die Kosten werden normalerweise auf 1 fr jede der Editieroperationen
     * eingestellt. Der diagonale Sprung kostet 1, wenn die zwei Buchstaben in
     * die Reihe und Spalte nicht bereinstimmen, oder im Falle einer
     * bereinstimmung 0.<br>
     * Jede Zelle minimiert jeweils die lokalen Kosten. Daher entspricht die
     * Zahl in der untereren rechten Ecke dem Levenshtein-Abstand zwischen den
     * beiden Wrtern.
     * 
     * @param s
     * @param t
     * @return the levenshtein dinstance
     */
    public static int levenshteinDistance(String s, String t) {
        final int sLen = s.length(), tLen = t.length();

        if (sLen == 0)
            return tLen;
        if (tLen == 0)
            return sLen;

        int[] costsPrev = new int[sLen + 1]; // previous cost array, horiz.
        int[] costs = new int[sLen + 1];     // cost array, horizontally
        int[] tmpArr;                        // helper to swap arrays
        int sIndex, tIndex;                  // current s and t index
        int cost;                            // current cost value
        char tIndexChar;                     // char of t at tIndexth pos.

        for (sIndex = 0; sIndex <= sLen; sIndex++)
            costsPrev[sIndex] = sIndex;

        for (tIndex = 1; tIndex <= tLen; tIndex++) {
            tIndexChar = t.charAt(tIndex - 1);
            costs[0] = tIndex;

            for (sIndex = 1; sIndex <= sLen; sIndex++) {
                cost = (s.charAt(sIndex - 1) == tIndexChar) ? 0 : 1;
                // minimum of cell to the left+1, to the top+1, to the
                // diagonally left and to the up +cost
                costs[sIndex] = Math.min(Math.min(costs[sIndex - 1] + 1,
                                                  costsPrev[sIndex] + 1),
                                         costsPrev[sIndex - 1] + cost);
            }

            // copy current distance counts to 'previous row' distance counts
            tmpArr = costsPrev;
            costsPrev = costs;
            costs = tmpArr;
        }

        // we just switched costArr and prevCostArr, so prevCostArr now actually
        // has the most recent cost counts
        return costsPrev[sLen];
    }
}

/*
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
*/