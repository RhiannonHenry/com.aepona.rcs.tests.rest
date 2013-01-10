package com.aepona.rcs.test.chat;

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
@ContextConfiguration(locations = { "/application-context.xml" })
public class RetrieveIndividual {

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
	public void readIndividualChatSubscriptionUser1() {
		String userID = user1;
		int i = 1;

		startNotificationChannel(userID, i);
		subscribeToChatNotificationsConfirmed(userID, i);

		String test = "Read Individual Chat Subscription for User 1 (Confirmed)";
		startTest(test);
		String cleanUserID = cleanPrefix(userID);
		String url = subscriptionURL[i];
		url = prepare(url);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);

		Response response = RestAssured
				.expect()
				.log()
				.ifError()
				.statusCode(200)
				.body("chatNotificationSubscription.resourceURL",
						StringContains.containsString(cleanUserID),
						"chatNotificationSubscription.callbackReference.callbackData",
						IsEqual.equalTo("GSMA1"),
						"chatNotificationSubscription.callbackReference.notifyURL",
						IsEqual.equalTo(callbackURL[i]),
						"chatNotificationSubscription.callbackReference.notificationFormat",
						IsEqual.equalTo("JSON")).get(url);

		if (response.getStatusCode() == 200) {
			LOGGER.info("EXPECTED RESPONSE 200");
		} else {
			LOGGER.error("UNEXPECTED RESPONSE. EXPECTED '200'");
		}

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());

		endTest(test);
	}

	@Test
	public void readIndividualChatSubscriptionUser2() {
		String userID = user2;
		int i = 2;

		startNotificationChannel(userID, i);
		subscribeToChatNotificationsAdhoc(userID, i);

		String test = "Read Individual Chat Subscription for User 2 (Adhoc)";
		startTest(test);
		String cleanUserID = cleanPrefix(userID);
		String url = subscriptionURL[i];
		url = prepare(url);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);

		Response response = RestAssured
				.expect()
				.log()
				.ifError()
				.statusCode(200)
				.body("chatNotificationSubscription.resourceURL",
						StringContains.containsString(cleanUserID),
						"chatNotificationSubscription.callbackReference.callbackData",
						IsEqual.equalTo("GSMA1"),
						"chatNotificationSubscription.callbackReference.notifyURL",
						IsEqual.equalTo(callbackURL[i]),
						"chatNotificationSubscription.callbackReference.notificationFormat",
						IsEqual.equalTo("JSON")).get(url);

		if (response.getStatusCode() == 200) {
			LOGGER.info("EXPECTED RESPONSE 200");
		} else {
			LOGGER.error("UNEXPECTED RESPONSE. EXPECTED '200'");
		}

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());

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

	private String prepare(String url) {
		return url.replaceAll(urlSplit, "").replace("%2B", "+")
				.replace("%3A", ":");
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
