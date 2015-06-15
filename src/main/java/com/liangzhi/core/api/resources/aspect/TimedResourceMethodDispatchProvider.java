package com.liangzhi.core.api.resources.aspect;

import java.util.Date;

import com.liangzhi.core.api.resources.ResourceSLA;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.dispatch.RequestDispatcher;

public class TimedResourceMethodDispatchProvider implements
		ResourceMethodDispatchProvider {
	
	private final ResourceMethodDispatchProvider provider;
	
	public TimedResourceMethodDispatchProvider(ResourceMethodDispatchProvider provider) {
		this.provider = provider;
	}
	
	private static class TimedRequestDispatcher implements RequestDispatcher {
		
		private final RequestDispatcher underlying;
		
		private TimedRequestDispatcher(RequestDispatcher underlying) {
			this.underlying = underlying;
		}
		
		@Override
		public void dispatch(Object resource, HttpContext context) {
			String method = context.getRequest().getMethod();
			String resourceClass = resource.getClass().getSimpleName();
			String path = context.getUriInfo().getRequestUri().toString();
			//String template = context.getUriInfo().getMatchedTemplates().get(0).getTemplate();
			long start = new Date().getTime();
			try {
				underlying.dispatch(resource, context);
			} finally {
				long end = new Date().getTime();
				long elapsed = end - start;
				ResourceSLA.LOGGER.info("Request={}, Elapsed Time={}ms, Resource={}", new Object[]{method + " " + path, elapsed, resourceClass});
			}

		}		
	}

	@Override
	public RequestDispatcher create(
			AbstractResourceMethod abstractResourceMethod) {
		RequestDispatcher dispatcher = provider.create(abstractResourceMethod);
		if (dispatcher == null) {
			return null;
		}
		dispatcher = new TimedRequestDispatcher(dispatcher);
		return dispatcher;
	}

	
}
