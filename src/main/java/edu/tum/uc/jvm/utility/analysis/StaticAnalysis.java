package edu.tum.uc.jvm.utility.analysis;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.objectweb.asm.Label;

import edu.tum.uc.jvm.utility.ConfigProperties;
import edu.tum.uc.jvm.utility.analysis.Flow.Chop;

public class StaticAnalysis {

	private static ReportReader reportreader = new ReportReader();

	public static enum NODETYPE {
		NONE, SOURCE, SINK, BOTH, ERROR, CHOP_NODE;
	}

//	static {
//		StaticAnalysis.importXML(
//				new File(ConfigProperties.getProperty(ConfigProperties.PROPERTIES.ANALYSIS_REPORT)).getAbsolutePath());
//	}

	// public StaticAnalysis() {
	// this.reportreader = new ReportReader();
	// }

	public static void importXML(String filename) {
		try {
			reportreader = new ReportReader();
			reportreader.readReport(filename);
		} catch (Exception e) {
		}
	}

	public static List<SinkSource> getSinks() {
		return reportreader.getSinks();
	}

	public static List<SinkSource> getSources() {
		return reportreader.getSources();
	}

	public static List<Flow> getFlows() {
		return reportreader.getFlows();
	}

	/*
	 * note that if the same invocation is both, output is only the one in
	 * sinks. (shouldn't matter) in case of error, nodeType.ERROR is returned
	 * and output=null
	 */
	public static List<SinkSource> getType(String fullyQualifiedName, int offset) {
		if (fullyQualifiedName.contains("zipIt")) {
			int u = 0;
			u++;
		}

		List<SinkSource> _return = new LinkedList<SinkSource>();

		Iterator<SinkSource> it = reportreader.getSinks().iterator();
		while (it.hasNext()) {
			SinkSource ss = it.next();
			if (fullyQualifiedName.equals(ss.getLocation()) && (offset == ss.getOffset())) {
				_return.add(ss);
			}
		}

		it = reportreader.getSources().iterator();
		while (it.hasNext()) {
			SinkSource ss = it.next();
			if (fullyQualifiedName.equals(ss.getLocation()) && (offset == ss.getOffset())) {
				_return.add(ss);
			}
		}
		return _return;
	}

	public static SinkSource getSinkSourceById(String id, NODETYPE type) {
		SinkSource _return = null;
		Iterator<SinkSource> it;
		if (type.equals(NODETYPE.SINK)) {
			it = reportreader.getSinks().iterator();
		} else {
			it = reportreader.getSources().iterator();
		}

		while (it.hasNext()) {
			SinkSource s = it.next();
			if (id.equals(s.getId())) {
				_return = s;
				break;
			}
		}

		if (_return == null) {
			int i = 0;
			i++;
		}

		return _return;
	}

	// TODO: chop not implemented and used yet
	public static List<Chop> getChop(String ownerMethod) {
		if (ownerMethod == null)
			return null;
		List<Flow.Chop> _return = new LinkedList<Flow.Chop>();
		List<Flow> flows = reportreader.getFlows();
		Iterator<Flow> flowIt = flows.iterator();
		while (flowIt.hasNext()) {
			Flow f = flowIt.next();
			if (f.getChopNodes() != null) {
				Iterator<Chop> chopIt = f.getChopNodes().iterator();
				while (chopIt.hasNext()) {
					Chop c = chopIt.next();
					if (ownerMethod.equals(c.getOwnerMethod()))
						_return.add(c);
				}
			}
		}

		return _return;
	}

	public static List<CreationSite> getCreationSite() {
		return reportreader.getCreationSites();
	}

	public static List<CreationSite> getCreationSiteByLabel(String location, Label lab) {
		List<CreationSite> _return = new LinkedList<CreationSite>();
		List<CreationSite> allCreationSites = getCreationSite();
		for (int i = 0; i < allCreationSites.size(); i++) {
			CreationSite cs = allCreationSites.get(i);
			if (cs.getOffset() == lab.getOffset() && cs.getLocation().startsWith(location)) {
				_return.add(cs);
			}
		}
		return _return;
	}

	public static List<CreationSite> getCreationSiteByLocation(String location, int offset, String type) {
		List<CreationSite> _return = new LinkedList<CreationSite>();
		List<CreationSite> allCreationSites = getCreationSite();
		for (int i = 0; i < allCreationSites.size(); i++) {
			CreationSite cs = allCreationSites.get(i);
			if (cs.getOffset() <= offset && cs.getLocation().startsWith(location) && type.equals(cs.getType())) {
				_return.add(cs);
			}
		}
		return _return;
	}

}
