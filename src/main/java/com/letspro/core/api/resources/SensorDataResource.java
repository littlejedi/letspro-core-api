package com.letspro.core.api.resources;

import io.dropwizard.jersey.params.DateTimeParam;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.letspro.commons.domain.SensorDataRecord;
import com.letspro.commons.domain.SensorDataRecordList;
import com.letspro.commons.domain.mongodb.DbSensorDataDocument;
import com.letspro.commons.utils.DateUtils;
import com.letspro.core.api.dao.SensorDataDocumentDao;

@Path("/sensordatadocs")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class SensorDataResource {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SensorDataResource.class);
    
    private SensorDataDocumentDao sensorDataDocumentDao;
    
    public SensorDataResource(SensorDataDocumentDao sensorDataDocumentDao) {
        this.sensorDataDocumentDao = sensorDataDocumentDao;
    }
    
    @Timed
    @POST
    public Response upsertDbSensorDataRecords(SensorDataRecordList payload) {
        try {
            sensorDataDocumentDao.insertSensorDataRecords(payload);
            return Response.ok().build();
        } catch (Exception e) {
            LOGGER.error("Error upserting sensor data records" , e);
            throw new WebApplicationException(e);
        }
    }
    
    @Path("/findBy")
    @Timed
    @GET
    public List<SensorDataRecord> findBy(@QueryParam("experimentId") String experimentId, @QueryParam("sensorId") String sensorId, @QueryParam("start") DateTimeParam startDate, @QueryParam("end") DateTimeParam endDate) {
        try {
            Long start = startDate != null ? DateUtils.getMillisFromDateTime(startDate.get()) : null;
            Long end = endDate != null ? DateUtils.getMillisFromDateTime(endDate.get()) : null;
            return sensorDataDocumentDao.findSensorDataDocuments(experimentId, sensorId, start, end);
        } catch (Exception e) {
            LOGGER.error("Error upserting sensor data records" , e);
            throw new WebApplicationException(e);
        }
    }
    
    @Path("/{timestamp}")
    @Timed
    @GET
    public DbSensorDataDocument getDbSensorDataDocument(@PathParam("timestamp") long timestamp) {
        try {
            return sensorDataDocumentDao.getSensorDataDocument(timestamp);
        } catch (Exception e) {
            LOGGER.error("Error getting sensor data document by timestamp=" + timestamp , e);
            throw new WebApplicationException(e);
        }
    }
}
