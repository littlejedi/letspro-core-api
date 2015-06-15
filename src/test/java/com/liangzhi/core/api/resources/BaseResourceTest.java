package com.liangzhi.core.api.resources;

import com.liangzhi.core.api.database.SqlService;
import com.sun.jersey.api.client.Client;

public abstract class BaseResourceTest {
	
	private static final String TEST_API_KEY = "dev";
	private static final String TEST_SECRET_KEY = "6fdd1400-a709-11e2-9e96-0800200c9a66";
	
	protected final SqlService sqlService;
	protected Client client;
	
	public BaseResourceTest() throws Exception {
		sqlService = new SqlService();
		client = Client.create();
		client.getProperties().put("api_key", TEST_API_KEY);
		client.getProperties().put("secret_key", TEST_SECRET_KEY);
		//DB connection
		sqlService.setEnvironment("dev");
		sqlService.init();
	}
	
	// Override this method
	public abstract void runTests() throws Exception;
}
