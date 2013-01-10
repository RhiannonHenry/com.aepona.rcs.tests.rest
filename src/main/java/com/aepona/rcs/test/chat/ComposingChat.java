package com.aepona.rcs.test.chat;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import javax.xml.bind.DatatypeConverter;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hamcrest.Matchers;
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
public class ComposingChat {

	private final Logger LOGGER = LoggerFactory.getLogger(ComposingChat.class);

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
	@Value("${contact1}")
	protected String contact1;
	@Value("${contact2}")
	protected String contact2;
	@Value("${chatSessionIMStatusURL}")
	protected String chatSessionIMStatusURL;
	@Value("${chatSessionIMURL}")
	protected String chatSessionIMURL;
	@Value("${sendIMURL}")
	protected String sendIMURL;
	@Value("${createIMChatSessionURL}")
	protected String createIMChatSessionURL;
	@Value("${chatRequestDataConfirmed}")
	protected String chatRequestDataConfirmed;
	@Value("${chatSubscriptionURL}")
	protected String chatSubscriptionURL;
	@Value("${notificationChannelURL}")
	protected String notificationChannelURL;
	@Value("${validLongPoll}")
	protected String validLongPoll;
	@Value("${registerURL}")
	protected String registerURL;
	@Value("${sessionRequestData}")
	protected String sessionRequestData;
	@Value("${sessionSubscriptionURL}")
	protected String sessionSubscriptionURL;
	@Value("${addressBookSubscriptionURL}")
	protected String addressBookSubscriptionURL;
	@Value("${addressBookRequestData}")
	protected String addressBookRequestData;

	String lastTest = null;
	Boolean initialised = false;
	private String[] RESOURCE_URL = new String[10];
	private String[] CHANNEL_URL = new String[10];
	private String[] CALLBACK_URL = new String[10];
	private String[] SESSION_SUBSCRIPTION_URL = new String[10];
	private String[] ADDRESS_BOOK_SUBSCRIPTION_URL = new String[10];
	private String[] CHAT_SUBSCRIPTION_URL = new String[10];
	private String[] CREATE_SEND_CHAT_SESSION_URL = new String[10];
	private String sentMessageID;
	private String receiveMessageID;
	private String receiveSessionID;
	private String sendSessionURL;
	private String receiveSessionURL;
	private String sessionID;

	@Before
	public void setup() {
		LOGGER.info("Proxy URL: " + proxyURL);
		Properties props = System.getProperties();
		props.put("http.proxyHost", proxyURL);
		props.put("http.proxyPort", proxyPort);
		start();
	}

	public void initialiseUser1() {
		LOGGER.info("Initialising User 1 (+441110000001)...");
		registerUser(user1);
		startNotificationChannel(user1, 1);
		subscribeToSession(user1, 1);
		subscribeToAddressBook(user1, 1);
		subscribeToChatNotificationsConfirmed(user1, 1);
		clearPendingNotifications(user1, 1);
		LOGGER.info("User 1 has been Initalised!");
	}

	public void initialiseUser2() {
		LOGGER.info("Initialising User 2 (+441110000002)...");
		registerUser(user2);
		startNotificationChannel(user2, 2);
		subscribeToSession(user2, 2);
		subscribeToAddressBook(user2, 2);
		subscribeToChatNotificationsConfirmed(user2, 2);
		clearPendingNotifications(user2, 2);
		LOGGER.info("User 2 has been Initalised!");
	}

	@Test
	public void sendIsComposingA1() throws JsonGenerationException,
			JsonMappingException, IOException {
		// Variables
		String sender = user1;
		String senderSIP = contact1;
		String receiver = contact2;
		String subject = "Test Created IM Session";
		String senderUsername = "MO-HOST";
		String receiverUsername = "MT-RECEIVER";
		String userID = user2;
		String contactID = contact1;
		// Setup
		initialiseUser1();
		initialiseUser2();
		createChatSession(sender, receiver, senderSIP, subject, senderUsername,
				receiverUsername);
		sleep();
		checkIMNotifications(userID, 2);
		acceptSession(userID, contactID);
		sleep();
		checkSessionStatusNotification(sender, 1);

		String test = "Starting send isComposing functionality between User ("
				+ sender + ") and Contact (" + receiver + ") - ACTIVE";
		startTest(test);

		String state = "active";
		Date lastActive = new java.util.Date();
		String contentType = "test/plain";
		int refresh = 60;
		userID = user1;
		contactID = contact2;
		String url = replaceExtraLong(sendIMURL, apiVersion, userID, contactID,
				sessionID);
		IsComposing isComposing = new IsComposing(state, lastActive,
				contentType, refresh);
		ObjectMapper mapper = new ObjectMapper();
		String requestData = "{\"isComposing\":"
				+ mapper.writeValueAsString(isComposing) + "}";

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);
		Response response = RestAssured.given().contentType("application/json")
				.body(requestData).expect().log().ifError().statusCode(204)
				.post(url);

