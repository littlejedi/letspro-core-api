package com.liangzhi.core.api.database;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.ibatis.datasource.DataSourceFactory;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;

/**
 * A simple {@link DataSourceFactory} for MyBatis to create a connection pool using C3P0.
 * <p/>
 * The properties to use in the MyBatis configuration are different from the included default pooled data source.
 * <p/>
 * Make sure to specify at least:
 * <ul>
 * <li>jdbcUrl</li>
 * <li>driverClass (if it is not preloaded somehow)</li>
 * </ul>
 * <p/>
 * But you usually also want:
 * <ul>
 * <li>user</li>
 * <li>password</li>
 * </ul>
 * <p/>
 * A sample usage would look like this:
 * <pre>
 * {@code
 *
 * <dataSource type="org.gbif.common.mybatis.C3P0DataSourceFactory">
 *   <property name="driverClass" value="org.postgresql.Driver"/>
 *   <property name="jdbcUrl" value="${url}"/>
 *   <property name="user" value="${username}"/>
 *   <property name="password" value="${password}"/>
 *   <property name="idleConnectionTestPeriod" value="60"/>
 *   <property name="maxPoolSize" value="20"/>
 *   <property name="maxIdleTime" value="600"/>
 *   <property name="preferredTestQuery" value="SELECT 1"/>
 * </dataSource>
 * }
 * </pre>
 *
 * @see <a href="http://www.mchange.com/projects/c3p0/index.html#configuration_properties">C3P0 Configuration</a>
 */
public class C3P0DataSourceFactory extends UnpooledDataSourceFactory {

  public C3P0DataSourceFactory() {
    this.dataSource = new ComboPooledDataSource();
  }
}