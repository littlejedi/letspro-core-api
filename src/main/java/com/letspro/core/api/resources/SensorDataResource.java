package com.letspro.core.api.resources;

import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.letspro.commons.domain.SensorDataRecord;
import com.letspro.commons.domain.SensorDataRecordList;
import com.letspro.core.api.dao.SensorDataDocumentDao;

@Path("/sensordatadocs")
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
            List<SensorDataRecord> records = payload.getRecords();
            if (records != null) {
                for (SensorDataRecord r : records) {
                    sensorDataDocumentDao.insertSensorDataRecord(r);
                }
            }
            return Response.ok().build();
        } catch (Exception e) {
            LOGGER.error("Error upserting sensor data records" , e);
            throw new WebApplicationException(e);
        }
    }
}
