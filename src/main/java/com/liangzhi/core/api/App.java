package com.liangzhi.core.api;


import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import org.springframework.beans.BeansException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.github.nhuray.dropwizard.spring.SpringBundle;
import com.liangzhi.commons.domain.CourseCategory;
import com.liangzhi.core.api.config.AppConfiguration;
import com.liangzhi.core.api.resources.aspect.TimedResourceMethodDispatchAdapter;
import com.sun.jersey.core.impl.provider.entity.StreamingOutputProvider;
import com.sun.jersey.multipart.impl.MultiPartConfigProvider;
import com.sun.jersey.multipart.impl.MultiPartReaderServerSide;

public class App extends Application<AppConfiguration> {

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {
        bootstrap.addBundle(new SpringBundle<AppConfiguration>(applicationContext(), true, true, true));
    }

    @Override
    public void run(AppConfiguration configuration, Environment environment) throws Exception {
       /*environment.jersey().register(new BasicAuthProvider<User>(new ExampleAuthenticator(),
                "SUPER SECRET STUFF"));*/
       environment.jersey().register(MultiPartConfigProvider.class);
       environment.jersey().register(MultiPartReaderServerSide.class);
       environment.jersey().register(StreamingOutputProvider.class); // for multi-part upoad
       environment.jersey().register(new TimedResourceMethodDispatchAdapter());
       // Important - This makes Jackson writes out actual dates rather than timestamps for Joda DateTime objects
       environment.getObjectMapper().disable(
    		   com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
       
    private ConfigurableApplicationContext applicationContext() throws BeansException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.scan(Constants.SPRING_BASE_PACKAGE);
        return context;
    }
}
