/*
 * Copyright (C) 2015-2017 NS Solutions Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
