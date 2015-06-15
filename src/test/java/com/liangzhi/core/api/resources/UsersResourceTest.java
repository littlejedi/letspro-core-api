package com.liangzhi.core.api.resources;

import java.util.ArrayList;
import java.util.UUID;

import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.eclipse.jetty.server.Response;

import com.liangzhi.commons.domain.Role;
import com.liangzhi.commons.domain.UserRoles;
import com.liangzhi.commons.domain.platform.User;
import com.liangzhi.commons.domain.platform.UserCredz;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class UsersResourceTest extends BaseResourceTest  {

	private String testEmail;
	private Integer testUserId = 1;

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
		testGetUserRoles();
		testUpdateUserRoles();
	}
	
	public void testRegisterUser() {
	    UUID uuid = UUID.randomUUID();
        String uuidNoDashes = uuid.toString().replace("-", "");
		WebResource webResource = client
				.resource("http://localhost:8181/users/register");
		UserCredz credz = new UserCredz(uuidNoDashes + "@test.com", "test123");
		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, credz);
		
		Assert.assertEquals(Response.SC_OK, response.getStatus());
		
		User newUser = response.getEntity(User.class);
		Assert.assertNotNull(newUser.getId());
		testUserId = newUser.getId();
		testEmail = newUser.getEmail();
	}
	
	public void testInsertUser() {
		WebResource webResource = client
				.resource("http://localhost:8181/users/");
		UUID uuid = UUID.randomUUID();
		String uuidNoDashes = uuid.toString().replace("-", "");
		User user = new User();
		user.setPassword("test123");
		user.setEmail(uuidNoDashes + "@test.com");

		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, user);
		
		Assert.assertEquals(Response.SC_OK, response.getStatus());
		
		User newUser = response.getEntity(User.class);
		Assert.assertNotNull(newUser.getId());
	}
		
	public void testGetUser() {
		// GET by username
		WebResource webResource = client
				.resource("http://localhost:8181/users").path(testUserId.toString());
		ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		// test we get 200 back
		Assert.assertEquals(Response.SC_OK, response.getStatus());
		User user = response.getEntity(User.class);
		Assert.assertEquals(testEmail, user.getEmail());
	}
	
	public void testLogin() {
		WebResource webResource = client
				.resource("http://localhost:8181/users/login");
		UserCredz credz = new UserCredz(testEmail, "test123");		
		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, credz);
		
		Assert.assertEquals(Response.SC_OK, response.getStatus());
	}
	
	public void testLogout() {
		WebResource webResource = client
				.resource("http://localhost:8181/users").path(testUserId.toString()).path("logout");	
		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class);
		
		Assert.assertEquals(Response.SC_OK, response.getStatus());
	}
	
	public void testUpdateUser() {
		WebResource webResource = client
				.resource("http://localhost:8181/users");
		ClientResponse response = webResource.path(testUserId.toString()).accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
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
		ClientResponse response = webResource.path(testUserId.toString()).path("roles").accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
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
		ClientResponse response = webResource.path(testUserId.toString()).path("roles").accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).put(ClientResponse.class, roles);
		Assert.assertEquals(Response.SC_OK, response.getStatus());
	}
}
