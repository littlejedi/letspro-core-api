package com.liangzhi.core.api.database;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.SqlSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.liangzhi.core.api.Constants;

@Service
public class SqlService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SqlService.class);
	private static SqlSessionFactory sqlSessionFactory = null;	
	private static SqlSessionManager sqlSessionManager = null;
	
	@Value("${config.env}")
	private String environment;
	
	@PostConstruct
	public void init() throws IOException {
	    LOGGER.info("Creating SqlSessionFactory for environment=" + environment);
	    InputStream inputStream = Resources.getResourceAsStream(Constants.MYBATIS_CONFIG_FILE);
	    sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, environment);
	    LOGGER.info("Creating SqlSessionManager for environment=" + environment);
	    sqlSessionManager = SqlSessionManager.newInstance(sqlSessionFactory);
	}
    
	public SqlSessionFactory getSessionFactory() {
	    return sqlSessionFactory;
	}
	
	public SqlSessionManager getSqlSessionManager() {
		return sqlSessionManager;
	}
		
	public <T> T getMapper(Class<T> clazz) {
		return sqlSessionManager.getMapper(clazz);
	}
	
	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}
}
