/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.core.config;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.htmlhifive.testlib.common.util.JSONUtils;

public class EnvironmentConfigTest {

	/**
	 * 全プロパティをJSONから取得するテスト
	 */
	@Test
	public void testAllPropertiesFromJSON() throws Exception {
		InputStream in = null;
		EnvironmentConfig env;
		try {
			in = getClass().getResourceAsStream("EnvironmentConfigTest_allProperties.json");
			env = JSONUtils.readValue(in, EnvironmentConfig.class);
		} finally {
			if (in != null) {
				in.close();
			}
		}

		assertThat(env.getExecMode(), is(ExecMode.RUN_TEST));
		assertThat(env.getHubHost(), is("127.0.0.1"));
		assertThat(env.getHubPort(), is(8080));
		assertThat(env.getMaxThreadCount(), is(100));
		assertThat(env.getMaxThreadExecuteTime(), is(200));
		assertThat(env.getMaxDriverWait(), is(300));
		assertThat(env.getCapabilitiesFilePath(), is("test.json"));
		assertThat(env.getPersister(), is("test"));
	}

	/**
	 * MrtConfigurationPropertyのテスト
	 */
	@Test
	public void testAnnotatedProperties_MrtConfigurationProperty() throws Exception {
		Map<String, String> props = new HashMap<String, String>();
		props.put("environmentConfig", "com/htmlhifive/testlib/core/config/EnvironmentConfigTest_allProperties.json");
		props.put("execMode", "TAKE_SCREENSHOT");

		MrtTestConfig config = new MrtTestConfig(props);
		EnvironmentConfig env = config.getConfig(EnvironmentConfig.class);

		assertThat(env.getExecMode(), is(ExecMode.TAKE_SCREENSHOT));
	}

}