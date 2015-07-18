package edu.tum.uc.jvm.instrum;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.validator.Var;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.thrift.types.TAny2Any.AsyncProcessor.newInitialRepresentation;
import edu.tum.uc.jvm.UcCommunicator;
import edu.tum.uc.jvm.utility.UnsafeUtil;
import edu.tum.uc.jvm.utility.analysis.SinkSource;
import edu.tum.uc.jvm.utility.analysis.StaticAnalysis;

public class InstrumDelegate {
	
	private static UcCommunicator ucCom = UcCommunicator.getInstance();
	
	public static Map<String, String> HelperMethods = new HashMap<String, String>();
	private static Set<String> InstrumentedClasses = new HashSet<String>();

	public static boolean classIsInstrumented(String className) {
		return InstrumentedClasses.contains(className);	
	}
	
	public static void addInstrumentedClassName(String className) {
		InstrumentedClasses.add(className.replace("/", "."));
	}
	
	public static void readArray(Object array, int index, Object arrayAtIndex, 
			Object parentObject, String parentMethod, String label) {
		System.out.println("Read array!!");
		System.out.println("Chopnode Label = " + label);
		System.out.println("Parent obj = " + objectToString(parentObject));
		System.out.println("Parent Method = " + parentMethod);
		System.out.println("Array = " + objectToString(array));
		System.out.println("Index = " + index);
		System.out.println("Array@Index = " + objectToString(arrayAtIndex));
		
		Map<String, String> eventParams = new HashMap<String, String>();
		eventParams.put("parentObjectAddress", getAddress(parentObject));
		eventParams.put("parentClass", getClass(parentObject, parentMethod));
		eventParams.put("parentMethod", getMethod(parentMethod));
		eventParams.put("arrayClass", getClass(array));
		eventParams.put("arrayAddress", getAddress(array));
		eventParams.put("index", String.valueOf(index));
		eventParams.put("elementClass", getClass(arrayAtIndex));
		eventParams.put("elementAddress", getAddress(arrayAtIndex));
		eventParams.put("chopLabel", label);
		sendEvent("ReadArray", eventParams);
		
		System.out.println();
	}
	
	public static void writeArray(Object array, int index, Object value,
			Object parentObject, String parentMethod, String label) {
		System.out.println("Write array!!");
		System.out.println("Chopnode Label = " + label);
		System.out.println("Parent obj = " + objectToString(parentObject));
		System.out.println("Parent Method = " + parentMethod);
		System.out.println("Array = " + objectToString(array));
		System.out.println("Index = " + index);
		System.out.println("Value to insert = " + objectToString(value));
		
		Map<String, String> eventParams = new HashMap<String, String>();
		eventParams.put("parentObjectAddress", getAddress(parentObject));
		eventParams.put("parentClass", getClass(parentObject, parentMethod));
		eventParams.put("parentMethod", getMethod(parentMethod));
		eventParams.put("arrayClass", getClass(array));
		eventParams.put("arrayAddress", getAddress(array));
		eventParams.put("index", String.valueOf(index));
		eventParams.put("valueClass", getClass(value));
		eventParams.put("valueAddress", getAddress(value));
		eventParams.put("chopLabel", label);
		sendEvent("WriteArray", eventParams);
		
		System.out.println();
	}
	
	public static void readField(Object fieldOwnerObject, Object fieldValue, String fieldOwnerClass, 
			String fieldName, Object parentObject, String parentMethod, String label) {
		System.out.println("Read field!!");
		System.out.println("Chopnode Label = " + label);
		System.out.println("Parent obj = " + objectToString(parentObject));
		System.out.println("Parent Method = " + parentMethod);
		System.out.println("Field owner object = " + objectToString(fieldOwnerObject));
		System.out.println("Field owner class = " + fieldOwnerClass);
		System.out.println("Field name = " + fieldName);
		System.out.println("Field value = " + objectToString(fieldValue));
		
		Map<String, String> eventParams = new HashMap<String, String>();
		eventParams.put("parentObjectAddress", getAddress(parentObject));
		eventParams.put("parentClass", getClass(parentObject, parentMethod));
		eventParams.put("parentMethod", getMethod(parentMethod));
		eventParams.put("fieldOwnerClass", fieldOwnerObject != null ? 
				getClass(fieldOwnerObject) : fieldOwnerClass);
		eventParams.put("fieldOwnerAddress", getAddress(fieldOwnerObject));
		eventParams.put("fieldName", fieldName);
		eventParams.put("fieldValueClass", getClass(fieldValue));
		eventParams.put("fieldValueAddress", getAddress(fieldValue));
		eventParams.put("chopLabel", label);
		sendEvent("ReadField", eventParams);
		
		System.out.println();
	}
	
