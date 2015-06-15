package com.liangzhi.core.api.jmx;

import java.lang.management.ManagementFactory;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.springframework.jmx.export.MBeanExporterListener;

public class HttpAdaptorMgr implements MBeanExporterListener {
	
	/**
	 * Default to the platform MBean Server
	 */
	private MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();

	/**
	 * This name must match the specified name in jmxBeans.xml
	 */
	private String adaptorName = "Server:name=HttpAdaptor";
	
	//TODO: Make this synchronized
	@Override
	public void mbeanRegistered(ObjectName objectName) {
		if (adaptorName.equals(objectName.getCanonicalName())) {
			try {
				mbeanServer.invoke(objectName, "start", null, null);
			} catch (InstanceNotFoundException | ReflectionException
					| MBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void mbeanUnregistered(ObjectName objectName) {
		if (adaptorName.equals(objectName.getCanonicalName())) {
			try {
				mbeanServer.invoke(objectName, "stop", null, null);
			} catch (InstanceNotFoundException | ReflectionException
					| MBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public MBeanServer getmbeanServer() {
		return mbeanServer;
	}

	public void setmbeanServer(MBeanServer mbeanServer) {
		this.mbeanServer = mbeanServer;
	}

	public String getAdaptorName() {
		return adaptorName;
	}

	public void setAdaptorName(String adaptorName) {
		this.adaptorName = adaptorName;
	}	
}
