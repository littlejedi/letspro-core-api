package com.letspro.core.api.dao;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;

import com.letspro.commons.domain.mongodb.Experiment;
import com.letspro.commons.domain.mongodb.Project;
import com.letspro.commons.utils.DateUtils;

public class ProjectDao extends EntityDao {
    
    public Project insertProject(Project project) {
        Datastore datastore = getCoreDatastore();
        project.setCreated(DateUtils.nowUtcDate());
        if (project.getExperiments() != null) {
            for (Experiment e : project.getExperiments()) {
                // Manually create IDs since MongoDB only assigns IDs for top level document
                e.setId(new ObjectId());
                e.setCreated(DateUtils.nowUtcDate());
            }
        }
        datastore.save(project);
        return project;
    }
    
    public Project getProject(String id) {
        Datastore datastore = getCoreDatastore();
        return datastore.get(Project.class, new ObjectId(id));
    }
    
    public Project updateProject(Project project) {
        Datastore datastore = getCoreDatastore();
        project.setUpdated(DateUtils.nowUtcDate());
        datastore.save(project);
        return project;
    }
    
    public void deleteProject(String id) {
        Datastore datastore = getCoreDatastore();
        datastore.delete(Project.class, new ObjectId(id));
    }
}