	public static void writeField(Object fieldOwnerObject, Object assignee, String fieldOwnerClass,
			String fieldName, Object parentObject, String parentMethod, String label) {
		System.out.println("Write field!!");
		System.out.println("Chopnode Label = " + label);
		System.out.println("Parent obj = " + objectToString(parentObject));
		System.out.println("Parent Method = " + parentMethod);
		System.out.println("Field owner object = " + objectToString(fieldOwnerObject));
		System.out.println("Field owner class = " + fieldOwnerClass);
		System.out.println("Field name = " + fieldName);
		System.out.println("Assignee = " + objectToString(assignee));
		
		Map<String, String> eventParams = new HashMap<String, String>();
		eventParams.put("parentObjectAddress", getAddress(parentObject));
		eventParams.put("parentClass", getClass(parentObject, parentMethod));
		eventParams.put("parentMethod", getMethod(parentMethod));
		eventParams.put("fieldOwnerClass", fieldOwnerObject != null ? 
				getClass(fieldOwnerObject) : fieldOwnerClass);
		eventParams.put("fieldOwnerClassIsInstrumented", String.valueOf(classIsInstrumented(getClass(fieldOwnerObject, fieldOwnerClass))));
		eventParams.put("fieldOwnerAddress", getAddress(fieldOwnerObject));
		eventParams.put("fieldName", fieldName);
		eventParams.put("assigneeClass", getClass(assignee));
		eventParams.put("assigneeAddress", getAddress(assignee));
		eventParams.put("chopLabel", label);
		sendEvent("WriteField", eventParams);
		
		System.out.println();
	}
	
	public static void unaryAssign(Object arg, Object parentObject, String parentMethod, String label) {
		System.out.println("Unary assign operation!!");
		System.out.println("Chopnode Label = " + label);
		System.out.println("Parent obj = " + objectToString(parentObject));
		System.out.println("Parent Method = " + parentMethod);
		System.out.println("Arguments = " + objectToString(arg));
		
		Map<String, String> eventParams = new HashMap<String, String>();
		eventParams.put("parentObjectAddress", getAddress(parentObject));
		eventParams.put("parentClass", getClass(parentObject, parentMethod));
		eventParams.put("parentMethod", getMethod(parentMethod));
		eventParams.put("argumentClass", getClass(arg));
		eventParams.put("argumentAddress", getAddress(arg));
		eventParams.put("chopLabel", label);
		sendEvent("UnaryAssign", eventParams);
		
		System.out.println();
	}
	
	public static void binaryAssign(Object arg1, Object arg2, Object parentObject, String parentMethod, String label) {
		System.out.println("Binary assign operation!!");
		System.out.println("Chopnode Label = " + label);
		System.out.println("Parent obj = " + objectToString(parentObject));
		System.out.println("Parent Method = " + parentMethod);
		System.out.println("Argument 1 = " + objectToString(arg1));
		System.out.println("Argument 2 = " + objectToString(arg2));
		
		Map<String, String> eventParams = new HashMap<String, String>();
		eventParams.put("parentObjectAddress", getAddress(parentObject));
		eventParams.put("parentClass", getClass(parentObject, parentMethod));
		eventParams.put("parentMethod", getMethod(parentMethod));
		eventParams.put("argument1", objectToString(arg1));
		eventParams.put("argument2", objectToString(arg2));
		eventParams.put("chopLabel", label);
		sendEvent("BinaryAssign", eventParams);
		
		System.out.println();
	}
	
	public static void instanceMethodInvoked(String parentMethod, String label, String calledMethod, Object[] args, Object parentObject, Object caller) {
		System.out.println("Instance method invoked!!");
		System.out.println("Chopnode Label = " + label);
		System.out.println("Parent obj = " + objectToString(parentObject));
		System.out.println("Parent Method = " + parentMethod);
		System.out.println("Caller obj = " + objectToString(caller));
		System.out.println("Called Method = " + calledMethod);
		System.out.println("Arguments = " + JSONArray.toJSONString(Arrays.asList(objectsToStrings(args))));
		
		Map<String, String> eventParams = new HashMap<String, String>();
		eventParams.put("parentObjectAddress", getAddress(parentObject));
		eventParams.put("parentClass", getClass(parentObject, parentMethod));
		eventParams.put("parentMethod", getMethod(parentMethod));
		eventParams.put("callerObjectAddress", getAddress(caller));
		eventParams.put("callerObjectClass", getClass(caller, calledMethod));
		eventParams.put("calledMethod", getMethod(calledMethod));
		eventParams.put("methodArgs", JSONArray.toJSONString(Arrays.asList(objectsToStrings(args))));
		eventParams.put("callerObjectIsInstrumented", String.valueOf(classIsInstrumented(getClass(caller, calledMethod))));
		eventParams.put("chopLabel", label);
		sendEvent("CallInstanceMethod", eventParams);
		
		System.out.println();
	}
	
