package edu.tum.uc.jvm.instrum;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.validator.Var;
import org.json.simple.JSONArray;

import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.thrift.types.TAny2Any.AsyncProcessor.newInitialRepresentation;
import edu.tum.uc.jvm.UcCommunicator;
import edu.tum.uc.jvm.archive.StaticAnalysis;
import edu.tum.uc.jvm.utility.UnsafeUtil;

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
	
	public static void assignFromArray(Object array, int index, Object arrayAtIndex, 
			Object parentObject, String parentMethod, String label) {
		System.out.println("Assign from array!!");
		System.out.println("Chopnode Label = " + label);
		System.out.println("Parent obj = " + objectToString(parentObject));
		System.out.println("Parent Method = " + parentMethod);
		System.out.println("Array = " + objectToString(array));
		System.out.println("Index = " + index);
		System.out.println("Array@Index = " + objectToString(arrayAtIndex));
		
		Map<String, String> eventParams = new HashMap<String, String>();
		eventParams.put("parentMethod", parentMethod);
		eventParams.put("parentObject", objectToString(parentObject));
		eventParams.put("array", objectToString(array));
		eventParams.put("index", String.valueOf(index));
		eventParams.put("arrayAtIndex", objectToString(arrayAtIndex));
		eventParams.put("chopLabel", label);
		sendEvent("AssignFromArray", eventParams);
		
		System.out.println();
	}
	
	public static void assignToArray(Object array, int index, Object value,
			Object parentObject, String parentMethod, String label) {
		System.out.println("Assign to array!!");
		System.out.println("Chopnode Label = " + label);
		System.out.println("Parent obj = " + objectToString(parentObject));
		System.out.println("Parent Method = " + parentMethod);
		System.out.println("Array = " + objectToString(array));
		System.out.println("Index = " + index);
		System.out.println("Value to insert = " + objectToString(value));
		
		Map<String, String> eventParams = new HashMap<String, String>();
		eventParams.put("parentMethod", parentMethod);
		eventParams.put("parentObject", objectToString(parentObject));
		eventParams.put("array", objectToString(array));
		eventParams.put("index", String.valueOf(index));
		eventParams.put("value", objectToString(value));
		eventParams.put("chopLabel", label);
		sendEvent("AssignToArray", eventParams);
		
		System.out.println();
	}
	
	public static void assignFromField(Object fieldOwnerObject, Object fieldValue, String fieldOwnerClass, 
			String fieldName, Object parentObject, String parentMethod, String label) {
		System.out.println("Assign from field!!");
		System.out.println("Chopnode Label = " + label);
		System.out.println("Parent obj = " + objectToString(parentObject));
		System.out.println("Parent Method = " + parentMethod);
		System.out.println("Field owner object = " + objectToString(fieldOwnerObject));
		System.out.println("Field owner class = " + fieldOwnerClass);
		System.out.println("Field name = " + fieldName);
		System.out.println("Field value = " + objectToString(fieldValue));
		
		Map<String, String> eventParams = new HashMap<String, String>();
		eventParams.put("parentMethod", parentMethod);
		eventParams.put("parentObject", objectToString(parentObject));
		eventParams.put("fieldOwnerObject", objectToString(fieldOwnerObject));
		eventParams.put("fieldOwnerClass", fieldOwnerClass);
		eventParams.put("fieldName", fieldName);
		eventParams.put("fieldValue", objectToString(fieldValue));
		eventParams.put("chopLabel", label);
		sendEvent("AssignFromField", eventParams);
		
		System.out.println();
	}
	
	public static void assignToField(Object fieldOwnerObject, Object assignee, String fieldOwnerClass,
			String fieldName, Object parentObject, String parentMethod, String label) {
		System.out.println("Assign to field!!");
		System.out.println("Chopnode Label = " + label);
		System.out.println("Parent obj = " + objectToString(parentObject));
		System.out.println("Parent Method = " + parentMethod);
		System.out.println("Field owner object = " + objectToString(fieldOwnerObject));
		System.out.println("Field owner class = " + fieldOwnerClass);
		System.out.println("Field name = " + fieldName);
		System.out.println("Assignee = " + objectToString(assignee));
		
		Map<String, String> eventParams = new HashMap<String, String>();
		eventParams.put("parentMethod", parentMethod);
		eventParams.put("parentObject", objectToString(parentObject));
		eventParams.put("fieldOwnerObject", objectToString(fieldOwnerObject));
		eventParams.put("fieldOwnerClass", fieldOwnerClass);
		eventParams.put("fieldName", fieldName);
		eventParams.put("assignee", objectToString(assignee));
		eventParams.put("chopLabel", label);
		sendEvent("AssignToField", eventParams);
		
		System.out.println();
	}
	
	public static void unaryAssign(Object arg, Object parentObject, String parentMethod, String label) {
		System.out.println("Unary assign operation!!");
		System.out.println("Chopnode Label = " + label);
		System.out.println("Parent obj = " + objectToString(parentObject));
		System.out.println("Parent Method = " + parentMethod);
		System.out.println("Arguments = " + objectToString(arg));
		
		Map<String, String> eventParams = new HashMap<String, String>();
		eventParams.put("parentMethod", parentMethod);
		eventParams.put("parentObject", objectToString(parentObject));
		eventParams.put("argument", objectToString(arg));
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
		eventParams.put("parentMethod", parentMethod);
		eventParams.put("parentObject", objectToString(parentObject));
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
		eventParams.put("parentMethod", parentMethod);
		eventParams.put("calledMethod", calledMethod);
		eventParams.put("callerObject", objectToString(caller));
		eventParams.put("parentObject", objectToString(parentObject));
		eventParams.put("methodArgs", JSONArray.toJSONString(Arrays.asList(objectsToStrings(args))));
		eventParams.put("callerObjectIsInstrumented", String.valueOf(classIsInstrumented(classNameFromMethod(calledMethod))));
		eventParams.put("chopLabel", label);
		sendEvent("InvokeVirtual", eventParams);
		
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
		eventParams.put("parentMethod", parentMethod);
		eventParams.put("calledMethod", calledMethod);
		eventParams.put("parentObject", objectToString(parentObject));
		eventParams.put("methodArgs", JSONArray.toJSONString(Arrays.asList(objectsToStrings(args))));
		eventParams.put("chopLabel", label);
		sendEvent("InvokeStatic", eventParams);
		
		System.out.println();
	}
	
	public static void instanceMethodReturned(Object returnValue, int argsCount, String parentMethod, String label, String calledMethod, Object parentObject, Object caller) {
		System.out.println("Instance method returned!!");
		System.out.println("Chopnode Label = " + label);
		System.out.println("Parent obj = " + objectToString(parentObject));
		System.out.println("Parent Method = " + parentMethod);
		System.out.println("Caller obj = " + objectToString(caller));
		System.out.println("Called Method = " + calledMethod);
		System.out.println("Return value = " + objectToString(returnValue));
		System.out.println("Arguments count = " + argsCount);
		
		Map<String, String> eventParams = new HashMap<String, String>();
		eventParams.put("parentMethod", parentMethod);
		eventParams.put("calledMethod", calledMethod);
		eventParams.put("callerObject", objectToString(caller));
		eventParams.put("parentObject", objectToString(parentObject));
		eventParams.put("returnValue", objectToString(returnValue));
		eventParams.put("argsCount", String.valueOf(argsCount));
		eventParams.put("callerObjectIsInstrumented", String.valueOf(classIsInstrumented(classNameFromMethod(calledMethod))));
		eventParams.put("chopLabel", label);
		sendEvent("ReturnVirtual", eventParams);
		
		System.out.println();
	}
	
	public static void staticMethodReturned(Object returnValue, int argsCount, String parentMethod, String label, String calledMethod, Object parentObject) {
		System.out.println("Static method returned!!");
		System.out.println("Chopnode Label = " + label);
		System.out.println("Parent obj = " + objectToString(parentObject));
		System.out.println("Parent Method = " + parentMethod);
		System.out.println("Called Method = " + calledMethod);
		System.out.println("Return value = " + objectToString(returnValue));
		System.out.println("Arguments count = " + argsCount);
		
		Map<String, String> eventParams = new HashMap<String, String>();
		eventParams.put("parentMethod", parentMethod);
		eventParams.put("calledMethod", calledMethod);
		eventParams.put("parentObject", objectToString(parentObject));
		eventParams.put("returnValue", objectToString(returnValue));
		eventParams.put("argsCount", String.valueOf(argsCount));
		eventParams.put("callerClassIsInstrumented", String.valueOf(classIsInstrumented(classNameFromMethod(calledMethod))));
		eventParams.put("chopLabel", label);
		sendEvent("ReturnStatic", eventParams);

		System.out.println();
	}
	
	public static void prepareMethodReturn(Object returnValue, Object parentObject, String parentMethod, String label) {
		System.out.println("Prepare method return!!");
		System.out.println("Chopnode Label = " + label);
		System.out.println("Parent obj = " + objectToString(parentObject));
		System.out.println("Parent Method = " + parentMethod);
		System.out.println("Return value = " + objectToString(returnValue));
		
		Map<String, String> eventParams = new HashMap<String, String>();
		eventParams.put("parentMethod", parentMethod);
		eventParams.put("parentObject", objectToString(parentObject));
		eventParams.put("returnValue", objectToString(returnValue));
		eventParams.put("chopLabel", label);
		sendEvent("PrepareReturn", eventParams);

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
		if (object instanceof Double
				|| object instanceof Float
				|| object instanceof Long
				|| object instanceof Integer
				|| object instanceof Character
				|| object instanceof Byte
				|| object instanceof Boolean
				|| object instanceof Short) {
			return object.toString();
		} else {
			return object.getClass().getName() + "@" + UnsafeUtil.getObjectAddress(object);
		}
	}
	
	private static String classNameFromMethod(String methodName) {
		return methodName.split("\\|")[0];
	}
}
