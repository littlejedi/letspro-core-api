package com.liangzhi.core.api.database.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.liangzhi.core.api.database.SqlService;
import com.liangzhi.core.api.database.mapper.Mapper;

// Basic abstract DAO class for entity
@Component
public abstract class EntityDao<M extends Mapper> {
	
	@Autowired
	protected SqlService databaseService;
	
	public void setDatabaseService(SqlService databaseService) {
		this.databaseService = databaseService;
	}

	protected abstract M getMapper();

}
