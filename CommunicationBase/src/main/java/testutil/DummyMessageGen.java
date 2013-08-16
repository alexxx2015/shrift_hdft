package testutil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.tum.in.i22.uc.cm.basic.ConditionBasic;
import de.tum.in.i22.uc.cm.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.basic.DataBasic;
import de.tum.in.i22.uc.cm.basic.DataEventMapBasic;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.basic.MechanismBasic;
import de.tum.in.i22.uc.cm.basic.OslFormulaBasic;
import de.tum.in.i22.uc.cm.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.basic.SimplifiedTemporalLogicBasic;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpStatus.EStatus;
import de.tum.in.i22.uc.cm.in.IMessageFactory;
import de.tum.in.i22.uc.cm.in.MessageFactory;

public class DummyMessageGen {
	private static IMessageFactory _factory = MessageFactory.getInstance();
	
	public static IMechanism createMechanism() {
		MechanismBasic m = new MechanismBasic();
		
		
		//* set condition
		ConditionBasic condition = new ConditionBasic();
		//** set condition condition
		OslFormulaBasic formula = new OslFormulaBasic("Formula xxxx");
		condition.setCondition(formula);
		//** set condition conditionSimp
		SimplifiedTemporalLogicBasic conditionSimp = new SimplifiedTemporalLogicBasic();
		conditionSimp.setFormula(new OslFormulaBasic("Formula yyyy"));
		
		//*** set condition conditionSimp dataEventMap
		Map<IData, IEvent> map1 = new HashMap<IData, IEvent>();
		map1.put(new DataBasic("id1"), new EventBasic("event1", null));
		DataEventMapBasic dataEventMap = new DataEventMapBasic(map1);
		conditionSimp.setDataEventMap(dataEventMap);
		condition.setConditionSimp(conditionSimp);
		m.setCondition(condition);
		return m;
	}
	
	public static IResponse createResponse() {
		EStatus status = EStatus.ALLOW;
		
		List<IEvent> executeActions = new ArrayList<IEvent>();
		Map<String, String> map1 = new HashMap<String, String>();
		map1.put("key1", "val1");
		map1.put("key2", "val2");
		IEvent action1 = _factory.createEvent("event1", map1);
		IEvent action2 = _factory.createEvent("event2", map1);
		executeActions.add(action1);
		executeActions.add(action2);
		
		IEvent modifiedEvent = _factory.createEvent("eventModified", map1);
		
		ResponseBasic response = new ResponseBasic(status, executeActions, modifiedEvent);
		return response;
	}
	
	public static EStatus createStatus() {
		return EStatus.ALLOW;
	}

	public static IEvent createEvent() {
		Map<String, String> map = createDummyMap();
		IEvent event = new EventBasic("event1", map);
		return event;
	}
	
	public static IEvent createActualEvent() {
		Map<String, String> map = createDummyMap();
		IEvent event = new EventBasic("event1", map, true);
		return event;
	}
	
	private static Map<String, String> createDummyMap() {
		Map<String, String> map = new HashMap<String, String>();
		// add some entries
		map.put("key1", "value1");
		map.put("key2", "value2");
		map.put("key3", "value3");
		return map;
	}

	public static IContainer createContainer() {
		IContainer container = new ContainerBasic("class value", null);
		return container;
	}
	
	public static IData createData() {
		IData data = new DataBasic(UUID.randomUUID().toString());
		return data;
	}
	
}