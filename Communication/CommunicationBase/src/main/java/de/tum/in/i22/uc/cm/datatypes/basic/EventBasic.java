package de.tum.in.i22.uc.cm.datatypes.basic;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.common.base.MoreObjects;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.settings.Settings;

public class EventBasic implements IEvent, Serializable {
	private static final long serialVersionUID = 4317254908091570374L;
	public static final String PEP_PARAMETER_KEY = Settings.getInstance().getPep();

	private String _name = null;
	private String _pep = null;
	private boolean _allowImpliesActual = false;
	private boolean _isActual = false;
	private final Map<String, String> _parameters = new HashMap<>();
	private long _timestamp;

	public EventBasic(String name, Map<String, String> map) {
		_name = name;
		if (map != null) {
			_parameters.putAll(map);
			_pep = _parameters.get(PEP_PARAMETER_KEY);

			String allowImpliesActual = _parameters.get(Settings.PROP_NAME_allowImpliesActual);
			if (allowImpliesActual != null) {
				// If the event has a AIA parameter, use it
				_allowImpliesActual = Boolean.valueOf(allowImpliesActual);
			}
			else {
				// otherwise, fallback to Setting default value
				_allowImpliesActual = Boolean.valueOf(Settings.getInstance().getAllowImpliesActual());
			}
		}
	}

	public EventBasic(String name, Map<String, String> map, boolean isActual) {
		this(name, map);
		_isActual = isActual;
	}

	public EventBasic(String name, Map<String, String> map, boolean isActual, long timeStamp) {
		this(name, map, isActual);
		_timestamp = timeStamp;
	}

	@Override
	public long getTimestamp() {
		return _timestamp;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public String getPep() {
		return _pep;
	}

	@Override
	public boolean isActual() {
		return _isActual;
	}

	@Override
	public Map<String, String> getParameters() {
		return Collections.unmodifiableMap(_parameters);
	}

	@Override
	public String getParameterValue(String name) {
		return name == null ? null : _parameters.get(name);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("_name", _name)
				.add("_pep", _pep)
				.add("_isActual", _isActual)
				.add("_parameters", _parameters)
				.add("_timestamp", _timestamp)
				.add(Settings.PROP_NAME_allowImpliesActual, _allowImpliesActual)
				.toString();
	}

	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj != null && this.getClass() == obj.getClass()) {
			EventBasic o = (EventBasic)obj;
			//TODO check if timestamp should be checked
			isEqual = Objects.equals(_name, o._name)
					&& Objects.equals(_isActual, o._isActual)
					&& Objects.equals(_parameters, o._parameters);
		}
		return isEqual;
	}


	@Override
	public int hashCode() {
		return Objects.hash(_name, _isActual, _parameters);
	}

	public String niceString() {
		return _name + (_isActual ? "[Actual]" : "[Desired]") +_parameters;
	}

	@Override
	public boolean allowImpliesActual() {
		return _allowImpliesActual;
	}
}
