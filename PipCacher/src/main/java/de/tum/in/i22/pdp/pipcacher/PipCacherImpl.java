package de.tum.in.i22.pdp.pipcacher;

import java.util.List;
import java.util.Map;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IKey;
import de.tum.in.i22.uc.cm.datatypes.IStatus;


public class PipCacherImpl implements IPdpCore2PipCacher,IPdpEngine2PipCacher {

	@Override
	public IStatus refresh(IEvent desiredEvent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus addPredicates(Map<IKey, String> predicates) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus revokePredicates(List<IKey> keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean eval(IKey key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean eval(IKey key, IEvent event2Simulate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getScopeId(IEvent event) {
		// TODO Auto-generated method stub
		return null;
	}

}
