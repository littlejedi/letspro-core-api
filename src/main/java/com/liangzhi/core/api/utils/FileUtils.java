package com.liangzhi.core.api.utils;

import java.io.File;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class FileUtils {

	private FileUtils() {
		// Hidden on purpose
	}

	public static void ensureParentDirectory(String parentDirectory) {
        File parentDir;
        if (parentDirectory != null) {
            parentDir = new File(parentDirectory);
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
        } else {
            throw new WebApplicationException(Response.Status.PRECONDITION_FAILED);
        }
    }
}
