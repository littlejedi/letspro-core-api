package com.liangzhi.core.api.database.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.liangzhi.commons.domain.Role;
import com.liangzhi.commons.domain.platform.User;

public interface UserMapper extends Mapper{
	
	/**
	 * Users
	 */
	int findUsersCount();
	
	List<String> findUsers(@Param(value = "start") int start, @Param(value = "end") int end);
	
	List<User> findUsersFullEntity(@Param(value = "start") int start, @Param(value = "end") int end);
	
	User getUserByEmail(@Param(value = "email") String email);
	
	User getUserByUserId(@Param(value = "userId") Integer userId);
	
	List<Role> getUserRolesByUserId(@Param(value = "userId") Integer userId);
		
	// The ID of the new user will be set by myBatis
	int insert(@Param(value = "user") User newUser);
	
	int update(@Param(value = "user") User user);
	
	int updateLastLoginDate(@Param(value = "email") String email);
	
	int updateLastLogoutDate(@Param(value = "email") String email);
	
	int deleteUserByEmail(@Param(value = "email") String email);
		
	/**
	 * Roles
	 */
	void clearRoles(@Param(value = "userId") Integer userId);
	void updateUserRoles(@Param(value = "userId") Integer userId, @Param(value = "role") Integer role);
	void insertUserRoles(@Param(value = "roles") List<Role> roles);
}
