package com.letspro.core.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;

import java.util.UUID;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import com.letspro.commons.domain.mongodb.School;

public class IntegrationTest {

    private static final String CONFIG_PATH = ResourceHelpers.resourceFilePath("test.yml");
    
    private static final String PASS = "coreapi!123";
    
    private static final String API_ADDRESS = "http://localhost:8080";
    
    @ClassRule
    public static final DropwizardAppRule<AppConfiguration> RULE = new DropwizardAppRule<>(
            App.class, CONFIG_PATH);

    private Client client;

    @Before
    public void setUp() throws Exception {
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.universalBuilder()
                .credentialsForBasic("user", PASS)
                .build();
        client = ClientBuilder.newClient();
        client.register(feature);
    }

    @After
    public void tearDown() throws Exception {
        client.close();
    }
    
    @Test
    public void testPostSchool() throws Exception {
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();
        final School school = new School("integrationtest-" + uuidString);
        final School newSchool = client.target(API_ADDRESS + "/schools")
                .request()
                .post(Entity.entity(school, MediaType.APPLICATION_JSON_TYPE))
                .readEntity(School.class);
        assertNotNull(newSchool.getId());
        assertEquals(newSchool.getName(), school.getName());
    }

    /*@Test
    public void testHelloWorld() throws Exception {
        final Optional<String> name = Optional.fromNullable("Dr. IntegrationTest");
        final Saying saying = client.target("http://localhost:" + RULE.getLocalPort() + "/hello-world")
                .queryParam("name", name.get())
                .request()
                .get(Saying.class);
        assertThat(saying.getContent()).isEqualTo(RULE.getConfiguration().buildTemplate().render(name));
    }

    @Test
    public void testPostPerson() throws Exception {
        final Person person = new Person("Dr. IntegrationTest", "Chief Wizard");
        final Person newPerson = client.target("http://localhost:" + RULE.getLocalPort() + "/people")
                .request()
                .post(Entity.entity(person, MediaType.APPLICATION_JSON_TYPE))
                .readEntity(Person.class);
        assertThat(newPerson.getId()).isNotNull();
        assertThat(newPerson.getFullName()).isEqualTo(person.getFullName());
        assertThat(newPerson.getJobTitle()).isEqualTo(person.getJobTitle());
    }*/
}
