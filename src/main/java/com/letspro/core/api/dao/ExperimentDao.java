package com.letspro.core.api.dao;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;

import com.letspro.commons.domain.mongodb.Experiment;
import com.letspro.commons.utils.DateUtils;

public class ExperimentDao extends EntityDao {
    
    public Experiment insertExperiment(Experiment experiment) {
        Datastore datastore = getCoreDatastore();
        experiment.setCreated(DateUtils.nowUtcDate());
        datastore.save(experiment);
        return experiment;
    }
    
    public Experiment getExperiment(String id) {
        Datastore datastore = getCoreDatastore();
        return datastore.get(Experiment.class, new ObjectId(id));
    }
    
    public Experiment updateExperiment(Experiment experiment) {
        Datastore datastore = getCoreDatastore();
        experiment.setUpdated(DateUtils.nowUtcDate());
        datastore.save(experiment);
        return experiment;
    }
    
    public void deleteExperiment(String id) {
        Datastore datastore = getCoreDatastore();
        datastore.delete(Experiment.class, new ObjectId(id));
    }

}
