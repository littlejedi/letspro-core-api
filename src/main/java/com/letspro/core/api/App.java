package com.letspro.core.api;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import com.letspro.core.api.auth.SimpleAuthenticator;
import com.letspro.core.api.auth.SimplePrincipal;
import com.letspro.core.api.dao.ExperimentDao;
import com.letspro.core.api.dao.ProjectDao;
import com.letspro.core.api.dao.SchoolsDao;
import com.letspro.core.api.dao.SensorDataDocumentDao;
import com.letspro.core.api.db.MongoDatastore;
import com.letspro.core.api.filter.DateRequiredFeature;
import com.letspro.core.api.health.MongoDatabaseHealthCheck;
import com.letspro.core.api.resources.ExperimentResource;
import com.letspro.core.api.resources.FilteredResource;
import com.letspro.core.api.resources.ProjectResource;
import com.letspro.core.api.resources.ProtectedResource;
import com.letspro.core.api.resources.SchoolsResource;
import com.letspro.core.api.resources.SensorDataResource;

public class App extends Application<AppConfiguration> {
    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public String getName() {
        return "letspro-core-api";
    }

    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );
    }

    @Override
    public void run(AppConfiguration configuration, Environment environment) {
        // Initialize db
        MongoDatastore.getInstance().initialize(configuration.getMongoConfiguration());
        
        // Initialize DAOs
        SchoolsDao schoolsDao = new SchoolsDao();
        ProjectDao projectDao = new ProjectDao();
        ExperimentDao experimentDao = new ExperimentDao();
        SensorDataDocumentDao sensorDataDocumentDao = new SensorDataDocumentDao();
        
        // Health checks
        environment.healthChecks().register("database", new MongoDatabaseHealthCheck());
        
        // Jackson
        environment.getObjectMapper().disable(
                com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // Jersey
        environment.jersey().register(DateRequiredFeature.class);
        environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<SimplePrincipal>()
                .setAuthenticator(new SimpleAuthenticator())
                //.setAuthorizer(new SimpleAuthorizer())
                .setRealm("SUPER SECRET STUFF")
                .buildAuthFilter()));
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(SimplePrincipal.class));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new ProtectedResource());
        environment.jersey().register(new FilteredResource());
        environment.jersey().register(new SchoolsResource(schoolsDao));
        environment.jersey().register(new ProjectResource(projectDao));
        environment.jersey().register(new ExperimentResource(experimentDao));
        environment.jersey().register(new SensorDataResource(sensorDataDocumentDao));
    }
}
