package com.letspro.core.api.dao;

import org.mongodb.morphia.Datastore;

import com.letspro.commons.domain.mongodb.Project;

public class ProjectDao extends EntityDao {
    
    public Project insertProject(Project project) {
        Datastore datastore = getCoreDatastore();
        datastore.save(project);
        return project;
    }
}
