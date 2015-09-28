package com.example.helloworld.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.testing.junit.ResourceTestRule;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;

import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.ClassRule;
import org.junit.Test;

import com.letspro.core.api.auth.SimpleAuthenticator;
import com.letspro.core.api.auth.SimplePrincipal;
import com.letspro.core.api.resources.ProtectedResource;

public class ProtectedResourceTest {
    private static final BasicCredentialAuthFilter<SimplePrincipal> BASIC_AUTH_HANDLER =
            new BasicCredentialAuthFilter.Builder<SimplePrincipal>()
                    .setAuthenticator(new SimpleAuthenticator())
                    .setPrefix("Basic")
                    .setRealm("SUPER SECRET STUFF")
                    .buildAuthFilter();

    @ClassRule
    public static final ResourceTestRule RULE = ResourceTestRule.builder()
            .addProvider(RolesAllowedDynamicFeature.class)
            .addProvider(new AuthDynamicFeature(BASIC_AUTH_HANDLER))
            .addProvider(new AuthValueFactoryProvider.Binder<>(SimplePrincipal.class))
            .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
            .addProvider(ProtectedResource.class)
            .build();

    @Test
    public void testProtectedEndpoint() {
        String secret = RULE.getJerseyTest().target("/protected").request()
                .header(HttpHeaders.AUTHORIZATION, "Basic d2l6YXJkOmNvcmVhcGkhMTIz")
                .get(String.class);
        assertThat(secret).startsWith("Hey there, wizard. You know the secret!");
    }

    @Test
    public void testProtectedEndpointNoCredentials401() {
        try {
             RULE.getJerseyTest().target("/protected").request()
                    .get(String.class);
            failBecauseExceptionWasNotThrown(WebApplicationException.class);
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(401);
            assertThat(e.getResponse().getHeaders().get(HttpHeaders.WWW_AUTHENTICATE))
                    .containsOnly("Basic realm=\"SUPER SECRET STUFF\"");
        }

    }

    @Test
    public void testProtectedAdminEndpoint() {
        String secret = RULE.getJerseyTest().target("/protected/admin").request()
                .header(HttpHeaders.AUTHORIZATION, "Basic d2l6YXJkOmNvcmVhcGkhMTIz")
                .get(String.class);
        assertThat(secret).startsWith("Hey there, wizard. It looks like you are an admin");
    }
}
