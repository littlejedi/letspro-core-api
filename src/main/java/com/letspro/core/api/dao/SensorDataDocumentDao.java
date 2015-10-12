package com.letspro.core.api.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.letspro.commons.domain.SensorDataRecord;
import com.letspro.commons.domain.SensorDataRecordList;
import com.letspro.commons.domain.elastic.SensorDataDocument;
import com.letspro.commons.domain.mongodb.DbSensorDataDocument;
import com.letspro.commons.domain.mongodb.DbSensorDataRecord;
import com.letspro.commons.utils.DateUtils;
import com.letspro.commons.utils.SensorDataUtils;
import com.letspro.core.api.AppConfiguration;
import com.letspro.core.api.elastic.ElasticSearchClient;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public class SensorDataDocumentDao extends EntityDao {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SensorDataDocumentDao.class);
    
    private AppConfiguration configuration;
    private ElasticSearchClient elasticSearchClient;
    
    public SensorDataDocumentDao(AppConfiguration configuration, ElasticSearchClient elasticSearchClient) {
        this.configuration = configuration;
        this.elasticSearchClient = elasticSearchClient;
    }
    
    public void insertSensorDataRecords(SensorDataRecordList records) {
        if (records == null || records.getRecords() == null) {
            return;
        }
        List<SensorDataDocument> elasticSensorDataDocs = new ArrayList<SensorDataDocument>();
        for (SensorDataRecord r : records.getRecords()) {
            insertSensorDataRecord(r);
            elasticSensorDataDocs.add(SensorDataUtils.toSensorDataDocument(r));
        }
        // Insert to elasticsearch for analytics purposes
        try {
            elasticSearchClient.indexMultipleSensorDataDocuments(elasticSensorDataDocs);
        } catch (Exception e) {
            LOGGER.error("Error occurred when inserting sensor data to elastic search", e);
        }
    }

    public List<SensorDataRecord> findSensorDataDocuments(String experimentId, String sensorId, Long start, Long end) throws Exception {
        if (configuration.isUseElasticForAnalytics()) {
            List<SensorDataRecord> records = getSensorDataFromElastic(experimentId, sensorId, start, end);
            return records;
        } else {
            List<SensorDataRecord> records = getSensorDataFromMongoDb(experimentId, sensorId, start, end);
            return records;
        }   
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
    
    private List<SensorDataRecord> getSensorDataFromElastic(String experimentId, String sensorId, Long start, Long end) throws Exception {
        List<SensorDataRecord> result = elasticSearchClient.queryData(sensorId, experimentId, start, end);
        return result;
    }
    
    private List<SensorDataRecord> getSensorDataFromMongoDb(String experimentId, String sensorId, Long start, Long end) {
        Datastore datastore = getCoreDatastore();
        List<SensorDataRecord> result = new ArrayList<SensorDataRecord>();
        BasicDBObject timestampMatchObject = null;
        if (start != null) {
            timestampMatchObject = new BasicDBObject("$gte", start);
        } 
        if (end != null) {
            if (timestampMatchObject == null) {
                timestampMatchObject = new BasicDBObject("$lte", end);
            } else {
                timestampMatchObject.append("$lte", end);
            }        
        }
        MongoClient client = datastore.getMongo();
        MongoCollection collection = client.getDatabase("core").getCollection("sensordatadocs");
        List<DBObject> dbObjects = new ArrayList<>();
        DBObject match = new BasicDBObject();
        DBObject unwind = new BasicDBObject("$unwind", "$records");
        dbObjects.add(unwind);
        if (timestampMatchObject != null) {
            dbObjects.add(new BasicDBObject("$match", new BasicDBObject("_id", timestampMatchObject)));
        }
        if (sensorId != null) {
            dbObjects.add(new BasicDBObject("$match", new BasicDBObject("records.sensorId", new BasicDBObject("$eq", sensorId))));
        }
        if (experimentId != null) {
            dbObjects.add(new BasicDBObject("$match", new BasicDBObject("records.experimentId", new BasicDBObject("$eq", experimentId))));
        }
        AggregateIterable<Document> output = collection.aggregate(dbObjects);
        MongoCursor<Document> doc = output.iterator();
        while (doc.hasNext()) {
            Document d = doc.next();
            final SensorDataRecord record = SensorDataUtils.flattenBsonSensorDataDocument(d);  
            result.add(record);
        }
        return result;
    }
}
