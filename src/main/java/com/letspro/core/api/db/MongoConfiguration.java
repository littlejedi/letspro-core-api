package com.letspro.core.api.db;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MongoConfiguration {
    
    @JsonProperty
    private String host = "localhost";
    
    @JsonProperty
    private int port = 27017;
    
    @JsonProperty
    private String db = "core";

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }
}
