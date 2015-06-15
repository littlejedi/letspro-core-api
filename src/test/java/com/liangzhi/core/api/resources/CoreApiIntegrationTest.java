package com.liangzhi.core.api.resources;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.liangzhi.core.api.App;
import com.liangzhi.core.api.DropwizardJunitRunner;
import com.liangzhi.core.api.ServiceConfiguration;
import com.liangzhi.core.api.TestConstants;

@RunWith(DropwizardJunitRunner.class)
@ServiceConfiguration(value = App.class, setting = TestConstants.TEST_YAML_CONFIG)
public class CoreApiIntegrationTest {
		
	@Test
	public void testUsersResource() throws Exception {
		UsersResourceTest usersResourceTest = new UsersResourceTest();
		usersResourceTest.runTests();
	}
		
	@Test
	public void testDeviceResource() throws Exception {
		DeviceResourceTest deviceResourceTest = new DeviceResourceTest();
		deviceResourceTest.runTests();
	}
	
}
