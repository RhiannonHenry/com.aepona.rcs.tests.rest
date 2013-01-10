package com.aepona.rcs.test.addressBook;

import java.util.List;
import java.util.Properties;

import org.hamcrest.Matchers;
import org.hamcrest.core.StringContains;
import org.junit.Before;
import org.junit.Ignore;
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
public class AddEditContact {

	private final Logger LOGGER = LoggerFactory.getLogger(AddEditContact.class);

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
	@Value("${invalidUser}")
	protected String invalidUser;
	@Value("${contactURL}")
	protected String contactURL;
	@Value("${contactsURL}")
	protected String contactsURL;
	@Value("${attributesURL}")
	protected String attributesURL;
	@Value("${attributeURL}")
	protected String attributeURL;
	@Value("${addingContactRequestData}")
	protected String addingContactRequestData;
	@Value("${contact1}")
	protected String contact1;
	@Value("${contact2}")
	protected String contact2;
	@Value("${contact3}")
	protected String contact3;
	@Value("${contact4}")
	protected String contact4;
	@Value("${invalidContact}")
	protected String invalidContact;
	@Value("${updateContactAttributeRequestData}")
	protected String updateContactAttributeRequestData;

	String lastTest = null;
	Boolean initialised = false;

	@Before
	public void setup() {
		Properties props = System.getProperties();
		props.put("http.proxyHost", proxyURL);
		props.put("http.proxyPort", proxyPort);
		start();
	}

	@Test
	public void clearContactsForUser1() {
		String test = "Deleting all existing contacts for User 1";
		startTest(test);
		String userID = user1;

		deleteAnyContacts(userID);

		endTest(test);

		test = "Checking that all contacts have been removed for User 1";
		startTest(test);
		String cleanUserID = cleanPrefix(userID);
		String url = replace(contactURL, apiVersion, userID);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);

		Response response = RestAssured
				.expect()
				.log()
				.ifError()
				.statusCode(200)
				.body("contactCollection.resourceURL",
						StringContains.containsString(cleanUserID),
						"contactCollection.contact", Matchers.nullValue())
				.when().get(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());

