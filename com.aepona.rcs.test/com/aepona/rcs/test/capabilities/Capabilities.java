package com.aepona.rcs.test.capabilities;

import java.io.IOException;
import java.util.Properties;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-context.xml" })
public class Capabilities {

	private final Logger LOGGER = LoggerFactory.getLogger(Capabilities.class);
	
	@Value("${proxyURL}")
	protected String proxyURL;
	@Value("${proxyPort}")
	protected String proxyPort;
	@Value("${baseURI}")
	protected String baseURI;
	@Value("${apiVersion}")
	protected String apiVersion;
	@Value("${port}")
	protected int port;
	@Value("${applicationPassword}")
	protected String applicationPassword;
	@Value("${user1}")
	protected String user1;
	@Value("${user2}")
	protected String user2;
	@Value("${invalidUser}")
	protected String invalidUser;
	@Value("${contact1}")
	protected String contact1;
	@Value("${contact2}")
	protected String contact2;
	@Value("${invalidContact}")
	protected String invalidContact;
	@Value("${capabilitiesURL}")
	protected String capabilitiesURL;
	@Value("${registerURL}")
	protected String registerURL;
	
	String lastTest = null;
	Boolean initialised = false;
	
	@Before
	public void setup() {
		LOGGER.info("Proxy URL: " + proxyURL);
		Properties props = System.getProperties();
		props.put("http.proxyHost", proxyURL);
		props.put("http.proxyPort", proxyPort);
		start();
		initialiseUser(user1, 1);
		initialiseUser(user2, 2);
	}
	
	@Test
	public void readCapabilities(){
		String userID = user1;
		int i = 1;
		String test = "Reading Capabilities for User "+i;
		startTest(test);
		
		String url = replace(capabilitiesURL, apiVersion, userID);
		
		RestAssured.authentication = RestAssured.basic(userID, applicationPassword);
		Response response = RestAssured.expect().log().ifError().statusCode(200).get(url);
		
		LOGGER.info("Received Response = "+response.getStatusCode());
		LOGGER.info("Body = "+response.asString());
		endTest(test);
	}
	
	@Test
	public void setCapabilities() throws JsonGenerationException, JsonMappingException, IOException{
		String userID = user1;
		int i = 1;
		String test = "Setting Capabilities for User "+i;
		startTest(test);
		
		Capability capabilities = new Capability();
		capabilities.setAddress(contact1);
		capabilities.setImSession(true);
		capabilities.setFileTransfer(true);
		capabilities.setImageShare(true);
		capabilities.setSocialPresence(true);
		capabilities.setDiscoveryPresence(true);
		
		ObjectMapper mapper=new ObjectMapper();	
		String requestData="{\"capabilities\":"+mapper.writeValueAsString(capabilities)+"}";
		LOGGER.info("Request Body = "+requestData);
		String url = replace(capabilitiesURL, apiVersion, userID);
		LOGGER.info("URRL = "+baseURI+url);
		RestAssured.authentication = RestAssured.basic(userID, applicationPassword);
		Response response = RestAssured.given().contentType("application/json").body(requestData).expect().log().ifError().statusCode(204).put(url);
		
		LOGGER.info("Received Response = "+response.getStatusCode());
		endTest(test);
	}
	
	@Test
	public void readCapabilities2(){
		String userID = user2;
		int i = 2;
		String test = "Reading Capabilities for User "+i;
		startTest(test);
		
		String url = replace(capabilitiesURL, apiVersion, userID);
		
		RestAssured.authentication = RestAssured.basic(userID, applicationPassword);
		Response response = RestAssured.expect().log().ifError().statusCode(200).get(url);
		
		LOGGER.info("Received Response = "+response.getStatusCode());
		LOGGER.info("Body = "+response.asString());
		endTest(test);
	}
	
