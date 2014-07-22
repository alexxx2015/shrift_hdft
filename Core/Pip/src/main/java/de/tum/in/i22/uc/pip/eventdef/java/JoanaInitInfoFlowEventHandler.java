package de.tum.in.i22.uc.pip.eventdef.java;

/**
 * This class initializes all sinks and sources according to the joana output
 */

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.java.SourceSinkName;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class JoanaInitInfoFlowEventHandler extends JavaEventHandler {

	public JoanaInitInfoFlowEventHandler() {
		super();
	}

	static IData data = null;

	@Override
	protected IStatus update() {

		// This event is used only during tests to initialize the information
		// flow schema to a specific state

		String id;
		String signature;
		String location;
		String parampos;
		// String type;
		String offset;

		String listOfSources;
		String listOfSinks;
		String listOfFlows;

		String pid;

		try {
			// type = getParameterValue(_paramType);
			pid = getParameterValue("PID");
			listOfSources = getParameterValue("listOfSources");
			listOfSinks = getParameterValue("listOfSinks");
			listOfFlows = getParameterValue("listOfFlows");

		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		// If this is an information flow mapping event then fill internal
		// static mapping
		// if (type.equals("iflow")) {

		String sep1 = Settings.getInstance().getJoanaDelimiter1();
		String sep2 = Settings.getInstance().getJoanaDelimiter2();

		parseList(listOfSources, pid, "source", sep1, sep2);

		parseList(listOfSinks, pid, "sink", sep1, sep2);

		
		String[] flowsArr = listOfFlows.split(sep2);
		for (String flow: flowsArr){
			if ((flow==null)||(flow.equals(""))) break;
			String[] map = flow.split(sep1);
			if (map.length>2) iFlow.put(map[0], Arrays.copyOfRange(map, 1, map.length-2)); //first element in map is sink and last element is always null
		}
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}

	private void parseList(String listOfPoi, String pid, String type,
			String sep1, String sep2) {
		String id, location, offset, parampos, signature;

		String[] listOfPoiArr = listOfPoi.split(sep2);
		for (int i = 0; i < listOfPoiArr.length - 1; i++) {
			String currPoi = listOfPoiArr[i];
			if (currPoi == null)
				break;
			String[] poiPars = currPoi.split(sep1);
			
			assert(poiPars.length>=10);

			Map<String, String> pars = new HashMap<String, String>();
			for (int o = 0; o < 4; o++)
				pars.put(poiPars[2 * o], poiPars[2 * o + 1]);
			id = pars.get(_paramId);
			location = pars.get(_paramLocation);
			offset = pars.get(_paramOffset);
			parampos = pars.get(_paramParamPos);

			for (int o = 10; o < poiPars.length; o++) {
				signature = poiPars[o];
				addPoi(pid, type, id, location, offset, signature, parampos);
			}
		}
	}

	private void addPoi(String pid, String type, String id, String location,
			String offset, String signature, String parampos) {
		String prefix = type;

		String[] infoConts = new String[] {
				signature,
				location + _javaIFDelim + offset + _javaIFDelim + signature,
				location + _javaIFDelim + offset + _javaIFDelim + signature
						+ _javaIFDelim + parampos,
				signature + _javaIFDelim + parampos };

		for (String infoCont : infoConts) {
			// infoCont = prefix + infoCont;
			IContainer infoContId = _informationFlowModel
					.getContainer(new SourceSinkName(pid, prefix, infoCont));

			_logger.debug("contID = " + infoContId);

			if (infoContId == null) {
				IContainer signatureCont = _messageFactory.createContainer();

				_informationFlowModel.addName(new SourceSinkName(pid, prefix,
						infoCont), signatureCont, true);
			}
			_logger.debug(_informationFlowModel.toString());
		}

		
		String poiName = pid+_javaIFDelim+id;
		IContainer poiId = _informationFlowModel
				.getContainer(new NameBasic(poiName));
		if (poiId == null) {
			poiId = _messageFactory.createContainer();
			_informationFlowModel.addName(new SourceSinkName(pid, prefix,
					poiName), poiId, true);
		}

		// Process alias relationship
		IContainer sig = _informationFlowModel.getContainer(new SourceSinkName(
				pid, prefix, infoConts[0]));
		IContainer locSig = _informationFlowModel
				.getContainer(new SourceSinkName(pid, prefix, infoConts[1]));
		IContainer locSigPar = _informationFlowModel
				.getContainer(new SourceSinkName(pid, prefix, infoConts[2]));
		IContainer sigPar = _informationFlowModel
				.getContainer(new SourceSinkName(pid, prefix, infoConts[3]));

		if (type.toLowerCase().equals("source")) {
			_informationFlowModel.addAlias(sig, locSig);
			_informationFlowModel.addAlias(locSig, locSigPar);
			_informationFlowModel.addAlias(sig, sigPar);
			_informationFlowModel.addAlias(sigPar, locSigPar);
			_informationFlowModel.addAlias(locSigPar, poiId);
		} else if (type.toLowerCase().equals("sink")) {
			_informationFlowModel.addAlias(poiId, locSigPar);
			_informationFlowModel.addAlias(locSigPar, locSig);
			_informationFlowModel.addAlias(locSigPar, sigPar);
			_informationFlowModel.addAlias(locSig, sig);
			_informationFlowModel.addAlias(sigPar, sig);
		}
	}
}
