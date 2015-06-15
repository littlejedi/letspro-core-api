package com.liangzhi.core.api.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

public class AppConfiguration extends Configuration {

    @JsonProperty
	private SpringConfiguration config;

    public SpringConfiguration getSpringConfiguration() {
        return config;
    }

    public void setSpringConfiguration(SpringConfiguration config) {
        this.config = config;
    }
}
