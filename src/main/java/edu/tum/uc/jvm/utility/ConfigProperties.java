package edu.tum.uc.jvm.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public final class ConfigProperties {
	
	public static enum PROPERTIES{
		ANALYSIS_REPORT
		, PDP_PORT, PDP_HOST
		, PIP_PORT, PIP_HOST
		, MYPEP_HOST, MYPEP_PORT
		, INSTRUMENTED_CLASS_PATH
		, ENFORCEMENT
		, STATISTICS
		, BLACKLIST
		, INSTRUMENTATION
		, TIMER_T1
		, TIMER_T2
		, TIMER_T3
		, TIMER_T4
		, TIMER_T5
	}
	
	private static Properties CONFIGURATION = null;	
	
	
	public static String getProperty(String property){
		if(CONFIGURATION == null){
			URL ucConfig = ConfigProperties.class.getResource("/uc.config");
			File f = new File("./uc.config");
			if(ucConfig != null){
				f = new File(ucConfig.getFile());
			}
//			File f = new File("./uc.config");
			if(f.exists()){
				try {
					FileInputStream fis = new FileInputStream(f);
					CONFIGURATION = new Properties();
					CONFIGURATION.load(fis);
//					BufferedReader br = new BufferedReader(new FileReader(f));
//					String line;
//					while((line = br.readLine()) != null){
//						String[] lineCmp = line.split(":");
//						if(lineCmp.length == 2){
//							ConfigProperties.CONFIGURATION.put(lineCmp[0].trim(), lineCmp[1].trim());
//						}
//					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		String _return = CONFIGURATION.getProperty(property);
 		return _return;
	}	
}
