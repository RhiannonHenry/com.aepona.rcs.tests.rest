package com.aepona.rcs.test.general;

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
	@Value("${sessionSubscriptionURL}")
	protected String sessionSubscriptionURL;
	@Value("${validLongPoll}")
	protected String validLongPoll;
	@Value("${notificationChannelURL}")
	protected String notificationChannelURL;
	@Value("${sessionRequestData}")
	protected String sessionRequestData;
	@Value("${urlSplit}")
	protected String urlSplit;

	private Boolean initialised = false;
	private String lastTest = null;
	private String[] resourceURL = new String[10];
	private String[] channelURL = new String[10];
	private String[] callbackURL = new String[10];
	private String[] subscriptionURL = new String[10];
	
	@Before
	public void setup() {
		LOGGER.info("Proxy URL: " + proxyURL);
		Properties props = System.getProperties();
		props.put("http.proxyHost", proxyURL);
		props.put("http.proxyPort", proxyPort);
		start();
	}
	
	@Test
	public void retrieveUser2SessionSubscription(){
		String userID = user2;
		int i = 2;
		
		startNotificationChannel(userID, i);
		subscribeToSessionNotifications(userID, i);
		
		String test = "Retrieve Individual Session Subscription for User 2";
		startTest(test);
		String cleanUserID = cleanPrefix(userID);
		String url = subscriptionURL[i];
		url = prepare(url);
		LOGGER.info("Making call: "+baseURI+url);
		
		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);
		
		Response response = RestAssured.expect().log().ifError().statusCode(200).body(
				"sessionSubscription.resourceURL", StringContains.containsString(cleanUserID),
				"sessionSubscription.callbackReference.callbackData", IsEqual.equalTo(userID),  
				"sessionSubscription.callbackReference.notifyURL", IsEqual.equalTo(callbackURL[i]),
				"sessionSubscription.callbackReference.notificationFormat", IsEqual.equalTo("JSON")
				).get(url);
		
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
	public void retrieveUser1SessionSubscription(){
		String userID = user1;
		int i = 1;
		
		startNotificationChannel(userID, i);
		subscribeToSessionNotifications(userID, i);
		
		String test = "Retrieve Individual Session Subscription for User 1";
		startTest(test);
		String cleanUserID = cleanPrefix(userID);
		String url = subscriptionURL[i];
		url = prepare(url);
		LOGGER.info("Making call: "+baseURI+url);
		
		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);
		
		Response response = RestAssured.expect().log().ifError().statusCode(200).body(
				"sessionSubscription.resourceURL", StringContains.containsString(cleanUserID),
				"sessionSubscription.callbackReference.callbackData", IsEqual.equalTo(userID),  
				"sessionSubscription.callbackReference.notifyURL", IsEqual.equalTo(callbackURL[i]),
				"sessionSubscription.callbackReference.notificationFormat", IsEqual.equalTo("JSON")
				).get(url);
		
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

	private String replace(String sessionSubscriptionURL, String apiVersion,
			String userID) {
		return sessionSubscriptionURL.replace("{apiVersion}",
				apiVersion).replace("{userID}", userID);
	}

	private String prepare(String url) {
		return url.replaceAll(urlSplit, "").replace("%2B", "+").replace("%3A", ":");
	}
	
	private String requestDataClean(String sessionRequestData, String userID,
			String callback) {
		String clean = sessionRequestData.replace("{CALLBACK}", callback).replace(
				"{USERID}", userID);
		return clean;
	}

	public String cleanPrefix(String userID) {
		return userID.replaceAll("tel\\:\\+", "").replaceAll("tel\\:", "")
				.replaceAll("\\+", "").replaceAll("\\:", "");
	}

	public void subscribeToSessionNotifications(String userID, int i){
		String test = "Subscribe User 1 to Session Notifications";
		startTest(test);
		String url = replace(sessionSubscriptionURL, apiVersion, userID);
		LOGGER.info("Making call to : " + baseURI + url);
		String body = requestDataClean(sessionRequestData, userID, callbackURL[i]);

		// Making HTTP POST Request...
		Response response = RestAssured.given().body(body).expect().log().ifError()
				.statusCode(201).when().post(url);

		if (response.getStatusCode() == 201) {
			LOGGER.info("EXPECTED RESPONSE");
		} else {
			LOGGER.error("UNEXPECTED RESPONSE. EXPECTED '201'");
		}

		JsonPath jsonData = response.jsonPath();
		subscriptionURL[i] = jsonData
				.getString("sessionSubscription.resourceURL");
		LOGGER.info("Response received = " + response.getStatusCode()
				+ response.asString());
		LOGGER.info("Session Subscription URL: " + subscriptionURL[i]);
		endTest(test);
	}
	
	public void startNotificationChannel(String userID, int i) {
		String test = "Starting the Notification Channel";
		startTest(test);

		String url = replace(notificationChannelURL, apiVersion, userID);
		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);

		// Make HTTP POST Request...
		Response response = RestAssured.given().body(validLongPoll).expect()
				.log().ifError().statusCode(201).post(url);

		JsonPath jsonData = response.jsonPath();
		resourceURL[i] = jsonData.get("notificationChannel.resourceURL");
		channelURL[i] = jsonData
				.get("notificationChannel.channelData.channelURL");
		callbackURL[i] = jsonData.get("notificationChannel.callbackURL");

		LOGGER.info(""+response.getStatusCode());
		LOGGER.info("Resource URL: " + resourceURL[i]);
		LOGGER.info("Channel URL: " + channelURL[i]);
		LOGGER.info("Callback URL: " + callbackURL[i]);

		endTest(test);
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

	public void setSessionSubscriptionURL(String sessionSubscriptionURL) {
		this.sessionSubscriptionURL = sessionSubscriptionURL;
	}

	public void setValidLongPoll(String validLongPoll) {
		this.validLongPoll = validLongPoll;
	}

	public void setNotificationChannelURL(String notificationChannelURL) {
		this.notificationChannelURL = notificationChannelURL;
	}

	public void setSessionRequestData(String sessionRequestData) {
		this.sessionRequestData = sessionRequestData;
	}
	
	public void setUrlSplit(String urlSplit) {
		this.urlSplit = urlSplit;
	}
}
