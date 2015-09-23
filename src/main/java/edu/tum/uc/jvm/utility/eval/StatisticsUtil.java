package edu.tum.uc.jvm.utility.eval;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.hibernate.validator.internal.metadata.raw.ConfigurationSource;

import edu.tum.uc.jvm.utility.ConfigProperties;
import edu.tum.uc.jvm.utility.Utility;

public class StatisticsUtil implements ServletContextListener {

    // thread -> method -> timers
    private static Map<String, Map<String, List<MethodTimer>>> MethodTimers = new HashMap<>();

    // thread -> event -> timers
    private static Map<String, Map<String, List<EventTimer>>> EventTimers = new HashMap<>();

    public static void startMethodTimer(String methodFQName) {
	String threadId = Utility.getThreadId();
	Map<String, List<MethodTimer>> methodTimers = getMethodTimersForThread(threadId);
	List<MethodTimer> timers = getTimersForMethod(methodTimers, methodFQName);
	MethodTimer timer = new MethodTimer(threadId, methodFQName);
	timer.start();
	timers.add(timer);
    }

    public static void stopMethodTimer(String methodFQName) {
	String threadId = Utility.getThreadId();
	Map<String, List<MethodTimer>> methodTimers = getMethodTimersForThread(threadId);
	List<MethodTimer> timers;
	if ((timers = methodTimers.get(methodFQName)) != null) {
	    MethodTimer lastTimer;
	    if ((lastTimer = timers.get(timers.size() - 1)) != null) {
		lastTimer.stop();
	    }
	} else {
	    System.out.println("Timer not found for " + threadId + "." + methodFQName);
	}
    }

    private static Map<String, List<MethodTimer>> getMethodTimersForThread(String threadId) {
	Map<String, List<MethodTimer>> methodTimers;
	if (MethodTimers.containsKey(threadId)) {
	    methodTimers = MethodTimers.get(threadId);
	} else {
	    methodTimers = new HashMap<>();
	    MethodTimers.put(threadId, methodTimers);
	}
	return methodTimers;
    }

    private static List<MethodTimer> getTimersForMethod(Map<String, List<MethodTimer>> methodTimers, String methodFQName) {
	List<MethodTimer> timers;
	if (methodTimers.containsKey(methodFQName)) {
	    timers = methodTimers.get(methodFQName);
	} else {
	    timers = new ArrayList<MethodTimer>();
	    methodTimers.put(methodFQName, timers);
	}
	return timers;
    }

    // as long as there is a running method timer for doGet/doPost, the thread has still to be inside that method, so
    // all chopnodes count towards the scope
    // in our example, only
    public static void logChopNode() {
	String threadId = Utility.getThreadId();
	Map<String, List<MethodTimer>> methodTimers = getMethodTimersForThread(threadId);
	// look for running timer of the thread
	MethodTimer lastTimer = null;
	for (List<MethodTimer> timers : methodTimers.values()) {
	    if ((lastTimer = timers.get(timers.size() - 1)) != null) {
		if (lastTimer.isRunning())
		    break;
	    }
	}
	if (lastTimer != null) {
	    lastTimer.chopNodePassed();
	}
    }

    public static void startEventTimer(String eventName) {
	String threadId = Utility.getThreadId();
	Map<String, List<EventTimer>> eventTimers = getEventTimersForThread(threadId);
	List<EventTimer> timers = getTimersForEvent(eventTimers, eventName);
	EventTimer timer = new EventTimer(threadId, eventName);
	timer.start();
	timers.add(timer);
    }

    public static void endEventCreation(String eventName) {
	String threadId = Utility.getThreadId();
	Map<String, List<EventTimer>> eventTimers = getEventTimersForThread(threadId);
	List<EventTimer> timers;
	if ((timers = eventTimers.get(eventName)) != null) {
	    EventTimer lastTimer;
	    if ((lastTimer = timers.get(timers.size() - 1)) != null) {
		lastTimer.setCreated();
	    }
	} else {
	    System.out.println("Timer not found for " + threadId + "." + eventName);
	}
    }

    public static void stopEventTimer(String eventName) {
	String threadId = Utility.getThreadId();
	Map<String, List<EventTimer>> eventTimers = getEventTimersForThread(threadId);
	List<EventTimer> timers;
	if ((timers = eventTimers.get(eventName)) != null) {
	    EventTimer lastTimer;
	    if ((lastTimer = timers.get(timers.size() - 1)) != null) {
		lastTimer.stop();
	    }
	} else {
	    System.out.println("Timer not found for " + threadId + "." + eventName);
	}
    }

    private static Map<String, List<EventTimer>> getEventTimersForThread(String threadId) {
	Map<String, List<EventTimer>> eventTimers;
	if (EventTimers.containsKey(threadId)) {
	    eventTimers = EventTimers.get(threadId);
	} else {
	    eventTimers = new HashMap<>();
	    EventTimers.put(threadId, eventTimers);
	}
	return eventTimers;
    }

