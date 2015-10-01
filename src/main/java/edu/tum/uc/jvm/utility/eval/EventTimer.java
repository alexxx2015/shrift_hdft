package edu.tum.uc.jvm.utility.eval;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.tum.uc.jvm.utility.analysis.SinkSource;

public class EventTimer extends Timer {

    private String eventName;
    private long createdTime;
    private int bci;
    private String cnLocation;
    private String cnLabel;
    private Set<String> sourcesAndSinks = new HashSet<>();
    
    public EventTimer(String threadId, String eventName, int bci, String cnLocation, String cnLabel, Set<String> sourcesAndSinks) {
	this.threadId = threadId;
	this.eventName = eventName;
	this.bci = bci;
	this.cnLabel = cnLabel;
	this.cnLocation = cnLocation;
	this.sourcesAndSinks = sourcesAndSinks;
    }
    
    public Set<String> getSourcesAndSinks() {
        return sourcesAndSinks;
    }
    
    public String getEventName() {
	return eventName;
    }
    
    public void setCreated() {
	createdTime = System.nanoTime();
    }
    
    public boolean isSetCreated() {
	return createdTime > 0;
    }
    
    public long getCreationTimeInterval() {
	return createdTime - startTime;
    }
    
    public long getCommTimeInterval() {
	return stopTime - createdTime;
    }
    
    public int getBci() {
        return bci;
    }

    public String getCnLocation() {
        return cnLocation;
    }

    public String getCnLabel() {
        return cnLabel;
    }
    
    public String getUniqueKey() {
	return threadId+eventName+startTime;
    }
    
    public String toString() {
	return threadId + " " + eventName + " creation=" + getCreationTimeInterval() + " comm=" + getCommTimeInterval();
    }
    
    public boolean equals(Object other) {
	if(other == null) return false;
	else if (!(other instanceof EventTimer)) return false;
	else {
	    EventTimer otherMt = (EventTimer)other;
	    return getUniqueKey().equals(otherMt.getUniqueKey());
	}
    }
    
}
