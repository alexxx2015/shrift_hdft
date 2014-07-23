package edu.tum.uc.jvm.utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
	
	protected static Map<String, List<Long>> runtimeExec = new HashMap<String,List<Long>>();

	StatisticsWriter(String filename, ClassNode cn, byte[] instrumented, long time) {
		this.filename = filename;
		this.classNode = cn;
		this.instrumented = instrumented;
		this.time = time;
	}

	@Override
	public void run() {
		// if (this.classNode.name.toLowerCase().contains("org/pec/uc")) {
			
			File f = new File(this.filename);
			FileWriter fw;
			try {
				if (!f.getParentFile().exists()) {
					f.getParentFile().mkdirs();
					fw = new FileWriter(f);
				} else {
					fw = new FileWriter(f, true);
				}
				
				//Prepare non-instrumented data
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
				

				StringBuilder sb = new StringBuilder("=== ").append("Class:").append(this.classNode.name).append(" ===\n");				
				
				//Prepare instrumented-data
				ClassReader cr = new ClassReader(this.instrumented);
				ClassNode cnInstrumentd = new ClassNode();
				cr.accept(cnInstrumentd, 0);
				int numInstrBytecodes = 0;
				if(cnInstrumentd.methods != null){
					Iterator<MethodNode> it = cnInstrumentd.methods.iterator();
					while(it.hasNext()){
						MethodNode md = it.next();
						if(md.instructions != null){		
							sb.append(">Method: ").append(md.name).append("\n");
							sb.append("#Bytecodes (instr): ").append(md.instructions.size()).append("\n");
							Iterator<MethodNode> it2 = this.classNode.methods.iterator();
							while (it2.hasNext()) {
								MethodNode md2 = it2.next();
								if (md2.instructions != null && md.name.equals(md2.name)) {
									sb.append("#Bytecodes: ").append(md2.instructions.size()).append("\n");
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
				
				
				
				sb.append("Total #Method: ").append(this.classNode.methods.size())
						.append("\n");
				sb.append("Total #Bytecodes: ").append(numBytecodes).append("\n");
				sb.append("Total #Bytecodes (instrumented): ").append(numInstrBytecodes).append("\n");
				sb.append("Total time for instrumentation: ").append(this.time).append(" ms").append("\n	===\n");
//				sb.append("=== ").append("#totalClasses: ")
//						.append(totalNumClasses).append(", #totalMethod: ")
//						.append(StatisticsWriter.totalNumMethods)
//						.append(", #totalByteCode: ").append(totalNumBytecode)
//						.append(", #totalByteCode (instr): ").append(totalNumInstrBytecode)
//						.append(", time for instrumentation: ").append(this.time).append(" ms")
//						.append(" ===\n");
				fw.append(sb.toString());
				fw.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//		}
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

	public static void write(String filename, ClassNode cn, byte[] instrumented, long time) {
		StatisticsWriter sw = new StatisticsWriter(filename, cn, instrumented, time);
		Thread t = new Thread(sw);
//		t.start();
		t.run();
	}

}
