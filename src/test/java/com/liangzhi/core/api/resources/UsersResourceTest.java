package com.liangzhi.core.api.resources;

import java.util.ArrayList;
import java.util.UUID;

import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.eclipse.jetty.server.Response;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.liangzhi.commons.domain.Country;
import com.liangzhi.commons.domain.EducationLevel;
import com.liangzhi.commons.domain.GoldTransaction;
import com.liangzhi.commons.domain.GoldTransactionType;
import com.liangzhi.commons.domain.Role;
import com.liangzhi.commons.domain.School;
import com.liangzhi.commons.domain.User;
import com.liangzhi.commons.domain.UserCredentials;
import com.liangzhi.commons.domain.UserEducationHistory;
import com.liangzhi.commons.domain.UserEducationHistoryRecord;
import com.liangzhi.commons.domain.UserRegistration;
import com.liangzhi.commons.domain.UserRoles;
import com.liangzhi.commons.domain.UserType;

public class UsersResourceTest extends BaseResourceTest  {

	private static final String TEST_USERNAME = "1ab6ae785c3b424983a258899a00f811@test.com";
	private static final Integer TEST_USER_ID = 1;
	private static final String TEST_COURSE_ID = "2";

	public UsersResourceTest() throws Exception {
		super();
	}
	
	@Override
	public void runTests() {	
		testInsertUser();
		testRegisterUser();
		testGetUser();
		testLogin();
		testLogout();
		testUpdateUser();
		testAuthroizePayment();
		testGetUserRoles();
		testUpdateUserRoles();
		testGetUserEducationHistory();
		testUpdateUserEducationHistory();
		testLikeCourse();
		testUnlikeCourse();
		testAddToFavoriteCourse();
		testRemoveFromFavoriteCourse();
		testPerformGoldTransaction_AddGold();
		testPerformGoldTransaction_DecreaseGold();
		testPerformGoldTransaction_TransferGold();
	    testPerformGoldTransaction_SetGold();
	}
	
	public void testRegisterUser() {
		WebResource webResource = client
				.resource("http://localhost:8181/users/register");
		UserRegistration registration = new UserRegistration();
		UUID uuid = UUID.randomUUID();
		String uuidNoDashes = uuid.toString().replace("-", "");
		registration.setUsername(uuidNoDashes);
		registration.setBasicPassword("abctest");
		registration.setEmail(uuidNoDashes + "@test.com");
		registration.setType(UserType.REGULAR);
		registration.setRealName("真实姓名");
		registration.setPhoneNumber("phone");
		registration.setNationalId("310104");
		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, registration);
		
		Assert.assertEquals(Response.SC_OK, response.getStatus());
		
