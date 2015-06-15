package com.liangzhi.core.api.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UuidUtils {

	private static final String FORGIVING_UUID = String.format("(%1$s{8})-?(%1$s{4})-?(%1$s{4})-?(%1$s{4})-?(%1$s{12})",
			"[0-9a-fA-F]");

	private static final Pattern FORGIVING_UUID_PATTERN = Pattern
			.compile(FORGIVING_UUID);

	private UuidUtils() {
		// hidden on purpose
	}

	public static boolean isValidUUIDString(String uuidString) {
		final Matcher m = FORGIVING_UUID_PATTERN.matcher(uuidString);
		return m.matches();
	}
}
