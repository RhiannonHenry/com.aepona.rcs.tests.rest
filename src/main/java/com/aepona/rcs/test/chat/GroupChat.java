package com.aepona.rcs.test.chat;

import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
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
@ContextConfiguration(locations = { "/spring/application-context.xml" })
public class GroupChat {

	private final Logger LOGGER = LoggerFactory.getLogger(GroupChat.class);

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
	@Value("${user4}")
	protected String user4;
	@Value("${contact1}")
	protected String contact1;
	@Value("${contact2}")
	protected String contact2;
	@Value("${contact3}")
	protected String contact3;
	@Value("${contact4}")
	protected String contact4;
	@Value("${chatRequestDataConfirmed}")
	protected String chatRequestDataConfirmed;
	@Value("${chatRequestDataAdhoc}")
	protected String chatRequestDataAdhoc;
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
	@Value("${groupChatURL}")
	protected String groupChatURL;
	@Value("${groupChatSessionURL}")
	protected String groupChatSessionURL;
	@Value("${groupChatMessageURL}")
	protected String groupChatMessageURL;
	@Value("${groupChatParticipantsURL}")
	protected String groupChatParticipantsURL;
	@Value("${groupChatParticipantURL}")
	protected String groupChatParticipantURL;
	@Value("${groupChatParticipantStatusURL}")
	protected String groupChatParticipantStatusURL;

	private String[] RESOURCE_URL = new String[10];
	private String[] CHANNEL_URL = new String[10];
	private String[] CALLBACK_URL = new String[10];
	private String[] SESSION_SUBSCRIPTION_URL = new String[10];
	private String[] ADDRESS_BOOK_SUBSCRIPTION_URL = new String[10];
	private String[] CHAT_SUBSCRIPTION_URL = new String[10];
	private String[] SESSION_ID = new String[10];
	private String[] SESSION_URL = new String[10];
	private String[] PARTICIPANT_ID = new String[10];
	String lastTest = null;
	Boolean initialised = false;

	@Before
	public void setup() {
		LOGGER.info("Proxy URL: " + proxyURL);
		Properties props = System.getProperties();
		props.put("http.proxyHost", proxyURL);
		props.put("http.proxyPort", proxyPort);
		start();
		initialiseUser1();
		initialiseUser2();
		initialiseUser3();
		initialiseUser4();
	}

	@Test
	public void groupChatSession() throws JsonGenerationException,
			JsonMappingException, IOException {
		ParticipantInformation originator = new ParticipantInformation(
				contact1, "Rhiannon", true, UUID.randomUUID().toString());
		LOGGER.info("Participant Information (originator) = "+originator.toString());
		ParticipantInformation participant1 = new ParticipantInformation(
				contact2, "Mark", false, UUID.randomUUID().toString());
		LOGGER.info("Participant Information (contact) = "+participant1.toString());
		ParticipantInformation participant2 = new ParticipantInformation(
				contact3, "Judith", false, UUID.randomUUID().toString());

		GroupChatSessionInformation sessionInformation = new GroupChatSessionInformation(
				"Subject: Trial", new ParticipantInformation[] { originator,
						participant1, participant2 }, UUID.randomUUID()
						.toString());
		LOGGER.info("Session information = "+sessionInformation.toString());
		String test = "Creating Group Chat between User 1 (" + contact1
				+ "), User 2 (" + contact2 + ") and User 3 (" + contact3 + ")";
		startTest(test);

		ObjectMapper mapper = new ObjectMapper();
		String requestData = "{\"groupChatSessionInformation\":"
				+ mapper.writeValueAsString(sessionInformation) + "}";
		String userID = user1;
		String cleanUserID = cleanPrefix(userID);
		String url = replace(groupChatURL, apiVersion, userID);
		LOGGER.info("URL = " + baseURI + url);
		LOGGER.info("Request Body = " + requestData);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);