		UserRegistration newUser = response.getEntity(UserRegistration.class);
		Assert.assertNotNull(newUser.getUserId());
	}
	
	public void testInsertUser() {
		WebResource webResource = client
				.resource("http://localhost:8181/users/");
		UUID uuid = UUID.randomUUID();
		String uuidNoDashes = uuid.toString().replace("-", "");
		User user = new User();
		user.setType(UserType.SCHOOL);
		user.setUsername(uuidNoDashes);
		user.setBasicPassword("test123");
		user.setPayPassword("123test");
		user.setEmail(uuidNoDashes + "@test.com");
		user.setRealName("真实姓名");
		user.setDisplayName("真实姓名" + uuidNoDashes);
		user.setDateOfBirthYear(1987);
		user.setDateOfBirthMonth(10);
		user.setDateOfBirthDay(28);
		user.setCity("Shanghai");
		user.setCountry(Country.CHINA.getIsoCode());
		user.setGold(0);
		user.setPhoneNumber("13761011234");
		user.setNationalId("31010x12x334xa");

		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, user);
		
		Assert.assertEquals(Response.SC_OK, response.getStatus());
		
		User newUser = response.getEntity(User.class);
		Assert.assertNotNull(newUser.getId());
	}
		
	public void testGetUser() {
		// GET by username
		WebResource webResource = client
				.resource("http://localhost:8181/users").path(TEST_USER_ID.toString());
		ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		// test we get 200 back
		Assert.assertEquals(Response.SC_OK, response.getStatus());
		User user = response.getEntity(User.class);
		Assert.assertEquals(TEST_USERNAME, user.getUsername());
	}
	
	public void testLogin() {
		WebResource webResource = client
				.resource("http://localhost:8181/users/login");
		UserCredentials credz = new UserCredentials(TEST_USERNAME, "test123");		
		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, credz);
		
		Assert.assertEquals(Response.SC_OK, response.getStatus());
	}
	
	public void testLogout() {
		WebResource webResource = client
				.resource("http://localhost:8181/users").path(TEST_USER_ID.toString()).path("logout");	
		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class);
		
		Assert.assertEquals(Response.SC_OK, response.getStatus());
	}
	
	public void testAuthroizePayment()
	{
		WebResource webResource = client
				.resource("http://localhost:8181/users/authorizepayment");
		UserCredentials credz = new UserCredentials(TEST_USERNAME, "123test");		
		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, credz);
		
		Assert.assertEquals(Response.SC_OK, response.getStatus());
	}
	
	public void testUpdateUser() {
		WebResource webResource = client
				.resource("http://localhost:8181/users");
		ClientResponse response = webResource.path(TEST_USER_ID.toString()).accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		// test we get 200 back
		Assert.assertEquals(Response.SC_OK, response.getStatus());
		User user = response.getEntity(User.class);
		String oldEmail = user.getEmail();
		user.setEmail("newEmail@test.com");
		response = webResource.type(MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, user);
		User updatedUser = response.getEntity(User.class);
		Assert.assertEquals(Response.SC_OK, response.getStatus());
		Assert.assertEquals("newEmail@test.com", updatedUser.getEmail());
		// revert to original
		updatedUser.setEmail(oldEmail);
		response = webResource.type(MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, updatedUser);
		Assert.assertEquals(Response.SC_OK, response.getStatus());
	}
	
	public void testGetUserRoles() {
		WebResource webResource = client
				.resource("http://localhost:8181/users");
		ClientResponse response = webResource.path(TEST_USER_ID.toString()).path("roles").accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		// test we get 200 back
		Assert.assertEquals(Response.SC_OK, response.getStatus());
		UserRoles roles = response.getEntity(UserRoles.class);
		//Assert.assertTrue(roles.getRoles().size() > 0);
	}
	
	public void testUpdateUserRoles() {
		WebResource webResource = client
				.resource("http://localhost:8181/users");
		UserRoles roles = new UserRoles();
		roles.setRoles(new ArrayList<Role>());
		roles.getRoles().add(Role.COURSE_ADMIN);
		roles.getRoles().add(Role.MASTER_ADMIN);
		roles.getRoles().add(Role.WORK_ADMIN);
		ClientResponse response = webResource.path(TEST_USER_ID.toString()).path("roles").accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).put(ClientResponse.class, roles);
		Assert.assertEquals(Response.SC_OK, response.getStatus());
	}
	
	public void testGetUserEducationHistory() {
		WebResource webResource = client
				.resource("http://localhost:8181/users");
		ClientResponse response = webResource.path(TEST_USER_ID.toString()).path("educationhistory").accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		// test we get 200 back
		Assert.assertEquals(Response.SC_OK, response.getStatus());
		UserEducationHistory history = response.getEntity(UserEducationHistory.class);
		Assert.assertNotNull(history.getHistory());
	}
	
	public void testUpdateUserEducationHistory() {
		WebResource webResource = client
				.resource("http://localhost:8181/users");
		UserEducationHistory history = new UserEducationHistory();
		history.setHistory(new ArrayList<UserEducationHistoryRecord>());
		UserEducationHistoryRecord record = new UserEducationHistoryRecord();
		record.setLevel(EducationLevel.MIDDLE_SCHOOL);
		record.setSchool(new School());
		record.getSchool().setId(1);
		record.setStartYear(1998);
		UserEducationHistoryRecord record2 = new UserEducationHistoryRecord();
		record2.setLevel(EducationLevel.HIGH_SCHOOL);
		record2.setSchool(new School());
		record2.getSchool().setId(1);
		record2.setStartYear(2002);
		history.getHistory().add(record);
		history.getHistory().add(record2);
		ClientResponse response = webResource.path(TEST_USER_ID.toString()).path("educationhistory").accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).put(ClientResponse.class, history);
		Assert.assertEquals(Response.SC_OK, response.getStatus());
	}
	
	public void testLikeCourse() {
		WebResource webResource = client
				.resource("http://localhost:8181/users").path(TEST_USER_ID.toString()).path("likes").path("courses").path(TEST_COURSE_ID);
		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).put(ClientResponse.class);
		Assert.assertEquals(Response.SC_OK, response.getStatus());
	}
	
	public void testUnlikeCourse() {
		WebResource webResource = client
				.resource("http://localhost:8181/users").path(TEST_USER_ID.toString()).path("likes").path("courses").path(TEST_COURSE_ID);
		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
		Assert.assertEquals(Response.SC_OK, response.getStatus());
	}
	
	public void testAddToFavoriteCourse() {
		WebResource webResource = client
				.resource("http://localhost:8181/users").path(TEST_USER_ID.toString()).path("favorites").path("courses").path(TEST_COURSE_ID);
		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).put(ClientResponse.class);
		Assert.assertEquals(Response.SC_OK, response.getStatus());
	}
	
	public void testRemoveFromFavoriteCourse() {
		WebResource webResource = client
				.resource("http://localhost:8181/users").path(TEST_USER_ID.toString()).path("favorites").path("courses").path(TEST_COURSE_ID);
		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
		Assert.assertEquals(Response.SC_OK, response.getStatus());
	}
	
	public void testPerformGoldTransaction_AddGold() {
		WebResource userResource = client.resource("http://localhost:8181/users").path(TEST_USER_ID.toString());
		User user = userResource.accept(MediaType.APPLICATION_JSON).get(User.class);
		Integer originalGold = user.getGold();
		WebResource webResource = client
				.resource("http://localhost:8181/users").path("goldtransaction");
		GoldTransaction transaction = new GoldTransaction();
		transaction.setType(GoldTransactionType.ADD);
		transaction.setOperatorUserId(1);
		transaction.setRecipientUserId(1);
		transaction.setAmount(100);
		ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, transaction);
		Assert.assertEquals(Response.SC_OK, response.getStatus());
		GoldTransaction result = response.getEntity(GoldTransaction.class);
		Assert.assertNotNull(result.getId());
		user = userResource.accept(MediaType.APPLICATION_JSON).get(User.class);
		Assert.assertEquals(originalGold + 100, (int)user.getGold());
	}
	
	public void testPerformGoldTransaction_DecreaseGold() {
		WebResource userResource = client.resource("http://localhost:8181/users").path(TEST_USER_ID.toString());
		User user = userResource.accept(MediaType.APPLICATION_JSON).get(User.class);
		Integer originalGold = user.getGold();
		WebResource webResource = client
				.resource("http://localhost:8181/users").path("goldtransaction");
		GoldTransaction transaction = new GoldTransaction();
		transaction.setType(GoldTransactionType.DECREASE);
		transaction.setOperatorUserId(1);
		transaction.setRecipientUserId(1);
		transaction.setAmount(100);
		ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, transaction);
		Assert.assertEquals(Response.SC_OK, response.getStatus());
		user = userResource.accept(MediaType.APPLICATION_JSON).get(User.class);
		Assert.assertEquals(originalGold - 100, (int)user.getGold());
	}
	
	public void testPerformGoldTransaction_TransferGold() {
		WebResource userResource = client.resource("http://localhost:8181/users").path(TEST_USER_ID.toString());
		User user = userResource.accept(MediaType.APPLICATION_JSON).get(User.class);
		WebResource userTwoResource = client.resource("http://localhost:8181/users").path("11");
		User userTwo = userTwoResource.accept(MediaType.APPLICATION_JSON).get(User.class);
		Integer originalGold = user.getGold();
		Integer originalGoldUserTwo = userTwo.getGold();
		WebResource webResource = client
				.resource("http://localhost:8181/users").path("goldtransaction");
		GoldTransaction transaction = new GoldTransaction();
		transaction.setType(GoldTransactionType.TRANSFER);
		transaction.setOperatorUserId(TEST_USER_ID);
		transaction.setTransferFromUserId(TEST_USER_ID);
		transaction.setRecipientUserId(11);
		transaction.setAmount(10);
		ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, transaction);
		Assert.assertEquals(Response.SC_OK, response.getStatus());
		user = userResource.accept(MediaType.APPLICATION_JSON).get(User.class);
		userTwo = userTwoResource.accept(MediaType.APPLICATION_JSON).get(User.class);
		Assert.assertEquals(originalGold - 10, (int)user.getGold());
		Assert.assertEquals(originalGoldUserTwo + 10, (int)userTwo.getGold());
	}
	
	public void testPerformGoldTransaction_SetGold() {
		WebResource userResource = client.resource("http://localhost:8181/users").path(TEST_USER_ID.toString());
		User user = userResource.accept(MediaType.APPLICATION_JSON).get(User.class);
		WebResource webResource = client
				.resource("http://localhost:8181/users").path("goldtransaction");
		GoldTransaction transaction = new GoldTransaction();
		transaction.setType(GoldTransactionType.SET);
		transaction.setOperatorUserId(1);
		transaction.setRecipientUserId(1);
		transaction.setAmount(15);
		ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, transaction);
		Assert.assertEquals(Response.SC_OK, response.getStatus());
		GoldTransaction resultTransaction = response.getEntity(GoldTransaction.class);
		user = userResource.accept(MediaType.APPLICATION_JSON).get(User.class);
		Assert.assertEquals(15, (int)user.getGold());
	}
}
