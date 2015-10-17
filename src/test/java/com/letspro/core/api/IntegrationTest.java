package com.letspro.core.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import com.letspro.commons.domain.FileUploadRequest;
import com.letspro.commons.domain.FileUploadStatus;
import com.letspro.commons.domain.FileUploadStatusResponse;
import com.letspro.commons.domain.SensorDataRecord;
import com.letspro.commons.domain.SensorDataRecordList;
import com.letspro.commons.domain.mongodb.Experiment;
import com.letspro.commons.domain.mongodb.FileUploadSession;
import com.letspro.commons.domain.mongodb.Project;
import com.letspro.commons.domain.mongodb.School;
import com.letspro.commons.utils.DateUtils;

public class IntegrationTest {

    private static final String CONFIG_PATH = ResourceHelpers.resourceFilePath("test.yml");
    
    private static final String PASS = "coreapi!123";
    
    private static final String API_ADDRESS = "http://localhost:8080";
    
    private static final String TEST_SCHOOL_ID = "560da8d7f2763b21d8243d5b";
    
    @ClassRule
    public static final DropwizardAppRule<AppConfiguration> RULE = new DropwizardAppRule<>(
            App.class, CONFIG_PATH);

    private Client client;

    @Before
    public void setUp() throws Exception {
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("user", PASS);
        client = ClientBuilder.newClient();
        client.register(feature);
    }

    @After
    public void tearDown() throws Exception {
        client.close();
    }
    
    /***
     * School resource
     */
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
        // Delete test entity
        Response response = client.target(API_ADDRESS + "/schools" + "/" + newSchool.getId().toString())
        .request()
        .delete();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }
    
    @Test
    public void testUpdateSchool() throws Exception {
        final School school = client.target(API_ADDRESS + "/schools/" + TEST_SCHOOL_ID)
                .request()
                .get(School.class);
        assertNotNull(school);
        final String currentName = school.getName();
        final String newName = "integrationtest-" + UUID.randomUUID().toString();
        school.setName(newName);
        final School newSchool = client.target(API_ADDRESS + "/schools")
                .request()
                .put(Entity.entity(school, MediaType.APPLICATION_JSON_TYPE))
                .readEntity(School.class);
        assertTrue(newSchool.getName() != currentName);
        assertEquals(newSchool.getId(), new ObjectId(TEST_SCHOOL_ID));
        assertEquals(newSchool.getName(), newName);
    }
    
    /**
     * Experiment resource
     */
    @Test
    public void postExperimentAndAddToProject() throws Exception {
        final Project project = createNewProject(getRandomUuidString());
        final String name = getRandomUuidString();
        final Experiment newExperiment = createNewExperiment(name, project);
        assertNotNull(newExperiment.getId());
        assertEquals(newExperiment.getName(), name);
        // Update project
        project.setExperiments(new ArrayList<Experiment>());
        project.getExperiments().add(newExperiment);
        final Project newProject = client.target(API_ADDRESS + "/projects")
                .request()
                .put(Entity.entity(project, MediaType.APPLICATION_JSON_TYPE))
                .readEntity(Project.class);
        assertNotNull(newProject.getExperiments());
        assertTrue(newProject.getExperiments().size() == 1);
        // Delete test entity
        Response response = client.target(API_ADDRESS + "/experiments" + "/" + newExperiment.getId().toString())
        .request()
        .delete();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    /**
     * Project resource
     */
    @Test
    public void postProject() throws Exception {
        final String name = getRandomUuidString();
        final Project newProject = createNewProject(name);
        assertNotNull(newProject.getId());
        assertEquals(newProject.getName(), name);
    }
    
    /**
     * Sensor data resource
     */
    @Test()
    public void postSensorDataRecords() throws Exception {
        SensorDataRecordList payload = new SensorDataRecordList();
        payload.setRecords(new ArrayList<SensorDataRecord>());
        Long timestamp = DateUtils.nowUtc().getMillis();
        
        SensorDataRecord r = new SensorDataRecord();
        r.setTimestampInMs(timestamp);
        r.setDataType(1);
        r.setValue("testvalue");
        r.setSensorId("testsensor");
        r.setExperimentId("56105b0af2763b25806d1365");
        payload.getRecords().add(r);
        
        SensorDataRecord r2 = new SensorDataRecord();
        r2.setTimestampInMs(timestamp);
        r2.setDataType(1);
        r2.setValue("testvalue2");
        r2.setSensorId("testsensor2");
        r2.setExperimentId("56105b0af2763b25806d1365");
        payload.getRecords().add(r2);
        
        Response response = client.target(API_ADDRESS + "/sensordatadocs")
                .request()
                .post(Entity.entity(payload, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        
        // Do another POST to test update
        r.setSensorId("testsensor3");
        r.setValue("testvalue3");
        r2.setSensorId("testsensor4");
        r2.setValue("testvalue4");
        response = client.target(API_ADDRESS + "/sensordatadocs")
                .request()
                .post(Entity.entity(payload, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }
    
    /**
     * File upload resources
     * @throws Exception
     */
    @Test()
    public void testUploadFileHappyPath() throws Exception {
        FileUploadSession session = new FileUploadSession();
        session.setFileType(0);
        
        // Create a session first
        final FileUploadSession newSession = client.target(API_ADDRESS + "/fileuploads")
                .request()
                .post(Entity.entity(session, MediaType.APPLICATION_JSON_TYPE))
                .readEntity(FileUploadSession.class);
        assertNotNull(newSession.getId());
        assertNotNull(newSession.getUuid());
        
        // Upload file
        FormDataMultiPart multiPart = new FormDataMultiPart();
        FileUploadRequest request = new FileUploadRequest();
        request.setFileType(0);
        request.setOffset(0);
        byte[] bytes = Files.readAllBytes(Paths.get("/test.mp3"));
        multiPart.field("metadata", request, MediaType.APPLICATION_JSON_TYPE);
        multiPart.field("file", new ByteArrayInputStream(bytes), MediaType.APPLICATION_OCTET_STREAM_TYPE);
        FileUploadStatusResponse response = client.target(API_ADDRESS + "/fileuploads/" + newSession.getUuid())
                .request()
                .post(Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA))
                .readEntity(FileUploadStatusResponse.class);
        assertEquals(FileUploadStatus.FINISHED.getValue(), response.getStatus());
    }
    
    /**
     * Helpers
     */
    private Project createNewProject(String name) throws Exception {
        // Construct test school object
        School school = new School();
        school.setId(new ObjectId(TEST_SCHOOL_ID));
        // Insert project
        Project project = new Project(name);
        project.setExperiments(new ArrayList<Experiment>());
        project.setSchool(school);
        final Project newProject = client.target(API_ADDRESS + "/projects")
                .request()
                .post(Entity.entity(project, MediaType.APPLICATION_JSON_TYPE))
                .readEntity(Project.class);
        return newProject;
    }
    
    private Experiment createNewExperiment(String name, Project project) throws Exception {
        final Experiment experiment = new Experiment(name);
        experiment.setProject(project);
        final Experiment newExperiment = client.target(API_ADDRESS + "/experiments")
                .request()
                .post(Entity.entity(experiment, MediaType.APPLICATION_JSON_TYPE))
                .readEntity(Experiment.class);
        return newExperiment;
    }
    
    private String getRandomUuidString() {
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();
        return "integrationtest-" + uuidString;
    }
}
