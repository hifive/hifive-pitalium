/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.core.selenium;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.htmlhifive.testlib.core.config.EnvironmentConfig;

public class MrtCapabilitiesTest {

	@Test
	public void capabilitiesをデフォルトパスから読み込み() throws Exception {
		String filePath = new EnvironmentConfig().getCapabilitiesFilePath();
		List<Map<String, Object>> result = MrtCapabilities.readCapabilitiesFromFileOrResources(filePath);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode expected = mapper.readValue(getClass().getClassLoader().getResource(filePath), JsonNode.class);
		assertThat(result.size(), is(expected.size()));
	}

	@Test
	public void getPlatformName_platformがある場合() throws Exception {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("platform", "WINDOWS");
		map.put("platformName", "hoge");

		MrtCapabilities capabilities = new MrtCapabilities(map);
		assertThat(capabilities.getPlatformName(), is("WINDOWS"));
	}

	@Test
	public void getPlatformName_platformが無い場合() throws Exception {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("platformName", "hoge");

		MrtCapabilities capabilities = new MrtCapabilities(map);
		assertThat(capabilities.getPlatformName(), is("hoge"));
	}

	@Test
	public void testGetPlatformVersion() throws Exception {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("platformVersion", "hoge");

		MrtCapabilities capabilities = new MrtCapabilities(map);
		assertThat(capabilities.getPlatformVersion(), is("hoge"));
	}

	@Test
	public void testGetDeviceName() throws Exception {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("deviceName", "hoge");

		MrtCapabilities capabilities = new MrtCapabilities(map);
		assertThat(capabilities.getDeviceName(), is("hoge"));
	}
}