package de.tum.in.i22.pip.core.manager;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.IActionHandler;
import de.tum.in.i22.pip.core.manager.db.EventHandlerDefinition;

public class EventHandlerManager implements IActionHandlerCreator {
	
	
	private static final Logger _logger = Logger
			.getLogger(EventHandlerManager.class);
	
	private Map<String, PipClassLoader> _classLoaderMap = null;
	
	public EventHandlerManager() {
		_classLoaderMap = new HashMap<>();
	}
	
	@Override
	public IActionHandler createEventHandler(String actionName) 
		throws IllegalAccessException, InstantiationException, ClassNotFoundException {
		
		String className = "de.tum.in.i22.pip.core.eventdef." + actionName + "ActionHandler";

		PipClassLoader pipClassLoader = _classLoaderMap.get(className);
		if (pipClassLoader != null) {
			_logger.trace("Load class: " + className);
	        Class<?> actionHandlerClass = pipClassLoader.loadClass(className);
	        _logger.trace("Create class " + className + " instance");
	        IActionHandler actionHandler = (IActionHandler)actionHandlerClass.newInstance();
	        return actionHandler;
		} else {
			return null;
		}
	}

	public void setClassToBeLoaded(EventHandlerDefinition eventHandlerDefinition) {
		_logger.debug("Creating class loader for class: " + eventHandlerDefinition.getClassName());
		String className = eventHandlerDefinition.getClassName();
		
		PipClassLoader pipClassLoader = new PipClassLoader(
				 PipClassLoader.class.getClassLoader(),
				 className,
				 eventHandlerDefinition.getClassFile());
		
		_classLoaderMap.put(className, pipClassLoader);
	
	}
}