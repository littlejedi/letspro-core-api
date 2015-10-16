package com.letspro.core.api.dao;

import java.util.UUID;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import com.letspro.commons.domain.mongodb.FileUploadSession;
import com.letspro.commons.utils.DateUtils;

public class FileUploadSessionDao extends EntityDao {
    
    public FileUploadSession insertFileUploadSession(FileUploadSession session) {
        Datastore datastore = getCoreDatastore();
        session.setUuid(UUID.randomUUID().toString());
        session.setCreated(DateUtils.nowUtcDate());
        datastore.save(session);
        return session;
    }
    
    public FileUploadSession getFileUploadSession(String uuid, boolean requestStatus) {
        Datastore datastore = getCoreDatastore();
        Query<FileUploadSession> result = datastore.find(FileUploadSession.class, "uuid", uuid);
        FileUploadSession session = result.get();
        if (session != null && requestStatus) {
            session.setStatusRequested(DateUtils.nowUtcDate());
            datastore.save(session);
        }
        return session;
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