	@Test
	public void setCapabilities2() throws JsonGenerationException, JsonMappingException, IOException{
		String userID = user2;
		int i = 2;
		String test = "Setting Capabilities for User "+i;
		startTest(test);
		
		Capability capabilities = new Capability();
		capabilities.setAddress(contact2);
		capabilities.setImSession(true);
		capabilities.setFileTransfer(true);
		capabilities.setImageShare(true);
		capabilities.setSocialPresence(true);
		capabilities.setDiscoveryPresence(true);
		
		ObjectMapper mapper=new ObjectMapper();	
		String requestData="{\"capabilities\":"+mapper.writeValueAsString(capabilities)+"}";
		LOGGER.info("Request Body = "+requestData);
		String url = replace(capabilitiesURL, apiVersion, userID);
		
		RestAssured.authentication = RestAssured.basic(userID, applicationPassword);
		Response response = RestAssured.given().contentType("application/json").body(requestData).expect().log().ifError().statusCode(204).put(url);
		
		LOGGER.info("Received Response = "+response.getStatusCode());
		endTest(test);
	}
	
	@Test
	public void readCapabilitiesInvalid(){
		String userID = invalidUser;
		String test = "Reading Capabilities for User "+invalidUser;
		startTest(test);
		
		String url = replace(capabilitiesURL, apiVersion, userID);
		
		RestAssured.authentication = RestAssured.basic(userID, applicationPassword);
		Response response = RestAssured.expect().log().ifError().statusCode(401).get(url);
		
		LOGGER.info("Received Response = "+response.getStatusCode());
		endTest(test);
	}
	
	@Test
	public void setCapabilitiesInvalid() throws JsonGenerationException, JsonMappingException, IOException{
		String userID = invalidUser;
		String test = "Setting Capabilities for User "+invalidUser;
		startTest(test);
		
		Capability capabilities = new Capability();
		capabilities.setAddress(invalidContact);
		capabilities.setImSession(true);
		capabilities.setFileTransfer(true);
		capabilities.setImageShare(true);
		capabilities.setSocialPresence(true);
		capabilities.setDiscoveryPresence(true);
		
		ObjectMapper mapper=new ObjectMapper();	
		String requestData="{\"capabilities\":"+mapper.writeValueAsString(capabilities)+"}";
		LOGGER.info("Request Body = "+requestData);
		String url = replace(capabilitiesURL, apiVersion, userID);
		
		RestAssured.authentication = RestAssured.basic(userID, applicationPassword);
		Response response = RestAssured.given().contentType("application/json").body(requestData).expect().log().ifError().statusCode(401).put(url);
		
		LOGGER.info("Received Response = "+response.getStatusCode());
		endTest(test);
	}
	
	@Test
	public void readCapabilitiesInvalid2(){
		String userID = invalidUser;
		String test = "Reading Capabilities for User "+invalidUser+" with valid authentication";
		startTest(test);
		
		String url = replace(capabilitiesURL, apiVersion, userID);
		
		RestAssured.authentication = RestAssured.basic(user1, applicationPassword);
		Response response = RestAssured.expect().log().ifError().statusCode(400).get(url);
		
		LOGGER.info("Received Response = "+response.getStatusCode());
		LOGGER.info("Error Message = "+response.jsonPath().getString("requestError.serviceException.variables[0]"));
		endTest(test);
	}
	
	@Test
	public void setCapabilitiesInvalid2() throws JsonGenerationException, JsonMappingException, IOException{
		String userID = invalidUser;
		String test = "Setting Capabilities for User "+invalidUser;
		startTest(test);
		
		Capability capabilities = new Capability();
		capabilities.setAddress(invalidContact);
		capabilities.setImSession(true);
		capabilities.setFileTransfer(true);
		capabilities.setImageShare(true);
		capabilities.setSocialPresence(true);
		capabilities.setDiscoveryPresence(true);
		
		ObjectMapper mapper=new ObjectMapper();	
		String requestData="{\"capabilities\":"+mapper.writeValueAsString(capabilities)+"}";
		LOGGER.info("Request Body = "+requestData);
		String url = replace(capabilitiesURL, apiVersion, userID);
		
		RestAssured.authentication = RestAssured.basic(user1, applicationPassword);
		Response response = RestAssured.given().contentType("application/json").body(requestData).expect().log().ifError().statusCode(400).put(url);
		
		LOGGER.info("Received Response = "+response.getStatusCode());
		LOGGER.info("Error Message = "+response.jsonPath().getString("requestError.serviceException.variables[0]"));
		endTest(test);
	}
	
