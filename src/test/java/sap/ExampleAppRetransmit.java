package sap;

import java.io.File;
import java.io.IOException;

import test.TestIntf;
import types.DelegateRequest;
import types.Utility;

/**
 * This sample application reads data from the PPL-engine and tries to inject
 * the data under another weaker policy
 * 
 * @author alex
 */

public class ExampleAppRetransmit implements TestIntf{
	private String serverUrl = "http://localhost:8080/ppl-rest";

	private long piiId = 51474897663035531L;
	private String owner = "OwnerP";

	private PPLClient client;

	public ExampleAppRetransmit() {
		this.client = new PPLClient(serverUrl);
		
//		this method set up the inital setting for the use case and deploy the policy 
		this.deployPolicyAndPii("sample_ppl_policy-permitall.xml", "secret.txt");
	}
	
	public static void main(String[] args) throws IOException {
		ExampleAppRetransmit m = new ExampleAppRetransmit();
		
//		fetch personal information
		File f = m.fetchPii();
		StringBuilder fileContent = Utility.readFile(f.getAbsolutePath());
		System.out.println("Fetched data from PII \"" + fileContent.toString() + "\"");
		
		File weakPolicy = new File("sample_ppl_policy-permitall-weaker.xml");
		m.deployPolicyAndPii(weakPolicy.getName(), f.getAbsolutePath());
		System.out.println("Deployed content "+f.getAbsolutePath()+" under policy "+weakPolicy.getAbsolutePath());
	}

	private File fetchPii() {
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

	@Override
	public void runtest() {
		// TODO Auto-generated method stub
		try {
			ExampleAppRetransmit.main(new String[] {});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
