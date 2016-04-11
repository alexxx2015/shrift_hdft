package de.tum.in.i22.ucwebmanager.analysis;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.print.Doc;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.tum.in.i22.ucwebmanager.Configuration;

public class DocBuilder {
	public static final String TAG_ANALYSISES = "analysises";
	public static final String TAG_ANALYSIS = "analysis";
	public static final String TAG_MODE = "mode";
	public static final String TAG_CLASSPATH = "classpath";
	public static final String TAG_THIRDPARTYLIBS = "thirdPartyLibs";
	public static final String TAG_STUBS = "stubs";
	public static final String TAG_ENTRYPOINT = "entrypoint";
	public static final String TAG_POINTSTO = "points-to";
	public static final String TAG_INCLUDECLASSES = "include-classes";
	public static final String TAG_EXCLUDECLASSES = "exclude-classes";
	public static final String TAG_IGNOREINDIRECTFLOWS = "ignoreIndirectFlows";
	public static final String TAG_MULTITHREADED = "multithreaded";
	public static final String TAG_SDGFILE = "sdgfile";
	public static final String TAG_CGFILE = "cgfile";
	public static final String TAG_REPORTFILE = "reportfile";
	public static final String TAG_STATISTICS = "statistics";
	public static final String TAG_LOGFILE = "logFile";
	public static final String TAG_COMPUTECHOPS = "computeChops";
	public static final String TAG_SYSTEMOUT = "systemout";
	public static final String TAG_OBJECTSENSITIVENES = "objectsensitivenes";
	public static final String TAG_SOURCESANDSINKS = "sourcesandsinks";
	public static final String TAG_SOURCE = "source";
	public static final String TAG_SINK = "sink";
	public static final String TAG_FILE = "file";
	public static final String ATTR_VALUE = "value";
	public static final String ATTR_POLICY = "policy";
	public static final String ATTR_FALLBACK = "fallback";
	public static final String ATTR_NAME = "name";
	public static final String ATTR_CLASS = "class";
	public static final String ATTR_SELECTOR = "selector";
	public static final String ATTR_PARAMS = "params";
	public static final String ATTR_INCLUDESUBCLASSES = "includeSubClasses";
	public static final String ATTR_INDIRECTCALLS = "indirectCalls";

