package edu.tum.uc.jvm.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import edu.tum.uc.jvm.UcTransformer;

public class MyClassWriter extends ClassWriter {
	
	
	public MyClassWriter(ClassReader arg0, int arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}
	
	@Override		
	protected String getCommonSuperClass(final String type1, final String type2) {
		if(UcTransformer.getMyClassLoader() == null){
			return super.getCommonSuperClass(type1, type2);
		}
		
		Class<?> c, d;
        ClassLoader classLoader = UcTransformer.getMyClassLoader();
        
        
        try {
//        	System.out.println("MCW GCSC: "+type1+", "+type2);
            c = Class.forName(type1.replace('/', '.'), false, classLoader);
//        	System.out.println("MCW GCSC1: "+type1);
            d = Class.forName(type2.replace('/', '.'), false, classLoader);
//        	System.out.println("MCW GCSC2: "+type2);
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        } 

        if (c.isAssignableFrom(d)) {
            return type1;
        }

        if (d.isAssignableFrom(c)) {
            return type2;
        }
        if (c.isInterface() || d.isInterface()) {
            return "java/lang/Object";
        } else {
            do {
                c = c.getSuperclass();
            } while (!c.isAssignableFrom(d));
            return c.getName().replace('.', '/');
        }
    }

}