	public static void staticMethodInvoked(String parentMethod, String label, String calledMethod, Object[] args, Object parentObject) {
		System.out.println("Static method invoked!!");
		System.out.println("Chopnode Label = " + label);
		System.out.println("Parent obj = " + objectToString(parentObject));
		System.out.println("Parent Method = " + parentMethod);
		System.out.println("Called Method = " + calledMethod);
		System.out.println("Arguments = " + JSONArray.toJSONString(Arrays.asList(objectsToStrings(args))));
		
		Map<String, String> eventParams = new HashMap<String, String>();
		eventParams.put("parentObjectAddress", getAddress(parentObject));
		eventParams.put("parentClass", getClass(parentObject, parentMethod));
		eventParams.put("parentMethod", getMethod(parentMethod));
		eventParams.put("callerClass", getClass(calledMethod));
		eventParams.put("calledMethod", getMethod(calledMethod));
		eventParams.put("methodArgs", JSONArray.toJSONString(Arrays.asList(objectsToStrings(args))));
		eventParams.put("chopLabel", label);
		sendEvent("CallStaticMethod", eventParams);
		
		System.out.println();
	}
	
	public static void instanceMethodReturned(Object returnValue, int argsCount, int bytecodeOffset, String parentMethod,
			String label, String calledMethod, Object parentObject, Object caller) {
		System.out.println("Instance method returned!!");
		System.out.println("Chopnode Label = " + label);
		System.out.println("Parent obj = " + objectToString(parentObject));
		System.out.println("Parent Method = " + parentMethod);
		System.out.println("Caller obj = " + objectToString(caller));
		System.out.println("Called Method = " + calledMethod);
		System.out.println("Return value = " + objectToString(returnValue));
		System.out.println("Arguments count = " + argsCount);
		
		Map<String, String> eventParams = new HashMap<String, String>();
		eventParams.put("parentObjectAddress", getAddress(parentObject));
		eventParams.put("parentClass", getClass(parentObject, parentMethod));
		eventParams.put("parentMethod", getMethod(parentMethod));
		eventParams.put("callerObjectAddress", getAddress(caller));
		eventParams.put("callerObjectClass", getClass(caller, calledMethod));
		eventParams.put("calledMethod", getMethod(calledMethod));
		eventParams.put("returnValueClass", getClass(returnValue));
		eventParams.put("returnValueAddress", getAddress(returnValue));
		eventParams.put("argsCount", String.valueOf(argsCount));
		eventParams.put("sourcesMap", JSONObject.toJSONString(getSourcesMap(parentMethod, bytecodeOffset)));
		eventParams.put("callerObjectIsInstrumented", String.valueOf(classIsInstrumented(getClass(caller, calledMethod))));
		eventParams.put("chopLabel", label);
		sendEvent("ReturnInstanceMethod", eventParams);
		
		System.out.println();
	}
	
	public static void staticMethodReturned(Object returnValue, int argsCount, int bytecodeOffset, String parentMethod,
			String label, String calledMethod, Object parentObject) {
		System.out.println("Static method returned!!");
		System.out.println("Chopnode Label = " + label);
		System.out.println("Parent obj = " + objectToString(parentObject));
		System.out.println("Parent Method = " + parentMethod);
		System.out.println("Called Method = " + calledMethod);
		System.out.println("Return value = " + objectToString(returnValue));
		System.out.println("Arguments count = " + argsCount);
		
		Map<String, String> eventParams = new HashMap<String, String>();
		eventParams.put("parentObjectAddress", getAddress(parentObject));
		eventParams.put("parentClass", getClass(parentObject, parentMethod));
		eventParams.put("parentMethod", getMethod(parentMethod));
		eventParams.put("callerClass", getClass(calledMethod));
		eventParams.put("calledMethod", getMethod(calledMethod));
		eventParams.put("returnValueClass", getClass(returnValue));
		eventParams.put("returnValueAddress", getAddress(returnValue));
		eventParams.put("argsCount", String.valueOf(argsCount));
		eventParams.put("sourcesMap", JSONObject.toJSONString(getSourcesMap(parentMethod, bytecodeOffset)));
		eventParams.put("callerClassIsInstrumented", String.valueOf(classIsInstrumented(getClass(calledMethod))));
		eventParams.put("chopLabel", label);
		sendEvent("ReturnStaticMethod", eventParams);

		System.out.println();
	}
	
