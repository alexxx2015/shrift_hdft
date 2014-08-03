import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Properties;

import org.junit.After;

import de.tum.in.i22.uc.Controller;
import de.tum.in.i22.uc.cm.commandLineOptions.CommandLineOptions;

public abstract class AbstractTest {
	protected static Controller box;
	private static Properties config = null;
	protected static int PDP_SERVER_PORT = 40010;
	protected static int PIP_SERVER_PORT = 40011;
	protected static int PMP_SERVER_PORT = 40012;
	protected static int ANY_SERVER_PORT = 40013;

	protected void copyConfigFile(String filename) throws Exception {
		URL url = this.getClass().getResource(filename);
		if (url != null) {
			File input = new File(url.getFile());
			File output = new File(input.getParent() + "/uc.config");
			System.out.println("COPY " + input.getAbsolutePath() + " to "
					+ output.getAbsolutePath());

			FileInputStream fis = new FileInputStream(input);
			FileOutputStream fos = new FileOutputStream(output);

			int read;
			while ((read = fis.read()) != -1) {
				fos.write(read);
			}

			fos.flush();
			fos.close();
			fis.close();

			fis = new FileInputStream(input);
			config = new Properties();
			config.load(fis);
		}
	}

	protected void init(String filename) throws Exception {
		copyConfigFile(filename);

		int pdpServerPort = Integer.valueOf((String) config.get("PDP_PORT"));
		int pipServerPort = Integer.valueOf((String) config.get("PIP_PORT"));
		int pmpServerPort = Integer.valueOf((String) config.get("PMP_PORT"));

		String[] args = {
				"--" + CommandLineOptions.OPTION_LOCAL_PDP_LISTENER_PORT_LONG,
				Integer.toString(pdpServerPort),
				"--" + CommandLineOptions.OPTION_LOCAL_PIP_LISTENER_PORT_LONG,
				Integer.toString(pipServerPort),
				"--" + CommandLineOptions.OPTION_LOCAL_PMP_LISTENER_PORT_LONG,
				Integer.toString(pmpServerPort),
				"--" + CommandLineOptions.OPTION_LOCAL_ANY_LISTENER_PORT_LONG,
				Integer.toString(ANY_SERVER_PORT) };

		box = new Controller(args);
		box.start();

	}

	@After
	public void cleanup() {
		URL url = this.getClass().getResource("/uc.config");
		if (url != null) {
			File f = new File(url.getFile());
			if (f.exists()) {
				// f.delete();
			}
		}
	}
}
