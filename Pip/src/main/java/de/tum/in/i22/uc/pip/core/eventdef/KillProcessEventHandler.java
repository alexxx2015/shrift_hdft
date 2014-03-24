package de.tum.in.i22.uc.pip.core.eventdef;


import java.util.Set;

import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.ParameterNotFoundException;

public class KillProcessEventHandler extends BaseEventHandler {

	public KillProcessEventHandler() {
		super();
	}

	@Override
	public IStatus execute() {
		String pid = null;
//		String processName = null;

		try {
			pid = getParameterValue("PID_Child");
//			processName = getParameterValue("ChildProcessName");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IContainer processContainer = ifModel.getContainer(new NameBasic(pid));

		// check if container for process exists
		if (processContainer != null) {
			ifModel.emptyContainer(processContainer);

			// also remove all depending containers
			Set<IContainer> closureSet = ifModel.getAliasTransitiveReflexiveClosure(processContainer);
			for (IContainer cont : closureSet) {
				ifModel.remove(cont);
			}

			ifModel.removeAllAliasesFrom(processContainer);
			ifModel.removeAllAliasesTo(processContainer);
			ifModel.remove(processContainer);

			for (IName nm : ifModel.getAllNamingsFrom(processContainer)) {
				ifModel.removeName(nm);
			}
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}
}