package edu.tum.uc.jvm;

import edu.tum.uc.jvm.utility.UnsafeUtil;

public class TestUnsafe {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StringBuilder s = new StringBuilder();
		s.append("Hello").append("World");
		
		long adr = UnsafeUtil.getObjectAddress(s);
		System.out.println(adr);
		
	}

}
