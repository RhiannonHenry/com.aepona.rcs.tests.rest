package com.aepona.rcs.test.notificationChannel;

import java.util.Properties;

import org.hamcrest.core.IsEqual;
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
public class RetrieveIndividual {
	
	private final Logger LOGGER = LoggerFactory.getLogger(RetrieveIndividual.class);

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
		Properties props = System.getProperties();
		props.put("http.proxyHost", proxyURL);
		props.put("http.proxyPort", proxyPort);
		start();
	}
	
	@Test
	public void readUser1Subscriptions(){
		String userID = user1;
		int i = 1;
		createNotificationChannel(userID, i);
		
		String test = "Read Individual Notification Channel for User 1";
		startTest(test);
		String cleanUserID = cleanPrefix(userID);
		String url = resourceURL[i];
		url = prepare(url);
		
		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);
		
		Response response = RestAssured.expect().log().ifError().statusCode(200).
				body(
			    		"notificationChannel.resourceURL", StringContains.containsString(cleanUserID),
			    		"notificationChannel.callbackURL", IsEqual.equalTo(callbackURL[i]),
			    		"notificationChannel.channelData.channelURL", IsEqual.equalTo(channelURL[i])	    		
			    ).when().get(url);
		
		if (response.getStatusCode() == 200) {
			LOGGER.info("EXPECTED RESPONSE 200");
		} else {
			LOGGER.error("UNEXPECTED RESPONSE. EXPECTED '200'");
		}
		
		LOGGER.info("Response Received = "+response.getStatusCode());
		LOGGER.info("Body = "+response.asString());
		
		endTest(test);
	}
	
	@Test
	public void readUser2Subscriptions(){
		String userID = user2;
		int i = 2;
		createNotificationChannel(userID, i);
		
		String test = "Read Individual Notification Channel for User 1";
		startTest(test);
		String cleanUserID = cleanPrefix(userID);
		String url = resourceURL[i];
		url = prepare(url);
		
		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);
		
		Response response = RestAssured.expect().log().ifError().statusCode(200).
				body(
			    		"notificationChannel.resourceURL", StringContains.containsString(cleanUserID),
			    		"notificationChannel.callbackURL", IsEqual.equalTo(callbackURL[i]),
			    		"notificationChannel.channelData.channelURL", IsEqual.equalTo(channelURL[i])	    		
			    ).when().get(url);
		
		if (response.getStatusCode() == 200) {
			LOGGER.info("EXPECTED RESPONSE 200");
		} else {
			LOGGER.error("UNEXPECTED RESPONSE. EXPECTED '200'");
		}
		
		LOGGER.info("Response Received = "+response.getStatusCode());
		LOGGER.info("Body = "+response.asString());
		
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

	private void createNotificationChannel(String userID, int i){
		String test = "Creating Notification Channel";
		startTest(test);
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
		resourceURL[i]=jsonData.get("notificationChannel.resourceURL");
		channelURL[i]=jsonData.get("notificationChannel.channelData.channelURL");
		callbackURL[i]=jsonData.get("notificationChannel.callbackURL");
		
		if (response.getStatusCode() == 201) {
			LOGGER.info("EXPECTED RESPONSE 201");
		} else {
			LOGGER.error("UNEXPECTED RESPONSE. EXPECTED '201'");
		}
		
		LOGGER.info("Response received = " + response.getStatusCode());
		LOGGER.info("Resource URL: "+resourceURL[i]);
		endTest(test);
		
	}
	
	private String replace(String notificationChannelURL, String apiVersion,
			String userID) {
		String newURL = notificationChannelURL.replace("{apiVersion}",
				apiVersion).replace("{userID}", userID);
		return newURL;
	}
	
	public String cleanPrefix(String userID) {
		return userID.replaceAll("tel\\:\\+", "").replaceAll("tel\\:", "")
				.replaceAll("\\+", "").replaceAll("\\:", "");
	}
	
	private String prepare(String url) {
		return url.replaceAll(urlSplit, "").replaceAll("%2B", "+").replaceAll("%3A", ":");
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
	
	public void setUrlSplit(String urlSplit) {
		this.urlSplit = urlSplit;
	}

}
