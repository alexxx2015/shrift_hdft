package edu.tum.uc.jvm;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.PxpSpec;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Any2PdpClient;
import de.tum.in.i22.uc.cm.factories.IMessageFactory;
import de.tum.in.i22.uc.cm.factories.MessageFactoryCreator;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;
import de.tum.in.i22.uc.thrift.server.IThriftServer;
import de.tum.in.i22.uc.thrift.server.ThriftServerFactory;
import edu.tum.uc.jvm.utility.ConfigProperties;
import edu.tum.uc.jvm.utility.MethEvent;
import edu.tum.uc.jvm.utility.analysis.CreationSite;

/**
 * Communication interface to the PDP
 */

public class UcCommunicator {
	private String PIP_HOST = ConfigProperties
			.getProperty(ConfigProperties.PROPERTIES.PIP_HOST.toString());// "172.16.195.202";
	private String PIP_PORT = ConfigProperties
			.getProperty(ConfigProperties.PROPERTIES.PIP_PORT.toString());// "10001";

	private String PDP_HOST = ConfigProperties
			.getProperty(ConfigProperties.PROPERTIES.PDP_HOST.toString());// "172.16.195.202";
	private String PDP_PORT = ConfigProperties
			.getProperty(ConfigProperties.PROPERTIES.PDP_PORT.toString());// "9984";

	private String MYPEP_HOST = ConfigProperties
			.getProperty(ConfigProperties.PROPERTIES.MYPEP_HOST.toString());
	private String MYPEP_PORT = ConfigProperties
			.getProperty(ConfigProperties.PROPERTIES.MYPEP_PORT.toString());

	private ThriftClientFactory thriftClientFactory = new ThriftClientFactory();
	private Any2PdpClient pdpClient;

	private static UcCommunicator ucCom;

	public static UcCommunicator getInstance() {
		if (UcCommunicator.ucCom == null) {
			UcCommunicator.ucCom = new UcCommunicator();
		}
		return UcCommunicator.ucCom;
	}

	private void initPDP() {
		try {
			if (this.pdpClient == null) {
				this.pdpClient = this.thriftClientFactory
						.createAny2PdpClient(new IPLocation(this.PDP_HOST,
								Integer.parseInt(this.PDP_PORT)));
				this.pdpClient.connect();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean sendEvent2PDP2(IEvent event) {
		this.initPDP();
		IResponse response = this.pdpClient.notifyEventSync(event);
		if (response != null)
			return (response.getAuthorizationAction().isStatus(EStatus.ALLOW));
		return false;
	}

	public boolean sendEvent2PDP(MethEvent event) {
		this.initPDP();
		Map<String, String> map = new HashMap<String, String>();
		map.put("location",
				event.getMethodInvoker() + event.getMethodInvokerSig() + ":"
						+ event.getOffset());
		map.put("signature",
				event.getMethodInvokee() + event.getMethodInvokeeSig());
		map.put("delimiter", event.getType().toString());
		map.put("PEP", "Java");
		map.put("ThreadId", String.valueOf(Thread.currentThread().getId()));
		map.put("fileDescriptor", event.getFileDescriptor());
		map.put("context", event.getContextIds());
		String runningVm = ManagementFactory.getRuntimeMXBean().getName();
		String[] runningVmComp = runningVm.split("@");
		if (runningVmComp.length > 0) {
			map.put("PID", runningVmComp[0]);// Add process id
		}

		String sinkSource = "Sink";
		if (event.getSinkSource().toLowerCase().equals("source")) {
			sinkSource = "Source";
		}

		// IEvent ievent = new
		// EventBasic(event.getMethodInvokee(),map,event.isActual());
		IEvent ievent = new EventBasic(sinkSource, map, event.isActual());
		IResponse response = this.pdpClient.notifyEventSync(ievent);

		if (response != null)
			return (response.getAuthorizationAction().isStatus(EStatus.ALLOW));
		return false;
	}

	public boolean sendContextEvent2PDP(Object obj, CreationSite cs) {

		Map<String, String> param = new HashMap<String, String>();
		param.put("objectId", String.valueOf(obj.hashCode()));
		param.put("context", cs.getId());
		param.put("contextLocation", cs.getLocation());
		param.put("contextOffset", String.valueOf(cs.getOffset()));
		param.put("PEP", "Java");
		param.put("ThreadId", String.valueOf(Thread.currentThread().getId()));
		String runningVm = ManagementFactory.getRuntimeMXBean().getName();
		String[] runningVmComp = runningVm.split("@");
		if (runningVmComp.length > 0) {
			param.put("PID", runningVmComp[0]);// Add process id
		}

		IMessageFactory _messageFactory = MessageFactoryCreator
				.createMessageFactory();

		IEvent event = _messageFactory.createActualEvent("Context", param);

		IResponse response = this.pdpClient.notifyEventSync(event);

		if (response != null)
			return (response.getAuthorizationAction().isStatus(EStatus.ALLOW));
		return false;
	}

	public void regPxp() {
		initPDP();
		PxpSpec pxpSpec = new PxpSpec(MYPEP_HOST, Integer.parseInt(MYPEP_PORT),
				"JAVAPXP", "This is a simple Java PXP");
		boolean b = this.pdpClient.registerPxp(pxpSpec);
		if (b == true) {
			this.startPxpServer(pxpSpec);
		}
	}

	private void startPxpServer(final PxpSpec pxpSpec) {
		IThriftServer pxpHandler = ThriftServerFactory.createPxpThriftServer(
				pxpSpec.getPort(), new JavaPxpHandler());

		// final JavaPxp.Processor pxpProcessor = new
		// JavaPxp.Processor(pxpHandler);
		// Runnable pxpRunner = new Runnable(){
		// public void run(){
		// try {
		// TServerTransport serverTransport = new
		// TServerSocket(pxpSpec.getPort());
		// TServer server = new TSimpleServer(new
		// Args(serverTransport).processor(pxpProcessor));
		// System.out.println("Running UcCommunicator PXP server started");
		// server.serve();
		// } catch (TTransportException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// };
		new Thread(pxpHandler).start();
	}
}