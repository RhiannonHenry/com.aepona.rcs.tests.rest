package com.aepona.rcs.test.addressBook;

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
	@Value("${user3}")
	protected String user3;
	@Value("${invalidUser}")
	protected String invalidUser;
	@Value("${addressBookSubscriptionURL}")
	protected String addressBookSubscriptionURL;
	@Value("${updateAddressBookRequestData}")
	protected String updateAddressBookRequestData;
	@Value("${addressBookRequestData}")
	protected String addressBookRequestData;
	@Value("${validLongPoll}")
	protected String validLongPoll;
	@Value("${notificationChannelURL}")
	protected String notificationChannelURL;
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
	public void retrieveIndividualSubscriptionForUser1(){
		String userID = user1;
		int i = 1;
		
		startNotificationChannel(userID, i);
		subscribeToAddressBookNotifications(userID, i);
		
		String test = "Read Individual Address Book Subscription for User 1";
		startTest(test);
		
		String url = subscriptionURL[i];
		url = prepare(url);
		String cleanUserID = cleanPrefix(userID);
		
		RestAssured.authentication = RestAssured.basic(userID, applicationPassword);
		
		Response response = RestAssured.expect().log().ifError().statusCode(200).body(
				"abChangesSubscription.resourceURL", StringContains.containsString(cleanUserID),
				"abChangesSubscription.callbackReference.callbackData", IsEqual.equalTo("GSMA1"), 				
				"abChangesSubscription.callbackReference.notifyURL", IsEqual.equalTo(callbackURL[i]),
				"abChangesSubscription.callbackReference.notificationFormat", IsEqual.equalTo("JSON")
				).get(url);
		
		LOGGER.info("Response Received = "+response.getStatusCode());
		LOGGER.info("Body = "+response.asString());
		
		endTest(test);
	}
	
	@Test
	public void retrieveIndividualSubscriptionForUser2(){
		String userID = user2;
		int i = 2;
		
		startNotificationChannel(userID, i);
		subscribeToAddressBookNotifications(userID, i);
		
		String test = "Read Individual Address Book Subscription for User 2";
		startTest(test);
		
		String url = subscriptionURL[i];
		url = prepare(url);
		String cleanUserID = cleanPrefix(userID);
		
		RestAssured.authentication = RestAssured.basic(userID, applicationPassword);
		
		Response response = RestAssured.expect().log().ifError().statusCode(200).body(
				"abChangesSubscription.resourceURL", StringContains.containsString(cleanUserID),
				"abChangesSubscription.callbackReference.callbackData", IsEqual.equalTo("GSMA1"), 				
				"abChangesSubscription.callbackReference.notifyURL", IsEqual.equalTo(callbackURL[i]),
				"abChangesSubscription.callbackReference.notificationFormat", IsEqual.equalTo("JSON")
				).get(url);
		
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

	private String replace(String chatSubscriptionURL, String apiVersion,
			String userID) {
		return chatSubscriptionURL.replace("{apiVersion}", apiVersion).replace(
				"{userID}", userID);
	}

	private String requestDataClean(String addressBookRequestData,
			String clientCorrelator, String callback) {
		String clean = addressBookRequestData.replace("{CALLBACK}", callback)
				.replace("{CLIENTCORRELATOR}", clientCorrelator);
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

		LOGGER.info("" + response.getStatusCode());
		LOGGER.info("Resource URL: " + resourceURL[i]);
		LOGGER.info("Channel URL: " + channelURL[i]);
		LOGGER.info("Callback URL: " + callbackURL[i]);

		endTest(test);
	}

	private void subscribeToAddressBookNotifications(String userID, int i) {
		String test = "Subscribing User 1 to Address Book Notifications";
		startTest(test);

		String clientCorrelator = Long.toString(System.currentTimeMillis());
		String callback = callbackURL[i];
		String requestData = requestDataClean(addressBookRequestData,
				clientCorrelator, callback);
		String url = replace(addressBookSubscriptionURL, apiVersion, userID);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);

		Response response = RestAssured.given().contentType("application/json")
				.body(requestData).expect().log().ifError().statusCode(201)
				.post(url);

		JsonPath jsonData = response.jsonPath();
		subscriptionURL[i] = jsonData
				.getString("abChangesSubscription.resourceURL");
		LOGGER.info("Response received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());

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

	public void setAddressBookSubscriptionURL(String addressBookSubscriptionURL) {
		this.addressBookSubscriptionURL = addressBookSubscriptionURL;
	}

	public void setUpdateAddressBookRequestData(
			String updateAddressBookRequestData) {
		this.updateAddressBookRequestData = updateAddressBookRequestData;
	}

	public void setValidLongPoll(String validLongPoll) {
		this.validLongPoll = validLongPoll;
	}

	public void setNotificationChannelURL(String notificationChannelURL) {
		this.notificationChannelURL = notificationChannelURL;
	}

	public void setAddressBookRequestData(String addressBookRequestData) {
		this.addressBookRequestData = addressBookRequestData;
	}

	public void setUrlSplit(String urlSplit) {
		this.urlSplit = urlSplit;
	}
}
