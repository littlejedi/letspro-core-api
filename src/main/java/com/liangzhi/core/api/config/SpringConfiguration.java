package com.liangzhi.core.api.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.liangzhi.core.api.health.AppHealthCheck;
import com.liangzhi.core.api.server_lifecycle_listeners.AppServerLifecycleListener;
import com.liangzhi.core.api.service.AppService;
import com.liangzhi.core.api.tasks.AppTask;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;

@Configuration
@EnableAspectJAutoProxy
@ImportResource({"classpath:applicationContext.xml"})
public class SpringConfiguration {
	
	@Value("${config.env}")
	private String env;

    @Value("${config.baseUrl}")
	private String baseUrl;
	
    @Value("${config.message}")
    @JsonProperty
    private String message;
    
	@Bean
    public AppService appService() {
        return new AppService(message);
    }

    @Bean
    public AppTask appTask() {
        return new AppTask();
    }

    @Bean
    public AppHealthCheck appHealthCheck() {
        return new AppHealthCheck();
    }

    @Bean
    public AppServerLifecycleListener appServerLifecycleListener() {
      return new AppServerLifecycleListener();
    }
    
    public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}
}
