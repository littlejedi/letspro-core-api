package com.letspro.core.api.resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import io.dropwizard.auth.Auth;

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
import javax.ws.rs.core.Response.Status;

import org.assertj.core.util.Strings;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.letspro.commons.domain.FileUploadRequest;
import com.letspro.commons.domain.FileUploadStatus;
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
    public FileUploadSession getFileUploadStatus(@Auth SimplePrincipal principal, @PathParam("uuid") String uuid) 
    {
        try {
            return fileUploadSessionDao.getFileUploadSession(uuid, true);
        } catch (Exception e) {
            LOGGER.error("Error getting fileUploadSession status, uuid = " + uuid, e);
            throw new WebApplicationException(e);
        }
    }
        
    @Timed
    @Path("/{uuid}")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public FileUploadStatusResponse uploadFile(@PathParam("uuid") String uuid,
            @FormDataParam("metadata") FileUploadRequest request,
            @FormDataParam("file") InputStream file,
            @FormDataParam("file") FormDataContentDisposition fileDisposition) {
        FileUtils.ensureParentDirectory(appConfiguration.getDefaultDirectory());
        FileUploadSession session = fileUploadSessionDao.getFileUploadSession(uuid, false);
        String path = session.getPath();
        if (Strings.isNullOrEmpty(path)) {
            return new FileUploadStatusResponse(FileUploadStatus.INTERNAL_ERROR);
        }
        try {
            File f = new File(path);
            long offset = request != null ? request.getOffset() : 0;
            writeToFile(file, path, offset);
            return new FileUploadStatusResponse(FileUploadStatus.FINISHED);
        } catch (Exception e) {
            LOGGER.error("An error occured uploading file, uuid={}, request={}", uuid, request);
            return new FileUploadStatusResponse(FileUploadStatus.INTERNAL_ERROR);
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
    
    private void writeToFile(InputStream uploadedInputStream, String path, long offset) throws Exception {
        FileOutputStream out;
        int read = 0;
        byte[] bytes = new byte[1024];

        out = new FileOutputStream(new File(path));
        FileChannel ch = out.getChannel();
        ch.position(offset);
        while ((read = uploadedInputStream.read(bytes)) != -1) {
            ch.write(ByteBuffer.wrap(bytes, 0, read));
        }
        out.flush();
        out.close();
   }
}
