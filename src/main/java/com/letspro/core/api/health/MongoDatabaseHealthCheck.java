package com.letspro.core.api.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.health.HealthCheck;
import com.letspro.core.api.db.MongoDatastore;
import com.mongodb.client.MongoIterable;

public class MongoDatabaseHealthCheck extends HealthCheck {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDatabaseHealthCheck.class);

    @Override
    protected Result check() throws Exception {
        MongoDatastore datastore = MongoDatastore.getInstance();
        try {
            MongoIterable<String> names = datastore.getClient().listDatabaseNames();
            LOGGER.info("Database names: " + names.toString());
            return Result.healthy();
        } catch (Exception e) {
            return Result.unhealthy("Cannot connect to " + datastore.getClient().getAllAddress());
        }
    }

}
