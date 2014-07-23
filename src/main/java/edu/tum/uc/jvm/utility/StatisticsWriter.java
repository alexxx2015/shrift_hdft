package edu.tum.uc.jvm.utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class StatisticsWriter implements Runnable {
	private String filename;
	private ClassNode classNode;
	private byte[] instrumented;
	private long time;

	private static int totalNumMethods = 0;
	private static int totalNumBytecode = 0;
	private static int totalNumClasses = 0;

	private static int totalNumInstrMethods = 0;
	private static int totalNumInstrBytecode = 0;
	private static int totalNumInstrClasses = 0;

	private static Map<String, Long> runtimeTotalExec = new HashMap<String, Long>();
	private static Map<String, Long> runtimeNetworkExec = new HashMap<String, Long>();
	private static StringBuilder ToBeDumpedData = new StringBuilder();
	
	private static long executionTimeTotal = 0;

	StatisticsWriter(ClassNode cn, byte[] instrumented, long time) {
		this.classNode = cn;
		this.instrumented = instrumented;
		this.time = time;
	}
	
	public static void clear(){
		totalNumMethods = 0;
		totalNumBytecode = 0;
		totalNumClasses = 0;

		totalNumInstrMethods = 0;
		totalNumInstrBytecode = 0;
		totalNumInstrClasses = 0;

		runtimeTotalExec = new HashMap<String, Long>();
		runtimeNetworkExec = new HashMap<String, Long>();
		ToBeDumpedData = new StringBuilder();
		
		executionTimeTotal = 0;
	}
	
	public static void logRuntimeExection(String event, long totalTime, long networkTime){		
		if(runtimeTotalExec.containsKey(event))
			totalTime += runtimeTotalExec.get(event);
		runtimeTotalExec.put(event, totalTime);
		
		if(runtimeNetworkExec.containsKey(event))
			networkTime += runtimeNetworkExec.get(event);
		runtimeNetworkExec.put(event, networkTime);
	}
	
	public static void logExecutionTime(long start, long end){
		executionTimeTotal = end - start;
	}

	public static void dumpFile(String filename) {
		File f = new File(filename);
		FileWriter fw;
		try {
			if (!f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
				fw = new FileWriter(f);
			} else {
				fw = new FileWriter(f, true);
			}
			//Add Instrumentationdata
			StringBuilder sb = new StringBuilder();
			sb.append("---- INSTRUMENTATION STATISTIC ----\n");
			sb.append(ToBeDumpedData).append("\n");
			
			sb.append("---- RUNTIME STATISTIC ----\n");
			Iterator<String> runtimeExecIt = runtimeTotalExec.keySet().iterator();
			long totalTime = 0;
			long totalNetwork = 0;
			while(runtimeExecIt.hasNext()){
				String key = runtimeExecIt.next();
				long t1 = runtimeTotalExec.get(key);
				long t2 = runtimeNetworkExec.get(key);
				sb.append("TotalTime (TT): "+t1+" ms, Time Network (TN): "+t2+" ms, "+key + "\n");
				totalTime += t1;
				totalNetwork += t2;
			}
			sb.append("=== TT: "+totalTime+" ms, TN: "+totalNetwork+" ms ===\n");
			sb.append("=== Total Execution Time: "+executionTimeTotal+" ms ===\n");
			fw.append(sb.toString());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// Prepare non-instrumented data
		int numBytecodes = 0;
		if (this.classNode.methods != null) {
			Iterator<MethodNode> it = this.classNode.methods.iterator();
			while (it.hasNext()) {
				MethodNode md = it.next();
				if (md.instructions != null) {
					numBytecodes += md.instructions.size();
					this.addTotalNumBytecode(md.instructions.size());
				}
			}
			this.addTotalNumMethods(this.classNode.methods.size());
		}
		this.addTotalNumClasses(1);

		StringBuilder sb = new StringBuilder("=== ").append("Class:")
				.append(this.classNode.name).append(" ===\n");

		// Prepare instrumented-data
		ClassReader cr = new ClassReader(this.instrumented);
		ClassNode cnInstrumentd = new ClassNode();
		cr.accept(cnInstrumentd, 0);
		int numInstrBytecodes = 0;
		if (cnInstrumentd.methods != null) {
			Iterator<MethodNode> it = cnInstrumentd.methods.iterator();
			while (it.hasNext()) {
				MethodNode md = it.next();
				if (md.instructions != null) {
					sb.append(">Method: ").append(md.name)
							.append("\n");
					sb.append("#Bytecodes (instr): ")
							.append(md.instructions.size()).append("\n");
					Iterator<MethodNode> it2 = this.classNode.methods
							.iterator();
					while (it2.hasNext()) {
						MethodNode md2 = it2.next();
						if (md2.instructions != null
								&& md.name.equals(md2.name)) {
							sb.append("#Bytecodes: ")
									.append(md2.instructions.size())
									.append("\n");
						}
					}
					sb.append("\n");
					numInstrBytecodes += md.instructions.size();
					this.addTotalNumInstrBytecode(md.instructions.size());
				}
			}
			this.addTotalNumInstrMethods(cnInstrumentd.methods.size());
		}
		this.addTotalNumInstrClasses(1);

		sb.append("Total #Method: ")
				.append(this.classNode.methods.size()).append("\n");
		sb.append("Total #Bytecodes: ").append(numBytecodes)
				.append("\n");
		sb.append("Total #Bytecodes (instrumented): ")
				.append(numInstrBytecodes).append("\n");
		sb.append("Total time for instrumentation: ")
				.append(this.time).append(" ms").append("\n===");
		ToBeDumpedData.append(sb);
		// sb.append("=== ").append("#totalClasses: ")
		// .append(totalNumClasses).append(", #totalMethod: ")
		// .append(StatisticsWriter.totalNumMethods)
		// .append(", #totalByteCode: ").append(totalNumBytecode)
		// .append(", #totalByteCode (instr): ").append(totalNumInstrBytecode)
		// .append(", time for instrumentation: ").append(this.time).append(" ms")
		// .append(" ===\n");
		// }
	}

	synchronized private void addTotalNumBytecode(int i) {
		this.totalNumBytecode += i;
	}

	synchronized private void addTotalNumClasses(int i) {
		this.totalNumClasses += i;
	}

	synchronized private void addTotalNumMethods(int i) {
		this.totalNumMethods += i;
	}

	synchronized private void addTotalNumInstrBytecode(int i) {
		this.totalNumInstrBytecode += i;
	}

	synchronized private void addTotalNumInstrClasses(int i) {
		this.totalNumInstrClasses += i;
	}

	synchronized private void addTotalNumInstrMethods(int i) {
		this.totalNumInstrMethods += i;
	}

	public static void logInstrumentation(ClassNode cn, byte[] instrumented, long time) {
		StatisticsWriter sw = new StatisticsWriter(cn, instrumented, time);
		Thread t = new Thread(sw);
		// t.start();
		t.run();
	}

}
