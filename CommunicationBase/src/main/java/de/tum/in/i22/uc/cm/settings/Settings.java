package de.tum.in.i22.uc.cm.settings;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.distribution.pip.EDistributedPipStrategy;

/**
 *
 * @author Stoimenov, Florian Kelbert
 * Settings are read from the specified properties file.
 * If no file is specified, file "pdp.properties" is used.
 *
 * Only the first invocation of getInstance(...) might carry the filename parameter.
 *
 */
public class Settings {

	private static Logger _logger = LoggerFactory.getLogger(Settings.class);

	private static Settings _instance;

	private static final String DEFAULT_PROPERTIES_FILE_NAME = "pdp.properties";

	private final String propertiesFilename;

	// default values will be overridden with the values from the properties file
	private int _pepGPBListenerPortNum = 10001;
	private int _pepThriftListenerPortNum=20001;
	private int _pmpListenerPortNum = 10002;
	private int _pipListenerPortNum = 10003;

	private String _pipAddress;
	private int _pipPortNum;
	private int _queueSize = 100;

	private String _pepPipeIn = "/tmp/pep2pdp";
	private String _pepPipeOut = "/tmp/pdp2pep";

	private boolean _pmpListenerEnabled = true;
	private boolean _pepGPBListenerEnabled = true;
	private boolean _pepThriftListenerEnabled = true;
	private boolean _pipListenerEnabled = true;
	private boolean _pepPipeListenerEnabled = false;

	EDistributedPipStrategy _distributedPipStrategy = EDistributedPipStrategy.PUSH;

	private Settings() {
		this(DEFAULT_PROPERTIES_FILE_NAME);
	}

	private Settings(String propertiesFilename) {
		this.propertiesFilename = propertiesFilename;
	}

	public static Settings getInstance() {
		return (_instance != null)
			? _instance
			: getInstance(DEFAULT_PROPERTIES_FILE_NAME);
	}

	public static Settings getInstance(String propertiesFilename) {
		if (_instance != null) {
			throw new IllegalStateException("PDP properties have already been initialized and loaded. "
					+ "Only the first invocation of " + Settings.class + ".getInstance() might be invoked with a parameter.");
		}

		_instance = new Settings(propertiesFilename);

		return _instance;
	}

	public void loadProperties() throws IOException {
		Properties props = SettingsLoader.loadProperties(propertiesFilename);

		loadPepGpbListenerProperties(props);
		loadPepThriftListenerProperties(props);
		loadPepPipeListenerProperties(props);
		loadPmpListenerProperties(props);
		loadPipListenerProperties(props);

		try {
			_pipAddress = props.getProperty("pip_address");
		} catch (Exception e) {
			_logger.error("Cannot read pip address.", e);
			_logger.debug("Killing the app.");
			System.exit(1);
		}

		try {
			_pipPortNum = Integer.valueOf(props.getProperty("pip_port_num"));
		} catch (Exception e) {
			_logger.error("Cannot read pip port number." + e);
			_logger.debug("Killing the app.");
			System.exit(1);
		}

		try {
			_queueSize = Integer.valueOf((String)props.get("queue_size"));
		} catch (Exception e) {
			_logger.warn("Cannot read queue size.", e);
			_logger.info("Default queue size: " + _queueSize);
		}

		try {
			_distributedPipStrategy = EDistributedPipStrategy.from((String) props.get("distributed_pip_strategy"));
		} catch (Exception e) {
			_logger.warn("Cannot read distributed pip strategy.", e);
			_logger.info("Default: " + _distributedPipStrategy);
		}
	}


	private void loadPepPipeListenerProperties(Properties props) {
		try {
			String s = props.getProperty("pep_pipe_listener_enabled");
			if (s != null) {
				_pepPipeListenerEnabled = Boolean.valueOf(s);
			}
		} catch (Exception e) {
			_logger.warn("Cannot read whether to enable pep pipe listener.", e);
			_logger.info("Enabling pep pipe listener by default: " + _pepPipeListenerEnabled);
		}

		if (_pepPipeListenerEnabled) {
			String tmpInPipe = (String) props.get("pep_pipe_in");
			String tmpOutPipe = (String) props.get("pep_pipe_out");

			if (tmpInPipe == null || tmpOutPipe == null) {
				_logger.info("Cannot read pipe information.");
				_logger.info("Default pipes: " + _pepPipeIn + " and " + _pepPipeOut);
			}
			else {
				_pepPipeIn = tmpInPipe;
				_pepPipeOut = tmpOutPipe;
			}
		}
	}

	private void loadPipListenerProperties(Properties props) {
		try {
			String s = props.getProperty("pip_listener_enabled");
			if (s != null) {
				_pipListenerEnabled = Boolean.valueOf(s);
			}
		} catch (Exception e) {
			_logger.warn("Cannot read whether to enable pip listener.", e);
			_logger.info("Enabling pip listener by default: " + _pipListenerEnabled);
		}

		if (_pipListenerEnabled) {
			try {
				_pipListenerPortNum = Integer.valueOf(props.getProperty("pip_listener_port_num"));
			} catch (Exception e) {
				_logger.warn("Cannot read pip listener port number.", e);
				_logger.info("Default port of pip listener: " + _pipListenerPortNum);
			}
		}
	}

