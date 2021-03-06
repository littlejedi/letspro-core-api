package com.letspro.core.api.dao;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import com.letspro.commons.domain.mongodb.School;
import com.letspro.commons.utils.DateUtils;

public class SchoolsDao extends EntityDao {
    
    public School insertSchool(School school) {
        Datastore datastore = getCoreDatastore();
        school.setCreated(DateUtils.nowUtcDate());
        datastore.save(school);
        return school;
    }
    
    public School updateSchool(School school) {
        Datastore datastore = getCoreDatastore();
        school.setUpdated(DateUtils.nowUtcDate());
        datastore.save(school);
        return school;
    }
    
    public void deleteSchool(String id) {
        Datastore datastore = getCoreDatastore();
        datastore.delete(School.class, new ObjectId(id));
    }
    
    public School getSchool(String id)
    {
        Datastore datastore = getCoreDatastore();
        School school = datastore.get(School.class, new ObjectId(id));
        return school;      
    }
    
    public List<School> getSchools() {
        Datastore datastore = getCoreDatastore();
        final Query<School> query = datastore.createQuery(School.class);
        final List<School> schools = query.asList();
        return schools;
    }
}
