package edu.tum.uc.jvm.utility;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class UnsafeUtil {
	
	public static long getObjectAddress(Object obj) {
	    Object[] array = new Object[] {obj};
	    long baseOffset = getUnsafe().arrayBaseOffset(Object[].class);
	    return normalize(getUnsafe().getInt(array, baseOffset));
	}

	public static Object objectFromAddress(long address) {
	    Object[] array = new Object[] {null};
	    long baseOffset = getUnsafe().arrayBaseOffset(Object[].class);
	    getUnsafe().putLong(array, baseOffset, address);
	    return array[0];
	}
	
	private static Unsafe getUnsafe() {
		try {
			Field f = Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			Unsafe unsafe = (Unsafe) f.get(null);
			return unsafe;
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static long normalize(int value) {
	    if(value >= 0) return value;
	    return (~0L >>> 32) & value;
	}
	
}
