package com.letspro.core.api.resources;

import io.dropwizard.auth.Auth;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Strings;

import com.codahale.metrics.annotation.Timed;
import com.letspro.commons.domain.mongodb.School;
import com.letspro.core.api.auth.SimplePrincipal;
import com.letspro.core.api.dao.SchoolsDao;

@Path("/schools")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class SchoolsResource {
    
    private SchoolsDao schoolsDao;
    
    public SchoolsResource(SchoolsDao schoolsDao) {
        this.schoolsDao = schoolsDao;
    }
    
    @Timed
    @GET
    @Path("/{id}")
    public School getSchool(@Auth SimplePrincipal principal, @PathParam("id") String id) 
    {
        return schoolsDao.getSchool(id);
    }
    
    @Timed
    @GET
    public List<School> getSchools(@Auth SimplePrincipal principal) {
        return schoolsDao.getSchools();
    }
    
    @Timed
    @POST
    public School insertSchool(@Auth SimplePrincipal principal, School school) {
        if (Strings.isNullOrEmpty(school.getName())) {
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
        schoolsDao.insertSchool(school);
        return school;
    }
   

}
