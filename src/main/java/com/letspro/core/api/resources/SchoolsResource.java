package com.letspro.core.api.resources;

import io.dropwizard.auth.Auth;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;
import com.letspro.commons.domain.mongodb.School;
import com.letspro.core.api.auth.SimplePrincipal;
import com.letspro.core.api.dao.SchoolsDao;

@Path("/schools")
@Produces(MediaType.APPLICATION_JSON)
public class SchoolsResource {
    
    private SchoolsDao schoolsDao;
    
    public SchoolsResource(SchoolsDao schoolsDao) {
        this.schoolsDao = schoolsDao;
    }
    
    @Timed
    @GET
    public List<School> getSchools(@Auth SimplePrincipal principal) {
        return schoolsDao.getSchools();
    }
    
    @Timed
    @POST
    public School insertSchool(School school) {
        schoolsDao.insertSchool(school);
        return school;
    }
   

}
