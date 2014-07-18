package edu.tum.uc.jvm.container;

public class MethodExit extends Container {
	
	public MethodExit(String p_name){
		super(ContainerType.METHOD_EXIT);		
		this.setName(p_name);
	}
}
