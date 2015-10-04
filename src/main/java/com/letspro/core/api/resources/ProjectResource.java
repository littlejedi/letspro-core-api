package com.letspro.core.api.resources;

import io.dropwizard.auth.Auth;

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
import com.letspro.commons.domain.mongodb.Project;
import com.letspro.core.api.auth.SimplePrincipal;
import com.letspro.core.api.dao.ProjectDao;

@Path("/projects")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ProjectResource {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectResource.class);
    
    private ProjectDao projectDao;
    
    public ProjectResource(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }
    
    @Timed
    @GET
    @Path("/{id}")
    public Project getProject(@Auth SimplePrincipal principal, @PathParam("id") String id) 
    {
        try {
            return projectDao.getProject(id);
        } catch (Exception e) {
            LOGGER.error("Error getting project, id = " + id, e);
            throw new WebApplicationException(e);
        }
    }
        
    @Timed
    @POST
    public Project insertProject(@Auth SimplePrincipal principal, Project project) {
        if (Strings.isNullOrEmpty(project.getName())) {
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
        try {
            return projectDao.insertProject(project);
        } catch (Exception e) {
            LOGGER.error("Error inserting project, project = " + project.toString(), e);
            throw new WebApplicationException(e);
        }
    }
    
    @Timed
    @PUT    
    public Project updateProject(@Auth SimplePrincipal principal, Project project) {
        if (project.getId() == null || Strings.isNullOrEmpty(project.getName())) {
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
        try {
            return projectDao.updateProject(project);
        } catch (Exception e) {
            LOGGER.error("Error updating project, project = " + project.toString(), e);
            throw new WebApplicationException(e);
        }
    }
    
    @Timed
    @DELETE
    @Path("/{id}")
    public void deleteProject(@Auth SimplePrincipal principal, @PathParam("id") String id) {
        try {
            projectDao.deleteProject(id);
        } catch (Exception e) {
            LOGGER.error("Error deleting project by id, id = " + id, e);
            throw new WebApplicationException(e);
        }
    }
}
