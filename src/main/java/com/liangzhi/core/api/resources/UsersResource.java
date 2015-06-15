package com.liangzhi.core.api.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.liangzhi.commons.api.CoreApiPath;
import com.liangzhi.commons.domain.Link;
import com.liangzhi.commons.domain.Paginator;
import com.liangzhi.commons.domain.UserRoles;
import com.liangzhi.commons.domain.platform.User;
import com.liangzhi.commons.domain.platform.UserCredz;
import com.liangzhi.core.api.Constants;
import com.liangzhi.core.api.config.SpringConfiguration;
import com.liangzhi.core.api.database.dao.UserDao;
import com.liangzhi.core.api.utils.PaginatorUtils;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Component
public class UsersResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(UsersResource.class);
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private SpringConfiguration configuration;
	
	@Path("findBy")
	@GET
	@Timed
	public Paginator findUsers(@QueryParam("pageSize") Integer pageSize, @QueryParam("pageNumber") Integer pageNumber,
			@QueryParam("fullEntity") Boolean fullEntity) {
		int totalUsersFound = userDao.findUsersCount();
		Paginator paginator = PaginatorUtils.fromResultAndPagingOptions(totalUsersFound, pageSize, pageNumber);
		int actualPageSize = paginator.getPageSize();
		int actualPageNumber = paginator.getPageNumber();
		int numberOfPages = paginator.getPageCount();
		boolean full = fullEntity != null ? fullEntity : Constants.SHOW_FULL_ENTITY;
		if (numberOfPages != 0 && paginator.getResultCount() != 0) {
			int start = (actualPageNumber - 1) * actualPageSize;
			int end = actualPageSize;
			if (full) {
				List<User> users = userDao.findUsersFullEntity(start, end);
				paginator.setResult(users);
			} else {
				List<String> ids = userDao.findUsers(start, end);
				ArrayList<Link> links = Lists.newArrayListWithCapacity(ids.size());
				for (String id : ids) {
					UriBuilder builder = UriBuilder.fromUri(configuration.getBaseUrl()).path(this.getClass()).path(CoreApiPath.USER_ID.getPath()).path(id);
					String url = builder.build().toASCIIString();
					links.add(new Link(url, null, Link.class.getSimpleName()));
				}
				paginator.setResult(links);
			}
			UriBuilder builder = UriBuilder.fromUri(configuration.getBaseUrl()).path(this.getClass()).path("findBy");
			PaginatorUtils.addPagingLinks(paginator, builder, full);
		}
		return paginator;
	}

	/**
	 * Find a user by username
	 * @param username
	 * @return
	 */
	@Path("/findByEmail")
	@GET
	@Timed
	public User findByEmal(@QueryParam("email") String username) {
		if (username == null) {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
		Optional<User> user = Optional.absent();
		user = Optional.fromNullable(userDao.getUserByEmail(username));
		if (user.isPresent()) {
			User result = user.get();
			return result;
		} else {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}
	
	/**
	 * Get a user by id
	 * @param id
	 * @return
	 */
	@Path("/{id}")
	@GET
	@Timed
	public User doGetByUserId(@PathParam("id") Integer id) {
		Optional<User> user = Optional.absent();
		user = Optional.fromNullable(userDao.getUserByUserId(id));
		if (user.isPresent()) {
			User result = user.get();
			return result;
		} else {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}
	
	/**
	 * Updates a user with the given payload
	 * Validation of the user object is not required since it's an update rather than insert
	 * @param adminUser
	 * @param id
	 * @return
	 */
	@PUT
	@Timed
	public User doPut(User user) {
        int result = userDao.updateUser(user);
        if (result == 0) {
        	throw new WebApplicationException(Status.NOT_FOUND);
        } else{
        	return this.doGetByUserId(user.getId());
        }
	}

	// Login
	@Path("/login")
	@POST
	@Timed
	public User doLogin(@Valid UserCredz credz) {
		Optional<User> user = Optional.fromNullable(userDao.getUserByEmail(credz.getEmail()));
		if (user.isPresent()) {
			BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
			// get the digest for this user
			String encryptedPassword = user.get().getPassword();
			if (passwordEncryptor.checkPassword(credz.getPassword(), encryptedPassword)) {
				// we have verified the credz are correct, update the timestamp and return this user
				userDao.updateLastLoginDate(credz.getEmail());
				return user.get();
			} else {
				// bad login!
				throw new WebApplicationException(Status.UNAUTHORIZED);
			}
		} else {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}
	
	// Log out
	@Path("/{id}/logout")
	@POST
	@Timed
	public Response doLogout(@PathParam("id") Integer userId) {
		Optional<User> user = Optional.fromNullable(userDao.getUserByUserId(userId));
		if (user.isPresent()) {
			userDao.updateLastLogoutDate(user.get().getEmail());
			return Response.ok(String.format("User %s Log Out successful", userId)).build();
		} else {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}
		
	// Register
	@Path("/register")
	@POST
	@Timed
	public User doRegister(@Valid UserCredz userCredz) {
		LOGGER.info("Registering UserRegistration={}", userCredz.toString());
		// User should be already validated by annotation
		// Encrypt both passwords
		BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
		String encryptedBasicPassword = passwordEncryptor.encryptPassword(userCredz.getPassword());
		UUID uuid = UUID.randomUUID();
        String uuidNoDashes = uuid.toString().replace("-", "");
		// Use DAO to create the user
		User user = new User();
		user.setEmail(userCredz.getEmail());
		user.setPassword(encryptedBasicPassword);
		user.setDeveloperToken(uuidNoDashes);
	    userDao.insertUser(user);
	    return user;
	}
	
	@POST
	@Timed
	public User doInsert(@Valid User user) {
		LOGGER.info("Inserting User={}", user.toString());
		// User should be already validated by annotation
		// Encrypt both passwords
		BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
		String encryptedBasicPassword = passwordEncryptor.encryptPassword(user.getPassword());
		// Store the password
		user.setPassword(encryptedBasicPassword);
		// Use DAO to create the user
	    userDao.insertUser(user);
	    return user;
	}
	
	/**
	 * Roles
	 */
	@Path("{id}/roles")
	@GET
	@Timed
	public UserRoles getRoles(@PathParam("id") Integer userId) {
		UserRoles roles = userDao.getUserRoles(userId);
		return roles;
	}
	
	@Path("{id}/roles")
	@PUT
	@Timed
	public Response updateRoles(@PathParam("id") Integer userId, UserRoles userRoles) {
		if (userRoles == null || userRoles.getRoles() == null) {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
		userDao.updateUserRoles(userId, userRoles);
		return Response.ok().build();
	}				
}
