package edu.tum.uc.jvm.utility.analysis;

import java.util.LinkedList;
import java.util.List;

public class Flow {
	private String sink;
	private List<String> source;
	private List<Chop> chopNodes;
	
	public void addSource(String source){
		if(this.source == null)
			this.source = new LinkedList<String>();
		this.source.add(source);
	}
	public void addChopNode(int p_byteCodeIndex, String p_ownerMethod){
		if(this.chopNodes == null) 
			chopNodes = new LinkedList<Chop>();
		this.chopNodes.add(new Chop(p_byteCodeIndex, p_ownerMethod));
		
	}
	public List<Chop> getChopNodes(){
		return this.chopNodes;
	}
	public String getSink() {
		return sink;
	}
	public void setSink(String sink) {
		this.sink = sink;
	}
	public List<String> getSource() {
		return source;
	}
	public void setSource(List<String> source) {
	 	this.source = source;
	}
	
	public class Chop{
		private int byteCodeIndex;
		private String ownerMethod;
		
		public Chop(int p_byteCodeIndex, String p_ownerMethod){
			this.byteCodeIndex = p_byteCodeIndex;
			this.ownerMethod = p_ownerMethod;
		}
		
		public int getByteCodeIndex(){
			return this.byteCodeIndex;
		}
		public String getOwnerMethod(){
			return this.ownerMethod;
		}
	}
}
