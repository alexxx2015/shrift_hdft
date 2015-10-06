package edu.tum.uc.jvm.utility.eval;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.hibernate.validator.internal.metadata.raw.ConfigurationSource;

import edu.tum.uc.jvm.utility.ConfigProperties;
import edu.tum.uc.jvm.utility.Utility;
import edu.tum.uc.jvm.utility.analysis.Flow.Chop;
import edu.tum.uc.jvm.utility.analysis.SinkSource;
import edu.tum.uc.jvm.utility.analysis.StaticAnalysis.NODETYPE;

@WebListener
public class StatisticsUtil implements ServletContextListener {

    private static List<MethodTimer> MethodTimers = new Vector<MethodTimer>();
    // thread -> eventtimer
    private static Map<String, List<EventTimer>> EventTimers = new HashMap<>();

    public static void startMethodTimer(String methodFQName) {
	String threadId = Utility.getThreadId();
	MethodTimer timer = new MethodTimer(threadId, methodFQName);
	MethodTimers.add(timer);
	timer.start();
    }

    public static void stopMethodTimer(String methodFQName) {
	String threadId = Utility.getThreadId();
	// search for method timer by traversing list from end to start
	MethodTimer timer = getLastMethodTimer(methodFQName, threadId, false);
	if (timer != null) {
	    if (timer.isRunning()) {
		timer.stop();
	    } else {
		//System.out.println("TERROR : Last timer is already stopped for " + threadId + "." + methodFQName);
	    }
	} else {
	    // no valid timer found
	    //System.out.println("TERROR : Timer not found for " + threadId + "." + methodFQName);
	}
    }

    /**
     * Returns the last method timer for given methodFQName and threadId. If hasToBeRunning==true, then the method
     * searches for the last running timer for the thread (regardless of method).
     * 
     * @param methodFQName
     * @param threadId
     * @param hasToBeRunning
     * @return
     */
    private static MethodTimer getLastMethodTimer(String methodFQName, String threadId, boolean hasToBeRunning) {
	// search for method timer by traversing list from end to start
	for (int i = MethodTimers.size() - 1; i >= 0; i--) {
	    MethodTimer timer = MethodTimers.get(i);
	    if (hasToBeRunning && timer.getThreadID().equals(threadId)) {
		return timer;
	    } else if (timer.getThreadID().equals(threadId) && timer.getMethodFQName().equals(methodFQName)) {
		return timer;
	    }
	}
	return null;
    }

    public static void startEventTimer(String eventName, int bci, String cnOwnerMethod, String cnLabel,
	    Set<String> sinksAndSources) {
	String threadId = Utility.getThreadId();
	List<EventTimer> eventTimers = getEventTimersForThread(threadId);
	EventTimer eventTimer = new EventTimer(threadId, eventName, bci, cnOwnerMethod, cnLabel, sinksAndSources);
	eventTimers.add(eventTimer);

	MethodTimer currentMethodTimer = getLastMethodTimer(null, threadId, true);
	if (currentMethodTimer != null) {
	    currentMethodTimer.addEvent(eventTimer);
	}

	eventTimer.start();
	//System.out.println("TLOG : Timer started for " + threadId + "." + eventName);
    }

    public static void endEventCreation(String eventName) {
	String threadId = Utility.getThreadId();
	EventTimer eventTimer = getLastEventTimer(threadId);
	if (eventTimer != null && eventTimer.getEventName().equals(eventName)) {
	    if (eventTimer.isSetCreated()) {
		//System.out.println("TERROR : Last timer is already set created for " + threadId + "." + eventName);
	    } else {
		eventTimer.setCreated();
		//System.out.println("TLOG : Timer set created for " + threadId + "." + eventName);
	    }
	} else {
	    //System.out.println("TERROR : Timer not found for " + threadId + "." + eventName);
	}
    }

    public static void stopEventTimer(String eventName) {
	String threadId = Utility.getThreadId();
	EventTimer eventTimer = getLastEventTimer(threadId);
	if (eventTimer != null && eventTimer.getEventName().equals(eventName)) {
	    if (!eventTimer.isRunning()) {
		//System.out.println("TERROR : Last timer is already stopped for " + threadId + "." + eventName);
	    } else {
		eventTimer.stop();
		//System.out.println("TLOG : Timer stopped for " + threadId + "." + eventName);
	    }
	} else {
	    //System.out.println("TERROR : Timer not found for " + threadId + "." + eventName);
	}
    }

