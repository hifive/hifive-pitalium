package com.htmlhifive.pitalium.it.util;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.htmlhifive.pitalium.core.selenium.PtlCapabilities;

public class ItUtils {

	private ItUtils() {

	}

	/**
	 * 結果JSONからこのメソッドのスクリーンショットの結果を取得
	 *
	 * @param results
	 * @param capabilities
	 */
	public static JsonNode getCurrentScreenshotResultJson(String methodName, JsonNode results,
			PtlCapabilities capabilities) {
		for (JsonNode jn : results.get("screenshotResults")) {
			JsonNode capabilitiesNode = jn.get("capabilities");
			String platform = capabilities.getPlatform() != null ? capabilities.getPlatform().toString() : "";
			if (methodName.equals(jn.get("testMethod").asText())
					&& capabilities.getBrowserName().equals(capabilitiesNode.get("browserName").asText())) {
				if (platform.equals("") || platform.endsWith(capabilitiesNode.get("platform").asText())) {
					JsonNode version = capabilitiesNode.get("version");
					if (version == null) {
						if (StringUtils.isEmpty(capabilities.getVersion())) {
							return jn;
						}
					} else {
						if (version.asText().equals(capabilities.getVersion())) {
							return jn;
						}
					}
				}
			}
		}
		return null;
	}
}
