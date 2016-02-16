package edu.tum.uc.jvm.utility;

public class MemoryAddress {
	static {
		System.loadLibrary("memadr");
	}
	
	private native void sayHello();
	
	public static void main(String[] args){
		new MemoryAddress().sayHello();
	}
}