	public void generateAnalysisConfigFile(AnalysisData data, String appName) {

		File directory = new File(Configuration.WebAppRoot
				+ "/apps/ws-flowAnalyser" + "/" + String.valueOf(appName.hashCode()) + "/"
				+ data.getAnalysisName());
		String strTableData;
		List<String> listtabledata;

		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(DocBuilder.TAG_ANALYSISES);
			doc.appendChild(rootElement);

			// staff elements
			Element analysis = doc.createElement(DocBuilder.TAG_ANALYSIS);
			rootElement.appendChild(analysis);

			// set attribute to staff element
			Attr attrname = doc.createAttribute(DocBuilder.ATTR_NAME);
			attrname.setValue(Configuration.getAppsRoot() + System.getProperty("file.separator")
					+ appName + System.getProperty("file.separator") + data.getAnalysisName());	
			analysis.setAttributeNode(attrname);

			// mode elements
			Element mode = doc.createElement(DocBuilder.TAG_MODE);

			analysis.appendChild(mode);

			Attr attrvalue = doc.createAttribute(DocBuilder.ATTR_VALUE);
			attrvalue.setValue(data.getMode());
			mode.setAttributeNode(attrvalue);

			listtabledata = data.getClasspath();
			strTableData = "";
			for (int i = 0; i < listtabledata.size(); i++) {
				if (i != 0)
					strTableData = strTableData
							+ Configuration.LOCAL_FILE_SEPARATOR
							+ listtabledata.get(i);
				else
					strTableData = strTableData + listtabledata.get(i);
			}
			String strClasspath = strTableData;
			String applicationname = Configuration.getAppsRoot()
					+ System.getProperty("file.separator") + appName + System.getProperty("file.separator")
					+ "Source" + "/" + strClasspath;

			Element classpath = doc.createElement(DocBuilder.TAG_CLASSPATH);

			analysis.appendChild(classpath);

			Attr attrvaluepath = doc.createAttribute(DocBuilder.ATTR_VALUE);

			attrvaluepath.setValue(applicationname);
			classpath.setAttributeNode(attrvaluepath);
			// temp
			// thirdpartylib elements
			Element thirdPartyLibs = doc
					.createElement(DocBuilder.TAG_THIRDPARTYLIBS);

			analysis.appendChild(thirdPartyLibs);

			Attr attrvaluetpl = doc.createAttribute(DocBuilder.ATTR_VALUE);
			listtabledata = data.getThirdPartyLibs();
			strTableData = "";
			for (int i = 0; i < listtabledata.size(); i++) {
				if (i != 0)
					strTableData = strTableData + "::" + listtabledata.get(i);
				else
					strTableData = strTableData + listtabledata.get(i);
			}
			String tblvalue = "";
			attrvaluetpl.setValue(tblvalue);
			thirdPartyLibs.setAttributeNode(attrvaluetpl);

			// stubs elements
			Element stubs = doc.createElement(DocBuilder.TAG_STUBS);
			// stubs.appendChild(doc.createTextNode("100000"));
			analysis.appendChild(stubs);

			Attr attrvaluestub = doc.createAttribute(DocBuilder.ATTR_VALUE);
			attrvaluestub.setValue(data.getStubs());
			stubs.setAttributeNode(attrvaluestub);

			Element entrypoint = doc.createElement(DocBuilder.TAG_ENTRYPOINT);

			analysis.appendChild(entrypoint);

			Attr attrvalueentry = doc.createAttribute(DocBuilder.ATTR_VALUE);
			attrvalueentry.setValue(data.getEntrypoint());
			entrypoint.setAttributeNode(attrvalueentry);
			Element pointsto = doc.createElement(DocBuilder.TAG_POINTSTO);

			analysis.appendChild(pointsto);
			Attr attrpolicy = doc.createAttribute(DocBuilder.ATTR_POLICY);

			attrpolicy.setValue(data.getPointsto_policy());
			pointsto.setAttributeNode(attrpolicy);
			Attr attrfallback = doc.createAttribute(DocBuilder.ATTR_FALLBACK);
			attrfallback.setValue(data.getPointsto_fallback());
			pointsto.setAttributeNode(attrfallback);
			// read data from points to include and exclude
			listtabledata = data.getPointsto_includeclasses();
			strTableData = "";

			for (int i = 0; i < listtabledata.size(); i++) {
				Element includeclass = doc
						.createElement(DocBuilder.TAG_INCLUDECLASSES);
				pointsto.appendChild(includeclass);
				Attr attriincludeclass = doc
						.createAttribute(DocBuilder.ATTR_VALUE);
				attriincludeclass.setValue(listtabledata.get(i));
				includeclass.setAttributeNode(attriincludeclass);
			}

			listtabledata = data.getPointsto_excludeclasses();
			strTableData = "";
			for (int i = 0; i < listtabledata.size(); i++) {
				Element excludeclass = doc
						.createElement(DocBuilder.TAG_EXCLUDECLASSES);
				pointsto.appendChild(excludeclass);
				Attr attriexcludeclass = doc
						.createAttribute(DocBuilder.ATTR_VALUE);
				attriexcludeclass.setValue(listtabledata.get(i));
				excludeclass.setAttributeNode(attriexcludeclass);
			}
			// ends

			Element ignoreIndirectFlows = doc
					.createElement(DocBuilder.TAG_IGNOREINDIRECTFLOWS);

			analysis.appendChild(ignoreIndirectFlows);
			Attr attrignoreindirect = doc
					.createAttribute(DocBuilder.ATTR_VALUE);
			attrignoreindirect.setValue(data.getIgnoreIndirectFlows());
			ignoreIndirectFlows.setAttributeNode(attrignoreindirect);

			Element multithreaded = doc
					.createElement(DocBuilder.TAG_MULTITHREADED);

			analysis.appendChild(multithreaded);
			Attr attrmultith = doc.createAttribute(DocBuilder.ATTR_VALUE);
			attrmultith.setValue(data.getMultiThreaded());
			multithreaded.setAttributeNode(attrmultith);

			Element sdgfile = doc.createElement(DocBuilder.TAG_SDGFILE);

			analysis.appendChild(sdgfile);
			Attr attrsdg = doc.createAttribute(DocBuilder.ATTR_VALUE);

			sdgfile.setAttributeNode(attrsdg);

			Element cgfile = doc.createElement(DocBuilder.TAG_CGFILE);

			analysis.appendChild(cgfile);
			Attr attrcgfile = doc.createAttribute(DocBuilder.ATTR_VALUE);

			cgfile.setAttributeNode(attrcgfile);

			Element reportfile = doc.createElement(DocBuilder.TAG_REPORTFILE);
			Element logfile = doc.createElement(DocBuilder.TAG_LOGFILE);
			analysis.appendChild(reportfile);
			analysis.appendChild(logfile);
			Attr attrreport = doc.createAttribute(DocBuilder.ATTR_VALUE);
			Attr attrlog = doc.createAttribute(DocBuilder.ATTR_VALUE);
			attrreport.setValue(data.getReportFile());
			attrlog.setValue(data.getLogFile());
			attrcgfile.setValue(data.getCgFile());
			attrsdg.setValue(data.getSdgFile());
			reportfile.setAttributeNode(attrreport);
			logfile.setAttributeNode(attrlog);

			Element computeChops = doc.createElement(DocBuilder.TAG_COMPUTECHOPS);

			analysis.appendChild(computeChops);
			Attr attrcompute = doc.createAttribute(DocBuilder.ATTR_VALUE);
			attrcompute.setValue(data.getComputeChops());
			computeChops.setAttributeNode(attrcompute);

			Element systemout = doc.createElement(DocBuilder.TAG_SYSTEMOUT);

			analysis.appendChild(systemout);
			Attr attrsystemout = doc.createAttribute(DocBuilder.ATTR_VALUE);
			attrsystemout.setValue(data.getSystemout());
			systemout.setAttributeNode(attrsystemout);

			Element objectsensitivenes = doc
					.createElement(DocBuilder.TAG_OBJECTSENSITIVENES);

			analysis.appendChild(objectsensitivenes);
			Attr attrsensitive = doc.createAttribute(DocBuilder.ATTR_VALUE);
			attrsensitive.setValue(data.getObjectsensitivenes());
			objectsensitivenes.setAttributeNode(attrsensitive);

			Element sourcesandsinks = doc.createElement(DocBuilder.TAG_SOURCESANDSINKS);
			analysis.appendChild(sourcesandsinks);
			// read tablesourcensinks data
			List<AnalysisData.SourcesSinks> listdatasourcensinks = data.getSourcesSinks();

			for (int i = 0; i < listdatasourcensinks.size(); i++) {
				if (listdatasourcensinks.get(i).getType().equals(AnalysisData.TYPES.SOURCE)) {
					Element source = doc.createElement(DocBuilder.TAG_SOURCE);
					sourcesandsinks.appendChild(source);
					Attr classvalue = doc.createAttribute(DocBuilder.ATTR_CLASS);
					classvalue.setValue(listdatasourcensinks.get(i).getClazz());
					source.setAttributeNode(classvalue);
					Attr selectorvalue = doc.createAttribute(DocBuilder.ATTR_SELECTOR);
					selectorvalue.setValue(listdatasourcensinks.get(i).getSelector());
					source.setAttributeNode(selectorvalue);
					Attr paramsvalue = doc.createAttribute(DocBuilder.ATTR_PARAMS);
					paramsvalue.setValue(listdatasourcensinks.get(i).getParams());
					source.setAttributeNode(paramsvalue);
					if (!"".equals(listdatasourcensinks.get(i).getIncludeSubClasses())) {
						Attr includeclassvalue = doc
								.createAttribute(DocBuilder.ATTR_INCLUDESUBCLASSES);
						includeclassvalue.setValue(listdatasourcensinks.get(i).getIncludeSubClasses());
						source.setAttributeNode(includeclassvalue);
					}
					if (!"".equals(listdatasourcensinks.get(i).getIndirectCalls())) {
						Attr indirectcallsvalue = doc
								.createAttribute(DocBuilder.ATTR_INDIRECTCALLS);
						indirectcallsvalue.setValue(listdatasourcensinks.get(i).getIndirectCalls());
						source.setAttributeNode(indirectcallsvalue);
					}
				} else if (listdatasourcensinks.get(i).getType().equals(AnalysisData.TYPES.SINK)) {
					Element sink = doc.createElement(DocBuilder.TAG_SINK);
					sourcesandsinks.appendChild(sink);
					Attr classvalue = doc.createAttribute(DocBuilder.ATTR_CLASS);
					classvalue.setValue(listdatasourcensinks.get(i).getClazz());
					sink.setAttributeNode(classvalue);
					Attr selectorvalue = doc.createAttribute(DocBuilder.ATTR_SELECTOR);
					selectorvalue.setValue(listdatasourcensinks.get(i).getSelector());
					sink.setAttributeNode(selectorvalue);
					Attr paramsvalue = doc.createAttribute(DocBuilder.ATTR_PARAMS);
					paramsvalue.setValue(listdatasourcensinks.get(i).getParams());
					sink.setAttributeNode(paramsvalue);
					Attr includeclassvalue = doc
							.createAttribute(DocBuilder.ATTR_INCLUDESUBCLASSES);
					includeclassvalue.setValue(listdatasourcensinks.get(i).getIncludeSubClasses());
					sink.setAttributeNode(includeclassvalue);
					Attr indirectcallsvalue = doc
							.createAttribute(DocBuilder.ATTR_INDIRECTCALLS);
					indirectcallsvalue.setValue(listdatasourcensinks.get(i).getIndirectCalls());
					sink.setAttributeNode(indirectcallsvalue);

				}
			}
			if (data.getSourcesSinksFiles().size() > 0) {
				Element Fileelement = doc.createElement(DocBuilder.TAG_FILE);
				sourcesandsinks.appendChild(Fileelement);
				Attr attrFileSave = doc.createAttribute(DocBuilder.ATTR_VALUE);
//				txtFldSnSFile.getValue().replace('\\', '/');
				attrFileSave.setValue(data.getSourcesSinksFiles().get(0));
				Fileelement.setAttributeNode(attrFileSave);
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			File dest = new File(
					Configuration.WebAppRoot + System.getProperty("file.separator") + appName + System.getProperty("file.separator")+ data.getAnalysisName() + System.getProperty("file.separator")+appName + ".xml");
			StreamResult result = new StreamResult(dest);

			transformer.transform(source, result);

			System.out.println("File saved! "+dest.getAbsolutePath());

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}
}
