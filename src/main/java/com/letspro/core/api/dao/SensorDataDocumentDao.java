package com.letspro.core.api.dao;

import java.util.ArrayList;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.letspro.commons.domain.SensorDataRecord;
import com.letspro.commons.domain.SensorDataRecordList;
import com.letspro.commons.domain.mongodb.DbSensorDataDocument;
import com.letspro.commons.domain.mongodb.DbSensorDataRecord;
import com.letspro.commons.utils.DateUtils;
import com.letspro.commons.utils.SensorDataUtils;

public class SensorDataDocumentDao extends EntityDao {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SensorDataDocumentDao.class);
    
    public void insertSensorDataRecords(SensorDataRecordList records) {
        if (records == null || records.getRecords() == null) {
            return;
        }
        for (SensorDataRecord r : records.getRecords()) {
            insertSensorDataRecord(r);
        }
    }
    
    public DbSensorDataDocument insertSensorDataDocument(DbSensorDataDocument document) {
        Datastore datastore = getCoreDatastore();
        datastore.save(document);
        return document;
    }
    
    public DbSensorDataDocument getSensorDataDocument(long timestamp) {
        Datastore datastore = getCoreDatastore();
        return datastore.get(DbSensorDataDocument.class, timestamp);
    }
    
    public DbSensorDataDocument updateSensorDataDocument(DbSensorDataDocument document) {
        Datastore datastore = getCoreDatastore();
        datastore.save(document);
        return document;
    }
    
    public void deleteSensorDataDocument(String id) {
        Datastore datastore = getCoreDatastore();
        datastore.delete(DbSensorDataDocument.class, new ObjectId(id));
    }
    
    private void insertSensorDataRecord(SensorDataRecord record) {
        Datastore datastore = getCoreDatastore();
        Long timestamp = record.getTimestampInMs();
        Query<DbSensorDataDocument> findDocumentQuery = datastore
                .createQuery(DbSensorDataDocument.class)
                .field("timestampInMs")
                .equal(timestamp);
        final DbSensorDataRecord dbRecord = SensorDataUtils.toDbSensorDataRecord(record);
        DbSensorDataDocument doc = findDocumentQuery.get();
        if (doc == null) {
            DbSensorDataDocument newDoc = new DbSensorDataDocument();
            newDoc.setTimestampInMs(timestamp);
            newDoc.setCreated(DateUtils.nowUtcDate());
            newDoc.setRecords(new ArrayList<DbSensorDataRecord>());
            newDoc.getRecords().add(dbRecord);
            datastore.save(newDoc);   
        } else {
            UpdateOperations<DbSensorDataDocument> ops = datastore.createUpdateOperations(DbSensorDataDocument.class).add("records", dbRecord).set("updated", DateUtils.nowUtcDate());
            datastore.updateFirst(findDocumentQuery, ops, true);
        }
    }
}
