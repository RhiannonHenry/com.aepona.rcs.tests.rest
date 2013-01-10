package com.aepona.rcs.test.fileTransfer;

import java.util.Properties;

import org.hamcrest.Matchers;
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
public class SendFile {

	private final Logger LOGGER = LoggerFactory.getLogger(RetrieveList.class);

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
	@Value("${sendFileURL}")
	protected String sendFileURL;
	@Value("${fileTransferStatusURL}")
	protected String fileTransferStatusURL;
	@Value("${fileTransferSessionURL}")
	protected String fileTransferSessionURL;

	String lastTest = null;
	Boolean initialised = false;
	private String[] RESOURCE_URL = new String[5];
	private String[] CHANNEL_URL = new String[5];
	private String[] CALLBACK_URL = new String[5];
	private String[] SESSION_SUB_URL = new String[5];
	private String[] ADDRESS_BOOK_SUB_URL = new String[5];
	private String[] FILE_TRANSFER_SUB_URL = new String[5];
	private String senderSessionID;
	private String recipientSessionID;
	private String attachmentURL;
	private String savedResourceURL;

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
	public void sendFileURLPointer() {
		String userID = user1;
		String userSIP = contact1;
		String cleanUserID = cleanPrefix(userID);
		int i = 1;
		String contactSIP = contact2;
		int j = 2;

		String test = "Sending File (URL Pointer) from User " + i + " to User "
				+ j;
		startTest(test);

		String requestData = "{\"fileTransferSessionInformation\": {\"fileInformation\": {\"fileDescription\": \"This is my latest picture\",\"fileDisposition\": \"Render\","
				+ "\"fileSelector\": {\"name\": \"tux.png\",\"size\": 56320,\"type\": \"image/png\"},\"fileURL\": \"http://tux.crystalxp.net/png/brightknight-doraemon-tux-3704.png\"},"
				+ "\"originatorAddress\": \""
				+ userSIP
				+ "\",\"originatorName\": \"G3\",\"receiverAddress\": \""
				+ contactSIP + "\",\"receiverName\": \"G4\"}}";
		String url = replace(sendFileURL, apiVersion, userID);
		LOGGER.info("Request Data = "+requestData);
		LOGGER.info("URL = "+baseURI+url);
		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);
		Response response = RestAssured
				.given()
				.body(requestData)
				.expect()
				.statusCode(201)
				.body("resourceReference.resourceURL",
						StringContains.containsString(cleanUserID)).post(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = "+ response.asString());
		String resourceURL = response.jsonPath().getString(
				"resourceReference.resourceURL");
		String[] parts = resourceURL.split("/sessions/");
		senderSessionID = parts[i];
		LOGGER.info("Sender Session ID = " + senderSessionID);
		endTest(test);
	}

	@Test
	public void checkingNotifications() {
		String userID = user1;
		String userSIP = contact1;
		String cleanUserID = cleanPrefix(userID);
		int i = 1;
		String contactID = user2;
		String contactSIP = contact2;
		int j = 2;

		sendFilePointer(userID, userSIP, contactSIP, i, j);
		sleep();
		sleep();
		sleep();
		String test = "Checking if User " + j + " received any Notifications";
		startTest(test);

		String url = (CHANNEL_URL[j].split("username\\=")[0]) + "username="
				+ contactID;

		RestAssured.authentication = RestAssured.DEFAULT_AUTH;
		Response response = RestAssured
				.given()
				.urlEncodingEnabled(true)
				.expect()
				.statusCode(200)
				.body("notificationList.ftSessionInvitationNotification[0].fileInformation.resourceURL",
						Matchers.notNullValue(),
						"notificationList.ftSessionInvitationNotification[0].sessionId",
						Matchers.notNullValue(),
						"notificationList.ftSessionInvitationNotification[0].originatorAddress",
						Matchers.containsString(cleanUserID),
						"notificationList.ftSessionInvitationNotification[0].receiverName",
						Matchers.equalTo(contactID),
						"notificationList.ftSessionInvitationNotification[0].fileInformation.fileSelector.name",
						Matchers.notNullValue(),
						"notificationList.ftSessionInvitationNotification[0].fileInformation.fileSelector.type",
						Matchers.equalTo("image/png")).post(url);

		// recipientSessionID =
		// response.jsonPath().getString("notificationList.ftSessionInvitationNotification[0].sessionId");
		// attachmentURL =
		// response.jsonPath().getString("notificationList.ftSessionInvitationNotification[0].fileInformation.fileURL");
		//
		// LOGGER.info("Response Received = "+response.getStatusCode());
		// LOGGER.info("Receiver Session = "+recipientSessionID);
		// LOGGER.info("File Name = "+response.jsonPath().getString("notificationList.ftSessionInvitationNotification[0].fileInformation.fileSelector.name"));
		// LOGGER.info("Attachment URL = "+attachmentURL);
		LOGGER.info("Body = " + response.asString());
		endTest(test);
	}

