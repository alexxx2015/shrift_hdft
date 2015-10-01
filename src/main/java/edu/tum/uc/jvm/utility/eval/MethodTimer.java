package edu.tum.uc.jvm.utility.eval;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

public class MethodTimer extends Timer {

    private String methodFQName;
    private List<EventTimer> events = new Vector<>();

    public MethodTimer(String threadId, String methodFQName) {
	this.threadId = threadId;
	this.methodFQName = methodFQName;
    }
    
    public String getMethodFQName() {
	return methodFQName;
    }
    
    public String getUniqueKey() {
	return threadId+methodFQName+startTime;
    }
    
    public void addEvent(EventTimer event) {
	events.add(event);
    }
    
    public List<EventTimer> getEvents() {
	return events;
    }
    
    public String toString() {
	return threadId + " " + methodFQName + " " + getTimeInterval();
    }
    
    public boolean equals(Object other) {
	if(other == null) return false;
	else if (!(other instanceof MethodTimer)) return false;
	else {
	    MethodTimer otherMt = (MethodTimer)other;
	    return getUniqueKey().equals(otherMt.getUniqueKey());
	}
    }
}
