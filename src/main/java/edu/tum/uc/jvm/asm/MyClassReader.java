package edu.tum.uc.jvm.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;

public class MyClassReader extends ClassReader {
	
	Label[] myLabel;

	public MyClassReader(byte[] arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
	@Override
    protected Label readLabel(int offset, Label[] labels) {
		myLabel = labels;		
		if(myLabel[offset] == null){
			myLabel[offset] = new MyLabel(offset);
		}
		
		return super.readLabel(offset, labels);
    }
}
