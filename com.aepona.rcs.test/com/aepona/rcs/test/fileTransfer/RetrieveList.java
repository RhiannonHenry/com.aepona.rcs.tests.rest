package com.aepona.rcs.test.fileTransfer;

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
@ContextConfiguration(locations = { "/application-context.xml" })
public class RetrieveList {

	private final Logger LOGGER = LoggerFactory
			.getLogger(RetrieveList.class);

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
	@Value("${registerURL}")
	protected String registerURL;
	@Value("${sessionRequestData}")
	protected String sessionRequestData;
	@Value("${sessionSubscriptionURL}")
	protected String sessionSubscriptionURL;
	@Value("${validLongPoll}")
	protected String validLongPoll;
	@Value("${notificationChannelURL}")
	protected String notificationChannelURL;
	@Value("${addressBookRequestData}")
	protected String addressBookRequestData;
	@Value("${addressBookSubscriptionURL}")
	protected String addressBookSubscriptionURL;
	@Value("${fileTransferRequestData}")
	protected String fileTransferRequestData;
	@Value("${fileTransferSubscriptionURL}")
	protected String fileTransferSubscriptionURL;
	@Value("${urlSplit}")
	protected String urlSplit;

	String lastTest = null;
	Boolean initialised = false;
	private String[] RESOURCE_URL = new String[5];
	private String[] CHANNEL_URL = new String[5];
	private String[] CALLBACK_URL = new String[5];
	private String[] SESSION_SUB_URL = new String[5];
	private String[] ADDRESS_BOOK_SUB_URL = new String[5];
	private String[] FILE_TRANSFER_SUB_URL = new String[5];

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
	public void retrieveList(){
		String userID = user1;
		int i = 1;
				
		String test = "Retrieve All File Transfer Subscriptions for User "+i;
		startTest(test);
		
		String cleanUserID = cleanPrefix(userID);
		String url = replace(fileTransferSubscriptionURL, apiVersion, userID);
		
		RestAssured.authentication = RestAssured.basic(userID, applicationPassword);
		Response response = RestAssured.expect().log().ifError().statusCode(200).body(
				"fileTransferSubscriptionList.resourceURL", StringContains.containsString(cleanUserID)).get(url);
		
		LOGGER.info("Response Received: "+response.getStatusCode());
		LOGGER.info("Body: "+response.asString());
		endTest(test);
	}
	
	@Test
	public void retrieveList2(){
		String userID = user2;
		int i = 2;
				
		String test = "Retrieve All File Transfer Subscriptions for User "+i;
		startTest(test);
		
		String cleanUserID = cleanPrefix(userID);
		String url = replace(fileTransferSubscriptionURL, apiVersion, userID);
		
		RestAssured.authentication = RestAssured.basic(userID, applicationPassword);
		Response response = RestAssured.expect().log().ifError().statusCode(200).body(
				"fileTransferSubscriptionList.resourceURL", StringContains.containsString(cleanUserID)).get(url);
		
		LOGGER.info("Response Received: "+response.getStatusCode());
		LOGGER.info("Body: "+response.asString());
		endTest(test);
	}
	
	@Test
	public void retrieveList3(){
		String userID = user1;
		int i = 1;
		unsubscribeFileTransferSubscriptions(userID, i);
		String test = "Retrieve All File Transfer Subscriptions for User "+i+" (NOT SUBSCRIBED)";
		startTest(test);
		
		String url = replace(fileTransferSubscriptionURL, apiVersion, userID);
		
		RestAssured.authentication = RestAssured.basic(userID, applicationPassword);
		Response response = RestAssured.expect().log().ifError().statusCode(400).get(url);
		
		LOGGER.info("Received Response = "+response.getStatusCode());
		LOGGER.info("Error Message = "+response.jsonPath().getString("requestError.serviceException.variables[0]"));
		endTest(test);
	}
	
	// *** Helper Methods ***
	
	private void unsubscribeFileTransferSubscriptions(String userID, int i){
		String test = "Unsubscribe User "+i+" from File Transfer";
		startTest(test);
		
		String url = FILE_TRANSFER_SUB_URL[i];
		url = prepare(url);
		
		RestAssured.authentication = RestAssured.basic(userID, applicationPassword);
		Response response = RestAssured.expect().log().ifError().statusCode(204).delete(url);
		
		LOGGER.info("Received Response: "+response.getStatusCode());
		endTest(test);
	}
	
		private void subscribeToFileTransfer(String userID, int i) {
			String test = "Attempting to Subscribe User " + i + " to FileTransfers";
			startTest(test);

			String requestData = requestDataClean(fileTransferRequestData, userID,
					CALLBACK_URL[i]);
			String url = replace(fileTransferSubscriptionURL, apiVersion, userID);
			LOGGER.info("Request Body = " + requestData);
			LOGGER.info("URL = " + baseURI + url);

			RestAssured.authentication = RestAssured.digest(userID,
					applicationPassword);
			Response response = RestAssured.given().contentType("application/json")
					.body(requestData).expect().statusCode(201).post(url);

			FILE_TRANSFER_SUB_URL[i] = response.jsonPath().getString(
					"fileTransferSubscription.resourceURL");
			LOGGER.info("Response Received = " + response.getStatusCode());
			LOGGER.info("File Transfer Subscription URL = "
					+ FILE_TRANSFER_SUB_URL[i]);

			endTest(test);
		}

		// *** General Methods ***

		public void start() {
			if (!initialised) {
				RestAssured.baseURI = baseURI;
				RestAssured.port = port;
				RestAssured.basePath = "";
				RestAssured.urlEncodingEnabled = true;
				initialised = true;
			}
			RESOURCE_URL = new String[5];
			CHANNEL_URL = new String[5];
			CALLBACK_URL = new String[5];
			SESSION_SUB_URL = new String[5];
			ADDRESS_BOOK_SUB_URL = new String[5];
			FILE_TRANSFER_SUB_URL = new String[5];
		}

