package com.letspro.core.api.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.aggregation.Projection;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.letspro.commons.domain.SensorDataRecord;
import com.letspro.commons.domain.SensorDataRecordList;
import com.letspro.commons.domain.mongodb.DbSensorDataDocument;
import com.letspro.commons.domain.mongodb.DbSensorDataRecord;
import com.letspro.commons.domain.mongodb.Experiment;
import com.letspro.commons.utils.DateUtils;
import com.letspro.commons.utils.SensorDataUtils;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

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
    
    public List<SensorDataRecord> findSensorDataDocuments(String experimentId, String sensorId, Long start, Long end) {
        Datastore datastore = getCoreDatastore();
        List<SensorDataRecord> result = new ArrayList<SensorDataRecord>();
        /*Query<DbSensorDataDocument> query = datastore
                .createQuery(DbSensorDataDocument.class).disableValidation();
        if (sensorId != null) {
            query.field("records.sensorId").equal(sensorId);
        }
        if (experimentId != null) {
            query.field("records.experiment.$id").equal(new ObjectId(experimentId));
        }
        if (start != null) {
            query.field("_id").greaterThanOrEq(start);
        }
        if (end != null) {
            query.field("_id").lessThanOrEq(end);
        }
        Iterator<DbSensorDataDocument> iter = datastore.createAggregation(DbSensorDataDocument.class)
                .unwind("records").match(query).aggregate(DbSensorDataDocument.class);
        while (iter.hasNext()) {
            DbSensorDataDocument doc = iter.next();
            final SensorDataRecord record = SensorDataUtils.flattenSensorDataDocument(doc);  
            result.add(record);
        }*/
        MongoClient client = datastore.getMongo();
        MongoCollection collection = client.getDatabase("core").getCollection("sensordatadocs");
        List<DBObject> unwindItems = new ArrayList<>();
        DBObject unwind = new BasicDBObject("$unwind", "$records");
        unwindItems.add(unwind);
        AggregateIterable<Document> output = collection.aggregate(unwindItems);
        MongoCursor<Document> doc = output.iterator();
        while (doc.hasNext()) {
            Document d = doc.next();
            final SensorDataRecord record = SensorDataUtils.flattenBsonSensorDataDocument(d);  
            result.add(record);
        }
        return result;
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
