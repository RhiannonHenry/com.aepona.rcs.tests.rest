package com.aepona.rcs.test.notificationChannel;

import java.util.Properties;

import org.hamcrest.core.StringContains;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/application-context.xml" })
public class Create {

	private final Logger LOGGER = LoggerFactory.getLogger(Create.class);

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
	@Value("${validLongPoll}")
	protected String validLongPoll;
	@Value("${notificationChannelURL}")
	protected String notificationChannelURL;

	private Boolean initialised = false;
	private String lastTest = null;
	private String[] resourceURL = new String[10];
	private String[] channelURL = new String[10];
	private String[] callbackURL = new String[10];

	@Before
	public void setup() {
		LOGGER.info("Proxy URL: " + proxyURL);
		Properties props = System.getProperties();
		props.put("http.proxyHost", proxyURL);
		props.put("http.proxyPort", proxyPort);
		start();
	}

	@Test
	public void subscribeUser1() {
		String test = "Subscribe User 1 to Notifications";
		startTest(test);
		String userID = user1;
		String cleanUserID = cleanPrefix(userID);
		String url = replace(notificationChannelURL, apiVersion, userID);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);
		// Make HTTP POST Request
		Response response = RestAssured.given().body(validLongPoll).expect().log().ifError().statusCode(201).
				body(
			    		"notificationChannel.resourceURL", StringContains.containsString(cleanUserID),
			    		"notificationChannel.callbackURL", StringContains.containsString(cleanUserID),
			    		"notificationChannel.channelData.channelURL", StringContains.containsString(cleanUserID)	    		
			    ).when().post(url);
		
		JsonPath jsonData = response.jsonPath();
		resourceURL[1]=jsonData.get("notificationChannel.resourceURL");
		channelURL[1]=jsonData.get("notificationChannel.channelData.channelURL");
		callbackURL[1]=jsonData.get("notificationChannel.callbackURL");
		
		if (response.getStatusCode() == 201) {
			LOGGER.info("EXPECTED RESPONSE 201");
		} else {
			LOGGER.error("UNEXPECTED RESPONSE. EXPECTED '201'");
		}
		
		LOGGER.info("Response received = " + response.getStatusCode());
		LOGGER.info("Resource URL: "+resourceURL[1]);
		LOGGER.info("Channel URL: "+channelURL[1]);
		LOGGER.info("Callback URL: "+callbackURL[1]);
		endTest(test);
	}
	
	@Test
	public void subscribeUser2() {
		String test = "Subscribe User 2 to Notifications";
		startTest(test);
		String userID = user2;
		String cleanUserID = cleanPrefix(userID);
		String url = replace(notificationChannelURL, apiVersion, userID);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);
		// Make HTTP POST Request
		Response response = RestAssured.given().body(validLongPoll).expect().log().ifError().statusCode(201).
				body(
			    		"notificationChannel.resourceURL", StringContains.containsString(cleanUserID),
			    		"notificationChannel.callbackURL", StringContains.containsString(cleanUserID),
			    		"notificationChannel.channelData.channelURL", StringContains.containsString(cleanUserID)	    		
			    ).when().post(url);
		
		JsonPath jsonData = response.jsonPath();
		resourceURL[2]=jsonData.get("notificationChannel.resourceURL");
		channelURL[2]=jsonData.get("notificationChannel.channelData.channelURL");
		callbackURL[2]=jsonData.get("notificationChannel.callbackURL");
		
		if (response.getStatusCode() == 201) {
			LOGGER.info("EXPECTED RESPONSE 201");
		} else {
			LOGGER.error("UNEXPECTED RESPONSE. EXPECTED '201'");
		}
		
		LOGGER.info("Response received = " + response.getStatusCode());
		LOGGER.info("Resource URL: "+resourceURL[2]);
		LOGGER.info("Channel URL: "+channelURL[2]);
		LOGGER.info("Callback URL: "+callbackURL[2]);
		endTest(test);
	}
	
	@Test
	public void subscribeInvalidUser() {
		String test = "Subscribe Non-Existant User to Notifications";
		startTest(test);
		String userID = invalidUser;
		String url = replace(notificationChannelURL, apiVersion, userID);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);
		// Make HTTP POST Request
		Response response = RestAssured.given().body(validLongPoll).expect().log().ifError().statusCode(401).
				when().post(url);
		
		if (response.getStatusCode() == 401) {
			LOGGER.info("EXPECTED RESPONSE 401");
		} else {
			LOGGER.error("UNEXPECTED RESPONSE. EXPECTED '401'");
		}

		LOGGER.info("Response received = " + response.getStatusCode());
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

	private String replace(String notificationChannelURL, String apiVersion,
			String userID) {
		String newURL = notificationChannelURL.replace("{apiVersion}",
				apiVersion).replace("{userID}", userID);
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

	public void setValidLongPoll(String validLongPoll) {
		this.validLongPoll = validLongPoll;
	}

	public void setNotificationChannelURL(String notificationChannelURL) {
		this.notificationChannelURL = notificationChannelURL;
	}

	public String cleanPrefix(String userID) {
		return userID.replaceAll("tel\\:\\+", "").replaceAll("tel\\:", "")
				.replaceAll("\\+", "").replaceAll("\\:", "");
	}
}
