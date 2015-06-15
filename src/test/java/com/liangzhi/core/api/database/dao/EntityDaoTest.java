package com.liangzhi.core.api.database.dao;

import com.liangzhi.core.api.database.SqlService;

public class EntityDaoTest {
	
	protected SqlService databaseService;
	
	public EntityDaoTest() throws Exception {
		databaseService = new SqlService();
		databaseService.init();
	}
}
