package com.letspro.core.api.resources;

import io.dropwizard.auth.Auth;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.assertj.core.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.letspro.commons.domain.mongodb.School;
import com.letspro.core.api.auth.SimplePrincipal;
import com.letspro.core.api.dao.SchoolsDao;

@Path("/schools")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class SchoolsResource {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SchoolsResource.class);
    
    private SchoolsDao schoolsDao;
    
    public SchoolsResource(SchoolsDao schoolsDao) {
        this.schoolsDao = schoolsDao;
    }
    
    @Timed
    @GET
    @Path("/{id}")
    public School getSchool(@Auth SimplePrincipal principal, @PathParam("id") String id) 
    {
        try {
            return schoolsDao.getSchool(id);
        } catch (Exception e) {
            LOGGER.error("Error getting school, id = " + id, e);
            throw new WebApplicationException(e);
        }
    }
    
    @Timed
    @GET
    public List<School> getSchools(@Auth SimplePrincipal principal) {
        try {
            return schoolsDao.getSchools();
        } catch (Exception e) {
            LOGGER.error("Error getting schools", e);
            throw new WebApplicationException(e);
        }      
    }
    
    @Timed
    @POST
    public School insertSchool(@Auth SimplePrincipal principal, School school) {
        if (Strings.isNullOrEmpty(school.getName())) {
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
        try {
            return schoolsDao.insertSchool(school);
        } catch (Exception e) {
            LOGGER.error("Error inserting school, school = " + school.toString(), e);
            throw new WebApplicationException(e);
        }
    }
    
    @Timed
    @PUT    
    public School updateSchool(@Auth SimplePrincipal principal, School school) {
        if (school.getId() == null || Strings.isNullOrEmpty(school.getName())) {
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
        try {
            return schoolsDao.updateSchool(school);
        } catch (Exception e) {
            LOGGER.error("Error updating school", e);
            throw new WebApplicationException(e);
        }
    }
    
    @Timed
    @DELETE
    @Path("/{id}")
    public void deleteSchool(@Auth SimplePrincipal principal, @PathParam("id") String id) {
        try {
            schoolsDao.deleteSchool(id);
        } catch (Exception e) {
            LOGGER.error("Error deleting school", e);
            throw new WebApplicationException(e);
        }
    }
}
