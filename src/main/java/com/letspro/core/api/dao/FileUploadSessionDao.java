package com.letspro.core.api.dao;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;

import com.letspro.commons.domain.mongodb.FileUploadSession;
import com.letspro.commons.utils.DateUtils;

public class FileUploadSessionDao extends EntityDao {
    
    public FileUploadSession insertFileUploadSession(FileUploadSession session) {
        Datastore datastore = getCoreDatastore();
        session.setCreated(DateUtils.nowUtcDate());
        datastore.save(session);
        return session;
    }
    
    public FileUploadSession getFileUploadSession(String id) {
        Datastore datastore = getCoreDatastore();
        return datastore.get(FileUploadSession.class, new ObjectId(id));
    }
    
    public FileUploadSession updateFileUploadSession(FileUploadSession session) {
        Datastore datastore = getCoreDatastore();
        session.setUpdated(DateUtils.nowUtcDate());
        datastore.save(session);
        return session;
    }
    
    public void deleteFileUploadSession(String id) {
        Datastore datastore = getCoreDatastore();
        datastore.delete(FileUploadSession.class, new ObjectId(id));
    }

}
