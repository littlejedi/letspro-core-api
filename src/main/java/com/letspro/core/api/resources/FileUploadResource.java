package com.letspro.core.api.resources;

import io.dropwizard.auth.Auth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.assertj.core.util.Strings;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.letspro.commons.domain.FileUploadRequest;
import com.letspro.commons.domain.FileUploadStatusResponse;
import com.letspro.commons.domain.mongodb.FileUploadSession;
import com.letspro.core.api.AppConfiguration;
import com.letspro.core.api.auth.SimplePrincipal;
import com.letspro.core.api.dao.FileUploadSessionDao;
import com.letspro.core.api.utils.FileUtils;

@Path("/fileuploads")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class FileUploadResource {
        
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadResource.class);
    
    private FileUploadSessionDao fileUploadSessionDao;
    
    private AppConfiguration appConfiguration;
    
    public FileUploadResource(FileUploadSessionDao fileUploadSessionDao, AppConfiguration appConfiguration) {
        this.fileUploadSessionDao = fileUploadSessionDao;
        this.appConfiguration = appConfiguration;
    }
    
    @Timed
    @GET
    @Path("/{uuid}")
    public FileUploadSession getFileUploadSession(@Auth SimplePrincipal principal, @PathParam("uuid") String uuid) 
    {
        try {
            return fileUploadSessionDao.getFileUploadSession(uuid, false);
        } catch (Exception e) {
            LOGGER.error("Error getting fileUploadSession, uuid = " + uuid, e);
            throw new WebApplicationException(e);
        }
    }
    
    @Timed
    @GET
    @Path("/{uuid}/status")
    public FileUploadStatusResponse getFileUploadStatus(@Auth SimplePrincipal principal, @PathParam("uuid") String uuid) 
    {
        try {
            FileUploadSession session = fileUploadSessionDao.getFileUploadSession(uuid, true);
            String path = session.getPath();
            if (Strings.isNullOrEmpty(path)) {
                // Path is null or empty, this shouldn't happen, returning 0 bytes uploaded
                return new FileUploadStatusResponse(0L, uuid);
            }
            File f = new File(session.getPath());
            if (!f.exists()) {
                return new FileUploadStatusResponse(0L, uuid);
            }
            long bytes = f.length();
            return new FileUploadStatusResponse(bytes, uuid);
        } catch (Exception e) {
            LOGGER.error("Error getting file upload status, uuid = " + uuid, e);
            throw new WebApplicationException(e);
        }
    }
    
    @Timed
    @POST
    public FileUploadSession insertFileUploadSession(@Auth SimplePrincipal principal, FileUploadRequest request) {
        try {
            FileUploadSession session = new FileUploadSession();
            UUID uuid = UUID.randomUUID();
            session.setFileType(request.getFileType());
            session.setUuid(uuid.toString());
            session.setPath(appConfiguration.getDefaultDirectory() + "/" + uuid.toString());
            return fileUploadSessionDao.insertFileUploadSession(session);
        } catch (Exception e) {
            LOGGER.error("Error inserting fileUploadSession, file upload request = " + request.toString(), e);
            throw new WebApplicationException(e);
        }
    }
        
    @Timed
    @Path("/{uuid}")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@PathParam("uuid") String uuid,
            @FormDataParam("metadata") FileUploadRequest request,
            @FormDataParam("file") InputStream file) {
        FileUtils.ensureParentDirectory(appConfiguration.getDefaultDirectory());
        FileUploadSession session = fileUploadSessionDao.getFileUploadSession(uuid, false);
        String path = session.getPath();
        if (Strings.isNullOrEmpty(path)) {
            return Response.ok().build();
        }
        try {
            long offset = request != null ? request.getOffset() : 0;
            writeToFile(file, path, offset);
            return Response.ok().build();
        } catch (Exception e) {
            LOGGER.error("An error occured uploading file, uuid={}, request={}", uuid, request);
            return Response.status(503).build();
        }
    }
    
    @Timed
    @PUT    
    public FileUploadSession updateFileUploadSession(@Auth SimplePrincipal principal, FileUploadSession fileUploadSession) {
        if (fileUploadSession.getId() == null || Strings.isNullOrEmpty(fileUploadSession.getUuid())) {
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
        try {
            return fileUploadSessionDao.updateFileUploadSession(fileUploadSession);
        } catch (Exception e) {
            LOGGER.error("Error updating fileUploadSession, fileUploadSession = " + fileUploadSession.toString(), e);
            throw new WebApplicationException(e);
        }
    }
    
    @Timed
    @DELETE
    @Path("/{id}")
    public void deleteFileUploadSession(@Auth SimplePrincipal principal, @PathParam("id") String id) {
        try {
            fileUploadSessionDao.deleteFileUploadSession(id);
        } catch (Exception e) {
            LOGGER.error("Error deleting fileUploadSession by id, id = " + id, e);
            throw new WebApplicationException(e);
        }
    }
    
    /**
     * Purely for integration test
     */
    @Timed
    @Path("/{uuid}/it")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFileIntegrationTest(@PathParam("uuid") String uuid,
            @FormDataParam("metadata") FileUploadRequest request,
            @FormDataParam("file") InputStream file) {
        FileUtils.ensureParentDirectory(appConfiguration.getDefaultDirectory());
        FileUploadSession session = fileUploadSessionDao.getFileUploadSession(uuid, false);
        String path = session.getPath();
        if (Strings.isNullOrEmpty(path)) {
            return Response.ok().build();
        }
        try {
            // Write 10 K to file
            writeXKbsToFile(file, path, 10);
            // Throw 503 no matter to indicate failure
            return Response.status(503).build();
        } catch (Exception e) {
            LOGGER.error("An error occured uploading file, uuid={}, request={}", uuid, request);
            return Response.status(503).build();
        }
    }
    
    private void writeToFile(InputStream uploadedInputStream, String path, long offset) throws Exception {
        FileOutputStream out;
        int read = 0;
        byte[] bytes = new byte[1024];

        out = new FileOutputStream(new File(path));
        FileChannel ch = out.getChannel();
        // Skip offset
        ch.position(offset);
        
        while ((read = uploadedInputStream.read(bytes)) != -1) {
            ch.write(ByteBuffer.wrap(bytes, 0, read));
        }
        out.flush();
        out.close();
   }
   
   // For test purposes, only write the first X * 1024 (at maximum) bytes.
   private void writeXKbsToFile(InputStream uploadedInputStream, String path, int x) throws Exception {
       FileOutputStream out;
       int read = 0;
       int i = 0;
       byte[] bytes = new byte[1024];
       out = new FileOutputStream(new File(path));
       FileChannel ch = out.getChannel();
       while ((read = uploadedInputStream.read(bytes)) != -1) {
           ch.write(ByteBuffer.wrap(bytes, 0, read));
           i++;
           if (i == x) {
               break;
           }
       }
       out.flush();
       out.close();     
   }
}
