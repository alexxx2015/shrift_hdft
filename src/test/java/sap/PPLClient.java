package sap;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONObject;

import com.sap.research.primelife.message.request.PiiDeleteRequest;
import com.sap.research.primelife.message.response.DelegateResponse;
import com.sap.research.primelife.message.response.PiiDeleteResponse;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.MultiPartConfig;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import com.sun.jersey.multipart.impl.MultiPartReaderClientSide;

import types.DelegateRequest;
import types.ResponseSendPii;

public class PPLClient {

	private final static Logger LOGGER = Logger.getLogger(PPLClient.class.getName());

	String serverUrl = "";
	
	public PPLClient(){
		
	}

	public PPLClient(String endpointURL) {
		serverUrl = endpointURL;
	}

	public String getCCO_ID_Value(String cco_id) {

		LOGGER.info("Getting " + cco_id + " value from the enforcement backend");
		LOGGER.info("Server Endpoint: " + serverUrl);

		System.err.println("parameter: " + cco_id);

		Client client = Client.create();

		WebResource webResource = client.resource(serverUrl + "/get/cco_id_value").queryParam("cco_id", cco_id);

		System.err.println(webResource.toString());

		ClientResponse response = webResource
				// .type(MediaType.WILDCARD)
				// .type(MediaType.APPLICATION_JSON)
				// .accept(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);

		if (response.getStatus() != 200) {
			LOGGER.severe("Backend returned error status: " + response.getStatus());
			return null;// "Error" + "Unable to save pii " + file.getName();
		} else {
			String repStr = response.getEntity(String.class);
			LOGGER.info("Backend retrieved the cco_id value= " + repStr);
			return repStr;
		}

	}

	public boolean deletePii(String owner, String uniqueId) {

		Client client;
		ClientResponse response;
		WebResource webResource;

		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		client = Client.create(clientConfig);
		webResource = client.resource(serverUrl + "pii/delete");

		try {
			PiiDeleteRequest piiDeleteRequest = new PiiDeleteRequest();
			piiDeleteRequest.setOwner(owner);
			piiDeleteRequest.setPiiUniqueId(Long.valueOf(uniqueId));
			response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, piiDeleteRequest);
		} catch (ClientHandlerException e) {
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			return false;
		}

		if (response.getStatus() != 200) {
			LOGGER.log(Level.SEVERE, "Backend return error status: " + response.getStatus());
			return false;

		}

		PiiDeleteResponse piiDeleteResponse = response.getEntity(PiiDeleteResponse.class);

