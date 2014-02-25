package de.tum.in.i22.pdp.cm.in.pip;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;

/**
 * Class for handling remote PIP requests.
 * @author Florian Kelbert
 *
 */
public class PipRequest {
	private final EPipRequestMethod _method;
	private Set<IData> _data = null;
	private Set<IContainer> _container = null;
	private IEvent _event = null;
	private EPipResponse _response = null;

	private PipRequest(EPipRequestMethod method, IEvent event, Set<IData> data, Set<IContainer> container, EPipResponse response) {
		_method = method;

		if (method == EPipRequestMethod.HAS_DATA && data != null && data.size() > 0) {
			_data = data;
		}
		else if (method == EPipRequestMethod.HAS_CONTAINER && container != null && container.size() > 0) {
			_container = container;
		}
		else if (method == EPipRequestMethod.NOTIFY_EVENT && event != null && response != null) {
			_event = event;
			_response = response;
		}
		else {
			throw new RuntimeException("Method parameter combination in PipRequest not possible!");
		}
	}

	public PipRequest(IEvent event, EPipResponse response) {
		this(EPipRequestMethod.NOTIFY_EVENT, event, null, null, response);
	}

	public <T> PipRequest(EPipRequestMethod method, IData ... data) {
		this(method, null, new HashSet<IData>(Arrays.asList(data)), null, null);
	}

	public PipRequest(EPipRequestMethod method, IContainer ... container) {
		this(method, null, null, new HashSet<IContainer>(Arrays.asList(container)), null);
	}

	public EPipRequestMethod getMethod() {
		return _method;
	}

	public Set<IData> getData() {
		return _data;
	}

	public Set<IContainer> getContainer() {
		return _container;
	}

	public IEvent getEvent() {
		return _event;
	}

	public EPipResponse getResponse() {
		return _response;
	}

	@Override
	public String toString() {
		return "PipRequest [_method=" + _method + ", _data=" + _data
				+ ", _container=" + _container + ", _event=" + _event + "]";
	}
}
