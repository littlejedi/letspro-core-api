package com.liangzhi.core.api.database.mapper;

import org.apache.ibatis.annotations.Param;

import com.liangzhi.commons.domain.platform.Device;
import com.liangzhi.commons.domain.platform.Sensor;

public interface DeviceMapper extends Mapper {
	
	Device getDeviceById(Integer id);
	
	// The ID of the new user will be set by myBatis
	int insertDevice(@Param(value = "device") Device newDevice);
	
	int updateDeviceById(@Param(value = "device") Device newDevice);
	
	int deleteDeviceById(Integer id);
		
	Sensor getSensorById(Integer id);
	
	// The ID of the new user will be set by myBatis
	int insertSensor(@Param(value = "sensor") Sensor newSensor);
		
	int updateSensorById(@Param(value = "sensor") Sensor newSensor);
		
	int deleteSensorById(Integer id);
}
