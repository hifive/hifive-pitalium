/*
 * Copyright (C) 2015-2016 NS Solutions Corporation
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
package com.htmlhifive.pitalium.core.selenium;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.htmlhifive.pitalium.core.config.EnvironmentConfig;

public class PtlCapabilitiesTest {

	@Test
	public void capabilitiesをデフォルトパスから読み込み() throws Exception {
		String filePath = new EnvironmentConfig().getCapabilitiesFilePath();
		List<Map<String, Object>> result = PtlCapabilities.readCapabilitiesFromFileOrResources(filePath);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode expected = mapper.readValue(getClass().getClassLoader().getResource(filePath), JsonNode.class);
		assertThat(result.size(), is(expected.size()));
	}

	@Test
	public void getPlatformName_platformがある場合() throws Exception {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("platform", "WINDOWS");
		map.put("platformName", "hoge");

		PtlCapabilities capabilities = new PtlCapabilities(map);
		assertThat(capabilities.getPlatformName(), is("WINDOWS"));
	}

	@Test
	public void getPlatformName_platformが無い場合() throws Exception {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("platformName", "hoge");

		PtlCapabilities capabilities = new PtlCapabilities(map);
		assertThat(capabilities.getPlatformName(), is("hoge"));
	}

	@Test
	public void testGetPlatformVersion() throws Exception {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("platformVersion", "hoge");

		PtlCapabilities capabilities = new PtlCapabilities(map);
		assertThat(capabilities.getPlatformVersion(), is("hoge"));
	}

	@Test
	public void testGetDeviceName() throws Exception {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("deviceName", "hoge");

		PtlCapabilities capabilities = new PtlCapabilities(map);
		assertThat(capabilities.getDeviceName(), is("hoge"));
	}
}