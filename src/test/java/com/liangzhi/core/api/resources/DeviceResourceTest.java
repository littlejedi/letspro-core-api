package com.liangzhi.core.api.resources;

import java.util.UUID;

import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.eclipse.jetty.server.Response;

import com.liangzhi.commons.domain.platform.Device;
import com.liangzhi.commons.domain.platform.Sensor;
import com.liangzhi.commons.domain.platform.SensorType;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class DeviceResourceTest extends BaseResourceTest {
	
	private UUID deviceUuid = UUID.randomUUID();
	private UUID sensorUuid = UUID.randomUUID();
	private Integer deviceId;
	private Integer sensorId;

	public DeviceResourceTest() throws Exception {
	    super();
    }

	@Override
	public void runTests() throws Exception {
	    testInsertDevice();
		testUpdateDevice();
		testDeleteDevice();
		testInsertSensor();
		testUpdateSensor();
		testDeleteSensor();
	}
	
	public void testInsertDevice() throws Exception {
		WebResource webResource = client.resource("http://localhost:8181/platform");
		Device device = new Device();
		device.setName("设备名字" + deviceUuid.toString());
		device.setDescription("设备描述");
		device.setLatitude(120.15);
		device.setLongtitude(130.16);
		ClientResponse response = webResource.path("devices").type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, device);
		Assert.assertEquals(Response.SC_OK, response.getStatus());
		device = response.getEntity(Device.class);
		Assert.assertNotNull(device.getId());
		deviceId = device.getId();
	}
	
	public void testUpdateDevice() throws Exception {
		WebResource webResource = client.resource("http://localhost:8181/platform");
		ClientResponse response = webResource.path("devices").path(deviceId.toString()).type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);
		Assert.assertEquals(Response.SC_OK, response.getStatus());
		UUID newUuid = UUID.randomUUID();
		Device device = response.getEntity(Device.class);
		Assert.assertEquals("设备名字"+deviceUuid.toString(), device.getName());
		device.setDescription("描述"+newUuid.toString());
		response = webResource.path("devices").type(MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, device);
		Assert.assertEquals(Response.SC_OK, response.getStatus());
		response = webResource.path("devices").path(deviceId.toString()).type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);
		device = response.getEntity(Device.class);
		Assert.assertEquals("描述"+newUuid.toString(), device.getDescription());
	}
	
	public void testDeleteDevice() throws Exception {
		WebResource webResource = client.resource("http://localhost:8181/platform");
		ClientResponse response = webResource.path("devices").path(deviceId.toString()).delete(ClientResponse.class);
		Assert.assertEquals(Response.SC_OK, response.getStatus());
	}
	
	public void testInsertSensor() throws Exception {
		WebResource webResource = client.resource("http://localhost:8181/platform");
		Sensor sensor = new Sensor();
		sensor.setName("传感器名字" + sensorUuid.toString());
		sensor.setDescription("传感器描述");
		sensor.setType(SensorType.HUMIDITY);
		sensor.setDeviceId(1);
		ClientResponse response = webResource.path("sensors").type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, sensor);
		Assert.assertEquals(Response.SC_OK, response.getStatus());
		sensor = response.getEntity(Sensor.class);
		Assert.assertNotNull(sensor.getId());
		sensorId = sensor.getId();
	}
	
	public void testUpdateSensor() throws Exception {
		WebResource webResource = client.resource("http://localhost:8181/platform");
		ClientResponse response = webResource.path("sensors").path(sensorId.toString()).type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);
		Assert.assertEquals(Response.SC_OK, response.getStatus());
		UUID newUuid = UUID.randomUUID();
		Sensor sensor = response.getEntity(Sensor.class);
		Assert.assertEquals("传感器名字"+sensorUuid.toString(), sensor.getName());
		sensor.setDescription("描述"+newUuid.toString());
		response = webResource.path("sensors").type(MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, sensor);
		Assert.assertEquals(Response.SC_OK, response.getStatus());
		response = webResource.path("sensors").path(sensorId.toString()).type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);
		sensor = response.getEntity(Sensor.class);
		Assert.assertEquals("描述"+newUuid.toString(), sensor.getDescription());
	}
	
	public void testDeleteSensor() throws Exception {
		WebResource webResource = client.resource("http://localhost:8181/platform");
		ClientResponse response = webResource.path("sensors").path(sensorId.toString()).delete(ClientResponse.class);
		Assert.assertEquals(Response.SC_OK, response.getStatus());
	}

}