    private static List<EventTimer> getTimersForEvent(Map<String, List<EventTimer>> eventTimers, String eventName) {
	List<EventTimer> timers;
	if (eventTimers.containsKey(eventName)) {
	    timers = eventTimers.get(eventName);
	} else {
	    timers = new ArrayList<EventTimer>();
	    eventTimers.put(eventName, timers);
	}
	return timers;
    }

    public static void writeToFile() {

	try {
	    File f = new File(ConfigProperties.getProperty(ConfigProperties.PROPERTIES.STATISTICS));
	    if (!f.getParentFile().exists()) {
		f.getParentFile().mkdirs();
	    }
	    FileWriter fw = new FileWriter(f);
	    StringBuilder sb = new StringBuilder();
	    sb.append("-----RUNTIME STATISTICS-----\n");
	    sb.append("tracking on: " + ConfigProperties.getProperty(ConfigProperties.PROPERTIES.INSTRUMENTATION)
		    + "\n");
	    sb.append("1. Time per method\n");
	    sb.append("<time> <chopnodes> <time per chopnode>");

	    // merge all timers over all threads into a method -> timers map
	    Map<String, List<MethodTimer>> methodTimers = new HashMap<String, List<MethodTimer>>();
	    MethodTimers.keySet().forEach(methodName -> {
		methodTimers.put(methodName, new ArrayList<MethodTimer>());
	    });
	    MethodTimers.values().stream().forEach(currentMap -> {
		currentMap.forEach((methodName, timers) -> {
		    methodTimers.get(methodName).addAll(timers);
		});
	    });

	    methodTimers.forEach((methodName, timers) -> {
		sb.append(methodName + " (" + timers.size() + " values):" + "\n");
		timers.forEach(timer -> {
		    sb.append("\t" + timer.getTimeInterval() + 
			    "\t" + timer.getChopNodeCount());
		});
		double average = timers.stream().mapToLong(MethodTimer::getTimeInterval).average().getAsDouble();
		double stdDev = stdDev(timers.stream().mapToLong(MethodTimer::getTimeInterval).toArray(), average);
		sb.append("\tAverage time: " + average);
		sb.append("\tStandard deviation: " + stdDev);
		average = timers.stream().mapToInt(MethodTimer::getChopNodeCount).average().getAsDouble();
		stdDev = stdDev(timers.stream().mapToLong(MethodTimer::getChopNodeCount).toArray(), average);
		sb.append("\tAverage chopnode count: " + average);
		sb.append("\tStandard deviation: " + stdDev);
		
	    });
	    
	    sb.append("2. Time per event\n");
	    sb.append("<creation> <communication> <sum>");

	    // merge all timers over all threads into an event -> timers map
	    Map<String, List<EventTimer>> eventTimers = new HashMap<String, List<EventTimer>>();
	    EventTimers.keySet().forEach(eventName -> {
		eventTimers.put(eventName, new ArrayList<EventTimer>());
	    });
	    EventTimers.values().stream().forEach(currentMap -> {
		currentMap.forEach((eventName, timers) -> {
		    eventTimers.get(eventName).addAll(timers);
		});
	    });

	    eventTimers.forEach((eventName, timers) -> {
		sb.append(eventName + " (" + timers.size() + " values):" + "\n");
		timers.forEach(timer -> {
		    sb.append("\t" + timer.getCreationTimeInterval() + 
			    "\t" + timer.getCommTimeInterval() + 
			    "\t" + timer.getTimeInterval());
		});
		double average = timers.stream().mapToLong(EventTimer::getCreationTimeInterval).average().getAsDouble();
		double stdDev = stdDev(timers.stream().mapToLong(EventTimer::getCreationTimeInterval).toArray(), average);
		sb.append("\tAverage creation time: " + average);
		sb.append("\tStandard deviation: " + stdDev);
		average = timers.stream().mapToLong(EventTimer::getCommTimeInterval).average().getAsDouble();
		stdDev = stdDev(timers.stream().mapToLong(EventTimer::getCommTimeInterval).toArray(), average);
		sb.append("\tAverage communication time: " + average);
		sb.append("\tStandard deviation: " + stdDev);
		average = timers.stream().mapToLong(EventTimer::getTimeInterval).average().getAsDouble();
		stdDev = stdDev(timers.stream().mapToLong(EventTimer::getTimeInterval).toArray(), average);
		sb.append("\tAverage summary time: " + average);
		sb.append("\tStandard deviation: " + stdDev);
	    });
	    
	    fw.append(sb.toString());
	    fw.close();
	    

	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    private static double stdDev(long[] values, double mean) {
	double result = 0;
	for (long value : values) {
	    result += Math.pow(mean - value, 2);
	}
	result = result / values.length;
	return Math.sqrt(result);
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
	// TODO Auto-generated method stub
	writeToFile();
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
	// TODO Auto-generated method stub

    }
}
