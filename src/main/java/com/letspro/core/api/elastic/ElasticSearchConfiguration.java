package com.letspro.core.api.elastic;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ElasticSearchConfiguration {
    
    @JsonProperty
    private String host = "localhost";
    
    @JsonProperty
    private int port = 9300;
    
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
}