		Response response = RestAssured
				.given()
				.contentType("application/json")
				.body(requestData)
				.expect()
				.log()
				.ifError()
				.statusCode(201)
				.body("resourceReference.resourceURL",
						StringContains.containsString(cleanUserID)).post(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());
		endTest(test);
	}

	@Test
	public void checkNotificationsForUser1() throws JsonGenerationException, JsonMappingException, IOException {
		String userID = user1;
		int i = 1;
		createGroupChat();
		sleep();
		String test = "Check IM Notifications for User 1 (" + userID + ")";
		startTest(test);
		
		String url = (CHANNEL_URL[i].split("username\\=")[0])+"username="+userID;
		LOGGER.info("URL = "+url);
		RestAssured.authentication = RestAssured.DEFAULT_AUTH;
		Response response = RestAssured.given().expect().log().ifError().statusCode(200).body(
				"notificationList.chatEventNotification[0].eventType", Matchers.equalTo("Successful"),
				"notificationList.chatEventNotification[0].link.rel[0]", Matchers.equalTo("GroupChatSessionInformation"),
				"notificationList.chatEventNotification[0].sessionId", Matchers.notNullValue(),
				"notificationList.participantStatusNotification", Matchers.notNullValue()
				).post(url);
		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = "+response.asString());
		JsonPath jsonData = response.jsonPath();
		SESSION_ID[i] = jsonData.getString("notificationList.chatEventNotification[0].sessionId");
		SESSION_URL[i] = jsonData.getString("notificationList[0].chatEventNotification.link.href[0]");
		LOGGER.info("Session ID = "+SESSION_ID[i]);
		LOGGER.info("Session URL = "+SESSION_URL[i]);
		endTest(test);
	}
	
	@Test
	public void checkNotificationsForUser2() throws JsonGenerationException, JsonMappingException, IOException {
		String userID = user2;
		int i = 2;
		createGroupChat();
		sleep();
		
		String test = "Check IM Notifications for User (" + userID + ")";
		startTest(test);
		
		String url = (CHANNEL_URL[i].split("username\\=")[0])+"username="+userID;
		LOGGER.info("URL = "+url);
		RestAssured.authentication = RestAssured.DEFAULT_AUTH;
		Response response = RestAssured.given().expect().log().ifError().statusCode(200).post(url);
		
		JsonPath jsonData = response.jsonPath();
		LOGGER.info("Response Received = " + response.getStatusCode());
		SESSION_ID[i] = jsonData.getString("notificationList.groupSessionInvitationNotification.sessionId");
		SESSION_URL[i]=jsonData.getString("notificationList[0].groupSessionInvitationNotification.link[0].href");
		LOGGER.info("Session ID = "+SESSION_ID[i]);
		LOGGER.info("Session URL = "+SESSION_URL[i]);
		endTest(test);
	}
	
	@Test
	public void checkNotificationsForUser3() throws JsonGenerationException, JsonMappingException, IOException {
		String userID = user3;
		int i = 3;
		createGroupChat();
		sleep();
		
		String test = "Check IM Notifications for User (" + userID + ")";
		startTest(test);
		
		String url = (CHANNEL_URL[i].split("username\\=")[0])+"username="+userID;
		RestAssured.authentication = RestAssured.DEFAULT_AUTH;
		Response response = RestAssured.given().expect().log().ifError().statusCode(200).post(url);
		
		JsonPath jsonData = response.jsonPath();
		LOGGER.info("Response Received = " + response.getStatusCode());
		SESSION_ID[i] = jsonData.getString("notificationList.groupSessionInvitationNotification.sessionId");
		SESSION_URL[i]=jsonData.getString("notificationList[0].groupSessionInvitationNotification.link[0].href");
		LOGGER.info("Session ID = "+SESSION_ID[i]);
		LOGGER.info("Session URL = "+SESSION_URL[i]);
		endTest(test);
	}
	
	@Test
	public void getSessionInformation1() throws JsonGenerationException, JsonMappingException, IOException{
		String userID = user1;
		int i = 1;
		createGroupChat();
		sleep();
		checkNotificationsOriginator(userID, i);
		sleep();
		checkNotificationsParticipant(user2, 2);
		checkNotificationsParticipant(user3, 3);
		
		String test = "Getting Session Information for User ("+userID+")";
		startTest(test);
		String sessionID = SESSION_ID[i];
		String cleanUserID = cleanPrefix(userID);
		String url = replaceLong(groupChatSessionURL, apiVersion, userID, sessionID);
		LOGGER.info("URL = "+url);
		RestAssured.authentication = RestAssured.basic(userID, applicationPassword);
		Response response = RestAssured.given().expect().log().ifError().statusCode(201).body(
				"groupChatSessionInformation.resourceURL", StringContains.containsString(cleanUserID+"/group/"+SESSION_ID[i]),
				"groupChatSessionInformation.subject", Matchers.equalTo("Subject: Trial")
		).get(url);
		
		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = "+response.asString());
		endTest(test);
	}
	
	@Test
	public void getSessionInformation2() throws JsonGenerationException, JsonMappingException, IOException{
		String userID = user1;
		int i = 1;
		createGroupChat();
		sleep();
		checkNotificationsOriginator(userID, i);
		sleep();
		checkNotificationsParticipant(user2, 2);
		checkNotificationsParticipant(user3, 3);
		
		userID = user2;
		i = 2;
		getSessionInformation(userID, i);
	}

	@Test
	public void getSessionInformation3() throws JsonGenerationException, JsonMappingException, IOException{
		String userID = user1;
		int i = 1;
		createGroupChat();
		sleep();
		checkNotificationsOriginator(userID, i);
		sleep();
		checkNotificationsParticipant(user2, 2);
		checkNotificationsParticipant(user3, 3);
		
		userID = user3;
		i = 3;
		getSessionInformation(userID, i);
	}
	
	@Test
	public void getParticipantsInformationPriorAcceptance() throws JsonGenerationException, JsonMappingException, IOException{
		String userID = user1;
		int i = 1;
		createGroupChat();
		sleep();
		checkNotificationsOriginator(userID, i);
		checkNotificationsParticipant(user2, 2);
		checkNotificationsParticipant(user3, 3);
		sleep();
		String test = "Getting ALL participants information for User "+i+" ("+userID+")";
		startTest(test);
		String sessionID = SESSION_ID[i].replace("[", "").replace("]", "");
		String url = replaceLong(groupChatParticipantsURL, apiVersion, userID, sessionID);
		LOGGER.info("URL = "+baseURI+url);
		RestAssured.authentication=RestAssured.basic(userID, applicationPassword);
		Response response = RestAssured.given().expect().log().ifError().statusCode(201).body(
				"participantList", Matchers.notNullValue(),
				"participantList.participant", Matchers.notNullValue()
		).get(url);
		
		LOGGER.info("Response Recieved = "+response.getStatusCode());
		LOGGER.info("Body = "+response.asString());
		LOGGER.info("Status for Paticipant 1 = "+response.jsonPath().getString("participantList.participant[0].status"));
		LOGGER.info("Status for Paticipant 2 = "+response.jsonPath().getString("participantList.participant[1].status"));
		endTest(test);
	}
	
	@Test 
	public void sendingAccept1() throws JsonGenerationException, JsonMappingException, IOException{
		String userID = user1;
		int i = 1;
		createGroupChat();
		sleep();
		checkNotificationsOriginator(userID, i);
		checkNotificationsParticipant(user2, 2);
		checkNotificationsParticipant(user3, 3);
		
		String test = "User 2 Accepting Group Chat Request";
		startTest(test);
		userID = user2;
		i = 2;
		String jsonRequestData="{\"participantSessionStatus\":{\"status\":\"Connected\"}}";
		String sessionID = SESSION_ID[i].replace("[", "").replace("]", "");
		String participantID = contact2;
		String url = replaceExtraLong(groupChatParticipantStatusURL, apiVersion, userID, sessionID, participantID);
		LOGGER.info("URL = "+baseURI+url);
		RestAssured.authentication=RestAssured.basic(userID, applicationPassword);
		
		Response response = RestAssured.given().contentType("application/json").body(jsonRequestData).expect().log().ifError()./*statusCode(204).*/
				put(url);
		
		LOGGER.info("Response Recieved = "+response.getStatusCode());
		LOGGER.info("Body = "+response.asString());
		endTest(test);
	}
	
	@Test
	public void getParticipantsInformationAfterAcceptDecline() throws JsonGenerationException, JsonMappingException, IOException{
		String userID = user1;
		int i = 1;
		createGroupChat();
		sleep();
		checkNotificationsOriginator(userID, i);
		checkNotificationsParticipant(user2, 2);
		checkNotificationsParticipant(user3, 3);
		acceptInvitation(user2, 2, contact2);
		sleep();
		
		String test = "User 3 Declining Group Chat Invitation";
		startTest(test);
		
		userID = user3;
		i = 3;
		String participantID = contact3;
		String sessionID = SESSION_ID[i].replace("[", "").replace("]", "");
		String url = replaceExtraLong(groupChatParticipantURL, apiVersion, userID, sessionID, participantID);
		LOGGER.info("URL = "+baseURI+url);
		RestAssured.authentication = RestAssured.basic(userID, applicationPassword);
		Response response = RestAssured.given().expect().log().ifError().statusCode(204).
				delete(url);
		
		LOGGER.info("Response Recieved = "+response.getStatusCode());
		LOGGER.info("Body = "+response.asString());
		endTest(test);
		
		sleep();
		sleep();
		
		userID = user1;
		i = 1;
		
		test = "Getting ALL participants information for User "+i+" ("+userID+")";
		startTest(test);
		sessionID = SESSION_ID[i].replace("[", "").replace("]", "");
		url = replaceLong(groupChatParticipantsURL, apiVersion, userID, sessionID);
		LOGGER.info("URL = "+baseURI+url);
		RestAssured.authentication = RestAssured.basic(userID, applicationPassword);
		response = RestAssured.given().expect().log().ifError().statusCode(201).get(url);
		
		LOGGER.info("Response Recieved = "+response.getStatusCode());
		LOGGER.info("Body = "+response.asString());
		endTest(test);
	}
	
	@Test
	public void getParticipantsInformationAfterAcceptance() throws JsonGenerationException, JsonMappingException, IOException{
		String userID = user1;
		int i = 1;
		createGroupChat();
		sleep();
		checkNotificationsOriginator(userID, i);
		checkNotificationsParticipant(user2, 2);
		checkNotificationsParticipant(user3, 3);
		acceptInvitation(user2, 2, contact2);
		acceptInvitation(user3, 3, contact3);
		sleep();
		sleep();
		sleep();
		
		getParticipantsInformation(userID, i);
	}
	
	@Test
	public void participantGettingParticipantsInformation1() throws JsonGenerationException, JsonMappingException, IOException{
		String userID = user1;
		int i = 1;
		createGroupChat();
		sleep();
		checkNotificationsOriginator(userID, i);
		checkNotificationsParticipant(user2, 2);
		checkNotificationsParticipant(user3, 3);
		acceptInvitation(user2, 2, contact2);
		acceptInvitation(user3, 3, contact3);
		sleep();
		sleep();
		sleep();
		
		getParticipantsInformation(user2, 2);
	}
	
	@Test
	public void participantGettingParticipantsInformation2() throws JsonGenerationException, JsonMappingException, IOException{
		String userID = user1;
		int i = 1;
		createGroupChat();
		sleep();
		checkNotificationsOriginator(userID, i);
		checkNotificationsParticipant(user2, 2);
		checkNotificationsParticipant(user3, 3);
		acceptInvitation(user2, 2, contact2);
		acceptInvitation(user3, 3, contact3);
		sleep();
		sleep();
		sleep();
		
		getParticipantsInformation(user3, 3);
	}
	
	@Test
	public void sendMessageFromOriginator() throws JsonGenerationException, JsonMappingException, IOException{
		String userID = user1;
		int i = 1;
		createGroupChat();
		sleep();
		checkNotificationsOriginator(userID, i);
		checkNotificationsParticipant(user2, 2);
		checkNotificationsParticipant(user3, 3);
		acceptInvitation(user2, 2, contact2);
		acceptInvitation(user3, 3, contact3);
		sleep();
		sleep();
		sleep();
		
		String test = "Sending Message from Originator to Participants";
		startTest(test);
		String text = "Hello Everybody!";
		String reportRequest = "Displayed";
		String encodedUserID = encode(userID);
		String sessionID = SESSION_ID[i].replace("[", "").replace("]", "");
		String url = replaceLong(groupChatMessageURL, apiVersion, userID, sessionID);
		LOGGER.info("URL = "+baseURI+url);
		ChatMessage chatMessage = new ChatMessage(text, reportRequest);
		ObjectMapper mapper=new ObjectMapper();	
		String requestData="{\"chatMessage\":"+mapper.writeValueAsString(chatMessage)+"}";
		
		LOGGER.info("Request Body = "+requestData);
		
		RestAssured.authentication = RestAssured.basic(userID, applicationPassword);
		
		Response response = RestAssured.given().contentType("application/json").body(requestData).expect().log().ifError().statusCode(201).body(
				"resourceReference.resourceURL", StringContains.containsString(encodedUserID+"/group/")).post(url);
		
		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());
		endTest(test);
	}
	
	@Test
	public void checkNotificationsForMessageSent() throws JsonGenerationException, JsonMappingException, IOException{
		String userID = user1;
		int i = 1;
		createGroupChat();
		sleep();
		checkNotificationsOriginator(userID, i);
		checkNotificationsParticipant(user2, 2);
		checkNotificationsParticipant(user3, 3);
		acceptInvitation(user2, 2, contact2);
		acceptInvitation(user3, 3, contact3);
		sleep();
		sleep();
		sleep();
	
		String text = "Hello Everybody!";
		sendMessage(userID, i, text);
		
		checkIMNotificationsOriginator(userID, i);
	}
	
	@Test
	public void checkNotificationsForMessageArrival1() throws JsonGenerationException, JsonMappingException, IOException{
		String userID = user1;
		int i = 1;
		createGroupChat();
		sleep();
		checkNotificationsOriginator(userID, i);
		checkNotificationsParticipant(user2, 2);
		checkNotificationsParticipant(user3, 3);
		acceptInvitation(user2, 2, contact2);
		acceptInvitation(user3, 3, contact3);
		sleep();
		sleep();
		sleep();
	
		String text = "Hello Everybody!";
		sendMessage(userID, i, text);
		String originatorSIP = contact1;
		sleep();
		checkIMNotificationsParticipant(user2, 2, originatorSIP, text);
		checkIMNotificationsParticipant(user3, 3, originatorSIP, text);
	}
	
	@Test
	public void sendMessageFromUser2() throws JsonGenerationException, JsonMappingException, IOException{
		String userID = user1;
		int i = 1;
		createGroupChat();
		sleep();
		checkNotificationsOriginator(userID, i);
		checkNotificationsParticipant(user2, 2);
		checkNotificationsParticipant(user3, 3);
		acceptInvitation(user2, 2, contact2);
		acceptInvitation(user3, 3, contact3);
		sleep();
		sleep();
		sleep();
	
		String text = "Hello Everybody!";
		sendMessage(userID, i, text);
		String originatorSIP = contact1;
		sleep();
		checkIMNotificationsParticipant(user2, 2, originatorSIP, text);
		checkIMNotificationsParticipant(user3, 3, originatorSIP, text);
		
		userID = user2;
		i = 2;
		text = "Hello! User 2 Here...";
		sendMessage(userID, i, text);
	}
	
	@Test
	public void checkNotificationOfMessageArrival2() throws JsonGenerationException, JsonMappingException, IOException{
		String userID = user1;
		int i = 1;
		createGroupChat();
		sleep();
		checkNotificationsOriginator(userID, i);
		checkNotificationsParticipant(user2, 2);
		checkNotificationsParticipant(user3, 3);
		acceptInvitation(user2, 2, contact2);
		acceptInvitation(user3, 3, contact3);
		sleep();
		sleep();
		sleep();
	
		String text = "Hello Everybody!";
		sendMessage(userID, i, text);
		String originatorSIP = contact1;
		sleep();
		sleep();
		checkIMNotificationsParticipant(user2, 2, originatorSIP, text);
		checkIMNotificationsParticipant(user3, 3, originatorSIP, text);
		
		userID = user2;
		i = 2;
		text = "Hello! User 2 Here...";
		sendMessage(userID, i, text);
		sleep();
		sleep();
		
		checkIMNotificationsParticipant(user1, 1, contact2, text);
		checkIMNotificationsParticipant(user3, 3, contact2, text);
	}
	
	@Test
	public void addUserToChat() throws JsonGenerationException, JsonMappingException, IOException{
		String userID = user1;
		int i = 1;
		createGroupChat();
		sleep();
		checkNotificationsOriginator(userID, i);
		checkNotificationsParticipant(user2, 2);
		checkNotificationsParticipant(user3, 3);
		acceptInvitation(user2, 2, contact2);
		acceptInvitation(user3, 3, contact3);
		
		ParticipantInformation participant3 = new ParticipantInformation(contact4, "Ronan", false, UUID.randomUUID().toString());
		
		String test = "Adding User 4 to the Group Chat";
		startTest(test);
		
		ParticipantInformation[] participants = new ParticipantInformation[]{participant3};
		ObjectMapper mapper=new ObjectMapper();	
		String requestData="{\"participantList\":{\"participant\":"+mapper.writeValueAsString(participants)+"}}";
		String cleanUserID = cleanPrefix(userID);
		String sessionID = SESSION_ID[i].replace("[", "").replace("]", "");
		String url = replaceLong(groupChatParticipantsURL, apiVersion, userID, sessionID);
		LOGGER.info("URL = "+baseURI+url);
		LOGGER.info("Request Data = "+requestData);
		RestAssured.authentication = RestAssured.basic(userID, applicationPassword);

		Response response = RestAssured.given().contentType("application/json").body(requestData).expect().log().ifError().statusCode(201).body(
				"resourceReference.resourceURL", StringContains.containsString(cleanUserID)).post(url);
		
		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());
		endTest(test);
	}
	
	@Test
	public void newUserAccept() throws JsonGenerationException, JsonMappingException, IOException{
		String userID = user1;
		int i = 1;
		createGroupChat();
		sleep();
		checkNotificationsOriginator(userID, i);
		checkNotificationsParticipant(user2, 2);
		checkNotificationsParticipant(user3, 3);
		acceptInvitation(user2, 2, contact2);
		acceptInvitation(user3, 3, contact3);
		
		ParticipantInformation participant3 = new ParticipantInformation(contact4, "Ronan", false, UUID.randomUUID().toString());
		
		String test = "Adding User 4 to the Group Chat";
		startTest(test);
		
		ParticipantInformation[] participants = new ParticipantInformation[]{participant3};
		ObjectMapper mapper=new ObjectMapper();	
		String requestData="{\"participantList\":{\"participant\":"+mapper.writeValueAsString(participants)+"}}";
		String cleanUserID = cleanPrefix(userID);
		String sessionID = SESSION_ID[i].replace("[", "").replace("]", "");
		String url = replaceLong(groupChatParticipantsURL, apiVersion, userID, sessionID);
		RestAssured.authentication = RestAssured.basic(userID, applicationPassword);

		Response response = RestAssured.given().contentType("application/json").body(requestData).expect().log().ifError().statusCode(201).body(
				"resourceReference.resourceURL", StringContains.containsString(cleanUserID)).post(url);
		
		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());
		endTest(test);
		
		sleep();
		checkNotificationsParticipant(user4, 4);
		sleep();
		acceptInvitation(user4, 4, contact4);
		sleep();
		sleep();
		getParticipantsInformationNew(userID, i);
	}
	
	@Test
	public void sendMessageFromNewParticipant() throws JsonGenerationException, JsonMappingException, IOException{
		String userID = user1;
		int i = 1;
		createGroupChat();
		sleep();
		checkNotificationsOriginator(userID, i);
		checkNotificationsParticipant(user2, 2);
		checkNotificationsParticipant(user3, 3);
		acceptInvitation(user2, 2, contact2);
		acceptInvitation(user3, 3, contact3);
		
		ParticipantInformation participant3 = new ParticipantInformation(contact4, "Ronan", false, UUID.randomUUID().toString());
		
		String test = "Adding User 4 to the Group Chat";
		startTest(test);
		
		ParticipantInformation[] participants = new ParticipantInformation[]{participant3};
		ObjectMapper mapper=new ObjectMapper();	
		String requestData="{\"participantList\":{\"participant\":"+mapper.writeValueAsString(participants)+"}}";
		String cleanUserID = cleanPrefix(userID);
		String sessionID = SESSION_ID[i].replace("[", "").replace("]", "");
		String url = replaceLong(groupChatParticipantsURL, apiVersion, userID, sessionID);
		RestAssured.authentication = RestAssured.basic(userID, applicationPassword);

		Response response = RestAssured.given().contentType("application/json").body(requestData).expect().log().ifError().statusCode(201).body(
				"resourceReference.resourceURL", StringContains.containsString(cleanUserID)).post(url);
		
		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());
		endTest(test);
		
		sleep();
		checkNotificationsParticipant(user4, 4);
		sleep();
		acceptInvitation(user4, 4, contact4);
		
		test = "Sending Message from New User (user 4)";
		startTest(test);
		userID = user4;
		i = 4;
		String text = "Hello! Thanks for including me!!";
		sendMessage(userID, i, text);
		endTest(test);
	}
	
	@Test
	public void leaveChat() throws JsonGenerationException, JsonMappingException, IOException{
		String userID = user1;
		int i = 1;
		createGroupChat();
		sleep();
		checkNotificationsOriginator(userID, i);
		checkNotificationsParticipant(user2, 2);
		checkNotificationsParticipant(user3, 3);
		acceptInvitation(user2, 2, contact2);
		acceptInvitation(user3, 3, contact3);
		
		ParticipantInformation participant3 = new ParticipantInformation(contact4, "Ronan", false, UUID.randomUUID().toString());
		
		String test = "Adding User 4 to the Group Chat";
		startTest(test);
		
		ParticipantInformation[] participants = new ParticipantInformation[]{participant3};
		ObjectMapper mapper=new ObjectMapper();	
		String requestData="{\"participantList\":{\"participant\":"+mapper.writeValueAsString(participants)+"}}";
		String cleanUserID = cleanPrefix(userID);
		String sessionID = SESSION_ID[i].replace("[", "").replace("]", "");
		String url = replaceLong(groupChatParticipantsURL, apiVersion, userID, sessionID);
		RestAssured.authentication = RestAssured.basic(userID, applicationPassword);

		Response response = RestAssured.given().contentType("application/json").body(requestData).expect().log().ifError().statusCode(201).body(
				"resourceReference.resourceURL", StringContains.containsString(cleanUserID)).post(url);
		
		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());
		endTest(test);
		
		sleep();
		checkNotificationsParticipant(user4, 4);
		sleep();
		acceptInvitation(user4, 4, contact4);
		
		test = "User 3 leaves chat";
		startTest(test);
		userID = user3;
		i = 3;
		sessionID = SESSION_ID[i].replace("[", "").replace("]", "");
		String participantID = contact3;
		url = replaceExtraLong(groupChatParticipantURL, apiVersion, userID, sessionID, participantID);
		LOGGER.info("URL = "+baseURI+url);
		RestAssured.authentication = RestAssured.basic(userID, applicationPassword);
		response = RestAssured.given().expect().log().ifError().statusCode(204).
				delete(url);
		
		LOGGER.info("Response Received = " + response.getStatusCode());
		endTest(test);
	}
	// *************************  HELPERS  *******************************
	private void createGroupChat() throws JsonGenerationException, JsonMappingException, IOException{
		ParticipantInformation originator = new ParticipantInformation(
				contact1, "Rhiannon", true, UUID.randomUUID().toString());
		ParticipantInformation participant1 = new ParticipantInformation(
				contact2, "Mark", false, UUID.randomUUID().toString());
		ParticipantInformation participant2 = new ParticipantInformation(
				contact3, "Judith", false, UUID.randomUUID().toString());

		GroupChatSessionInformation sessionInformation = new GroupChatSessionInformation(
				"Subject: Trial", new ParticipantInformation[] { originator,
						participant1, participant2 }, UUID.randomUUID()
						.toString());

		String test = "Creating Group Chat between User 1 (" + contact1
				+ "), User 2 (" + contact2 + ") and User 3 (" + contact3 + ")";
		startTest(test);

		ObjectMapper mapper = new ObjectMapper();
		String requestData = "{\"groupChatSessionInformation\":"
				+ mapper.writeValueAsString(sessionInformation) + "}";

		String userID = user1;
		String cleanUserID = cleanPrefix(userID);
		String url = replace(groupChatURL, apiVersion, userID);
		LOGGER.info("URL = " + baseURI + url);
		LOGGER.info("Request Body = " + requestData);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);

		Response response = RestAssured
				.given()
				.contentType("application/json")
				.body(requestData)
				.expect()
				.log()
				.ifError()
				.statusCode(201)
				.body("resourceReference.resourceURL",
						StringContains.containsString(cleanUserID)).post(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());
		endTest(test);
	}
	
	private void checkNotificationsOriginator(String userID, int i){
		String test = "Check IM Notifications for User (" + userID + ")";
		startTest(test);
		
		String url = (CHANNEL_URL[i].split("username\\=")[0])+"username="+userID;
		RestAssured.authentication = RestAssured.DEFAULT_AUTH;
		Response response = RestAssured.given().expect().log().ifError().statusCode(200).post(url);
		
		JsonPath jsonData = response.jsonPath();
		LOGGER.info("Response Received = " + response.getStatusCode());
		SESSION_ID[i] = jsonData.getString("notificationList.chatEventNotification[0].sessionId");
		SESSION_URL[i] = jsonData.getString("notificationList[0].chatEventNotification.link.href[0]");
		LOGGER.info("Session ID = "+SESSION_ID[i]);
		LOGGER.info("Session URL = "+SESSION_URL[i]);
		endTest(test);
	}
	
	public void checkNotificationsParticipant(String userID, int i){
		String test = "Check IM Notifications for User (" + userID + ")";
		startTest(test);
		
		String url = (CHANNEL_URL[i].split("username\\=")[0])+"username="+userID;
		RestAssured.authentication = RestAssured.DEFAULT_AUTH;
		Response response = RestAssured.given().expect().log().ifError().statusCode(200).post(url);
		
		JsonPath jsonData = response.jsonPath();
		LOGGER.info("Response Received = " + response.getStatusCode());
		SESSION_ID[i] = jsonData.getString("notificationList.groupSessionInvitationNotification.sessionId");
		SESSION_URL[i]=jsonData.getString("notificationList[0].groupSessionInvitationNotification.link[0].href");
		LOGGER.info("Session ID = "+SESSION_ID[i]);
		LOGGER.info("Session URL = "+SESSION_URL[i]);
		endTest(test);
	}
	
	private void getSessionInformation(String userID, int i){
		String test = "Getting Session Information for User ("+userID+")";
		startTest(test);
		String sessionID = SESSION_ID[i].replace("[", "").replace("]", "");
		String cleanUserID = cleanPrefix(userID);
		String url = replaceLong(groupChatSessionURL, apiVersion, userID, sessionID);
		LOGGER.info("URL = "+baseURI+url);
		
		RestAssured.authentication = RestAssured.basic(userID, applicationPassword);
		Response response = RestAssured.given().expect().log().ifError().statusCode(201).body(
				"groupChatSessionInformation.resourceURL", StringContains.containsString(cleanUserID+"/group/"+sessionID),
				"groupChatSessionInformation.subject", Matchers.equalTo("Subject: Trial")
		).get(url);
		
		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = "+response.asString());
		endTest(test);
	}
	
	private void acceptInvitation(String userID, int i, String participantID){
		String test = "Accepting Group Chat Request - User "+i;
		startTest(test);
		String jsonRequestData="{\"participantSessionStatus\":{\"status\":\"Connected\"}}";
		String sessionID = SESSION_ID[i].replace("[", "").replace("]", "");
		String url = replaceExtraLong(groupChatParticipantStatusURL, apiVersion, userID, sessionID, participantID);
		RestAssured.authentication=RestAssured.basic(userID, applicationPassword);
		
		Response response = RestAssured.given().contentType("application/json").body(jsonRequestData).expect().log().ifError().statusCode(204).
				put(url);
		
		LOGGER.info("Response Recieved = "+response.getStatusCode());
		endTest(test);
	}
	
	private void getParticipantsInformation(String userID, int i){
		String test = "Getting ALL participants information for User "+i+" ("+userID+")";
		startTest(test);
		String sessionID = SESSION_ID[i].replace("[", "").replace("]", "");
		String url = replaceLong(groupChatParticipantsURL, apiVersion, userID, sessionID);
		
		RestAssured.authentication=RestAssured.basic(userID, applicationPassword);
		Response response = RestAssured.given().expect().log().ifError().statusCode(201).body(
				"participantList", Matchers.notNullValue(),
				"participantList.participant", Matchers.notNullValue()
		).get(url);
		
		LOGGER.info("Response Recieved = "+response.getStatusCode());
		LOGGER.info("Body = "+response.asString());
		LOGGER.info("Status for Paticipant "+response.jsonPath().getString("participantList.participant[0].address")+" = "+response.jsonPath().getString("participantList.participant[0].status"));
		LOGGER.info("Status for Paticipant "+response.jsonPath().getString("participantList.participant[1].address")+" = "+response.jsonPath().getString("participantList.participant[1].status"));
		endTest(test);
	}
	
	private void getParticipantsInformationNew(String userID, int i){
		String test = "Getting ALL participants information for User "+i+" ("+userID+")";
		startTest(test);
		String sessionID = SESSION_ID[i].replace("[", "").replace("]", "");
		String url = replaceLong(groupChatParticipantsURL, apiVersion, userID, sessionID);
		
		RestAssured.authentication=RestAssured.basic(userID, applicationPassword);
		Response response = RestAssured.given().expect().log().ifError().statusCode(201).body(
				"participantList", Matchers.notNullValue(),
				"participantList.participant", Matchers.notNullValue()
		).get(url);
		
		LOGGER.info("Response Recieved = "+response.getStatusCode());
		LOGGER.info("Body = "+response.asString());
		LOGGER.info("Status for Paticipant "+response.jsonPath().getString("participantList.participant[0].address")+" = "+response.jsonPath().getString("participantList.participant[0].status"));
		LOGGER.info("Status for Paticipant "+response.jsonPath().getString("participantList.participant[1].address")+" = "+response.jsonPath().getString("participantList.participant[1].status"));
		LOGGER.info("Status for Paticipant "+response.jsonPath().getString("participantList.participant[2].address")+" = "+response.jsonPath().getString("participantList.participant[2].status"));
		endTest(test);
	}
	
	private void sendMessage(String userID, int i, String text) throws JsonGenerationException, JsonMappingException, IOException{
		String test = "Sending Message";
		startTest(test);
		String reportRequest = "Displayed";
		String encodedUserID = encode(userID);
		String sessionID = SESSION_ID[i].replace("[", "").replace("]", "");
		String url = replaceLong(groupChatMessageURL, apiVersion, userID, sessionID);
		
		ChatMessage chatMessage = new ChatMessage(text, reportRequest);
		ObjectMapper mapper=new ObjectMapper();	
		String requestData="{\"chatMessage\":"+mapper.writeValueAsString(chatMessage)+"}";
		
		LOGGER.info("Request Body = "+requestData);
		
		RestAssured.authentication = RestAssured.basic(userID, applicationPassword);
		
		Response response = RestAssured.given().contentType("application/json").body(requestData).expect().log().ifError().statusCode(201).body(
				"resourceReference.resourceURL", StringContains.containsString(encodedUserID+"/group/")).post(url);
		
		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());
		endTest(test);
	}
	
	private void checkIMNotificationsOriginator(String userID, int i){
		String test = "Check IM Notifications for User (" + userID + ")";
		startTest(test);
		
		String url = (CHANNEL_URL[i].split("username\\=")[0])+"username="+userID;
		RestAssured.authentication = RestAssured.DEFAULT_AUTH;
		Response response = RestAssured.given().expect().log().ifError().statusCode(200).post(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());
		endTest(test);
	}
	
	private void checkIMNotificationsParticipant(String userID, int i, String originatorSIP, String text){
		String test = "Check IM Notifications for User (" + userID + ")";
		startTest(test);
		
		String url = (CHANNEL_URL[i].split("username\\=")[0])+"username="+userID;
		RestAssured.authentication = RestAssured.DEFAULT_AUTH;
		Response response = RestAssured.given().expect().log().ifError().statusCode(200).post(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());
		endTest(test);
	}
	
	// METHODS AND RESOURCES
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

	public void initialiseUser3() {
		LOGGER.info("Initialising User 3 (+441110000003)...");
		registerUser(user3);
		startNotificationChannel(user3, 3);
		subscribeToSession(user3, 3);
		subscribeToAddressBook(user3, 3);
		subscribeToChatNotificationsConfirmed(user3, 3);
		clearPendingNotifications(user3, 3);
		LOGGER.info("User 3 has been Initalised!");
	}

	public void initialiseUser4() {
		LOGGER.info("Initialising User 4 (+441110000004)...");
		registerUser(user4);
		startNotificationChannel(user4, 4);
		subscribeToSession(user4, 4);
		subscribeToAddressBook(user4, 4);
		subscribeToChatNotificationsConfirmed(user4, 4);
		clearPendingNotifications(user4, 4);
		LOGGER.info("User 4 has been Initalised!");
	}

	// ********* GENERAL METHODS **********
	public void start() {
		if (!initialised) {
			RestAssured.baseURI = baseURI;
			RestAssured.port = port;
			RestAssured.basePath = "";
			RestAssured.urlEncodingEnabled = true;
			initialised = true;
		}
		SESSION_ID = new String[10];
		SESSION_URL = new String[10];
		PARTICIPANT_ID = new String[10];
	}

	private void sleep() {
		try {
			LOGGER.info("Waiting........");
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

	private String replace(String url, String apiVersion,
			String userID) {
		return url.replace("{apiVersion}", apiVersion).replace(
				"{userID}", userID);
	}

	private String replaceLong(String url, String apiVersion, String userID,
			String sessionID) {
		return url.replace("{apiVersion}", apiVersion)
				.replace("{userID}", userID).replace("{sessionID}", sessionID);
	}

	private String replaceExtraLong(String url, String apiVersion,
			String userID, String sessionID, String participantID) {
		return url.replace("{apiVersion}", apiVersion)
				.replace("{userID}", userID).replace("{participantID}", participantID)
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
		String test = "Starting the Notification Channel for - User " + i;
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
		String test = "Subscribe User to Session Notifications - User " + i;
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
		String test = "Subscribing User to Address Book Notifications - User "
				+ i;
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
		String test = "Subscribe User to Chat Notifications (Confirmed)- User "
				+ i;
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

	public void setUser4(String user4) {
		this.user4 = user4;
	}

	public void setContact1(String contact1) {
		this.contact1 = contact1;
	}

	public void setContact2(String contact2) {
		this.contact2 = contact2;
	}

	public void setContact3(String contact3) {
		this.contact3 = contact3;
	}

	public void setContact4(String contact4) {
		this.contact4 = contact4;
	}

	public void setChatRequestDataConfirmed(String chatRequestDataConfirmed) {
		this.chatRequestDataConfirmed = chatRequestDataConfirmed;
	}

	public void setChatRequestDataAdhoc(String chatRequestDataAdhoc) {
		this.chatRequestDataAdhoc = chatRequestDataAdhoc;
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

	public void setGroupChatURL(String groupChatURL) {
		this.groupChatURL = groupChatURL;
	}

	public void setGroupChatSessionURL(String groupChatSessionURL) {
		this.groupChatSessionURL = groupChatSessionURL;
	}

	public void setGroupChatMessageURL(String groupChatMessageURL) {
		this.groupChatMessageURL = groupChatMessageURL;
	}

	public void setGroupChatParticipantsURL(String groupChatParticipantsURL) {
		this.groupChatParticipantsURL = groupChatParticipantsURL;
	}

	public void setGroupChatParticipantURL(String groupChatParticipantURL) {
		this.groupChatParticipantURL = groupChatParticipantURL;
	}

	public void setGroupChatParticipantStatusURL(
			String groupChatParticipantStatusURL) {
		this.groupChatParticipantStatusURL = groupChatParticipantStatusURL;
	}

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

	static class ParticipantInformation {
		String address;
		String name;
		boolean isOriginator;
		String clientCorrelator;
		String resourceURL;

		public String getAddress() {
			return address;
		}

		public String getName() {
			return name;
		}

		public boolean getIsOriginator() {
			return isOriginator;
		}

		public String getClientCorrelator() {
			return clientCorrelator;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setIsOriginator(boolean isOriginator) {
			this.isOriginator = isOriginator;
		}

		public void setClientCorrelator(String clientCorrelator) {
			this.clientCorrelator = clientCorrelator;
		}

		@JsonIgnore
		public String getResourceURL() {
			return resourceURL;
		}

		public void setResourceURL(String resourceURL) {
			this.resourceURL = resourceURL;
		}

		public ParticipantInformation(String address, String name,
				boolean isOriginator, String clientCorrelator) {
			this.address = address;
			this.name = name;
			this.isOriginator = isOriginator;
			this.clientCorrelator = clientCorrelator;
		}
	}

	static class GroupChatSessionInformation {
		String subject;
		ParticipantInformation[] participant;
		String clientCorrelator;
		String resourceURL;

		public String getSubject() {
			return subject;
		}

		public ParticipantInformation[] getParticipant() {
			return participant;
		}

		public String getClientCorrelator() {
			return clientCorrelator;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}

		public void setParticipant(ParticipantInformation[] participant) {
			this.participant = participant;
		}

		public void setClientCorrelator(String clientCorrelator) {
			this.clientCorrelator = clientCorrelator;
		}

		@JsonIgnore
		public String getResourceURL() {
			return resourceURL;
		}

		public void setResourceURL(String resourceURL) {
			this.resourceURL = resourceURL;
		}

		public GroupChatSessionInformation(String subject,
				ParticipantInformation[] participant, String clientCorrelator) {
			this.subject = subject;
			this.participant = participant;
			this.clientCorrelator = clientCorrelator;
		}

	}
}
