package edu.tum.uc.jvm.utility.analysis;

import java.util.LinkedList;
import java.util.List;

public class Flow {
	private String sink;
	private List<String> source;
	
	public void addSource(String source){
		if(this.source == null)
			this.source = new LinkedList<String>();
		this.source.add(source);
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
	
	
}
