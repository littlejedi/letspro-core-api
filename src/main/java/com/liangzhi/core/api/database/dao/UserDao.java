package com.liangzhi.core.api.database.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;
import com.liangzhi.commons.domain.Role;
import com.liangzhi.commons.domain.platform.User;
import com.liangzhi.commons.domain.UserRoles;
import com.liangzhi.core.api.database.SqlService;
import com.liangzhi.core.api.database.mapper.UserMapper;
import com.liangzhi.core.api.database.transaction.Transactional;

@Component
public class UserDao extends EntityDao<UserMapper> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserDao.class);
		
	@Autowired
	private SqlService sqlService;
	
	@Transactional
	public int findUsersCount() {
		UserMapper mapper = getMapper();
		return mapper.findUsersCount();
	}
	
	@Transactional
	public List<String> findUsers(int start, int end) {
		UserMapper mapper = getMapper();
		return mapper.findUsers(start, end);
	}
	
	@Transactional
	public List<User> findUsersFullEntity(int start, int end) {
		UserMapper mapper = getMapper();
		return mapper.findUsersFullEntity(start, end);
	}
	
	@Transactional
	public User getUserByEmail(String email) {
		Preconditions.checkArgument(email!= null, "Email is required!");
		UserMapper mapper = getMapper();
        return mapper.getUserByEmail(email);
	}
	
	@Transactional
	public User getUserByUserId(Integer userId) {
		Preconditions.checkArgument(userId != null, "User ID is required!");
		UserMapper mapper = getMapper();
        return mapper.getUserByUserId(userId);
	}
	
	@Transactional
	public int updateLastLoginDate(String id) {
		Preconditions.checkArgument(id != null, "User Username / UUID is required!");
		UserMapper mapper = getMapper();
		int result = mapper.updateLastLoginDate(id);
		if (result == 0) {
			LOGGER.warn("Update Last Login Date did not affect user {}", id);
		}
		return result;
	}
	
	@Transactional
	public int updateLastLogoutDate(String id) {
		Preconditions.checkArgument(id != null, "User Username / UUID is required!");
		UserMapper mapper = getMapper();
	    int result = mapper.updateLastLogoutDate(id);
		if (result == 0) {
			LOGGER.warn("Update Last Logout Date did not affect user {}", id);
		}
	    return result;
	}
		
	@Transactional
	public int updateUser(User user) {
		Preconditions.checkArgument(user != null, "User object is required!");
		UserMapper mapper = getMapper();
		return mapper.update(user);
	}
	
	@Transactional
	public User insertUser(User user) {
		Preconditions.checkArgument(user != null, "User object is required!");
		UserMapper mapper = getMapper();
		mapper.insert(user);
		return user;
	}
	
	@Transactional
	public int deleteUserByEmail(String email) {
		Preconditions.checkArgument(email != null, "User email is required!");
		UserMapper mapper = getMapper();
		return mapper.deleteUserByEmail(email);
	}
	
	@Transactional
	public UserRoles getUserRoles(Integer userId) {
		Preconditions.checkArgument(userId != null, "User Id should not be null");
		UserMapper mapper = getMapper();
		List<Role> roles = mapper.getUserRolesByUserId(userId);
		UserRoles userRoles = new UserRoles();
		userRoles.setRoles(roles);
		return userRoles;
	}
	
	//FIXME: Optimize performance here
	@Transactional
	public void updateUserRoles(Integer userId, UserRoles roles) {
		Preconditions.checkArgument(userId != null, "User Id should not be null");
		Preconditions.checkArgument(roles != null, "Roles should not be null");
		Preconditions.checkArgument(roles.getRoles() != null, "Role list should not be null");
		UserMapper mapper = getMapper();
		mapper.clearRoles(userId);
		for (Role role : roles.getRoles()) {
			mapper.updateUserRoles(userId, role.getValue());
		}
	}
	
	public SqlService getSqlService() {
		return sqlService;
	}

	public void setSqlService(SqlService sqlService) {
		this.sqlService = sqlService;
	}

	@Override
	protected UserMapper getMapper() {
		return sqlService.getMapper(UserMapper.class);
	}	
}
