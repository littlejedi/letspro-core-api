package com.liangzhi.core.api.utils;

import javax.ws.rs.core.UriBuilder;

import com.liangzhi.commons.api.CoreApiPath;
import com.liangzhi.commons.domain.Link;

public class LinkUtils {
	
	public static Link getUserParentLink(String baseUrl, Integer userId) {
		Link link = new Link();
		UriBuilder builder = UriBuilder.fromUri(baseUrl).path(CoreApiPath.USERS.getPath()).path(userId.toString());
		link.setHref(builder.build().toString());
		link.setRel("parent");
		link.setType("Link");
		return link;
	}
	
	public static Link getCourseParentLink(String baseUrl, Integer courseId) {
		Link link = new Link();
		UriBuilder builder = UriBuilder.fromUri(baseUrl).path(CoreApiPath.COURSES.getPath()).path(courseId.toString());
		link.setHref(builder.build().toString());
		link.setRel("parent");
		link.setType("Link");
		return link;
	}

}
