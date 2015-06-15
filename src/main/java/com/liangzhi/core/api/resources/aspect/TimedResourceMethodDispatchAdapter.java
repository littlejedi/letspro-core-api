package com.liangzhi.core.api.resources.aspect;

import javax.ws.rs.ext.Provider;

import com.sun.jersey.spi.container.ResourceMethodDispatchAdapter;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;

@Provider
public class TimedResourceMethodDispatchAdapter implements
		ResourceMethodDispatchAdapter {

	@Override
	public ResourceMethodDispatchProvider adapt(
			ResourceMethodDispatchProvider provider) {
		return new TimedResourceMethodDispatchProvider(provider);
	}

}