		LOGGER.info("Received Response = " + response.getStatusCode());
		endTest(test);
	}

	@Test
	public void checkNotificationsForComposingA1()
			throws JsonGenerationException, JsonMappingException, IOException {
		// Variables
		String sender = user1;
		String senderSIP = contact1;
		String receiver = contact2;
		String subject = "Test Created IM Session";
		String senderUsername = "MO-HOST";
		String receiverUsername = "MT-RECEIVER";
		String userID = user2;
		String contactID = contact1;
		String state = "active";
		String contentType = "text/plain";
		// Setup
		initialiseUser1();
		initialiseUser2();
		createChatSession(sender, receiver, senderSIP, subject, senderUsername,
				receiverUsername);
		sleep();
		checkIMNotifications(userID, 2);
		acceptSession(userID, contactID);
		sleep();
		checkSessionStatusNotification(sender, 1);
		sleep();
		userID = user1;
		contactID = contact2;
		startComposing(userID, contactID, state, contentType, sessionID);
		sleep();

		String test = "Checking that User (" + receiver
				+ ") Received 'composing' Notification - ACTIVE";
		startTest(test);
		userID = user2;
		contactID = contact1;
		String url = (CHANNEL_URL[2].split("username\\=")[0]) + "username="
				+ userID;
		String cleanContact1 = cleanPrefix(contact1);

		RestAssured.authentication = RestAssured.DEFAULT_AUTH;
		Response response = RestAssured
				.given()
				.expect()
				.log()
				.ifError()
				.statusCode(200)
				.body("notificationList.messageNotification[0].isComposing.refresh",
						Matchers.equalTo(60),
						"notificationList.messageNotification[0].isComposing.status",
						Matchers.equalTo("active"),
						"notificationList.messageNotification[0].sessionId",
						Matchers.equalTo(receiveSessionID),
						"notificationList.messageNotification[0].senderAddress",
						Matchers.containsString(cleanContact1)).post(url);

		LOGGER.info("Received Response = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());
		endTest(test);
	}

	@Test
	public void sendIsComposingB1() throws JsonGenerationException,
			JsonMappingException, IOException {
		String sender = user1;
		String senderSIP = contact1;
		String receiver = contact2;
		String subject = "Test Created IM Session";
		String senderUsername = "MO-HOST";
		String receiverUsername = "MT-RECEIVER";
		String userID = user2;
		String contactID = contact1;
		String state = "active";
		String contentType = "text/plain";
		// Setup
		initialiseUser1();
		initialiseUser2();
		createChatSession(sender, receiver, senderSIP, subject, senderUsername,
				receiverUsername);
		sleep();
		checkIMNotifications(userID, 2);
		acceptSession(userID, contactID);
		sleep();
		checkSessionStatusNotification(sender, 1);
		sleep();
		userID = user1;
		contactID = contact2;
		startComposing(userID, contactID, state, contentType, sessionID);
		sleep();
		userID = user2;
		contactID = contact1;
		isComposingNotification(userID, 1, contactID);
		sleep();
		// Start composing User 2
		startComposing(userID, contactID, state, contentType, receiveSessionID);
	}

	@Test
	public void checkNotificationsForComposingB1()
			throws JsonGenerationException, JsonMappingException, IOException {
		String sender = user1;
		String senderSIP = contact1;
		String receiver = contact2;
		String subject = "Test Created IM Session";
		String senderUsername = "MO-HOST";
		String receiverUsername = "MT-RECEIVER";
		String userID = user2;
		String contactID = contact1;
		String state = "active";
		String contentType = "text/plain";
		// Setup
		initialiseUser1();
		initialiseUser2();
		createChatSession(sender, receiver, senderSIP, subject, senderUsername,
				receiverUsername);
		sleep();
		checkIMNotifications(userID, 2);
		acceptSession(userID, contactID);
		sleep();
		checkSessionStatusNotification(sender, 1);
		sleep();
		userID = user1;
		contactID = contact2;
		// Send 'isComposing' from 1 - 2
		startComposing(userID, contactID, state, contentType, sessionID);
		sleep();
		userID = user2;
		contactID = contact1;
		// 'isComposing' Notification for User 2
		isComposingNotification(userID, 2, contactID);
		sleep();
		// Start composing from 2 - 1
		startComposing(userID, contactID, state, contentType, receiveSessionID);
		sleep();
		// 'isComposing' Notification for User 1
		userID = user1;
		contactID = contact2;
		isComposingNotification(userID, 1, contactID);
	}

	@Test
	public void sendIsComposingA2() throws JsonGenerationException,
			JsonMappingException, IOException {
		// Variables
		String sender = user1;
		String senderSIP = contact1;
		String receiver = contact2;
		String subject = "Test Created IM Session";
		String senderUsername = "MO-HOST";
		String receiverUsername = "MT-RECEIVER";
		String userID = user2;
		String contactID = contact1;
		// Setup
		initialiseUser1();
		initialiseUser2();
		createChatSession(sender, receiver, senderSIP, subject, senderUsername,
				receiverUsername);
		sleep();
		checkIMNotifications(userID, 2);
		acceptSession(userID, contactID);
		sleep();
		checkSessionStatusNotification(sender, 1);

		String test = "Starting send isComposing functionality between User ("
				+ sender + ") and Contact (" + receiver + ") - IDLE";
		startTest(test);

		String state = "idle";
		Date lastActive = new java.util.Date();
		String contentType = "test/plain";
		int refresh = 60;
		userID = user1;
		contactID = contact2;
		String url = replaceExtraLong(sendIMURL, apiVersion, userID, contactID,
				sessionID);
		IsComposing isComposing = new IsComposing(state, lastActive,
				contentType, refresh);
		ObjectMapper mapper = new ObjectMapper();
		String requestData = "{\"isComposing\":"
				+ mapper.writeValueAsString(isComposing) + "}";

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);
		Response response = RestAssured.given().contentType("application/json")
				.body(requestData).expect().log().ifError().statusCode(204)
				.post(url);

		LOGGER.info("Received Response = " + response.getStatusCode());
		endTest(test);
	}

	@Test
	public void checkNotificationsForComposingA2()
			throws JsonGenerationException, JsonMappingException, IOException {
		// Variables
		String sender = user1;
		String senderSIP = contact1;
		String receiver = contact2;
		String subject = "Test Created IM Session";
		String senderUsername = "MO-HOST";
		String receiverUsername = "MT-RECEIVER";
		String userID = user2;
		String contactID = contact1;
		String state = "idle";
		String contentType = "text/plain";
		// Setup
		initialiseUser1();
		initialiseUser2();
		createChatSession(sender, receiver, senderSIP, subject, senderUsername,
				receiverUsername);
		sleep();
		checkIMNotifications(userID, 2);
		acceptSession(userID, contactID);
		sleep();
		checkSessionStatusNotification(sender, 1);
		sleep();
		userID = user1;
		contactID = contact2;
		startComposing(userID, contactID, state, contentType, sessionID);
		sleep();

		String test = "Checking that User (" + receiver
				+ ") Received 'composing' Notification - IDLE";
		startTest(test);
		userID = user2;
		contactID = contact1;
		String url = (CHANNEL_URL[2].split("username\\=")[0]) + "username="
				+ userID;
		String cleanContact1 = cleanPrefix(contact1);

		RestAssured.authentication = RestAssured.DEFAULT_AUTH;
		Response response = RestAssured
				.given()
				.expect()
				.log()
				.ifError()
				.statusCode(200)
				.body("notificationList.messageNotification[0].isComposing.refresh",
						Matchers.equalTo(60),
						"notificationList.messageNotification[0].senderAddress",
						Matchers.containsString(cleanContact1)).post(url);

		LOGGER.info("Received Response = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());
		endTest(test);
	}

	@Test
	public void sendIsComposingB2() throws JsonGenerationException,
			JsonMappingException, IOException {
		String sender = user1;
		String senderSIP = contact1;
		String receiver = contact2;
		String subject = "Test Created IM Session";
		String senderUsername = "MO-HOST";
		String receiverUsername = "MT-RECEIVER";
		String userID = user2;
		String contactID = contact1;
		String state = "idle";
		String contentType = "text/plain";
		// Setup
		initialiseUser1();
		initialiseUser2();
		createChatSession(sender, receiver, senderSIP, subject, senderUsername,
				receiverUsername);
		sleep();
		checkIMNotifications(userID, 2);
		acceptSession(userID, contactID);
		sleep();
		checkSessionStatusNotification(sender, 1);
		sleep();
		userID = user1;
		contactID = contact2;
		startComposing(userID, contactID, state, contentType, sessionID);
		sleep();
		userID = user2;
		contactID = contact1;
		isComposingNotification(userID, 1, contactID);
		sleep();
		// Start composing User 2
		startComposing(userID, contactID, state, contentType, receiveSessionID);
	}

	@Test
	public void checkNotificationsForComposingB2()
			throws JsonGenerationException, JsonMappingException, IOException {
		String sender = user1;
		String senderSIP = contact1;
		String receiver = contact2;
		String subject = "Test Created IM Session";
		String senderUsername = "MO-HOST";
		String receiverUsername = "MT-RECEIVER";
		String userID = user2;
		String contactID = contact1;
		String state = "idle";
		String contentType = "text/plain";
		// Setup
		initialiseUser1();
		initialiseUser2();
		createChatSession(sender, receiver, senderSIP, subject, senderUsername,
				receiverUsername);
		sleep();
		checkIMNotifications(userID, 2);
		acceptSession(userID, contactID);
		sleep();
		checkSessionStatusNotification(sender, 1);
		sleep();
		userID = user1;
		contactID = contact2;
		// Send 'isComposing' from 1 - 2
		startComposing(userID, contactID, state, contentType, sessionID);
		sleep();
		userID = user2;
		contactID = contact1;
		// 'isComposing' Notification for User 2
		isComposingNotification(userID, 2, contactID);
		sleep();
		// Start composing from 2 - 1
		startComposing(userID, contactID, state, contentType, receiveSessionID);
		sleep();
		// 'isComposing' Notification for User 1
		userID = user1;
		contactID = contact2;
		isComposingNotification(userID, 1, contactID);
	}

	@Test
	public void sendIsComposingA3() throws JsonGenerationException,
			JsonMappingException, IOException {
		// Variables
		String sender = user1;
		String senderSIP = contact1;
		String receiver = contact2;
		String subject = "Test Created IM Session";
		String senderUsername = "MO-HOST";
		String receiverUsername = "MT-RECEIVER";
		String userID = user2;
		String contactID = contact1;
		// Setup
		initialiseUser1();
		initialiseUser2();
		createChatSession(sender, receiver, senderSIP, subject, senderUsername,
				receiverUsername);
		sleep();
		checkIMNotifications(userID, 2);
		acceptSession(userID, contactID);
		sleep();
		checkSessionStatusNotification(sender, 1);

		String test = "Starting send isComposing functionality between User ("
				+ sender + ") and Contact (" + receiver + ") - INVALID";
		startTest(test);

		String state = "invalid";
		Date lastActive = new java.util.Date();
		String contentType = "test/plain";
		int refresh = 60;
		userID = user1;
		contactID = contact2;
		String url = replaceExtraLong(sendIMURL, apiVersion, userID, contactID,
				sessionID);
		IsComposing isComposing = new IsComposing(state, lastActive,
				contentType, refresh);
		ObjectMapper mapper = new ObjectMapper();
		String requestData = "{\"isComposing\":"
				+ mapper.writeValueAsString(isComposing) + "}";

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);
		Response response = RestAssured.given().contentType("application/json")
				.body(requestData).expect().log().ifError().statusCode(400)
				.post(url);

		JsonPath jsonData = response.jsonPath();
		String errorCode = jsonData
				.get("requestError.serviceException.messageId");
		String errorMessage = jsonData
				.get("requestError.serviceException.variables[0]");

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Error Code = " + errorCode);
		LOGGER.info("Error Message = " + errorMessage);
		endTest(test);
	}

	@Test
	public void closeChat() throws JsonGenerationException,
			JsonMappingException, IOException {
		// Variables
		String sender = user1;
		String senderSIP = contact1;
		String receiver = contact2;
		String subject = "Test Created IM Session";
		String senderUsername = "MO-HOST";
		String receiverUsername = "MT-RECEIVER";
		String userID = user2;
		String contactID = contact1;
		// Setup
		initialiseUser1();
		initialiseUser2();
		createChatSession(sender, receiver, senderSIP, subject, senderUsername,
				receiverUsername);
		sleep();
		checkIMNotifications(userID, 2);
		acceptSession(userID, contactID);
		sleep();
		checkSessionStatusNotification(sender, 1);

		String test = "Closing Chat Session between User 1 and Contact 2 - User 2 CLOSING";
		startTest(test);
		String url = replaceExtraLong(chatSessionIMURL, apiVersion, userID,
				contactID, receiveSessionID);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);
		Response response = RestAssured.given().expect().log().ifError()
				.statusCode(204).delete(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		endTest(test);
	}

	@Test
	public void receiveCloseNotification() throws JsonGenerationException,
			JsonMappingException, IOException {
		// Variables
		String sender = user1;
		String senderSIP = contact1;
		String receiver = contact2;
		String subject = "Test Created IM Session";
		String senderUsername = "MO-HOST";
		String receiverUsername = "MT-RECEIVER";
		String userID = user2;
		String contactID = contact1;
		// Setup
		initialiseUser1();
		initialiseUser2();
		createChatSession(sender, receiver, senderSIP, subject, senderUsername,
				receiverUsername);
		sleep();
		checkIMNotifications(userID, 2);
		acceptSession(userID, contactID);
		sleep();
		checkSessionStatusNotification(sender, 1);
		sleep();
		closeChatSession(userID, contactID, receiveSessionID);
		sleep();

		String test = "Checking User 1 for Close Notification";
		startTest(test);

		RestAssured.authentication = RestAssured.DEFAULT_AUTH;
		userID = user1;
		String url = (CHANNEL_URL[1].split("username\\=")[0]) + "username="
				+ userID;
		Response response = RestAssured
				.given()
				.expect()
				.log()
				.ifError()
				.statusCode(200)
				.body("notificationList.chatEventNotification.eventType",
						Matchers.hasItem("SessionEnded"),
						"notificationList.chatEventNotification.sessionId",
						Matchers.hasItem(sessionID),
						"notificationList.chatEventNotification.link[0].rel",
						Matchers.hasItem("ChatSessionInformation")).post(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Notification Event = "
				+ response.jsonPath().getString(
						"notificationList.chatEventNotification.eventType"));
		endTest(test);
	}

	private void closeChatSession(String userID, String contactID,
			String session) {
		String test = "Closing Chat Session";
		startTest(test);
		String url = replaceExtraLong(chatSessionIMURL, apiVersion, userID,
				contactID, session);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);
		Response response = RestAssured.given().expect().log().ifError()
				.statusCode(204).delete(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
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
		sentMessageID = null;
		receiveMessageID = null;
		receiveSessionID = null;
		sendSessionURL = null;
		receiveSessionURL = null;
	}

	// ********* METHODS TO PERFORM FUNTIONALITY FOR CHAT CLASS **********
	private void createChatSession(String sender, String receiver,
			String senderSIP, String subject, String senderUsername,
			String receiverUsername) throws JsonGenerationException,
			JsonMappingException, IOException {
		String test = "Creating Chat Session... " + sender + " (Sender) "
				+ receiver + " (Reciever)";
		startTest(test);
		String endpoint = createIMChatSessionURL;
		String requestData;
		String url = replaceLong(endpoint, apiVersion, sender, receiver);
		ChatSessionInformation chatSessionInformation = new ChatSessionInformation(
				subject, senderSIP, senderUsername, receiver, receiverUsername);
		ObjectMapper mapper = new ObjectMapper();
		requestData = "{\"chatSessionInformation\":"
				+ mapper.writeValueAsString(chatSessionInformation) + "}";
		RestAssured.authentication = RestAssured.basic(sender,
				applicationPassword);
		Response response = RestAssured.given().contentType("application/json")
				.body(requestData).expect().log().ifError().statusCode(201)
				.post(url);
		JsonPath jsonData = response.jsonPath();
		CREATE_SEND_CHAT_SESSION_URL[1] = jsonData
				.getString("chatSessionInformation.resourceURL");
		LOGGER.info("Received Response = " + response.getStatusCode());
		endTest(test);
	}

	private void checkIMNotifications(String userID, int i) {
		String test = "Checking IM Notifications for User (" + userID + ")";
		startTest(test);
		String url = (CHANNEL_URL[i].split("username\\=")[0]) + "username="
				+ userID;

		RestAssured.authentication = RestAssured.DEFAULT_AUTH;
		Response notificationResponse = RestAssured.given().expect().log()
				.ifError().statusCode(200).post(url);

		JsonPath jsonData = notificationResponse.jsonPath();
		receiveSessionURL = jsonData
				.getString("notificationList.chatSessionInvitationNotification[0].link[0].href");
		LOGGER.info("Extracted receiveSessionURL=" + receiveSessionURL);
		receiveSessionID = jsonData
				.getString("notificationList.messageNotification.sessionId[0]");
		LOGGER.info("Extracted receiveSessionID=" + receiveSessionID);
		receiveMessageID = jsonData
				.getString("notificationList.messageNotification.messageId[0]");
		LOGGER.info("Extracted receiveMessageID=" + receiveMessageID);

		endTest(test);
	}

	private void acceptSession(String userID, String contactID) {
		String test = "Accepting Chat Session";
		startTest(test);
		String url = replaceExtraLong(chatSessionIMStatusURL, apiVersion,
				userID, contactID, receiveSessionID);
		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);
		Response response = RestAssured.given().expect().log().ifError()
				.statusCode(204).put(url);
		LOGGER.info("Received Response = " + response.getStatusCode());
		endTest(test);
	}

	private void checkSessionStatusNotification(String sender, int i) {
		String test = "Checking Notifications for User (" + sender + ")";
		startTest(test);
		String url = (CHANNEL_URL[i].split("username\\=")[0]) + "username="
				+ sender;
		RestAssured.authentication = RestAssured.DEFAULT_AUTH;
		Response notificationResponse = RestAssured.given().expect().log()
				.ifError().statusCode(200)
				.body("notificationList", Matchers.notNullValue()).post(url);

		JsonPath jsonData = notificationResponse.jsonPath();
		sentMessageID = jsonData
				.getString("notificationList.messageStatusNotification[0].messageId");
		LOGGER.info("Extracted messageId=" + sentMessageID);
		sendSessionURL = jsonData
				.getString("notificationList.chatEventNotification.link[0].href[0]");
		LOGGER.info("Extracted sendSessionURL=" + sendSessionURL);
		sessionID = jsonData
				.getString("notificationList.chatEventNotification.sessionId[0]");
		LOGGER.info("Extracted sessionId=" + sessionID);
		endTest(test);
	}

	private void startComposing(String userID, String contactID, String state,
			String contentType, String session) throws JsonGenerationException,
			JsonMappingException, IOException {
		String test = "Starting send isComposing functionality...";
		startTest(test);

		Date lastActive = new java.util.Date();
		int refresh = 60;
		String url = replaceExtraLong(sendIMURL, apiVersion, userID, contactID,
				session);
		IsComposing isComposing = new IsComposing(state, lastActive,
				contentType, refresh);
		ObjectMapper mapper = new ObjectMapper();
		String requestData = "{\"isComposing\":"
				+ mapper.writeValueAsString(isComposing) + "}";

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);
		Response response = RestAssured.given().contentType("application/json")
				.body(requestData).expect().log().ifError().statusCode(204)
				.post(url);

		LOGGER.info("Received Response = " + response.getStatusCode());
		endTest(test);
	}

	private void isComposingNotification(String userID, int i,
			String senderContact) {
		String test = "Checking that User (" + userID
				+ ") Received 'composing' Notification";
		startTest(test);
		String url = (CHANNEL_URL[i].split("username\\=")[0]) + "username="
				+ userID;
		String cleanContact1 = cleanPrefix(senderContact);

		RestAssured.authentication = RestAssured.DEFAULT_AUTH;
		Response response = RestAssured
				.given()
				.expect()
				.log()
				.ifError()
				.statusCode(200)
				.body("notificationList.messageNotification[0].isComposing.refresh",
						Matchers.equalTo(60),
						"notificationList.messageNotification[0].senderAddress",
						Matchers.containsString(cleanContact1)).post(url);

		LOGGER.info("Received Response = " + response.getStatusCode());
		// LOGGER.info("Body = " + response.asString());
		LOGGER.info("isComposing Status = "
				+ response
						.jsonPath()
						.getString(
								"notificationList.messageNotification[0].isComposing.status"));
		endTest(test);
	}

	// ********* GENERAL METHODS **********
	private void sleep() {
		try {
			LOGGER.info("Waiting......");
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
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

	private String requestDataClean(String chatRequestData,
			String clientCorrelator, String callback) {
		String clean = chatRequestData.replace("{CALLBACK}", callback).replace(
				"{CLIENTCORRELATOR}", clientCorrelator);
		return clean;
	}

	private String replace(String chatSubscriptionURL, String apiVersion,
			String userID) {
		return chatSubscriptionURL.replace("{apiVersion}", apiVersion).replace(
				"{userID}", userID);
	}

	private String replaceLong(String url, String apiVersion, String userID,
			String contactID) {
		return url.replace("{apiVersion}", apiVersion)
				.replace("{userID}", userID).replace("{contactID}", contactID);
	}

	private String replaceExtraLong(String url, String apiVersion,
			String userID, String contactID, String sessionID) {
		return url.replace("{apiVersion}", apiVersion)
				.replace("{userID}", userID).replace("{contactID}", contactID)
				.replace("{sessionID}", sessionID);
	}

	public String encode(String userID) {
		return userID.replaceAll("\\:", "%3A").replaceAll("\\+", "%2B")
				.replaceAll("\\@", "%40");
	}

	public String cleanPrefix(String userID) {
		return userID.replaceAll("tel\\:\\+", "").replaceAll("tel\\:", "")
				.replaceAll("\\+", "").replaceAll("\\:", "");
	}

	// ******** METHODS FOR THE INITIALIZATION **********
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
		String test = "Subscribe User 1 to Session Notifications";
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
		SESSION_SUBSCRIPTION_URL[i] = jsonData
				.getString("sessionSubscription.resourceURL");
		LOGGER.info("Response received = " + response.getStatusCode()
				+ response.asString());
		LOGGER.info("Session Subscription URL: " + SESSION_SUBSCRIPTION_URL[i]);
		endTest(test);
	}

	private void subscribeToAddressBook(String userID, int i) {
		String test = "Subscribing User 1 to Address Book Notifications";
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
		ADDRESS_BOOK_SUBSCRIPTION_URL[i] = jsonData
				.getString("abChangesSubscription.resourceURL");
		LOGGER.info("Response received = " + response.getStatusCode());
		LOGGER.info("Address Book Subscription URL: "
				+ ADDRESS_BOOK_SUBSCRIPTION_URL[i]);
		endTest(test);
	}

	private void subscribeToChatNotificationsConfirmed(String userID, int i) {
		String test = "Subscribe User to Chat Notifications (Confirmed)";
		startTest(test);
		String clientCorrelator = Long.toString(System.currentTimeMillis());
		String callback = CALLBACK_URL[i];
		String requestData = requestDataClean(chatRequestDataConfirmed,
				clientCorrelator, callback);
		String url = replace(chatSubscriptionURL, apiVersion, userID);
		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);
		Response response = RestAssured.given().contentType("application/json")
				.body(requestData).expect().log().ifError().statusCode(201)
				.post(url);
		JsonPath jsonData = response.jsonPath();
		CHAT_SUBSCRIPTION_URL[i] = jsonData
				.get("chatNotificationSubscription.resourceURL");
		LOGGER.info("Response received = " + response.getStatusCode());
		endTest(test);
	}

	public void clearPendingNotifications(String userID, int i) {
		if (CHANNEL_URL[i] != null) {
			RestAssured.authentication = RestAssured.DEFAULT_AUTH;
			String url = (CHANNEL_URL[i].split("username\\=")[0]) + "username="
					+ userID;
			RestAssured.given().post(url);
		}
	}

	// ******** REQUIRED CLASSES *********
	public class ChatMessage {
		String text;
		String reportRequest;
		String resourceURL;

		public String getText() {
			return text;
		}

		public String getReportRequest() {
			return reportRequest;
		}

		@JsonIgnore
		public String getResourceURL() {
			return resourceURL;
		}

		public void setText(String text) {
			this.text = text;
		}

		public void setReportRequest(String reportRequest) {
			this.reportRequest = reportRequest;
		}

		public void setResourceURL(String resourceURL) {
			this.resourceURL = resourceURL;
		}

		public ChatMessage(String text, String reportRequest) {
			this.text = text;
			this.reportRequest = reportRequest;
		}
	}

	public class ChatSessionInformation {
		String subject;
		String originatorAddress;
		String originatorName;
		String tParticipantAddress;
		String tParticipantName;
		String status;
		String resourceURL;

		public String getSubject() {
			return subject;
		}

		public String getOriginatorAddress() {
			return originatorAddress;
		}

		public String getOriginatorName() {
			return originatorName;
		}

		public String gettParticipantAddress() {
			return tParticipantAddress;
		}

		public String gettParticipantName() {
			return tParticipantName;
		}

		@JsonIgnore
		public String getStatus() {
			return status;
		}

		@JsonIgnore
		public String getResourceURL() {
			return resourceURL;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}

		public void setOriginatorAddress(String originatorAddress) {
			this.originatorAddress = originatorAddress;
		}

		public void setOriginatorName(String originatorName) {
			this.originatorName = originatorName;
		}

		public void settParticipantAddress(String tParticipantAddress) {
			this.tParticipantAddress = tParticipantAddress;
		}

		public void settParticipantName(String tParticipantName) {
			this.tParticipantName = tParticipantName;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public void setResourceURL(String resourceURL) {
			this.resourceURL = resourceURL;
		}

		public ChatSessionInformation(String subject, String originatorAddress,
				String originatorName, String tParticipantAddress,
				String tParticipantName) {
			this.subject = subject;
			this.originatorAddress = originatorAddress;
			this.originatorName = originatorName;
			this.tParticipantAddress = tParticipantAddress;
			this.tParticipantName = tParticipantName;
		}
	}

	public class IsComposing {
		String state;
		java.util.Date lastActive;
		String contentType;
		int refresh;
		boolean outputISO8601 = false;

		public String getState() {
			return state;
		}

		public String getLastActive() {
			String dt = null;
			if (lastActive != null) {
				TimeZone zone = TimeZone.getTimeZone("UTC");
				Calendar c = Calendar.getInstance(zone);
				c.setTime(lastActive);
				dt = DatatypeConverter.printDateTime(c).substring(0, 19) + "Z";
			}
			return dt;
		}

		public String getContentType() {
			return contentType;
		}

		public int getRefresh() {
			return refresh;
		}

		public void setState(String state) {
			this.state = state;
		}

		public void setLastActive(java.util.Date lastActive) {
			this.lastActive = lastActive;
		}

		public void setContentType(String contentType) {
			this.contentType = contentType;
		}

		public void setRefresh(int refresh) {
			this.refresh = refresh;
		}

		public IsComposing(String state, java.util.Date lastActive,
				String contentType, int refresh) {
			this.state = state;
			this.lastActive = lastActive;
			this.contentType = contentType;
			this.refresh = refresh;
		}
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

	public void setContact1(String contact1) {
		this.contact1 = contact1;
	}

	public void setContact2(String contact2) {
		this.contact2 = contact2;
	}

	public void setChatSessionIMStatusURL(String chatSessionIMStatusURL) {
		this.chatSessionIMStatusURL = chatSessionIMStatusURL;
	}

	public void setChatSessionIMURL(String chatSessionIMURL) {
		this.chatSessionIMURL = chatSessionIMURL;
	}

	public void setSendIMURL(String sendIMURL) {
		this.sendIMURL = sendIMURL;
	}

	public void setCreateIMChatSessionURL(String createIMChatSessionURL) {
		this.createIMChatSessionURL = createIMChatSessionURL;
	}

	public void setChatRequestDataConfirmed(String chatRequestDataConfirmed) {
		this.chatRequestDataConfirmed = chatRequestDataConfirmed;
	}

	public void setChatSubscriptionURL(String chatSubscriptionURL) {
		this.chatSubscriptionURL = chatSubscriptionURL;
	}

	public void setNotificationChannelURL(String notificationChannelURL) {
		this.notificationChannelURL = notificationChannelURL;
	}

	public void setValidLongPoll(String validLongPoll) {
		this.validLongPoll = validLongPoll;
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

	public void setAddressBookSubscriptionURL(String addressBookSubscriptionURL) {
		this.addressBookSubscriptionURL = addressBookSubscriptionURL;
	}

	public void setAddressBookRequestData(String addressBookRequestData) {
		this.addressBookRequestData = addressBookRequestData;
	}

}
