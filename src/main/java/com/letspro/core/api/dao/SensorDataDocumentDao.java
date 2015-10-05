package com.letspro.core.api.dao;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import com.letspro.commons.domain.SensorDataRecord;
import com.letspro.commons.domain.mongodb.DbSensorDataDocument;
import com.letspro.commons.domain.mongodb.DbSensorDataRecord;
import com.letspro.commons.utils.SensorDataUtils;

public class SensorDataDocumentDao extends EntityDao {
    
    public void insertSensorDataRecord(SensorDataRecord record) {
        Datastore datastore = getCoreDatastore();
        Query<DbSensorDataDocument> updateQuery = datastore
                .createQuery(DbSensorDataDocument.class)
                .field("timestampInMs")
                .equal(record.getTimestampInMs());
        final DbSensorDataRecord dbRecord = SensorDataUtils.toDbSensorDataRecord(record);
        UpdateOperations<DbSensorDataDocument> ops = datastore.createUpdateOperations(DbSensorDataDocument.class).add("records", dbRecord);
        datastore.updateFirst(updateQuery, ops, true);
    }
    
    public DbSensorDataDocument insertSensorDataDocument(DbSensorDataDocument document) {
        Datastore datastore = getCoreDatastore();
        datastore.save(document);
        return document;
    }
    
    public DbSensorDataDocument getSensorDataDocument(String id) {
        Datastore datastore = getCoreDatastore();
        return datastore.get(DbSensorDataDocument.class, new ObjectId(id));
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
    
}
