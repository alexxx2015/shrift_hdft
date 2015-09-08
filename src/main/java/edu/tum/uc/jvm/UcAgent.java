package edu.tum.uc.jvm;

import java.lang.instrument.*;
import java.lang.management.ManagementFactory;

public final class UcAgent {	

	public static void premain(String p_args, Instrumentation p_instr) throws Exception{
		//uca4jvm.log.info("Invoke uca4jvm.premain");
		//uca4jvm.log.info("Loaded Class: "+p_instr.getClass().getName());
		//for(Class clazz : p_instr.getAllLoadedClasses())
			//System.out.println(clazz.getName());
			
		p_instr.addTransformer(new MyUcTransformer());
		MirrorStack.runnable = true;	
	}

	public static void main(String args, Instrumentation p_instr) throws Exception{
		//uca4jvm.log.info("Invoke ucs4jvm.main");
		//p_instr.addTransformer(new ucTransformer());
	}

	public static void agentmain(String args, Instrumentation p_instr) throws Exception{
		//uca4jvm.log.info("Invoke uca4jvm.agentmain");
		//p_instr.addTransformer(new ucTransformer());
		//for(Class clazz : p_instr.getAllLoadedClasses())
			//System.out.println(clazz.getName());
//		System.out.println("KK");
	}	
	
	
	/*
	static{
		loadAgent();
	}
	private static final String jarFilePath = "/Users/ladmin/Documents/workspace/uc4jvm.jar";

    public static void loadAgent() {
        
        String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
        int p = nameOfRunningVM.indexOf('@');
        String pid = nameOfRunningVM.substring(0, p);

        try {
            VirtualMachine vm = VirtualMachine.attach(pid);
            vm.loadAgent(jarFilePath);
            vm.detach();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }*/	
}