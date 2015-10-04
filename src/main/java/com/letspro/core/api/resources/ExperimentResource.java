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
import com.letspro.commons.domain.mongodb.Experiment;
import com.letspro.core.api.auth.SimplePrincipal;
import com.letspro.core.api.dao.ExperimentDao;

@Path("/experiments")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ExperimentResource {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ExperimentResource.class);
    
    private ExperimentDao experimentDao;
    
    public ExperimentResource(ExperimentDao experimentDao) {
        this.experimentDao = experimentDao;
    }
    
    @Timed
    @GET
    @Path("/{id}")
    public Experiment getExperiment(@Auth SimplePrincipal principal, @PathParam("id") String id) 
    {
        try {
            return experimentDao.getExperiment(id);
        } catch (Exception e) {
            LOGGER.error("Error getting experiment, id = " + id, e);
            throw new WebApplicationException(e);
        }
    }
        
    @Timed
    @POST
    public Experiment insertExperiment(@Auth SimplePrincipal principal, Experiment experiment) {
        if (Strings.isNullOrEmpty(experiment.getName())) {
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
        try {
            return experimentDao.insertExperiment(experiment);
        } catch (Exception e) {
            LOGGER.error("Error inserting experiment, experiment = " + experiment.toString(), e);
            throw new WebApplicationException(e);
        }
    }
    
    @Timed
    @PUT    
    public Experiment updateExperiment(@Auth SimplePrincipal principal, Experiment experiment) {
        if (experiment.getId() == null || Strings.isNullOrEmpty(experiment.getName())) {
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
        try {
            return experimentDao.updateExperiment(experiment);
        } catch (Exception e) {
            LOGGER.error("Error updating experiment = " + experiment.toString(), e);
            throw new WebApplicationException(e);
        }
    }
    
    @Timed
    @DELETE
    @Path("/{id}")
    public void deleteExperiment(@Auth SimplePrincipal principal, @PathParam("id") String id) {
        try {
            experimentDao.deleteExperiment(id);
        } catch (Exception e) {
            LOGGER.error("Error deleting experiment by id, id = " + id, e);
            throw new WebApplicationException(e);
        }
    }

}
