package de.tum.in.i22.uc.pdp.requests;

import java.util.List;
import java.util.Map;

import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.server.PdpProcessor;

public class ListMechanismsPdpRequest extends PdpRequest<Map<String,List<String>>> {

	@Override
	public Map<String,List<String>> process(PdpProcessor processor) {
		return processor.listMechanisms();
	}

}