		if (!piiDeleteResponse.isDeleted()) {
			LOGGER.log(Level.SEVERE, "Backend returned error, could not delete file");
			return false;

		} else {
			LOGGER.log(Level.INFO, "Deleted file: " + uniqueId);
			return true;
		}

	}

	public String sendPii(File file, String stickyPolicy, String owner, String tenant) {

		try {
			LOGGER.info("Sending file " + file.getName() + " to enforcement backend");
			LOGGER.info("Server Endpoint: " + serverUrl);

			Client client = Client.create();

			FormDataMultiPart form = new FormDataMultiPart();
			form.bodyPart(new FileDataBodyPart("file", file));
			form.field("stickyPolicy", stickyPolicy);
			form.field("owner", owner);

			if (tenant != null) {
				form.field("tenant", tenant);
			}

			WebResource webResource = client.resource(serverUrl + "/pii/createPiiFile");
//.accept(MediaType.APPLICATION_JSON)
			ClientResponse response = webResource.type(MediaType.MULTIPART_FORM_DATA)
					.put(ClientResponse.class, form);

			if (response.getStatus() != 201) {
				LOGGER.severe("Backend returned error status: " + response.getStatus());
				return null;// "Error" + "Unable to save pii " + file.getName();
			} else {
//				ResponseSendPii repStr = response.getEntity(ResponseSendPii.class);
				JSONObject r = response.getEntity(JSONObject.class);
				ResponseSendPii repStr = new ResponseSendPii();
				repStr.setUniqueId(r.getString("uniqueId"));
				LOGGER.info("Backend stored the pii and its policy with success. PiiId= " + repStr.getUniqueId());
				return repStr.getUniqueId();
			}
		} catch (Exception e) {
			LOGGER.severe("error: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public File getFileDownstream(DelegateRequest delegateRequest) {

		// Send request to cococloud backend
		Client client;
		ClientResponse response;
		WebResource webResource;

		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		client = Client.create(clientConfig);
		webResource = client.resource(serverUrl + "/get/file");

		// Get the PPL for test purposes

		LOGGER.info("PPL URL = " + serverUrl);
		try {
			response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, delegateRequest);

			// Just for test purposes,

		} catch (ClientHandlerException e) {
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, "Error in PPLClient.getFileDownstream: " + e.getMessage(), e);
			return null;
		}

		/**
		 * TODO handle in a better way the return values
		 */
		switch (response.getStatus()) {
		case 200:
			return this.handleDelegateAnswer(response);
		case 204:
			LOGGER.log(Level.INFO, "PPLClient.getFileDownstream: Request returns no Pii");
			return null;
		case 401:
			LOGGER.log(Level.SEVERE, "PPLClient.getFileDownstream: Invalid credentials, please try again");
			return null;
		default:
			LOGGER.log(Level.SEVERE, "PPLClient.getFileDownstream: Error: " + response.getStatus());
			return null;
		}
	}

	private File handleDelegateAnswer(ClientResponse response) {

		MultiPartReaderClientSide multiPartReaderClientSide;

		LOGGER.info("PPLClient.getFileDownstream: return success status: " + response.getStatus());

		try {
			response.bufferEntity();
			multiPartReaderClientSide = new MultiPartReaderClientSide(response.getClient().getProviders(),
					new MultiPartConfig());
			MultiPart m = multiPartReaderClientSide.readFrom(MultiPart.class, BodyPart.class, new Annotation[0],
					response.getType(), response.getHeaders(), response.getEntityInputStream());
			List<BodyPart> list = m.getBodyParts();

			for (BodyPart b : list) {
				File f = b.getEntityAs(File.class);

				return f;
			}

			/**
			 * OLD LOGIC HERE, kept for potential bug fixing
			 */

			// is our file a ZIP archive?
			// FileInputStream toCopyFromZIP = checkAndExtractZipFile(new
			// FileInputStream(f),
			// b.getContentDisposition().getFileName());
			// if (toCopyFromZIP != null) {
			// // it was a ZIP archive, we consider the only relevant
			// // content
			// fileService.store(f.getName() + "-" +
			// b.getContentDisposition().getFileName(), toCopyFromZIP);
			// } else {
			// fileService.store(f.getName() + "-" +
			// b.getContentDisposition().getFileName(),
			// new FileInputStream(f));
			// }
			// UiDelegateResponseItem item = new
			// UiDelegateResponseItem(b.getContentDisposition().getFileName(),
			// f.getName() + "-" + b.getContentDisposition().getFileName());
			// uim.getContent().getFiles().add(item);
			// }
			// uim.getMessages().add(new UiMessageItem("Success", "Request
			// returns " + list.size() + " Piis"));
		} catch (WebApplicationException e) {
			LOGGER.info("PPLClient.getFileDownstream: No File received");
			return null;
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE,
					"PPLClient.getFileDownstream: Error occurred when retrieving response, caused by: " + e.toString(),
					e);
		}
		return null;
	}

	public DelegateResponse getFileList(DelegateRequest delegateRequest) {
		// Send request to PPL-REST
		Client client;
		ClientResponse response = null;
		WebResource webResource;

		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		client = Client.create(clientConfig);
		webResource = client.resource(serverUrl + "/get/list");

		// Get the PPL for test purposes

		try {
			response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, delegateRequest);
		} catch (ClientHandlerException e) {
			e.printStackTrace();
			return null;
		}
		return response.getEntity(DelegateResponse.class);

	}

	public File getPii(DelegateRequest delegateRequest) {

		// Send request to cococloud backend
		Client client;
		ClientResponse response = null;
		WebResource webResource;

		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		client = Client.create(clientConfig);
		webResource = client.resource(serverUrl + "/pii/getPii");

		// Get the PPL for test purposes

		// LOGGER.info("PPL URL = " + serverUrl);

		try {
			response = webResource.queryParam("uniqueId", delegateRequest.getResource())
					.queryParam("owner", delegateRequest.getSubject()).type(MediaType.WILDCARD)
					.get(ClientResponse.class);
			// Just for test purposes,

		} catch (ClientHandlerException e) {
			e.printStackTrace();
			// LOGGER.log(Level.SEVERE,"Error in PPLClient.getFileDownstream:
			// "+e.getMessage(), e);
			// return null;
		}

		/**
		 * TODO handle in a better way the return values
		 */
		switch (response.getStatus()) {
		case 200:
			return response.getEntity(File.class);
		case 204:
			LOGGER.log(Level.INFO, "PPLClient.getPii: Request returns no Pii");
			return null;
		case 401:
			LOGGER.log(Level.SEVERE, "PPLClient.getPii: Invalid credentials, please try again");
			return null;
		default:
			LOGGER.log(Level.SEVERE, "PPLClient.getPii: Error: " + response.getStatus());
			return null;
		}

	}

}