	@Test
	public void acceptTransfer() {
		String userID = user1;
		String userSIP = contact1;
		String cleanUserID = cleanPrefix(userID);
		int i = 1;
		String contactID = user2;
		String contactSIP = contact2;
		int j = 2;

		sendFilePointer(userID, userSIP, contactSIP, i, j);
		sleep();
		checkNotificationsForTransfer(contactID, cleanUserID, j);

		String test = "Accepting Transfer from User " + i;
		startTest(test);
		String sessionID = recipientSessionID;
		LOGGER.info("***" + sessionID);
		String acceptData = "{\"receiverSessionStatus\": {\"status\": \"Connected\"}}";
		String url = replaceLong(fileTransferStatusURL, apiVersion, contactID,
				sessionID);
		LOGGER.info("URL : " + url);
		LOGGER.info("Request Data : " + acceptData);
		RestAssured.authentication = RestAssured.basic(contactID,
				applicationPassword);
		Response response = RestAssured.given().body(acceptData)
				.urlEncodingEnabled(true).expect().statusCode(204).post(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		endTest(test);
	}

	@Test
	public void declineTransfer() {
		String userID = user1;
		String userSIP = contact1;
		String cleanUserID = cleanPrefix(userID);
		int i = 1;
		String contactID = user2;
		String contactSIP = contact2;
		int j = 2;

		sendFilePointer(userID, userSIP, contactSIP, i, j);
		sleep();
		checkNotificationsForTransfer(contactID, cleanUserID, j);

		String test = "Declining Transfer from User " + i;
		startTest(test);
		String sessionID = recipientSessionID;
		LOGGER.info("***" + sessionID);
		String url = replaceLong(fileTransferSessionURL, apiVersion, contactID,
				sessionID);
		LOGGER.info("URL = " + url);
		RestAssured.authentication = RestAssured.basic(contactID,
				applicationPassword);
		Response response = RestAssured.given().expect().log().ifError()
				.statusCode(204).delete(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		endTest(test);
	}

	@Test
	public void checkingSenderNotifications() {
		String userID = user1;
		String userSIP = contact1;
		String cleanUserID = cleanPrefix(userID);
		int i = 1;
		String contactID = user2;
		String contactSIP = contact2;
		int j = 2;

		sendFilePointer(userID, userSIP, contactSIP, i, j);
		sleep();
		checkNotificationsForTransfer(contactID, cleanUserID, j);
		sleep();
		acceptTransfer(contactID, i);

		String test = "Checking if User " + i + " received any Notifications";
		startTest(test);

		String url = (CHANNEL_URL[i].split("username\\=")[0]) + "username="
				+ userID;

		RestAssured.authentication = RestAssured.DEFAULT_AUTH;
		Response response = RestAssured
				.given()
				.urlEncodingEnabled(true)
				.expect()
				.statusCode(200)
				.body("notificationList.receiverAcceptanceNotification[0].receiverSessionStatus.status",
						Matchers.equalTo("Connected"),
						"notificationList.receiverAcceptanceNotification[0].sessionId",
						Matchers.equalTo(senderSessionID)).post(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());
		endTest(test);
	}

	@Test
	public void gettingFileURL() {
		String userID = user1;
		String userSIP = contact1;
		String cleanUserID = cleanPrefix(userID);
		int i = 1;
		String contactID = user2;
		String contactSIP = contact2;
		int j = 2;

		sendFilePointer(userID, userSIP, contactSIP, i, j);
		sleep();
		checkNotificationsForTransfer(contactID, cleanUserID, j);
		sleep();
		acceptTransfer(contactID, i);

		String test = "Getting the File URL...";
		startTest(test);

		RestAssured.authentication = RestAssured.DEFAULT_AUTH;
		String url = (CHANNEL_URL[j].split("username\\=")[0]) + "username="
				+ contactID;
		LOGGER.info("URL = "+url);
		Response response = RestAssured.given().expect().statusCode(200)
				.body("notificationList", Matchers.notNullValue()).post(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());
		attachmentURL = response.jsonPath().getString(
				"notificationList.fileNotification[0].fileInformation.fileURL");
		LOGGER.info("Attachment URL = " + attachmentURL);
		endTest(test);
	}

	@Test
	public void receiveAttachment() {
		String userID = user1;
		String userSIP = contact1;
		String cleanUserID = cleanPrefix(userID);
		int i = 1;
		String contactID = user2;
		String contactSIP = contact2;
		int j = 2;

		sendFilePointer(userID, userSIP, contactSIP, i, j);
		sleep();
		checkNotificationsForTransfer(contactID, cleanUserID, j);
		sleep();
		acceptTransfer(contactID, i);
		sleep();
		getFileURL(contactID, j);

		String test = "Receiving attachment...";
		startTest(test);

		RestAssured.authentication = RestAssured.DEFAULT_AUTH;
		Response response = RestAssured.given().urlEncodingEnabled(false)
				.expect().statusCode(200).get(attachmentURL);

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Content Type = " + response.getContentType());
		LOGGER.info("Content disposition = "
				+ response.getHeader("Content-Disposition"));
		LOGGER.info("Content length = " + response.getHeader("Content-Length"));
		endTest(test);
	}

	@Test
	public void readFileTransferSession() {
		String userID = user1;
		String userSIP = contact1;
		String cleanUserID = cleanPrefix(userID);
		String contactSIP = contact2;

		String test = "Creating session that will be read...";
		String requestData = "{\"fileTransferSessionInformation\": {\"fileInformation\": {\"fileDescription\": \"This is my latest picture\",\"fileDisposition\": \"Render\","
				+ "\"fileSelector\": {\"name\": \"tux.png\",\"size\": 56320,\"type\": \"image/png\"},\"fileURL\": \"http://tux.crystalxp.net/png/brightknight-doraemon-tux-3704.png\"},"
				+ "\"originatorAddress\": \""
				+ userSIP
				+ "\",\"originatorName\": \"G1\",\"receiverAddress\": \""
				+ contactSIP + "\",\"receiverName\": \"G2\"}}";
		String url = replace(sendFileURL, apiVersion, userID);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);
		Response response = RestAssured
				.given()
				.body(requestData)
				.expect()
				.statusCode(201)
				.body("resourceReference.resourceURL",
						StringContains.containsString(cleanUserID)).post(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());
		savedResourceURL = response.jsonPath().getString(
				"resourceReference.resourceURL");
		LOGGER.info("Message resourceURL = " + savedResourceURL);
		endTest(test);

		url = prepare(savedResourceURL);
		test = "Reading Session Information URL " + url;
		startTest(test);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);
		response = RestAssured
				.expect()
				.statusCode(200)
				.body("fileTransferSessionInformation.resourceURL",
						StringContains.containsString(cleanUserID),
						"fileTransferSessionInformation.status",
						Matchers.equalTo("Invited")).get(url);
		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());
		endTest(test);

	}

	@Test
	public void mismatchedSenderInURL() {
		String userSIP = contact1;
		String contactSIP = contact2;
		String mismatchedSender = user3;

		String test = "Error Test = Mismatched Sender Address";
		startTest(test);

		String requestData = "{\"fileTransferSessionInformation\": {\"fileInformation\": {\"fileDescription\": \"This is my latest picture\",\"fileDisposition\": \"Render\","
				+ "\"fileSelector\": {\"name\": \"tux.png\",\"size\": 56320,\"type\": \"image/png\"},\"fileURL\": \"http://tux.crystalxp.net/png/brightknight-doraemon-tux-3704.png\"},"
				+ "\"originatorAddress\": \""
				+ userSIP
				+ "\",\"originatorName\": \"G1\",\"receiverAddress\": \""
				+ contactSIP + "\",\"receiverName\": \"G2\"}}";
		String url = replace(sendFileURL, apiVersion, mismatchedSender);

		RestAssured.authentication = RestAssured.basic(mismatchedSender,
				applicationPassword);
		Response response = RestAssured
				.given()
				.body(requestData)
				.expect()
				.statusCode(400)
				.body("requestError.serviceException.messageId",
						Matchers.equalTo("SVC002"),
						"requestError.serviceException.variables",
						Matchers.hasItem("Originator's Address is wrong"))
				.post(url);

		LOGGER.info("Received Response = " + response.getStatusCode());
		LOGGER.info("Error Message = "
				+ response.jsonPath().getString(
						"requestError.serviceException.variables[0]"));
		endTest(test);
	}

	@Test
	public void checkNoNotifications() {
		String userSIP = contact1;
		int i = 1;
		String contactSIP = contact2;
		int j = 2;
		String mismatchedSender = user3;

		mismatchedSender(userSIP, mismatchedSender, contactSIP);
		sleep();

		String test = "Checking that User " + i + " has no notifications";
		startTest(test);

		RestAssured.authentication = RestAssured.DEFAULT_AUTH;
		Response response = RestAssured.given().expect().statusCode(200)
				.body("notificationList", Matchers.nullValue())
				.post(CHANNEL_URL[i]);

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());
		endTest(test);

		test = "Checking that User " + j + " has no notifications";
		startTest(test);

		RestAssured.authentication = RestAssured.DEFAULT_AUTH;
		response = RestAssured.given().expect().statusCode(200)
				.body("notificationList", Matchers.nullValue())
				.post(CHANNEL_URL[j]);

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());
		endTest(test);
	}

	// *** Helper Methods ***
	private void mismatchedSender(String userSIP, String mismatchedSender,
			String contactSIP) {
		String test = "Error Test = Mismatched Sender Address";
		startTest(test);

		String requestData = "{\"fileTransferSessionInformation\": {\"fileInformation\": {\"fileDescription\": \"This is my latest picture\",\"fileDisposition\": \"Render\","
				+ "\"fileSelector\": {\"name\": \"tux.png\",\"size\": 56320,\"type\": \"image/png\"},\"fileURL\": \"http://tux.crystalxp.net/png/brightknight-doraemon-tux-3704.png\"},"
				+ "\"originatorAddress\": \""
				+ userSIP
				+ "\",\"originatorName\": \"G1\",\"receiverAddress\": \""
				+ contactSIP + "\",\"receiverName\": \"G2\"}}";
		String url = replace(sendFileURL, apiVersion, mismatchedSender);

		RestAssured.authentication = RestAssured.basic(mismatchedSender,
				applicationPassword);
		Response response = RestAssured
				.given()
				.body(requestData)
				.expect()
				.statusCode(400)
				.body("requestError.serviceException.messageId",
						Matchers.equalTo("SVC002"),
						"requestError.serviceException.variables",
						Matchers.hasItem("Originator's Address is wrong"))
				.post(url);

		LOGGER.info("Received Response = " + response.getStatusCode());
		LOGGER.info("Error Message = "
				+ response.jsonPath().getString(
						"requestError.serviceException.variables[0]"));
		endTest(test);
	}

	private void getFileURL(String contactID, int j) {
		String test = "Getting the File URL...";
		startTest(test);

		RestAssured.authentication = RestAssured.DEFAULT_AUTH;
		String url = (CHANNEL_URL[j].split("username\\=")[0]) + "username="
				+ contactID;
		Response response = RestAssured.given().expect().statusCode(200)
				.body("notificationList", Matchers.notNullValue()).post(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());
		attachmentURL = response.jsonPath().getString(
				"notificationList.fileNotification[0].fileInformation.fileURL");
		LOGGER.info("Attachment URL = " + attachmentURL);
		endTest(test);

	}

	private void acceptTransfer(String contactID, int i) {
		String test = "Accepting Transfer from User " + i;
		startTest(test);
		String sessionID = recipientSessionID;
		LOGGER.info("***" + sessionID);
		String acceptData = "{\"receiverSessionStatus\": {\"status\": \"Connected\"}}";
		String url = replaceLong(fileTransferStatusURL, apiVersion, contactID,
				sessionID);

		RestAssured.authentication = RestAssured.basic(contactID,
				applicationPassword);
		Response response = RestAssured.given().body(acceptData)
				.urlEncodingEnabled(true).expect().statusCode(204).post(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		endTest(test);
	}

	private void checkNotificationsForTransfer(String contactID,
			String cleanUserID, int j) {
		String test = "Checking if User " + j + " received any Notifications";
		startTest(test);

		String url = (CHANNEL_URL[j].split("username\\=")[0]) + "username="
				+ contactID;
		LOGGER.info("Notification URL = " + url);
		RestAssured.authentication = RestAssured.DEFAULT_AUTH;
		Response response = RestAssured
				.given()
				.urlEncodingEnabled(true)
				.expect()
				.statusCode(200)
				.body("notificationList.ftSessionInvitationNotification[0].fileInformation.resourceURL",
						Matchers.notNullValue(),
						"notificationList.ftSessionInvitationNotification[0].sessionId",
						Matchers.notNullValue(),
						"notificationList.ftSessionInvitationNotification[0].originatorAddress",
						Matchers.containsString(cleanUserID),
						"notificationList.ftSessionInvitationNotification[0].receiverName",
						Matchers.equalTo(contactID),
						"notificationList.ftSessionInvitationNotification[0].fileInformation.fileSelector.name",
						Matchers.notNullValue(),
						"notificationList.ftSessionInvitationNotification[0].fileInformation.fileSelector.type",
						Matchers.equalTo("image/png")).post(url);

		recipientSessionID = response
				.jsonPath()
				.getString(
						"notificationList.ftSessionInvitationNotification[0].sessionId");
		attachmentURL = response
				.jsonPath()
				.getString(
						"notificationList.ftSessionInvitationNotification[0].fileInformation.fileURL");

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Receiver Session = " + recipientSessionID);
		LOGGER.info("File Name = "
				+ response
						.jsonPath()
						.getString(
								"notificationList.ftSessionInvitationNotification[0].fileInformation.fileSelector.name"));
		LOGGER.info("Attachment URL = " + attachmentURL);
		LOGGER.info("Body = " + response.asString());
		endTest(test);
	}

	private void sendFilePointer(String userID, String userSIP,
			String contactSIP, int i, int j) {
		String cleanUserID = cleanPrefix(userID);

		String test = "Sending File (URL Pointer) from User " + i + " to User "
				+ j;
		startTest(test);

		String requestData = "{\"fileTransferSessionInformation\": {\"fileInformation\": {\"fileDescription\": \"This is my latest picture\",\"fileDisposition\": \"Render\","
				+ "\"fileSelector\": {\"name\": \"tux.png\",\"size\": 56320,\"type\": \"image/png\"},\"fileURL\": \"http://tux.crystalxp.net/png/brightknight-doraemon-tux-3704.png\"},"
				+ "\"originatorAddress\": \""
				+ userSIP
				+ "\",\"originatorName\": \"G3\",\"receiverAddress\": \""
				+ contactSIP + "\",\"receiverName\": \"G4\"}}";
		String url = replace(sendFileURL, apiVersion, userID);

		LOGGER.info("Sending body = " + requestData);
		LOGGER.info("URL = " + baseURI + url);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);
		Response response = RestAssured
				.given()
				.body(requestData)
				.expect()
				.statusCode(201)
				.body("resourceReference.resourceURL",
						StringContains.containsString(cleanUserID)).post(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());
		String resourceURL = response.jsonPath().getString(
				"resourceReference.resourceURL");
		String[] parts = resourceURL.split("/sessions/");
		senderSessionID = parts[i];
		LOGGER.info("Sender Session ID = " + senderSessionID);
		endTest(test);
	}

	private void unsubscribeFileTransferSubscriptions(String userID, int i) {
		String test = "Unsubscribe User " + i + " from File Transfer";
		startTest(test);

		String url = FILE_TRANSFER_SUB_URL[i];
		url = prepare(url);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);
		Response response = RestAssured.expect().log().ifError()
				.statusCode(204).delete(url);

		LOGGER.info("Received Response: " + response.getStatusCode());
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
	private void sleep() {
		try {
			LOGGER.info("Waiting........");
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

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

	private String replaceLong(String url, String apiVersion, String userID,
			String sessionID) {
		return url.replace("{apiVersion}", apiVersion)
				.replace("{userID}", userID).replace("{sessionID}", sessionID);
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
		CHANNEL_URL[i] = jsonData
				.get("notificationChannel.channelData.channelURL");
		CALLBACK_URL[i] = jsonData.get("notificationChannel.callbackURL");
		LOGGER.info("Response Received = " + response.getStatusCode());
		endTest(test);
	}

	private void subscribeToSession(String userID, int i) {
		String test = "Subscribe User " + i + " to Session Notifications";
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
		String test = "Subscribing User " + i
				+ " to Address Book Notifications";
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

	public void setUser3(String user3) {
		this.user3 = user3;
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

	public void setSendFileURL(String sendFileURL) {
		this.sendFileURL = sendFileURL;
	}

	public void setFileTransferStatusURL(String fileTransferStatusURL) {
		this.fileTransferStatusURL = fileTransferStatusURL;
	}

	public void setFileTransferSessionURL(String fileTransferSessionURL) {
		this.fileTransferSessionURL = fileTransferSessionURL;
	}

}
