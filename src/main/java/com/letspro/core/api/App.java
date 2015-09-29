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
import com.letspro.core.api.db.MongoDatastore;
import com.letspro.core.api.filter.DateRequiredFeature;
import com.letspro.core.api.health.MongoDatabaseHealthCheck;
import com.letspro.core.api.resources.FilteredResource;
import com.letspro.core.api.resources.ProtectedResource;

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
        
        // Health checks
        environment.healthChecks().register("database", new MongoDatabaseHealthCheck());
        
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
    }
}
