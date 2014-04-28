package de.tum.in.i22.uc.pip.extensions.structured;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.pip.core.ifm.BasicInformationFlowModel;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModelExtension;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModelManager;

/**
 * Visibility of this class and its methods has been developed carefully. Access
 * via {@link InformationFlowModelManager}.
 * 
 * @author Enrico Lovat
 * 
 */
public final class StructuredInformationFlowModel extends
		InformationFlowModelExtension {
	private static final Logger _logger = LoggerFactory
			.getLogger(StructuredInformationFlowModel.class);

	private BasicInformationFlowModel _bif;

	// Structured data map
	private Map<IData, Map<String, Set<IData>>> _structureMap;

	// BACKUP TABLES FOR SIMULATION
	private Map<IData, Map<String, Set<IData>>> _structureBackup;

	/**
	 * Basic constructor. Here we initialize the reference to the basic
	 * information flow model and the tables to store the structured data
	 * information.
	 */
	public StructuredInformationFlowModel() {
		_bif = InformationFlowModelManager.getInstance()
				.getBasicInformationFlowModel();
		this.reset();
	}

	@Override
	public void reset() {
		_structureMap = new HashMap<IData, Map<String, Set<IData>>>();
		_structureBackup = null;
	}

	/**
	 * Simulation step: push. Stores the current IF state, if not already stored
	 * 
	 * @return true if the state has been successfully pushed, false otherwise
	 */
	@Override
	public void push() {
		_logger.info("Pushing current PIP state...");
		if (_structureMap != null) {
			_structureBackup = new HashMap<IData, Map<String, Set<IData>>>();
			for (IData d : _structureMap.keySet()) {
				Map<String, Set<IData>> m = _structureMap.get(d);
				Map<String, Set<IData>> mbackup = new HashMap<String, Set<IData>>();
				for (String s : m.keySet()) {
					mbackup.put(s, new HashSet<IData>(mbackup.get(d)));
				}
				_structureBackup.put(d, mbackup);
			}
		}
	}

	/**
	 * Simulation step: pop. Restore a previously pushed IF state, if any.
	 * 
	 * @return true if the state has been successfully restored, false otherwise
	 */
	@Override
	public void pop() {
		_logger.info("Popping current PIP state...");
		if (_structureBackup != null) {
			_structureMap = _structureBackup;
			_structureBackup = null;
		}
	}

	/**
	 * This method takes as parameter a list of pairs (label - set of data) that
	 * represents the structure to be associated to a new structured data item,
	 * which should be returned. The behavior is to add another entry in our
	 * _structureMap table where a new IData is associated to the structure
	 * given as parameter.
	 * 
	 * The new data item associated to the structured is returned.
	 * 
	 */
	public IData newStructuredData(Map<String, Set<IData>> structure) {
		IData d = new DataBasic();
		_logger.debug("new data [ " + d + " ] for structure created.");

		_logger.debug("Current size of structured data map : "
				+ _structureMap.size());
		if (structure != null) {
			_logger.debug("Adding structure for " + d + " to the map [ "
					+ structure + "]");
			_structureMap.put(d, structure);
			_logger.debug("Current size of structured data map : "
					+ _structureMap.size());
			return d;
		} else {
			_logger.debug("structure is null. nothing to do here. returning null");
			return null;
		}
	}

	/**
	 * This method takes as parameter a data item and returns the structure
	 * associated to it. If no structure for it exists, then the
	 * <code>null</code> value is returned.
	 */
	public Map<String, Set<IData>> getStructureOf(IData data) {
		if (data == null) {
			_logger.error("no structure for NULL. returning empty map");
			return Collections.emptyMap();
		}
		Map<String, Set<IData>> map = _structureMap.get(data);
		if (map == null) {
			_logger.debug("No structure associated to data " + data);
			_logger.debug("returning empty map.");
			return Collections.emptyMap();
		}
		_logger.debug("returning structure for data " + data);
		_logger.debug("[ " + map + "]");
		return map;

	}

	@Override
	public String niceString() {
		StringBuilder sb = new StringBuilder();

		String nl = System.getProperty("line.separator");
		String arrow = " ---> ";

		sb.append("  Structure:" + nl);
		for (IData d : _structureMap.keySet()) {
			sb.append("    " + d.getId() + arrow);
			boolean first = true;
			Map<String, Set<IData>> set = _structureMap.get(d);
			for (String s : set.keySet()) {
				if (first) {
					first = false;
				} else {
					sb.append("    ");
					for (int i = 0; i < d.getId().length() + arrow.length(); i++) {
						sb.append(" ");
					}
				}
				sb.append(String.format("%-10%s [%s]", s, set.get(s)));
			}
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this)
				.add("_structure", _structureMap).toString();
	}

}
