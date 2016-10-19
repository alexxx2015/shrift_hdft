package edu.tum.uc.jvm.declassification;

import java.util.Map;

import com.sun.jersey.api.representation.Form;

public class Declassifier {

	public static String declassify(String o) {
		return new StringBuilder(o).reverse().toString();
	}
	
	public static <T> T declassify (T o){
		if(o instanceof Form){
			Form z = (Form)o;
			z.clear();
		}
		else if (o instanceof String){
			
		}
		return o;
	}
	
	public static <T> T declassify (T o, Map<?,?> modifyParam){
		if(o instanceof Form){
			Form z = (Form)o;
			z.clear();
			for(Object k : modifyParam.keySet()){
				z.putSingle((String) k, modifyParam.get(k));
			}
		}
		else if (o instanceof String){
			
		}
		return o;
	}
}
