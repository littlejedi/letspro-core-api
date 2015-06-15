package com.liangzhi.core.api.database.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;
import com.liangzhi.commons.domain.platform.Device;
import com.liangzhi.commons.domain.platform.Sensor;
import com.liangzhi.core.api.database.SqlService;
import com.liangzhi.core.api.database.mapper.DeviceMapper;
import com.liangzhi.core.api.database.transaction.Transactional;

@Component
public class DeviceDao extends EntityDao<DeviceMapper> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DeviceDao.class);
	
	@Autowired
	private SqlService sqlService;
	
	@Transactional
	public Device getDeviceById(Integer id) {
		DeviceMapper mapper = getMapper();
		Preconditions.checkState(id != null, "Id should not be null");
		return mapper.getDeviceById(id);
	}
	
	@Transactional
	public Device insertDevice(Device device) {
		Preconditions.checkState(device != null, "Device should not be null");
		DeviceMapper mapper = getMapper();
		mapper.insertDevice(device);
		return device;
	}
	
	@Transactional
	public int updateDevice(Device device) {
		Preconditions.checkState(device != null, "Device should not be null");
		DeviceMapper mapper = getMapper();
		return mapper.updateDeviceById(device);
	}
	
	@Transactional
	public int deleteDevice(Integer id) {
		Preconditions.checkState(id != null, "Id should not be null");
		DeviceMapper mapper = getMapper();
		return mapper.deleteDeviceById(id);
	}
	
	@Transactional
	public Sensor getSensorById(Integer id) {
		DeviceMapper mapper = getMapper();
		Preconditions.checkState(id != null, "Id should not be null");
		return mapper.getSensorById(id);
	}
	
	@Transactional
	public Sensor insertSensor(Sensor sensor) {
		Preconditions.checkState(sensor != null, "Sesnsor should not be null");
		DeviceMapper mapper = getMapper();
		mapper.insertSensor(sensor);
		return sensor;
	}
	
	@Transactional
	public int updateSensor(Sensor sensor) {
		Preconditions.checkState(sensor != null, "Sensor should not be null");
		DeviceMapper mapper = getMapper();
		return mapper.updateSensorById(sensor);
	}
	
	@Transactional
	public int deleteSensor(Integer id) {
		Preconditions.checkState(id != null, "Id should not be null");
		DeviceMapper mapper = getMapper();
		return mapper.deleteSensorById(id);
	}

	@Override
    protected DeviceMapper getMapper() {
	    return sqlService.getMapper(DeviceMapper.class);
    }

}
