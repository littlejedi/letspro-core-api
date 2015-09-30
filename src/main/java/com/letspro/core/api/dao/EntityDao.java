package com.letspro.core.api.dao;

import org.mongodb.morphia.Datastore;

import com.letspro.core.api.db.MongoDatastore;

public abstract class EntityDao {
    
    protected Datastore getCoreDatastore() {
        return MongoDatastore.getInstance().getCoreDatastore();
    }

}
