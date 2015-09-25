package edu.tum.uc.jvm.utility.eval;

public class MethodTimer extends Timer {

    private String methodFQName;
    private int chopNodeCount = 0;

    public MethodTimer(String threadId, String methodFQName) {
	this.threadId = threadId;
	this.methodFQName = methodFQName;
    }
    
    public String getMethodFQName() {
	return methodFQName;
    }
    
    public int getChopNodeCount() {
	return chopNodeCount;
    }
    
    public long getTimePerChopNode() {
	return getTimeInterval() / getChopNodeCount();
    }
    
    public String getUniqueKey() {
	return threadId+methodFQName+startTime;
    }
    
    public void chopNodePassed() {
	chopNodeCount++;
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
