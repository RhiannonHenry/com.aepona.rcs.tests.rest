package com.aepona.rcs.test.general;

import java.util.Properties;

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
public class Unregister {

	private final Logger LOGGER = LoggerFactory.getLogger(Unregister.class);

	@Value("${proxyURL}")
	protected String proxyURL;
	@Value("${proxyPort}")
	protected String proxyPort;
	@Value("${baseURI}")
	protected String baseURI;
	@Value("${apiVersion}")
	protected String apiVersion;
	@Value("${urlSplit}")
	protected String urlSplit;
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
	@Value("${unregisterURL}")
	protected String unregisterURL;

	private Boolean initialised = false;
	private String lastTest = null;

	@Before
	public void setup() {
		Properties props = System.getProperties();
		props.put("http.proxyHost", proxyURL);
		props.put("http.proxyPort", proxyPort);
		start();
	}

	@Test
	public void unregisterUser1() {
		String test = "Unregister User 1";
		startTest(test);
		String userID = user1;
		String url = replace(unregisterURL, apiVersion, userID);
		LOGGER.info("Making call to: " + baseURI + url);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);
		// Make HTTP DELETE Request...
		Response response = RestAssured.expect().log().ifError()
				.statusCode(204).when().delete(url);
		
		if(response.getStatusCode()==204){
			LOGGER.info("SUCCESS");
		}else{
			LOGGER.error("FAILURE");
		}
		
		LOGGER.info("Response received = "+response.getStatusCode()+response.asString());
		endTest(test);
	}
	
	@Test
	public void unregisterUser2() {
		String test = "Unregister User 2";
		startTest(test);
		String userID = user2;
		String url = replace(unregisterURL, apiVersion, userID);
		LOGGER.info("Making call to: " + baseURI + url);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);
		// Make HTTP DELETE Request...
		Response response = RestAssured.expect().log().ifError()
				.statusCode(204).when().delete(url);
		
		if(response.getStatusCode()==204){
			LOGGER.info("SUCCESS");
		}else{
			LOGGER.error("FAILURE");
		}
		
		LOGGER.info("Response received = "+response.getStatusCode()+response.asString());
		endTest(test);
	}
	
	@Test
	public void unregisterInvalidUser() {
		String test = "Unregister Non-Existant User";
		startTest(test);
		String userID = invalidUser;
		String url = replace(unregisterURL, apiVersion, userID);
		LOGGER.info("Making call to: " + baseURI + url);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);
		// Make HTTP DELETE Request...
		Response response = RestAssured.expect().log().ifError()
				.statusCode(401).when().delete(url);
		
		if(response.getStatusCode()==401){
			LOGGER.info("EXPECTED RESPONSE");
		}else{
			LOGGER.error("UNEXPECTED RESPONSE. EXPECTED '401'");
		}
		
		LOGGER.info("Response received = "+response.getStatusCode());
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

	private void startTest(String test) {
		if (lastTest != null) {
			LOGGER.info("Ending the test: '" + lastTest + "' premeturely...");
		}
		LOGGER.info("Starting the test: '" + test + "'");
	}

	private void endTest(String test) {
		LOGGER.info("End of test: '" + test + "'");
	}

	private String replace(String registerURL, String apiVersion, String userID) {
		String newURL = registerURL.replace("{apiVersion}", apiVersion)
				.replace("{userID}", userID);
		LOGGER.info("Register URL = " + newURL);
		return newURL;
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

	public void setUrlSplit(String urlSplit) {
		this.urlSplit = urlSplit;
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

	public void setUnregisterURL(String unregisterURL) {
		this.unregisterURL = unregisterURL;
	}

}
