package edu.tum.uc.jvm.instrum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

	
	/*public static void vstore(String dest)
	{
		//String principal = Thread.currentThread().getName();
		System.out.println("action = vstore");
		System.out.println("dest = " + dest);
	}
	
	public static void vload(String src)
	{
		System.out.println("action = vload");
		System.out.println("src = " + src);
	}
	
	public static void rstore(String dest)
	{
		System.out.println("action = rstore");
		System.out.println("dest = " + dest);
	}
	
	public static void rload(String src)
	{
		System.out.println("action = rload");
		System.out.println("src = " + src);
	}
	
	public static void getfield(String src) 
	{
		System.out.println("action = getfield");
		System.out.println("src = " + src);
	}
	
	public static void putfield(String dest) {
		// model knows which action to invoke, because it knows if field has value or reference type
		System.out.println("action = vputfield or rputfield");
		System.out.println("dest = " + dest);
	}
	
	public static void arith2() {
		System.out.println("action = arith2");
	}
	
	public static void ldc(String src) {
		// src: strings are enclosed by "", values go without them
		System.out.println("action = ldc");
		System.out.println("src = " + src);
	}
	
	public static void methodInvoked(String m) {
		// model knows where to store method arguments, containers can be derived from method + objectid
		System.out.println("action = invokevirtual");
		System.out.println("m = " + m);
	}*/
	
	public static void assignFromArray(Object array, int index, Object parentObject, String parentMethod, String label) {
		System.out.println("Assign from array!!");
		System.out.println("Chopnode Label = " + label);
		System.out.println("Parent obj = " + objectToString(parentObject));
		System.out.println("Parent Method = " + parentMethod);
		System.out.println("Array = " + objectToString(array));
		System.out.println("Index = " + index);
		
		HashMap<String, String> eventParams = new HashMap<String, String>();
		eventParams.put("PEP", "Java");
		eventParams.put("parentMethod", parentMethod);
		eventParams.put("parentObject", objectToString(parentObject));
		eventParams.put("parentClass", classNameFromMethod(parentMethod));
		eventParams.put("array", objectToString(array));
		eventParams.put("index", String.valueOf(index));
		eventParams.put("chopLabel", label);
		eventParams.put("threadId", "Thread" + String.valueOf(Thread.currentThread().getId()));
		IEvent event = new EventBasic("AssignFromArray", eventParams, true);
		boolean success = ucCom.sendEvent2Pdp(event);
		System.out.println(success ? "Event sent successfully" : "Error sending event");
		
		System.out.println();
	}
	
	public static void assignFromField(Object fieldOwnerObject, String fieldOwnerClass, 
			String fieldName, Object parentObject, String parentMethod, String label) {
		System.out.println("Assign from field!!");
		System.out.println("Chopnode Label = " + label);
		System.out.println("Parent obj = " + objectToString(parentObject));
		System.out.println("Parent Method = " + parentMethod);
		System.out.println("Field owner object = " + objectToString(fieldOwnerObject));
		System.out.println("Field owner class = " + fieldOwnerClass);
		System.out.println("Field name = " + fieldName);
		
		HashMap<String, String> eventParams = new HashMap<String, String>();
		eventParams.put("PEP", "Java");
		eventParams.put("parentMethod", parentMethod);
		eventParams.put("parentObject", objectToString(parentObject));
		eventParams.put("parentClass", classNameFromMethod(parentMethod));
		eventParams.put("fieldOwnerObject", objectToString(fieldOwnerObject));
		eventParams.put("fieldOwnerClass", fieldOwnerClass);
		eventParams.put("fieldName", fieldName);
		eventParams.put("chopLabel", label);
		eventParams.put("threadId", "Thread" + String.valueOf(Thread.currentThread().getId()));
		IEvent event = new EventBasic("AssignFromField", eventParams, true);
		boolean success = ucCom.sendEvent2Pdp(event);
		System.out.println(success ? "Event sent successfully" : "Error sending event");
		
		System.out.println();
	}
	
	public static void unaryAssign(Object arg, Object parentObject, String parentMethod, String label) {
		System.out.println("Unary assign operation!!");
		System.out.println("Chopnode Label = " + label);
		System.out.println("Parent obj = " + objectToString(parentObject));
		System.out.println("Parent Method = " + parentMethod);
		System.out.println("Arguments = " + objectToString(arg));
		
		HashMap<String, String> eventParams = new HashMap<String, String>();
		eventParams.put("PEP", "Java");
		eventParams.put("parentMethod", parentMethod);
		eventParams.put("parentObject", objectToString(parentObject));
		eventParams.put("parentClass", classNameFromMethod(parentMethod));
		eventParams.put("argument", objectToString(arg));
		eventParams.put("chopLabel", label);
		eventParams.put("threadId", "Thread" + String.valueOf(Thread.currentThread().getId()));
		IEvent event = new EventBasic("UnaryAssign", eventParams, true);
		boolean success = ucCom.sendEvent2Pdp(event);
		System.out.println(success ? "Event sent successfully" : "Error sending event");
		
		System.out.println();
	}
	
	public static void binaryAssign(Object arg1, Object arg2, Object parentObject, String parentMethod, String label) {
		System.out.println("Binary assign operation!!");
		System.out.println("Chopnode Label = " + label);
		System.out.println("Parent obj = " + objectToString(parentObject));
		System.out.println("Parent Method = " + parentMethod);
		System.out.println("Argument 1 = " + objectToString(arg1));
		System.out.println("Argument 2 = " + objectToString(arg2));
		
		HashMap<String, String> eventParams = new HashMap<String, String>();
		eventParams.put("PEP", "Java");
		eventParams.put("parentMethod", parentMethod);
		eventParams.put("parentObject", objectToString(parentObject));
		eventParams.put("parentClass", classNameFromMethod(parentMethod));
		eventParams.put("argument1", objectToString(arg1));
		eventParams.put("argument2", objectToString(arg2));
		eventParams.put("chopLabel", label);
		eventParams.put("threadId", "Thread" + String.valueOf(Thread.currentThread().getId()));
		IEvent event = new EventBasic("BinaryAssign", eventParams, true);
		boolean success = ucCom.sendEvent2Pdp(event);
		System.out.println(success ? "Event sent successfully" : "Error sending event");
		
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
		
		HashMap<String, String> eventParams = new HashMap<String, String>();
		eventParams.put("PEP", "Java");
		eventParams.put("parentMethod", parentMethod);
		eventParams.put("calledMethod", calledMethod);
		eventParams.put("callerObject", objectToString(caller));
		eventParams.put("parentObject", objectToString(parentObject));
		eventParams.put("parentClass", classNameFromMethod(parentMethod));
		eventParams.put("methodArgs", JSONArray.toJSONString(Arrays.asList(objectsToStrings(args))));
		eventParams.put("chopLabel", label);
		eventParams.put("threadId", "Thread" + String.valueOf(Thread.currentThread().getId()));
		IEvent event = new EventBasic("InvokeVirtual", eventParams, true);
		boolean success = ucCom.sendEvent2Pdp(event);
		System.out.println(success ? "Event sent successfully" : "Error sending event");
		
		System.out.println();
	}
	
	public static void staticMethodInvoked(String parentMethod, String label, String calledMethod, Object[] args, Object parentObject) {
		System.out.println("Static method invoked!!");
		System.out.println("Chopnode Label = " + label);
		System.out.println("Parent obj = " + objectToString(parentObject));
		System.out.println("Parent Method = " + parentMethod);
		System.out.println("Called Method = " + calledMethod);
		System.out.println("Arguments = " + JSONArray.toJSONString(Arrays.asList(objectsToStrings(args))));
		
		HashMap<String, String> eventParams = new HashMap<String, String>();
		eventParams.put("PEP", "Java");
		eventParams.put("parentMethod", parentMethod);
		eventParams.put("calledMethod", calledMethod);
		eventParams.put("callerClass", classNameFromMethod(calledMethod));
		eventParams.put("parentObject", objectToString(parentObject));
		eventParams.put("parentClass", classNameFromMethod(parentMethod));
		eventParams.put("methodArgs", JSONArray.toJSONString(Arrays.asList(objectsToStrings(args))));
		eventParams.put("chopLabel", label);
		eventParams.put("threadId", "Thread" + String.valueOf(Thread.currentThread().getId()));
		IEvent event = new EventBasic("InvokeStatic", eventParams, true);
		boolean success = ucCom.sendEvent2Pdp(event);
		System.out.println(success ? "Event sent successfully" : "Error sending event");
		
		System.out.println();
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
