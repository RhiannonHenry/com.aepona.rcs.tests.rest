package com.aepona.rcs.test.chat;

import java.util.Properties;

import org.hamcrest.Matchers;
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
public class RetrieveList {
	
	private final Logger LOGGER = LoggerFactory
			.getLogger(RetrieveIndividual.class);

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
	@Value("${user3}")
	protected String user3;
	@Value("${invalidUser}")
	protected String invalidUser;
	@Value("${validLongPoll}")
	protected String validLongPoll;
	@Value("${notificationChannelURL}")
	protected String notificationChannelURL;
	@Value("${chatRequestDataAdhoc}")
	protected String chatRequestDataAdhoc;
	@Value("${chatRequestDataConfirmed}")
	protected String chatRequestDataConfirmed;
	@Value("${chatSubscriptionURL}")
	protected String chatSubscriptionURL;
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
	public void retrieveListChatSubscriptionsUser1(){
		String userID = user1;
		int i = 1;
		
		startNotificationChannel(userID, i);
		subscribeToChatNotificationsConfirmed(userID, i);
		
		String test = "Retrieve List of all Chat Subscriptions for User 2";
		startTest(test);
		
		String cleanUserID = cleanPrefix(userID);
		String url = replace(chatSubscriptionURL, apiVersion, userID);
		
		RestAssured.authentication = RestAssured.basic(userID, applicationPassword);
		
		Response response = RestAssured.expect().log().ifError().statusCode(200).
				body(
				"chatSubscriptionList.chatNotificationSubscription.size()", Matchers.is(1),
				"chatSubscriptionList.resourceURL", StringContains.containsString(cleanUserID),
				"chatSubscriptionList.chatNotificationSubscription[0].resourceURL", StringContains.containsString(cleanUserID),
				"chatSubscriptionList.chatNotificationSubscription[0].callbackReference.callbackData", IsEqual.equalTo("GSMA1"),
				"chatSubscriptionList.chatNotificationSubscription[0].callbackReference.notifyURL", IsEqual.equalTo(callbackURL[i]),
				"chatSubscriptionList.chatNotificationSubscription[0].callbackReference.notificationFormat", IsEqual.equalTo("JSON")
				).
				get(url);
		
		if (response.getStatusCode() == 200) {
			LOGGER.info("EXPECTED RESPONSE 200");
		} else {
			LOGGER.error("UNEXPECTED RESPONSE. EXPECTED '200'");
		}
		LOGGER.info("Making call to: "+baseURI+url);
		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());

