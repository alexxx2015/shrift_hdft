package pip.core.test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tum.in.i22.pip.core.IPdp2Pip;
import de.tum.in.i22.pip.core.PipHandler;
import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class PipCoreTest {
	private static final Logger _logger = Logger.getLogger(PipCoreTest.class);
	
	private static IPdp2Pip _pipHandler;
	private static IMessageFactory _messageFactory;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		_pipHandler = PipHandler.getInstance();
		_messageFactory = MessageFactoryCreator.createMessageFactory();
		File file = getJarFile();
		
		if (file != null && file.exists()) {
			_logger.debug("File file " + file.getAbsolutePath() + " found.");
			_pipHandler.updateInformationFlowSemantics(null, file, EConflictResolution.OVERWRITE);
		} else {
			_logger.fatal("Zip file not found.");
		}
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
//		_pipHandler.updateInformationFlowSemantics(null, getJarFile(), EConflictResolution.OVERWRITE);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testReadFileActionMissingParameters() {
		IEvent event = _messageFactory.createActualEvent("ReadFile", null);
		IStatus status = _pipHandler.notifyActualEvent(event);
		Assert.assertEquals(EStatus.ERROR_EVENT_PARAMETER_MISSING, status.getEStatus());
	}
	
	@Test
	public void testReadFileAction() {
		Map<String, String> map = new HashMap<>();
		map.put("InFileName", "notes1.txt");
		map.put("PID", "8293");
		map.put("ProcessName", "notepad.exe");
		IEvent event = _messageFactory.createActualEvent("ReadFile", map);
		IStatus status = _pipHandler.notifyActualEvent(event);
		Assert.assertEquals(EStatus.OKAY, status.getEStatus());
	}
	
	@Test
	public void testWriteFileAction() {
		Map<String, String> map = new HashMap<>();
		map.put("InFileName", "notes1.txt");
		map.put("PID", "1293");
		map.put("ProcessName", "notepad.exe");
		IEvent event = _messageFactory.createActualEvent("WriteFile", map);
		IStatus status = _pipHandler.notifyActualEvent(event);
		Assert.assertEquals(EStatus.OKAY, status.getEStatus());
	}
	
	@Test
	public void testCreateProcessAction() {
		Map<String, String> map = new HashMap<>();
		map.put("PID_Child", "8101");
        map.put("PID", "7192");
        map.put("VisibleWindows", "8412");
        map.put("ChildProcessName", "java");
        map.put("ParentProcessName", "eclipse");
        IEvent event = _messageFactory.createActualEvent("CreateProcess", map);
		IStatus status = _pipHandler.notifyActualEvent(event);
		Assert.assertEquals(EStatus.OKAY, status.getEStatus());
		_pipHandler.updateInformationFlowSemantics(null, new File("D:/temp/test.jar"), EConflictResolution.OVERWRITE);
	}
	
	@Test
	public void testKillProcessAction() {
		Map<String, String> map = new HashMap<>();
		map.put("PID_Child", "8101");
        IEvent event = _messageFactory.createActualEvent("KillProcess", map);
		IStatus status = _pipHandler.notifyActualEvent(event);
		Assert.assertEquals(EStatus.OKAY, status.getEStatus());
	}
	
	@Test
	public void testSetClipboardDataAction() {
		Map<String, String> map = new HashMap<>();
		map.put("PID", "8101");
		map.put("ProcessName", "thunderbird");
        IEvent event = _messageFactory.createActualEvent("SetClipboardData", map);
		IStatus status = _pipHandler.notifyActualEvent(event);
		Assert.assertEquals(EStatus.OKAY, status.getEStatus());
	}
	
	private static File getJarFile() {
		File file = FileUtils.toFile(PipCoreTest.class.getResource("/test.jar"));
		return file;
	}

}
