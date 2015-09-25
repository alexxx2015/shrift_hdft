package edu.tum.uc.jvm.utility.eval;

public class EventTimer extends Timer {

    private String eventName;
    private long createdTime;

    public EventTimer(String threadId, String eventName) {
	this.threadId = threadId;
	this.eventName = eventName;
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