		endTest(test);
		
	}
	
	@Test
	public void retrieveListChatSubscriptionsUser2(){
		String userID = user2;
		int i = 2;
		
		startNotificationChannel(userID, i);
		subscribeToChatNotificationsConfirmed(userID, i);
		
		String test = "Retrieve List of all Chat Subscriptions for User 1";
		startTest(test);
		
		String cleanUserID = cleanPrefix(userID);
		String url = replace(chatSubscriptionURL, apiVersion, userID);
		
		RestAssured.authentication = RestAssured.basic(userID, applicationPassword);
		
		Response response = RestAssured.expect().log().ifError().statusCode(200).
				body(
				"chatSubscriptionList.chatNotificationSubscription.size()", Matchers.is(1),
				"chatSubscriptionList.resourceURL", StringContains.containsString(cleanUserID),
				"chatSubscriptionList.chatNotificationSubscription[0].resourceURL", StringContains.containsString(cleanUserID),
				"chatSubscriptionList.chatNotificationSubscription[0].callbackReference.callbackData", IsEqual.equalTo("GSMA1"),
				"chatSubscriptionList.chatNotificationSubscription[0].callbackReference.notifyURL", IsEqual.equalTo(callbackURL[i]),
				"chatSubscriptionList.chatNotificationSubscription[0].callbackReference.notificationFormat", IsEqual.equalTo("JSON")
				).
				get(url);
		
		if (response.getStatusCode() == 200) {
			LOGGER.info("EXPECTED RESPONSE 200");
		} else {
			LOGGER.error("UNEXPECTED RESPONSE. EXPECTED '200'");
		}
		LOGGER.info("Making call to: "+baseURI+url);
		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());

		endTest(test);
		
	}
	
	@Test
	public void retrieveListChatSubscriptionsUser3NoSubscriptions(){
		String userID = user3;
		int i = 3;
		
		startNotificationChannel(userID, i);
		//subscribeToChatNotificationsConfirmed(userID, i);
		
		String test = "Retrieve List of all Chat Subscriptions for User 3 with no Subscriptions";
		startTest(test);
		
		String url = replace(chatSubscriptionURL, apiVersion, userID);
		
		RestAssured.authentication = RestAssured.basic(userID, applicationPassword);
		
		Response response = RestAssured.expect().log().ifError().statusCode(400).
				get(url);
		
		if (response.getStatusCode() == 400) {
			LOGGER.info("EXPECTED RESPONSE 400");
		} else {
			LOGGER.error("UNEXPECTED RESPONSE. EXPECTED '400'");
		}
		
		JsonPath jsonData = response.jsonPath();
		String errorCode = jsonData.get("requestError.serviceException.messageId");
		String errorMessage = jsonData.get("requestError.serviceException.variables[0]");
		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Error Code = " + errorCode);
		LOGGER.info("Error Message = " + errorMessage);

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

	private String replace(String chatSubscriptionURL, String apiVersion,
			String userID) {
		return chatSubscriptionURL.replace("{apiVersion}", apiVersion).replace(
				"{userID}", userID);
	}

	private String requestDataClean(String chatRequestData,
			String clientCorrelator, String callback) {
		String clean = chatRequestData.replace("{CALLBACK}", callback).replace(
				"{CLIENTCORRELATOR}", clientCorrelator);
		return clean;
	}

	public String cleanPrefix(String userID) {
		return userID.replaceAll("tel\\:\\+", "").replaceAll("tel\\:", "")
				.replaceAll("\\+", "").replaceAll("\\:", "");
	}

	private void startNotificationChannel(String userID, int i) {
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

		LOGGER.info("" + response.getStatusCode());
		LOGGER.info("Resource URL: " + resourceURL[i]);
		LOGGER.info("Channel URL: " + channelURL[i]);
		LOGGER.info("Callback URL: " + callbackURL[i]);

		endTest(test);
	}

	@SuppressWarnings("unused")
	private void subscribeToChatNotificationsAdhoc(String userID, int i) {
		String test = "Subscribe User to Chat Notifications (Adhoc)";
		startTest(test);

		String clientCorrelator = Long.toString(System.currentTimeMillis());
		String callback = callbackURL[i];
		String requestData = requestDataClean(chatRequestDataAdhoc,
				clientCorrelator, callback);
		String url = replace(chatSubscriptionURL, apiVersion, userID);
		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);

		Response response = RestAssured.given().contentType("application/json")
				.body(requestData).expect().log().ifError().statusCode(201)
				.post(url);

		JsonPath jsonData = response.jsonPath();
		subscriptionURL[i] = jsonData
				.getString("chatNotificationSubscription.resourceURL");
		LOGGER.info("Response received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());
		LOGGER.info("Session Subscription URL: " + subscriptionURL[i]);

		endTest(test);
	}

	private void subscribeToChatNotificationsConfirmed(String userID, int i) {
		String test = "Subscribe User to Chat Notifications (Confirmed)";
		startTest(test);

		String clientCorrelator = Long.toString(System.currentTimeMillis());
		String callback = callbackURL[i];
		String requestData = requestDataClean(chatRequestDataConfirmed,
				clientCorrelator, callback);
		String url = replace(chatSubscriptionURL, apiVersion, userID);
		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);

		Response response = RestAssured.given().contentType("application/json")
				.body(requestData).expect().log().ifError().statusCode(201)
				.post(url);

		JsonPath jsonData = response.jsonPath();
		subscriptionURL[i] = jsonData
				.getString("chatNotificationSubscription.resourceURL");
		LOGGER.info("Response received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());
		LOGGER.info("Session Subscription URL: " + subscriptionURL[i]);

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
	
	public void setUser3(String user3) {
		this.user3 = user3;
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

	public void setChatRequestDataAdhoc(String chatRequestDataAdhoc) {
		this.chatRequestDataAdhoc = chatRequestDataAdhoc;
	}

	public void setChatRequestDataConfirmed(String chatRequestDataConfirmed) {
		this.chatRequestDataConfirmed = chatRequestDataConfirmed;
	}

	public void setChatSubscriptionURL(String chatSubscriptionURL) {
		this.chatSubscriptionURL = chatSubscriptionURL;
	}

	public void setUrlSplit(String urlSplit) {
		this.urlSplit = urlSplit;
	}
	
}
