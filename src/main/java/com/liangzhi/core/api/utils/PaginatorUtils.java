package com.liangzhi.core.api.utils;

import javax.ws.rs.core.UriBuilder;

import com.liangzhi.core.api.Constants;
import com.liangzhi.commons.domain.Paginator;

public class PaginatorUtils {

	private PaginatorUtils() {
		// Hidden on purpose
	}
	
	public static Paginator fromResultAndPagingOptions(int resultCount, Integer pageSize, Integer pageNumber) {
		Paginator paginator = new Paginator();
		int actualPageSize = pageSize != null ? pageSize : Constants.DEFAULT_PAGE_SIZE;
		int actualPageNumber = pageNumber != null ? pageNumber : 1;
		int numberOfPages = (resultCount + actualPageSize - 1) / actualPageSize;
		paginator.setPageNumber(actualPageNumber);
		paginator.setPageCount(numberOfPages);
		paginator.setResultCount(resultCount);
		paginator.setPageSize(actualPageSize);
		return paginator;
	}
	
	public static void addPagingLinks(Paginator paginator, UriBuilder baseUrlBuilder, boolean fullEntity) {
		int actualPageSize = paginator.getPageSize();
		int actualPageNumber = paginator.getPageNumber();
		int numberOfPages = paginator.getPageCount();
		if (fullEntity == true) {
			baseUrlBuilder.queryParam("fullEntity", fullEntity);
		}
		baseUrlBuilder.queryParam("pageSize", actualPageSize);
		Integer prevPageNumber = null;
		Integer nextPageNumber = null;
		if (actualPageNumber > paginator.getPageCount()) {
			return;
		}
		if (actualPageNumber != 1) {
			prevPageNumber = actualPageNumber - 1;
		}
		if (actualPageNumber != numberOfPages) {
			nextPageNumber = actualPageNumber + 1;
		}
		if (prevPageNumber != null) {
			paginator.setPrev(baseUrlBuilder.queryParam("pageNumber", prevPageNumber).build().toASCIIString());
		}
        if (nextPageNumber != null) {
			paginator.setNext(baseUrlBuilder.replaceQueryParam("pageNumber", nextPageNumber).build().toASCIIString());
        }
	}

}