	public void start() {
		if (!initialised) {
			RestAssured.baseURI = baseURI;
			RestAssured.port = port;
			RestAssured.basePath = "";
			RestAssured.urlEncodingEnabled = true;
			initialised = true;
		}
	}
	
	public void initialiseUser(String userID, int i) {
		LOGGER.info("Initialising User "+i+"");
		registerUser(userID);
		LOGGER.info("User "+i+" has been Initalised!");
	}
	
	private void registerUser(String userID) {
		String test = "Register User 1";
		startTest(test);
		String url = replace(registerURL, apiVersion, userID);
		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);
		Response response = RestAssured.expect().log().ifError()
				.statusCode(204).when().post(url);
		LOGGER.info("Response received = " + response.getStatusCode()
				+ response.asString());
		endTest(test);
	}
	
	private String replace(String url, String apiVersion,
			String userID) {
		return url.replace("{apiVersion}", apiVersion).replace(
				"{userID}", userID);
	}
	
	private void startTest(String test) {
		if (lastTest != null) {
			LOGGER.info("Ending the test: '" + lastTest + "' premeturely...");
		}
		LOGGER.info("Starting the test: '" + test + "'");
	}

	private void endTest(String test) {
		LOGGER.info("End of test: '" + test + "'");
	}
	
	public void setProxyURL(String proxyURL) {
		this.proxyURL = proxyURL;
	}
	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	}
	public void setBaseURI(String baseURI) {
		this.baseURI = baseURI;
	}
	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public void setApplicationPassword(String applicationPassword) {
		this.applicationPassword = applicationPassword;
	}
	public void setUser1(String user1) {
		this.user1 = user1;
	}
	public void setUser2(String user2) {
		this.user2 = user2;
	}
	public void setInvalidUser(String invalidUser) {
		this.invalidUser = invalidUser;
	}
	public void setContact1(String contact1) {
		this.contact1 = contact1;
	}
	public void setContact2(String contact2) {
		this.contact2 = contact2;
	}
	public void setInvalidContact(String invalidContact) {
		this.invalidContact = invalidContact;
	}
	public void setCapabilitiesURL(String capabilitiesURL) {
		this.capabilitiesURL = capabilitiesURL;
	}
	public void setRegisterURL(String registerURL) {
		this.registerURL = registerURL;
	}
	
	public class Capability {
		String address;
		boolean imSession;
		boolean fileTransfer;
		boolean imageShare;
		boolean videoShare;
		boolean socialPresence;
		boolean discoveryPresence;
		public String getAddress() {
			return address;
		}
		public boolean isImSession() {
			return imSession;
		}
		public boolean isFileTransfer() {
			return fileTransfer;
		}
		public boolean isImageShare() {
			return imageShare;
		}
		public boolean isVideoShare() {
			return videoShare;
		}
		public boolean isSocialPresence() {
			return socialPresence;
		}
		public boolean isDiscoveryPresence() {
			return discoveryPresence;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public void setImSession(boolean imSession) {
			this.imSession = imSession;
		}
		public void setFileTransfer(boolean fileTransfer) {
			this.fileTransfer = fileTransfer;
		}
		public void setImageShare(boolean imageShare) {
			this.imageShare = imageShare;
		}
		public void setVideoShare(boolean videoShare) {
			this.videoShare = videoShare;
		}
		public void setSocialPresence(boolean socialPresence) {
			this.socialPresence = socialPresence;
		}
		public void setDiscoveryPresence(boolean discoveryPresence) {
			this.discoveryPresence = discoveryPresence;
		}
	}
}