		public void initialiseUser(String userID, int i) {
			LOGGER.info("Initialising User " + i + "");
			registerUser(userID);
			startNotificationChannel(userID, i);
			subscribeToSession(userID, i);
			subscribeToAddressBook(userID, i);
			subscribeToFileTransfer(userID, i);
			clearPendingNotifications(userID, i);
			LOGGER.info("User " + i + " has been Initalised!");
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

		private String requestDataClean(String fileTransferRequestData,
				String userID, String callback) {
			String clean = fileTransferRequestData.replace("{CALLBACK}", callback)
					.replace("{USERID}", userID);
			return clean;
		}

		private String replace(String chatSubscriptionURL, String apiVersion,
				String userID) {
			return chatSubscriptionURL.replace("{apiVersion}", apiVersion).replace(
					"{userID}", userID);
		}

		public String cleanPrefix(String userID) {
			return userID.replaceAll("tel\\:\\+", "").replaceAll("tel\\:", "")
					.replaceAll("\\+", "").replaceAll("\\:", "");
		}
		
		private String prepare(String url) {
			return url.replaceAll(urlSplit, "").replace("%2B", "+")
					.replace("%3A", ":");
		}
		
		// *** Set Up Methods ***
		private void clearPendingNotifications(String userID, int i) {
			if (CHANNEL_URL[i] != null) {
				RestAssured.authentication = RestAssured.DEFAULT_AUTH;
				String url = (CHANNEL_URL[i].split("username\\=")[0]) + "username="
						+ userID;
				RestAssured.given().post(url);
			}
		}

		private void registerUser(String userID) {
			String test = "Register User";
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

		private void startNotificationChannel(String userID, int i) {
			String test = "Starting the Notification Channel";
			startTest(test);
			String url = replace(notificationChannelURL, apiVersion, userID);
			RestAssured.authentication = RestAssured.basic(userID,
					applicationPassword);
			Response response = RestAssured.given().body(validLongPoll).expect()
					.log().ifError().statusCode(201).post(url);

			JsonPath jsonData = response.jsonPath();
			RESOURCE_URL[i] = jsonData.get("notificationChannel.resourceURL");
			CHANNEL_URL[i] = jsonData
					.get("notificationChannel.channelData.channelURL");
			CALLBACK_URL[i] = jsonData.get("notificationChannel.callbackURL");
			LOGGER.info("Response Received = " + response.getStatusCode());
			endTest(test);
		}

		private void subscribeToSession(String userID, int i) {
			String test = "Subscribe User "+i+" to Session Notifications";
			startTest(test);
			String url = replace(sessionSubscriptionURL, apiVersion, userID);
			LOGGER.info("Making call to : " + baseURI + url);
			String body = requestDataClean(sessionRequestData, userID,
					CALLBACK_URL[i]);
			RestAssured.authentication = RestAssured.basic(userID,
					applicationPassword);
			Response response = RestAssured.given().body(body).expect().log()
					.ifError().statusCode(201).when().post(url);
			JsonPath jsonData = response.jsonPath();
			SESSION_SUB_URL[i] = jsonData
					.getString("sessionSubscription.resourceURL");
			LOGGER.info("Response received = " + response.getStatusCode()
					+ response.asString());
			LOGGER.info("Session Subscription URL: " + SESSION_SUB_URL[i]);
			endTest(test);
		}

		private void subscribeToAddressBook(String userID, int i) {
			String test = "Subscribing User "+i+" to Address Book Notifications";
			startTest(test);
			String clientCorrelator = Long.toString(System.currentTimeMillis());
			String callback = CALLBACK_URL[i];
			String requestData = requestDataClean(addressBookRequestData,
					clientCorrelator, callback);
			String url = replace(addressBookSubscriptionURL, apiVersion, userID);
			RestAssured.authentication = RestAssured.basic(userID,
					applicationPassword);
			Response response = RestAssured.given().contentType("application/json")
					.body(requestData).expect().log().ifError().statusCode(201)
					.post(url);
			JsonPath jsonData = response.jsonPath();
			ADDRESS_BOOK_SUB_URL[i] = jsonData
					.getString("abChangesSubscription.resourceURL");
			LOGGER.info("Response received = " + response.getStatusCode());
			LOGGER.info("Address Book Subscription URL: " + ADDRESS_BOOK_SUB_URL[i]);
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

		public void setContact1(String contact1) {
			this.contact1 = contact1;
		}

		public void setContact2(String contact2) {
			this.contact2 = contact2;
		}

		public void setInvalidContact(String invalidContact) {
			this.invalidContact = invalidContact;
		}

		public void setRegisterURL(String registerURL) {
			this.registerURL = registerURL;
		}

		public void setSessionRequestData(String sessionRequestData) {
			this.sessionRequestData = sessionRequestData;
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

		public void setAddressBookRequestData(String addressBookRequestData) {
			this.addressBookRequestData = addressBookRequestData;
		}

		public void setAddressBookSubscriptionURL(String addressBookSubscriptionURL) {
			this.addressBookSubscriptionURL = addressBookSubscriptionURL;
		}

		public void setFileTransferRequestData(String fileTransferRequestData) {
			this.fileTransferRequestData = fileTransferRequestData;
		}

		public void setFileTransferSubscriptionURL(
				String fileTransferSubscriptionURL) {
			this.fileTransferSubscriptionURL = fileTransferSubscriptionURL;
		}

		public void setUrlSplit(String urlSplit) {
			this.urlSplit = urlSplit;
		}
}
