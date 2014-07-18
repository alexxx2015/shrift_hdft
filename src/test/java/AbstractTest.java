import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;

import org.junit.After;


public abstract class AbstractTest {

	protected void init(String filename) throws Exception {
		URL url = this.getClass().getResource(filename);
		if (url != null) {
			File input = new File(url.getFile());
			File output = new File(input.getParent() + "/uc.config");
			System.out.println("COPY "+input.getAbsolutePath()+" to "+output.getAbsolutePath());

			FileInputStream fis = new FileInputStream(input);
			FileOutputStream fos = new FileOutputStream(output);

			int read;
			while ((read = fis.read()) != -1) {
				fos.write(read);
			}

			fos.flush();
			fos.close();
			fis.close();

		}
	}

	@After
	public void cleanup() {
		URL url = this.getClass().getResource("/uc.config");
		if (url != null) {
			File f = new File(url.getFile());
			if (f.exists()) {
//				f.delete();
			}
		}
	}
}