	private void loadPepThriftListenerProperties(Properties props) {
		try {
			String s = props.getProperty("pep_Thrift_listener_enabled");
			if (s != null) {
				_pepThriftListenerEnabled  = Boolean.valueOf(s);
			}
		} catch (Exception e) {
			_logger.warn("Cannot read whether to enable pep Thrift listener.", e);
			_logger.info("Enabling pep Thrift listener by default: " + _pepThriftListenerEnabled);
		}

		if (_pepThriftListenerEnabled) {
			try {
				_pepThriftListenerPortNum = Integer.valueOf((String)props.get("pep_Thrift_listener_port_num"));
			} catch (Exception e) {
				_logger.warn("Cannot read Thrift pep listener port number.", e);
				_logger.info("Default port of Thrift pep listener: " + _pepGPBListenerPortNum);
			}
		}
	}

	private void loadPepGpbListenerProperties(Properties props) {
		try {
			String s = props.getProperty("pep_GPB_listener_enabled");
			if (s != null) {
				_pepGPBListenerEnabled  = Boolean.valueOf(s);
			}
		} catch (Exception e) {
			_logger.warn("Cannot read whether to enable pep GPB listener.", e);
			_logger.info("Enabling pep GPB listener by default: " + _pepGPBListenerEnabled);
		}

		if (_pepGPBListenerEnabled) {
			try {
				_pepGPBListenerPortNum = Integer.valueOf((String)props.get("pep_GPB_listener_port_num"));
			} catch (Exception e) {
				_logger.warn("Cannot read GPB pep listener port number.", e);
				_logger.info("Default port of GPB pep listener: " + _pepGPBListenerPortNum);
			}
		}
	}

	private void loadPmpListenerProperties(Properties props) {
		try {
			String s = props.getProperty("pmp_listener_enabled");
			if (s != null) {
				_pmpListenerEnabled  = Boolean.valueOf(s);
			}
		} catch (Exception e) {
			_logger.warn("Cannot read whether to enable pmp listener.", e);
			_logger.info("Enabling pmp listener by default: " + _pmpListenerEnabled);
		}

		if (_pmpListenerEnabled) {
			try {
				_pmpListenerPortNum = Integer.valueOf(props.getProperty("pmp_listener_port_num"));
			} catch (Exception e) {
				_logger.warn("Cannot read pmp listener port number.", e);
				_logger.info("Default port of pmp listener: " + _pmpListenerPortNum);
			}
		}
	}

	public String getPropertiesFileName() {
		return propertiesFilename;
	}

	public int getPepGPBListenerPortNum() {
		return _pepGPBListenerPortNum;
	}

	public int getPepThriftListenerPortNum() {
		return _pepThriftListenerPortNum;
	}

	public String getPepPipeIn() {
		return _pepPipeIn;
	}

	public String getPepPipeOut() {
		return _pepPipeOut;
	}

	public int getPmpListenerPortNum() {
		return _pmpListenerPortNum;
	}

	public int getPipListenerPortNum() {
		return _pipListenerPortNum;
	}

	public String getPipAddress() {
		return _pipAddress;
	}

	public int getPipPortNum() {
		return _pipPortNum;
	}

	public int getQueueSize() {
		return _queueSize;
	}

	public boolean isPmpListenerEnabled() {
		return _pmpListenerEnabled;
	}

	public boolean isPepGPBListenerEnabled() {
		return _pepGPBListenerEnabled;
	}

	public boolean isPepThriftListenerEnabled() {
		return _pepThriftListenerEnabled;
	}

	public boolean isPipListenerEnabled() {
		return _pipListenerEnabled;
	}

	public boolean isPepPipeListenerEnabled() {
		return _pepPipeListenerEnabled;
	}

	public void setPepGPBListenerPortNum(int pepGPBListenerPortNum) {
		_pepGPBListenerPortNum = pepGPBListenerPortNum;
	}

	public void setPepThriftListenerPortNum(int pepThriftListenerPortNum) {
		_pepThriftListenerPortNum = pepThriftListenerPortNum;
	}

	public void setPmpListenerPortNum(int pmpListenerPortNum) {
		_pmpListenerPortNum = pmpListenerPortNum;
	}

	public void setPipAddress(String pipAddress) {
		_pipAddress = pipAddress;
	}

	public void setPipPortNum(int pipPortNum) {
		_pipPortNum = pipPortNum;
	}

	public void setQueueSize(int queueSize) {
		_queueSize = queueSize;
	}

	public EDistributedPipStrategy getDistributedPipStrategy() {
		return _distributedPipStrategy;
	}
}

