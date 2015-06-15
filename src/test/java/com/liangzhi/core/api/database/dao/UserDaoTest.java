package com.liangzhi.core.api.database.dao;

import java.util.UUID;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import com.liangzhi.commons.domain.platform.User;

public class UserDaoTest extends EntityDaoTest {
	
	private static final String TEST_USER_UUID = "5102e2a9-201c-4a26-8d69-7b8b93f85a55";
	private static final String TEST_USERNAME = "littlejedi";
	
	protected final UserDao userDao;
	
	public UserDaoTest() throws Exception {
		super();
		userDao = new UserDao();
		userDao.setSqlService(databaseService);
	}

	@Test
	@Ignore
	public void testGetUserByUsername() {
		User user = userDao.getUserByEmail(TEST_USERNAME);
		Assert.assertEquals("littlejedi", user.getEmail());
		Assert.assertEquals("test456", user.getPassword());
	}
	
	@Test
	@Ignore
	public void testInsertUser() {
		User user = new User();
		UUID uuid = UUID.randomUUID();
		String uuidNoDashes = uuid.toString().replace("-", "");
		user.setEmail(uuidNoDashes);
		user.setPassword("password");
		User result = userDao.insertUser(user);
		Assert.assertNotNull(result);
		// delete this user
		int rows = userDao.deleteUserByEmail(uuidNoDashes);
		Assert.assertTrue(rows == 1);
	}
	
	@Test
	@Ignore
	public void testUpdateUser() {
		User user = userDao.getUserByEmail(TEST_USERNAME);
		String oldEmail = user.getEmail();
		user.setEmail("newEmail2@blackhole");
		int result = userDao.updateUser(user);
		Assert.assertTrue(result == 1);
		user = userDao.getUserByEmail(TEST_USERNAME);
		Assert.assertEquals("newEmail2@blackhole", user.getEmail());
		// revert to original
		user.setEmail(oldEmail);
		userDao.updateUser(user);
	}

}
