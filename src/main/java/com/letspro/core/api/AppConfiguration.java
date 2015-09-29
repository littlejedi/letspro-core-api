package com.letspro.core.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.letspro.core.api.db.MongoConfiguration;

import io.dropwizard.Configuration;

public class AppConfiguration extends Configuration {
    
    @JsonProperty("mongo")
    private MongoConfiguration mongoConfiguration;
    
    public AppConfiguration() {
    }

    public MongoConfiguration getMongoConfiguration() {
        return mongoConfiguration;
    }

    public void setMongoConfiguration(MongoConfiguration mongoConfiguration) {
        this.mongoConfiguration = mongoConfiguration;
    }
}
