package com.liangzhi.core.api.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.liangzhi.commons.domain.platform.Device;
import com.liangzhi.commons.domain.platform.Sensor;
import com.liangzhi.core.api.database.dao.DeviceDao;

@Path("/platform")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Component
public class DeviceResource {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceResource.class);
	
	@Autowired
	private DeviceDao deviceDao;
	
	@Path("/devices/{id}")
	@GET
	@Timed
	public Device doGetDevice(@PathParam("id") Integer id) {
		Optional<Device> device = Optional.fromNullable(deviceDao.getDeviceById(id));
		if (device.isPresent()) {
			return device.get();
		} else {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}
	
	@Path("/devices")
	@POST
	@Timed
	public Device doCreateDevice(Device device) {
		if (isDeviceInvalid(device)) {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
		return deviceDao.insertDevice(device);
	}
	
	@Path("/devices")
	@PUT
	@Timed
	public Device doUpdateDevice(Device device) {
		if (isDeviceInvalid(device)) {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
		int result = deviceDao.updateDevice(device);
		if (result == 0) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		return device;
	}
	
	@Path("/devices/{id}")
	@DELETE
	@Timed
	public Response doDeleteDevice(@PathParam("id") Integer id) {
		int result = deviceDao.deleteDevice(id);
		if (result == 0) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		return Response.ok().build();
	}
	
	@Path("/sensors/{id}")
	@GET
	@Timed
	public Sensor doGetSensor(@PathParam("id") Integer id) {
		Optional<Sensor> sensor = Optional.fromNullable(deviceDao.getSensorById(id));
		if (sensor.isPresent()) {
			return sensor.get();
		} else {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}
	
	@Path("/sensors")
	@POST
	@Timed
	public Sensor doCreateSensor(Sensor sensor) {
		if (isSensorInvalid(sensor)) {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
		return deviceDao.insertSensor(sensor);
	}
	
	@Path("/sensors")
	@PUT
	@Timed
	public Sensor doUpdateSensor(Sensor sensor) {
		if (isSensorInvalid(sensor)) {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
		int result = deviceDao.updateSensor(sensor);
		if (result == 0) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		return sensor;
	}
	
	@Path("/sensors/{id}")
	@DELETE
	@Timed
	public Response doDeleteSensor(@PathParam("id") Integer id) {
		int result = deviceDao.deleteSensor(id);
		if (result == 0) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		return Response.ok().build();
	}
	
	private boolean isDeviceInvalid(Device device) {
		return (device == null || Strings.isNullOrEmpty(device.getName()) || device.getLatitude() == null || device.getLongtitude() == null);
	}
	
	private boolean isSensorInvalid(Sensor sensor) {
		return (sensor == null || Strings.isNullOrEmpty(sensor.getName()) || sensor.getDevice() == null || sensor.getDevice().getId() == null || sensor.getType() == null);
	}

}
