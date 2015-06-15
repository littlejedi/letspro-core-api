package com.liangzhi.core.api.resources;


import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.liangzhi.commons.domain.User;
import com.liangzhi.core.api.service.AppService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/hello")
@Component
public class HelloResource {

    @Autowired
    private AppService helloService;

    @Value("${server.applicationConnectors[0].port}")
    private Integer port;

    @Value("#{dw}")
    private Configuration configuration;

    @Value("#{dwEnv}")
    private Environment environment;

    @GET
    public Response doGet() {
        return Response.ok(String.format("%s<br/>Hello application is running on port : %d;",
                helloService.greeting(),
                port)
        ).build();
    }
    
    @Path("/test")
    @GET
    public User doGetUser()
    {
    	return new User();
    }

    public AppService getHelloService() {
        return helloService;
    }

    public Integer getPort() {
        return port;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Environment getEnvironment() {
        return environment;
    }
}
