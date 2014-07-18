package edu.tum.uc.jvm.container;

public class MethodInvoked extends Container {
	
	public MethodInvoked(String p_name){
		super(ContainerType.METHOD);		
		this.setName(p_name);
	}
}