		endTest(test);
	}

	@Test
	public void clearContactsForUser2() {
		String test = "Deleting all existing contacts for User 2";
		startTest(test);
		String userID = user2;

		deleteAnyContacts(userID);

		endTest(test);

		test = "Checking that all contacts have been removed for User 2";
		startTest(test);
		String cleanUserID = cleanPrefix(userID);
		String url = replace(contactURL, apiVersion, userID);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);

		Response response = RestAssured
				.expect()
				.log()
				.ifError()
				.statusCode(200)
				.body("contactCollection.resourceURL",
						StringContains.containsString(cleanUserID),
						"contactCollection.contact", Matchers.nullValue())
				.when().get(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());

		endTest(test);
	}

	@Test
	public void clearContactsForUser3() {
		String test = "Deleting all existing contacts for User 3";
		startTest(test);
		String userID = user3;

		deleteAnyContacts(userID);

		endTest(test);

		test = "Checking that all contacts have been removed for User 3";
		startTest(test);
		String cleanUserID = cleanPrefix(userID);
		String url = replace(contactURL, apiVersion, userID);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);

		Response response = RestAssured
				.expect()
				.log()
				.ifError()
				.statusCode(200)
				.body("contactCollection.resourceURL",
						StringContains.containsString(cleanUserID),
						"contactCollection.contact", Matchers.nullValue())
				.when().get(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());

		endTest(test);
	}

	@Test
	public void clearContactsForUser4() {
		String test = "Deleting all existing contacts for User 4";
		startTest(test);
		String userID = user4;

		deleteAnyContacts(userID);

		endTest(test);

		test = "Checking that all contacts have been removed for User 4";
		startTest(test);
		String cleanUserID = cleanPrefix(userID);
		String url = replace(contactURL, apiVersion, userID);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);

		Response response = RestAssured
				.expect()
				.log()
				.ifError()
				.statusCode(200)
				.body("contactCollection.resourceURL",
						StringContains.containsString(cleanUserID),
						"contactCollection.contact", Matchers.nullValue())
				.when().get(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());

		endTest(test);
	}

	@Test
	public void addingAContactForUser1() {
		String test = "Adding a contact to User 1's contact list";
		startTest(test);
		String userID = user1;
		String contactID = contact1;
		String cleanContactID = cleanPrefix(contactID);
		String requestData = requestDataClean(addingContactRequestData,
				contactID, cleanContactID);
		String url = replaceContact(contactsURL, apiVersion, userID, contactID);
		String encodedContactID = encode(contactID);
		String encodedUserID = encode(userID);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);

		Response response = RestAssured
				.given()
				.body(requestData)
				.expect()
				.log()
				.ifError()
				.statusCode(201)
				.body("contact.resourceURL",
						StringContains.containsString(encodedUserID
								+ "/contacts/" + encodedContactID),
						"contact.contactId", Matchers.equalTo(contactID),
						"contact.attributeList.attribute.value",
						Matchers.hasItem(cleanContactID)).when().put(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());

		endTest(test);
	}

	@Test
	public void addingAnUnknownContactForUnknownUser() {
		String test = "Adding an Unknown Contact to Unknown User Address Book";
		startTest(test);
		String userID = invalidUser;
		String contactID = invalidContact;
		String cleanContactID = "InvalidContact";
		String requestData = requestDataClean(addingContactRequestData,
				contactID, cleanContactID);
		String url = replaceContact(contactsURL, apiVersion, userID, contactID);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);

		Response response = RestAssured.given().body(requestData).expect()
				.log().ifError().statusCode(401).when().put(url);

		LOGGER.info("Response Received = " + response.getStatusCode());

		endTest(test);
	}

	@Test
	public void addingAValidContactForUnknownUser() {
		String test = "Adding an Valid Contact to Unknown User Address Book";
		startTest(test);
		String userID = invalidUser;
		String contactID = contact1;
		String cleanContactID = cleanPrefix(contactID);
		String requestData = requestDataClean(addingContactRequestData,
				contactID, cleanContactID);
		String url = replaceContact(contactsURL, apiVersion, userID, contactID);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);

		Response response = RestAssured.given().body(requestData).expect()
				.log().ifError().statusCode(401).when().put(url);

		LOGGER.info("Response Received = " + response.getStatusCode());

		endTest(test);
	}

	@Test
	public void addingAnUnknownContactForValidUser1() {
		String test = "Adding an Unknown Contact to Valid User 1 Address Book";
		startTest(test);
		String userID = user1;
		String contactID = invalidContact;
		String cleanContactID = "InvalidContact";
		String encodedUserID = encode(userID);
		String encodedContactID = encode(contactID);
		String requestData = requestDataClean(addingContactRequestData,
				contactID, cleanContactID);
		String url = replaceContact(contactsURL, apiVersion, userID, contactID);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);

		Response response = RestAssured
				.given()
				.body(requestData)
				.expect()
				.log()
				.ifError()
				.statusCode(201)
				.body("contact.resourceURL",
						StringContains.containsString(encodedUserID
								+ "/contacts/" + encodedContactID),
						"contact.contactId", Matchers.equalTo(contactID),
						"contact.attributeList.attribute.value",
						Matchers.hasItem(cleanContactID)).when().put(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());

		endTest(test);
	}

	@Test
	public void addingMismatchedContactForValidUser1() {
		String test = "Adding a Mismatched Contact to Valid User 1 Address Book";
		startTest(test);
		String userID = user1;
		String contactID = invalidContact;
		String mismatchedContactID = "sip:MismatchedContact@aepona.com";
		String cleanContactID = "InvalidContact";
		String requestData = requestDataClean(addingContactRequestData,
				mismatchedContactID, cleanContactID);
		String url = replaceContact(contactsURL, apiVersion, userID, contactID);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);

		Response response = RestAssured
				.given()
				.body(requestData)
				.expect()
				.log()
				.ifError()
				.statusCode(403)
				.body("requestError.serviceException.variables",
						Matchers.hasItem("Parameters are not valid (contactIds are differents)"),
						"requestError.serviceException.messageId",
						Matchers.equalTo("SVC002")).when().put(url);

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
	public void addContactsToUser2() {
		clearContactsForUser2();
		String test = "Adding First Contact to User 2";
		startTest(test);
		String userID = user2;
		String contactID = contact1;
		addContact(userID, contactID);
		endTest(test);

		test = "Adding Second Contact to User 2";
		startTest(test);
		contactID = contact2;
		addContact(userID, contactID);
		endTest(test);
	}

	@Test
	public void addContactsToUser3() {
		clearContactsForUser3();
		String test = "Adding First Contact to User 3";
		startTest(test);
		String userID = user3;
		String contactID = contact1;
		addContact(userID, contactID);
		endTest(test);

		test = "Adding Second Contact to User 3";
		startTest(test);
		contactID = contact2;
		addContact(userID, contactID);
		endTest(test);

		test = "Adding Third Contact to User 3";
		startTest(test);
		contactID = contact3;
		addContact(userID, contactID);
		endTest(test);
	}

	@Test
	public void addContactsToUser4() {
		clearContactsForUser4();
		String test = "Adding First Contact to User 4";
		startTest(test);
		String userID = user4;
		String contactID = contact1;
		addContact(userID, contactID);
		endTest(test);

		test = "Adding Second Contact to User 4";
		startTest(test);
		contactID = contact2;
		addContact(userID, contactID);
		endTest(test);

		test = "Adding Third Contact to User 4";
		startTest(test);
		contactID = contact3;
		addContact(userID, contactID);
		endTest(test);

		test = "Adding Fourth Contact to User 4";
		startTest(test);
		contactID = contact4;
		addContact(userID, contactID);
		endTest(test);
	}

	@Test
	public void updatingContact1ForUser1() {
		String userID = user1;

		LOGGER.info("Listing User 1 current Contacts:");
		listContacts(userID);

		String test = "Updating Display Name for Contact (tel:+15554000001) of User 1";
		startTest(test);

		String contactID = contact1;
		String updatedDisplayName = "Contact 1";
		String encodedUserID = encode(userID);
		String encodedContactID = encode(contactID);
		String requestData = requestDataClean(addingContactRequestData,
				contactID, updatedDisplayName);
		String url = replaceContact(contactsURL, apiVersion, userID, contactID);

		Response response = RestAssured
				.given()
				.body(requestData)
				.expect()
				.log()
				.ifError()
				.statusCode(200)
				.body("contact.resourceURL",
						StringContains.containsString(encodedUserID
								+ "/contacts/" + encodedContactID),
						"contact.contactId", Matchers.equalTo(contactID),
						"contact.attributeList.attribute.value",
						Matchers.hasItem(updatedDisplayName)).when().put(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());

		endTest(test);
	}

	@Test
	public void updating2ContactsForUser2() {
		addContactsToUser2();

		String userID = user2;

		LOGGER.info("Listing User 2 current Contacts:");
		listContacts(userID);

		String test = "Updating Display Name for Contact (tel:+15554000001) of User 2";
		startTest(test);
		String contactID = contact1;
		String updatedDisplayName = "Contact 1";
		updateContactDisplayName(userID, contactID, updatedDisplayName);
		endTest(test);

		test = "Updating Display Name for Contact (tel:+15554000002) of User 2";
		startTest(test);
		contactID = contact2;
		updatedDisplayName = "Contact 2";
		updateContactDisplayName(userID, contactID, updatedDisplayName);
		endTest(test);
	}

	@Test
	public void updating3ContactsForUser3() {
		addContactsToUser3();

		String userID = user3;

		LOGGER.info("Listing User 3 current Contacts:");
		listContacts(userID);

		String test = "Updating Display Name for Contact (tel:+15554000001) of User 3";
		startTest(test);
		String contactID = contact1;
		String updatedDisplayName = "Contact 1";
		updateContactDisplayName(userID, contactID, updatedDisplayName);
		endTest(test);

		test = "Updating Display Name for Contact (tel:+15554000002) of User 3";
		startTest(test);
		contactID = contact2;
		updatedDisplayName = "Contact 2";
		updateContactDisplayName(userID, contactID, updatedDisplayName);
		endTest(test);

		test = "Updating Display Name for Contact (tel:+15554000003) of User 3";
		startTest(test);
		contactID = contact3;
		updatedDisplayName = "Contact 3";
		updateContactDisplayName(userID, contactID, updatedDisplayName);
		endTest(test);
	}

	@Test
	public void updating4ContactsForUser4() {
		addContactsToUser4();

		String userID = user4;

		LOGGER.info("Listing User 4 current Contacts:");
		listContacts(userID);

		String test = "Updating Display Name for Contact (tel:+15554000001) of User 4";
		startTest(test);
		String contactID = contact1;
		String updatedDisplayName = "Contact 1";
		updateContactDisplayName(userID, contactID, updatedDisplayName);
		endTest(test);

		test = "Updating Display Name for Contact (tel:+15554000002) of User 4";
		startTest(test);
		contactID = contact2;
		updatedDisplayName = "Contact 2";
		updateContactDisplayName(userID, contactID, updatedDisplayName);
		endTest(test);

		test = "Updating Display Name for Contact (tel:+15554000003) of User 4";
		startTest(test);
		contactID = contact3;
		updatedDisplayName = "Contact 3";
		updateContactDisplayName(userID, contactID, updatedDisplayName);
		endTest(test);

		test = "Updating Display Name for Contact (tel:+15554000004) of User 4";
		startTest(test);
		contactID = contact4;
		updatedDisplayName = "Contact 4";
		updateContactDisplayName(userID, contactID, updatedDisplayName);
		endTest(test);
	}

	@Test
	public void removeNonExistantContactFromUser4() {
		String test = "Removing Non-Existant Contact from User 4";
		startTest(test);
		String userID = user4;
		String contactID = invalidContact;
		String url = replaceContact(contactsURL, apiVersion, userID, contactID);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);

		Response response = RestAssured
				.expect()
				.log()
				.ifError()
				.statusCode(400)
				.body("requestError.serviceException.variables",
						Matchers.hasItem("Contact doesn't exist: "
								+ invalidContact),
						"requestError.serviceException.messageId",
						Matchers.equalTo("SVC001")).delete(url);

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
	public void getAttributesForContact1User4() {
		addContactsToUser4();
		String userID = user4;

		LOGGER.info("Listing User 4 current Contacts:");
		listContacts(userID);

		String test = "Getting all Attributes for Contact (tel:+15554000001) of User 4";
		startTest(test);

		String encodedUserID = encode(userID);
		String contactID = contact1;
		String encodedContactID = encode(contactID);
		String cleanContactID = cleanPrefix(contactID);
		String url = replaceContact(contactsURL, apiVersion, userID, contactID);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);
		Response response = RestAssured
				.expect()
				.log()
				.ifError()
				.statusCode(200)
				.body("contact.attributeList.resourceURL",
						StringContains.containsString(encodedUserID
								+ "/contacts/" + encodedContactID
								+ "/attributes"),
						"contact.resourceURL",
						StringContains.containsString(encodedUserID
								+ "/contacts/" + encodedContactID),
						"contact.attributeList.attribute.value",
						Matchers.hasItem(cleanContactID),
						"contact.attributeList.attribute.name",
						Matchers.hasItem("display-name"), "contact.contactId",
						Matchers.equalTo(contactID)).get(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());

		endTest(test);
	}

	@Test
	public void getAttributesForNonExistantContactOfUser4() {
		addContactsToUser4();
		String userID = user4;

		LOGGER.info("Listing User 4 current Contacts:");
		listContacts(userID);

		String test = "Getting all Attributes for Non-Existant Contact of User 4";
		startTest(test);

		String contactID = invalidContact;
		String url = replaceContact(contactsURL, apiVersion, userID, contactID);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);

		Response response = RestAssured
				.expect()
				.log()
				.ifError()
				.statusCode(400)
				.body("requestError.serviceException.variables",
						Matchers.hasItem("Contact not found (" + invalidContact
								+ ")"),
						"requestError.serviceException.messageId",
						Matchers.equalTo("SVC001")).get(url);

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
	public void getAttributesForNonExistantContactAndUser() {
		String test = "Getting all Attributes for Non-Existant Contact of Non-Existant User";
		startTest(test);
		String userID = invalidUser;
		String contactID = invalidContact;
		String url = replaceContact(contactsURL, apiVersion, userID, contactID);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);
		Response response = RestAssured.expect().log().ifError()
				.statusCode(401).get(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		endTest(test);
	}

	@Test
	public void retrieveAttributeForContact1User4() {
		addContactsToUser4();

		String test = "Retrieving 'display-name' attribute for Contact (tel:+15554000001) of User 4";
		startTest(test);

		String userID = user4;
		String contactID = contact1;
		String attributeValue = cleanPrefix(contactID);
		String attribute = "display-name";
		String url = replaceAll(attributeURL, apiVersion, userID, contactID,
				attribute);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);

		Response response = RestAssured
				.expect()
				.log()
				.ifError()
				.statusCode(200)
				.body("attribute.value", Matchers.equalTo(attributeValue),
						"attribute.name", Matchers.equalTo(attribute)).get(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());
		endTest(test);
	}

	@Test
	public void retrieveNonExistantAttributeForContact1User4() {
		addContactsToUser4();

		String test = "Retrieving Non-Existant Attribute for Contact (tel:+15554000001) of User 4";
		startTest(test);

		String userID = user4;
		String contactID = contact1;
		String attributeValue = "NULL";
		String attribute = "NON-EXIST_ATTRIBUTE";
		String url = replaceAll(attributeURL, apiVersion, userID, contactID,
				attribute);
		String requestData = requestDataAttributeClean(
				updateContactAttributeRequestData, attribute, attributeValue);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);

		Response response = RestAssured
				.given()
				.body(requestData)
				.expect()
				.log()
				.ifError()
				.statusCode(400)
				.body("requestError.serviceException.variables",
						Matchers.hasItem("Attribute is not valid (only 'display-name' and 'relationship' can be modified). Received '"
								+ attribute + "'")).put(url);

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
	public void updateAttributeForContact1User4() {
		addContactsToUser4();

		String test = "Updating the attribute 'display-name' for Contact (tel:+15554000001) of User 4";
		startTest(test);

		String userID = user4;
		String contactID = contact1;
		String attribute = "display-name";
		String attributeValue = "Contact 1";
		String requestData = requestDataAttributeClean(
				updateContactAttributeRequestData, attribute, attributeValue);
		String url = replaceAll(attributeURL, apiVersion, userID, contactID,
				attribute);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);

		Response response = RestAssured
				.given()
				.body(requestData)
				.expect()
				.log()
				.ifError()
				.statusCode(200)
				.body("attribute.value", Matchers.equalTo(attributeValue),
						"attribute.name", Matchers.equalTo(attribute)).put(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());
		endTest(test);
	}

	@Test
	public void updateMismatchedAttributeForContact1User4() {
		addContactsToUser4();

		String test = "Updating Mismatched Attribut for Contact (tel:+15554000001) of User 4";
		startTest(test);

		String userID = user4;
		String contactID = contact1;
		String attribute = "display-name";
		String mismatchedAttribute = "capabilities";
		String attributeValue = "Contact 1";
		String requestData = requestDataAttributeClean(
				updateContactAttributeRequestData, attribute, attributeValue);
		String url = replaceAll(attributeURL, apiVersion, userID, contactID,
				mismatchedAttribute);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);

		Response response = RestAssured
				.given()
				.body(requestData)
				.expect()
				.log()
				.ifError()
				.statusCode(400)
				.body("requestError.serviceException.variables",
						Matchers.hasItem("Attribute ('display-name') and ResourceRelPath  ('capabilities') do not have the same value"),
						"requestError.serviceException.messageId",
						Matchers.equalTo("SVC002")).put(url);

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
	public void deleteAttributeOfContact1User4() {
		addContactsToUser4();

		String test = "Deleting the attribute 'display-name' for Contact (tel:+15554000001) of User 4";
		startTest(test);
		String userID = user4;
		String contactID = contact1;
		String attribute = "display-name";
		String url = replaceAll(attributeURL, apiVersion, userID, contactID,
				attribute);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);

		Response response = RestAssured.expect().log().ifError()
				.statusCode(204).delete(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());
		endTest(test);
	}

	@Test
	public void deleteNonExistantAttributeOfContact1User4() {
		addContactsToUser4();

		String test = "Deleting Non-Existant Attribute for Contact (tel:+15554000001) of User 4";
		startTest(test);
		String userID = user4;
		String contactID = contact1;
		String attribute = "NON-EXIST";
		String url = replaceAll(attributeURL, apiVersion, userID, contactID,
				attribute);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);

		Response response = RestAssured.expect().log().ifError()
				.statusCode(400).delete(url);

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

	public String cleanPrefix(String userID) {
		return userID.replaceAll("tel\\:\\+", "").replaceAll("tel\\:", "")
				.replaceAll("\\+", "").replaceAll("\\:", "");
	}

	public String encode(String userID) {
		return userID.replaceAll("\\:", "%3A").replaceAll("\\+", "%2B")
				.replaceAll("\\@", "%40");
	}

	private String replace(String url, String apiVersion, String userID) {
		return url.replace("{apiVersion}", apiVersion).replace("{userID}",
				userID);
	}

	private String replaceContact(String url, String apiVersion, String userID,
			String contactID) {
		return url.replace("{apiVersion}", apiVersion)
				.replace("{userID}", userID).replace("{contactID}", contactID);
	}

	private String replaceAll(String url, String apiVersion, String userID,
			String contactID, String attribute) {
		return url.replace("{apiVersion}", apiVersion)
				.replace("{userID}", userID).replace("{contactID}", contactID)
				.replace("{attribute}", attribute);
	}

	private String requestDataClean(String addContactRequestData,
			String contactID, String cleanContactID) {
		String clean = addContactRequestData.replace("{CONTACTID}", contactID)
				.replace("{CONTACTDISPLAYNAME}", cleanContactID);
		return clean;
	}

	private String requestDataAttributeClean(String attributeRequestData,
			String attribute, String attributeValue) {
		String clean = attributeRequestData.replace("{ATTRIBUTE}", attribute)
				.replace("{ATTRIBUTEVALUE}", attributeValue);
		return clean;
	}

	private void deleteAnyContacts(String userID) {
		String url = replace(contactURL, apiVersion, userID);
		LOGGER.info("Making call to: " + baseURI + url);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);

		Response response = RestAssured.expect().log().ifError()
				.statusCode(200).when().get(url);

		JsonPath jsonData = response.jsonPath();

		LOGGER.info("Received Response = " + response.getStatusCode());

		if (jsonData.get("contactCollection.contact") != null) {
			List<String> contacts = jsonData
					.get("contactCollection.contact.contactId");
			int i = 1;
			for (String contact : contacts) {
				LOGGER.info(i + ". " + contact);
				i++;
			}
			if (contacts != null) {
				for (String contact : contacts) {
					LOGGER.info("Removing contact " + contact + " for User "
							+ userID);
					deleteContact(userID, contact);
				}
			}
		}

	}

	private void deleteContact(String userID, String contact) {
		String contactID = contact;
		String url = replaceContact(contactsURL, apiVersion, userID, contactID);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);
		Response response = RestAssured.expect().log().ifError()
				.statusCode(204).delete(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());

	}

	private void addContact(String userID, String contactID) {
		String cleanContactID = cleanPrefix(contactID);
		String requestData = requestDataClean(addingContactRequestData,
				contactID, cleanContactID);
		String url = replaceContact(contactsURL, apiVersion, userID, contactID);
		String encodedContactID = encode(contactID);
		String encodedUserID = encode(userID);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);

		Response response = RestAssured
				.given()
				.body(requestData)
				.expect()
				.log()
				.ifError()
				.statusCode(201)
				.body("contact.resourceURL",
						StringContains.containsString(encodedUserID
								+ "/contacts/" + encodedContactID),
						"contact.contactId", Matchers.equalTo(contactID),
						"contact.attributeList.attribute.value",
						Matchers.hasItem(cleanContactID)).when().put(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());
	}

	private void listContacts(String userID) {
		String url = replace(contactURL, apiVersion, userID);
		LOGGER.info("Making call to: " + baseURI + url);

		RestAssured.authentication = RestAssured.basic(userID,
				applicationPassword);

		Response response = RestAssured.expect().log().ifError()
				.statusCode(200).when().get(url);

		JsonPath jsonData = response.jsonPath();
		List<String> contacts = jsonData
				.get("contactCollection.contact.contactId");

		LOGGER.info("Received Response = " + response.getStatusCode());
		int i = 1;
		for (String contact : contacts) {
			LOGGER.info(i + ". " + contact);
			i++;
		}
	}

	private void updateContactDisplayName(String userID, String contactID,
			String updatedDisplayName) {
		String encodedUserID = encode(userID);
		String encodedContactID = encode(contactID);
		String requestData = requestDataClean(addingContactRequestData,
				contactID, updatedDisplayName);
		String url = replaceContact(contactsURL, apiVersion, userID, contactID);

		Response response = RestAssured
				.given()
				.body(requestData)
				.expect()
				.log()
				.ifError()
				.statusCode(200)
				.body("contact.resourceURL",
						StringContains.containsString(encodedUserID
								+ "/contacts/" + encodedContactID),
						"contact.contactId", Matchers.equalTo(contactID),
						"contact.attributeList.attribute.value",
						Matchers.hasItem(updatedDisplayName)).when().put(url);

		LOGGER.info("Response Received = " + response.getStatusCode());
		LOGGER.info("Body = " + response.asString());
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

	public void setInvalidUser(String invalidUser) {
		this.invalidUser = invalidUser;
	}

	public void setContactURL(String contactURL) {
		this.contactURL = contactURL;
	}

	public void setContactsURL(String contactsURL) {
		this.contactsURL = contactsURL;
	}

	public void setAttributesURL(String attributesURL) {
		this.attributesURL = attributesURL;
	}

	public void setAttributeURL(String attributeURL) {
		this.attributeURL = attributeURL;
	}

	public void setAddingContactRequestData(String addingContactRequestData) {
		this.addingContactRequestData = addingContactRequestData;
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

	public void setInvalidContact(String invalidContact) {
		this.invalidContact = invalidContact;
	}

	public void setUpdateContactAttributeRequestData(
			String updateContactAttributeRequestData) {
		this.updateContactAttributeRequestData = updateContactAttributeRequestData;
	}

}
