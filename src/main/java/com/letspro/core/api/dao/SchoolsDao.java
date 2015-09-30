package com.letspro.core.api.dao;

import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import com.letspro.commons.domain.mongodb.School;

public class SchoolsDao extends EntityDao {
    
    public School insertSchool(School school) {
        Datastore datastore = getCoreDatastore();
        datastore.save(school);
        return school;
    }
    
    public List<School> getSchools() {
        Datastore datastore = getCoreDatastore();
        final Query<School> query = datastore.createQuery(School.class);
        final List<School> schools = query.asList();
        return schools;
    }
}
