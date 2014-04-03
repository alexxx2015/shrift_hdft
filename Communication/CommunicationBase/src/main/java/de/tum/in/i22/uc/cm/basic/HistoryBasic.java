package de.tum.in.i22.uc.cm.basic;

import java.util.List;
import java.util.Objects;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IHistory;

public class HistoryBasic implements IHistory {

	private List<IEvent> _trace;

	public HistoryBasic(List<IEvent> trace) {
		super();
		_trace = trace;
	}

	@Override
	public List<IEvent> getTrace() {
		return _trace;
	}


	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj instanceof HistoryBasic) {
			isEqual = Objects.equals(_trace, ((HistoryBasic) obj)._trace);
		}
		return isEqual;
	}
}