    private static List<EventTimer> getEventTimersForThread(String threadId) {
	List<EventTimer> eventTimers;
	if (EventTimers.containsKey(threadId)) {
	    eventTimers = EventTimers.get(threadId);
	} else {
	    eventTimers = new Vector<>();
	    EventTimers.put(threadId, eventTimers);
	}
	return eventTimers;
    }

    private static EventTimer getLastEventTimer(String threadId) {
	List<EventTimer> eventTimers = getEventTimersForThread(threadId);
	if (eventTimers.size() > 0) {
	    EventTimer lastTimer = eventTimers.get(eventTimers.size() - 1);
	    return lastTimer;
	}
	return null;
    }

    public static void writeToFile() {

	try {
	    File statsFile = new File(ConfigProperties.getProperty(ConfigProperties.PROPERTIES.STATISTICS));
	    if (!statsFile.getParentFile().exists()) {
		statsFile.getParentFile().mkdirs();
	    }
	    FileWriter statsFW = new FileWriter(statsFile, true);
	    StringBuilder sb = new StringBuilder();
	    sb.append("-----RUNTIME STATISTICS-----" + (new Date()) + "\n");
	    sb.append("tracking on: " + ConfigProperties.getProperty(ConfigProperties.PROPERTIES.INSTRUMENTATION)
		    + "\n");
	    sb.append("1. Method timers\n");
	    CSVStringBuilder csvSb = new CSVStringBuilder();
	    csvSb.append("Request#");
	    csvSb.append("MethodName");
	    csvSb.append("Stopped?");
	    csvSb.append("CN#proposed");
	    csvSb.append("CN#actual");
	    csvSb.append("Time");
	    csvSb.newLine();

	    for (int i = 0; i < MethodTimers.size(); i++) {
		MethodTimer methodTimer = MethodTimers.get(i);
		csvSb.append("" + (i + 1));
		csvSb.append(methodTimer.getMethodFQName());
		csvSb.append("" + !methodTimer.isRunning());
		csvSb.append("??");
		csvSb.append("" + methodTimer.getEvents().size());
		csvSb.append(Timer.formatInMilliSecs(methodTimer.getTimeInterval()));
		csvSb.newLine();
	    }

	    sb.append(csvSb.toString());
	    sb.append("\n");
	    sb.append("2. Event timers\n");
	    csvSb.reset();
	    csvSb.append("Request#");
	    csvSb.append("Event#");
	    csvSb.append("Sources/Sinks");
	    csvSb.append("CNLabel");
	    csvSb.append("CNbci");
	    csvSb.append("CNLocation");
	    csvSb.append("EventType");
	    csvSb.append("CreationTime");
	    csvSb.append("CommunicationTime");
	    csvSb.append("SummaryTime");
	    csvSb.newLine();

	    for (int i = 0; i < MethodTimers.size(); i++) {
		MethodTimer methodTimer = MethodTimers.get(i);
		for (int j = 0; j < methodTimer.getEvents().size(); j++) {
		    EventTimer eventTimer = methodTimer.getEvents().get(j);
		    csvSb.append(""+(i + 1));
		    csvSb.append(""+(j + 1));
		    csvSb.append(eventTimer.getSourcesAndSinks().toString().substring(1,
			    eventTimer.getSourcesAndSinks().toString().length() - 1)); // no NRE if flow==null
		    csvSb.append(eventTimer.getCnLabel());
		    csvSb.append("" + eventTimer.getBci());
		    csvSb.append(eventTimer.getCnLocation());
		    csvSb.append(eventTimer.getEventName());
		    csvSb.append(Timer.formatInMilliSecs(eventTimer.getCreationTimeInterval()));
		    csvSb.append(Timer.formatInMilliSecs(eventTimer.getCommTimeInterval()));
		    csvSb.append(Timer.formatInMilliSecs(eventTimer.getCreationTimeInterval()
			    + eventTimer.getCommTimeInterval()));
		    csvSb.newLine();
		}
	    }
	    sb.append(csvSb.toString());
	    sb.append("\n\n\n\n");

	    statsFW.append(sb.toString());
	    statsFW.close();

	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
	//System.out.println("Writing statistics ...");
	writeToFile();
	//System.out.println("Statistics can be found in " + ConfigProperties.getProperty(ConfigProperties.PROPERTIES.STATISTICS));
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {

    }
}
