package com.letspro.core.api.db;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;

public class MongoDatastore {
    
    private final Morphia morphia = new Morphia();
    
    private static MongoClient client;
    
    private static Datastore coreDatastore;
    
    private static MongoDatastore instance;
    
    private MongoDatastore(){}
    
    public static synchronized MongoDatastore getInstance() {
        if (instance == null) {
            instance = new MongoDatastore();
        }
        return instance;
    }
    
    public void initialize(MongoConfiguration configuration)
    {
        morphia.mapPackage("com.letspro.commons.domain.mongodb");
        String host = configuration.getHost();
        int port = configuration.getPort();
        String db = configuration.getDb();
        client = new MongoClient(host, port);
        coreDatastore = morphia.createDatastore(client, db);
        coreDatastore.ensureIndexes();
    }
    
    public Datastore getCoreDatastore() {
        return coreDatastore;
    }
    
    public MongoClient getClient() {
        return client;
    }
}
