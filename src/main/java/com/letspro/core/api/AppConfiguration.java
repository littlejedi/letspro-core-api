package com.letspro.core.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.letspro.core.api.db.MongoConfiguration;
import com.letspro.core.api.elastic.ElasticSearchConfiguration;

import io.dropwizard.Configuration;

public class AppConfiguration extends Configuration {
    
    @JsonProperty("mongo")
    private MongoConfiguration mongoConfiguration;
    
    @JsonProperty("elastic")
    private ElasticSearchConfiguration elasticSearchConfiguration;
    
    private boolean useElasticForAnalytics = false;
    
    public AppConfiguration() {
    }

    public MongoConfiguration getMongoConfiguration() {
        return mongoConfiguration;
    }

    public void setMongoConfiguration(MongoConfiguration mongoConfiguration) {
        this.mongoConfiguration = mongoConfiguration;
    }

    public ElasticSearchConfiguration getElasticSearchConfiguration() {
        return elasticSearchConfiguration;
    }

    public void setElasticSearchConfiguration(
            ElasticSearchConfiguration elasticSearchConfiguration) {
        this.elasticSearchConfiguration = elasticSearchConfiguration;
    }

    public boolean isUseElasticForAnalytics() {
        return useElasticForAnalytics;
    }

    public void setUseElasticForAnalytics(boolean useElasticForAnalytics) {
        this.useElasticForAnalytics = useElasticForAnalytics;
    }
}
