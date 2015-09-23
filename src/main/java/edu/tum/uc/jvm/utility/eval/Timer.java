package edu.tum.uc.jvm.utility.eval;

public abstract class Timer {

    protected String threadId;
    
    protected long startTime;
    protected long stopTime;
    
    public long getTimeInterval() {
	return stopTime - startTime;
    }
   
    public void start() {
	this.startTime = System.nanoTime();
    }
    
    public void stop() {
	this.stopTime = System.nanoTime();
    }
    
    public boolean isRunning() {
	return startTime > 0 && stopTime == 0;
    }
    
    public boolean hasFinished() {
	return startTime > 0 && stopTime > 0;
    }
}