	public static void mainMethodReturned(String calledMethod) {
		System.out.println("Main method returned!!");
		System.out.println("Called Method = " + calledMethod);
		
		Map<String, String> eventParams = new HashMap<String, String>();
		eventParams.put("callerClass", getClass(calledMethod));
		eventParams.put("calledMethod", getMethod(calledMethod));
		sendEvent("ReturnMainMethod", eventParams);

		System.out.println();
	}
	
	public static void prepareMethodReturn(Object returnValue, Object parentObject, String parentMethod, String label) {
		System.out.println("Prepare method return!!");
		System.out.println("Chopnode Label = " + label);
		System.out.println("Parent obj = " + objectToString(parentObject));
		System.out.println("Parent Method = " + parentMethod);
		System.out.println("Return value = " + objectToString(returnValue));
		
		Map<String, String> eventParams = new HashMap<String, String>();
		eventParams.put("parentObjectAddress", getAddress(parentObject));
		eventParams.put("parentClass", getClass(parentObject, parentMethod));
		eventParams.put("parentMethod", getMethod(parentMethod));
		eventParams.put("returnValueClass", getClass(returnValue));
		eventParams.put("returnValueAddress", getAddress(returnValue));
		eventParams.put("chopLabel", label);
		sendEvent("PrepareMethodReturn", eventParams);

		System.out.println();
	}
	
	private static void sendEvent(String eventName, Map<String, String> specificParams) {
		Map<String, String> allParams = new HashMap<String, String>(specificParams);
		allParams.put("PEP", "Java");
		allParams.put("threadId", "Thread" + String.valueOf(Thread.currentThread().getId()));
		String runningVm = ManagementFactory.getRuntimeMXBean().getName();
		String[] runningVmComp = runningVm.split("@");
		if (runningVmComp.length > 0) {
			allParams.put("processId", "Proc" + runningVmComp[0]);// Add process id
		}
		IEvent event = new EventBasic(eventName, allParams, true);
		boolean success = ucCom.sendEvent2Pdp(event);
		System.out.println(success ? "Event sent successfully" : "Error sending event");
	}
	
	private static String[] objectsToStrings(Object[] objects) {
		String[] strings = new String[objects.length];
		for (int i = 0; i < objects.length; i++) {
			strings[i] = objectToString(objects[i]);
		}
		return strings;
	}
	
	private static String objectToString(Object object) {
		if (object == null) return "null";
		return getClass(object) + "|" + getAddress(object);
	}
	
	private static String getAddress(Object object) {
		if (object == null) return "null";
		if (object instanceof Double
				|| object instanceof Float
				|| object instanceof Long
				|| object instanceof Integer
				|| object instanceof Character
				|| object instanceof Byte
				|| object instanceof Boolean
				|| object instanceof Short) {
			return "null";
		}
		return String.valueOf(UnsafeUtil.getObjectAddress(object));
	}
	
	private static String getClass(Object object) {
		if (object == null) return "null";
		return object.getClass().getName();
	}
	
	/**
	 * Returns class of object if it is not null, class of methodFQN otherwise
	 * @param object
	 * @param methodFQN
	 * @return
	 */
	private static String getClass(Object object, String methodFQN) {
		if (object == null) return getClass(methodFQN);
		return getClass(object);
	}
	
	private static String getClass(String methodFQN) {
		return methodFQN.split("\\|")[0];
	}
	
	private static String getMethod(String methodFQN) {
		return methodFQN.split("\\|")[1];
	}
	
	private static Map<String, String> getSourcesMap(String parentMethodFQN, int bytecodeOffset) {
		Map<String, String> sources = new HashMap<>();
		for (SinkSource source : StaticAnalysis.getSources()) {
			if (source.getLocation().equals(parentMethodFQN.replace("|", "."))
					&& source.getOffset() == bytecodeOffset) {
				String sourceIdAndDate = source.getId() + "|" + System.currentTimeMillis();
				if (source.is_return()) {
					sources.put("ret", sourceIdAndDate);
				} else {
					if (source.getParam() == 0) {
						sources.put("obj", sourceIdAndDate);
					} else {
						sources.put("p" + source.getParam(), sourceIdAndDate);
					}
				}
			}
		}
		return sources;
	}
}
