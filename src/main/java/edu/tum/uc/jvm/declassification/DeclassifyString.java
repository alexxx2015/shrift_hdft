package edu.tum.uc.jvm.declassification;

public class DeclassifyString {

	public static String declassify(String o) {
		return new StringBuilder(o).reverse().toString();
	}

}
