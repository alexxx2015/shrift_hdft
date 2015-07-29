package edu.tum.uc.jvm.utility;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class UnsafeUtil {

    public final static int NUM_BYTES_BOOLEAN = 1;
    public final static int NUM_BYTES_BYTE = 1;
    public final static int NUM_BYTES_CHAR = 2;
    public final static int NUM_BYTES_SHORT = 2;
    public final static int NUM_BYTES_INT = 4;
    public final static int NUM_BYTES_FLOAT = 4;
    public final static int NUM_BYTES_LONG = 8;
    public final static int NUM_BYTES_DOUBLE = 8;

    private static final Map<Class<?>, Integer> primitiveSizes;
    static {
	primitiveSizes = new IdentityHashMap<Class<?>, Integer>();
	primitiveSizes.put(boolean.class, NUM_BYTES_BOOLEAN);
	primitiveSizes.put(byte.class, NUM_BYTES_BYTE);
	primitiveSizes.put(char.class, NUM_BYTES_CHAR);
	primitiveSizes.put(short.class, NUM_BYTES_SHORT);
	primitiveSizes.put(int.class, NUM_BYTES_INT);
	primitiveSizes.put(float.class, NUM_BYTES_FLOAT);
	primitiveSizes.put(double.class, NUM_BYTES_DOUBLE);
	primitiveSizes.put(long.class, NUM_BYTES_LONG);
    }
    
    private static boolean compressedOops;
    static {
	try {
	    MBeanServer server = ManagementFactory.getPlatformMBeanServer();
	    ObjectName mbean = new ObjectName("com.sun.management:type=HotSpotDiagnostic");
	    CompositeDataSupport compressedOopsValue = (CompositeDataSupport) server.invoke(mbean, "getVMOption",
		    new Object[] { "UseCompressedOops" }, new String[] { "java.lang.String" });
	    compressedOops = Boolean.valueOf(compressedOopsValue.get("value").toString());
	} catch (Exception e) {
	    compressedOops = false;
	}
	
    }

    public static long getObjectAddress(Object obj) {
	Object[] array = new Object[] { obj };
	long baseOffset = getUnsafe().arrayBaseOffset(Object[].class);
	return normalize(getUnsafe().getInt(array, baseOffset));
    }

    public static Object objectFromAddress(long address) {
	Object[] array = new Object[] { null };
	long baseOffset = getUnsafe().arrayBaseOffset(Object[].class);
	getUnsafe().putLong(array, baseOffset, address);
	return array[0];
    }

    public static void replaceObject(Object oldObject, Object newObject) {
	long fromAddress = getObjectAddress(newObject);
	long toAddress = getObjectAddress(oldObject);
	long oldSize = sizeOf(oldObject);
	long newSize = sizeOf(newObject);
	if (oldSize == newSize) {
		getUnsafe().copyMemory(decode(fromAddress), decode(toAddress), oldSize);
	} else {
	    throw new IllegalArgumentException("Objects must have the same size");
	}
    }

    private static Unsafe getUnsafe() {
	try {
	    Field f = Unsafe.class.getDeclaredField("theUnsafe");
	    f.setAccessible(true);
	    Unsafe unsafe = (Unsafe) f.get(null);
	    return unsafe;
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }
    
    private static long decode(long coopAddress) {
	return compressedOops ? coopAddress << 3 : coopAddress;
    }

    private static long normalize(int value) {
	if (value >= 0)
	    return value;
	return (~0L >>> 32) & value;
    }

    private static long sizeOfInstance(Object o) {
	Unsafe u = getUnsafe();
	HashSet<Field> fields = new HashSet<Field>();
	Class<?> c = o.getClass();
	while (c != Object.class) {
	    for (Field f : c.getDeclaredFields()) {
		if ((f.getModifiers() & Modifier.STATIC) == 0) {
		    fields.add(f);
		}
	    }
	    c = c.getSuperclass();
	}

	// get offset
	long maxSize = 0;
	for (Field f : fields) {
	    long offset = u.objectFieldOffset(f);
	    if (offset > maxSize) {
		maxSize = offset;
	    }
	}

	return ((maxSize / 8) + 1) * 8;
    }

    private static long sizeOf(Object o) {
	if (o.getClass().isArray()) {
	    return sizeOfArray(o);
	} else {
	    return sizeOfInstance(o);
	}
    }

    private static long sizeOfArray(Object array) {
	long size = getUnsafe().arrayBaseOffset(array.getClass());
	final int len = Array.getLength(array);
	if (len > 0) {
	    Class<?> arrayElementClass = array.getClass().getComponentType();
	    if (arrayElementClass.isPrimitive()) {
		size += (long) len * getUnsafe().arrayIndexScale(array.getClass());
	    } else {
		size += (long) len * 8;
	    }
	}
	return ((size / 8) + 1) * 8;
    }
}
