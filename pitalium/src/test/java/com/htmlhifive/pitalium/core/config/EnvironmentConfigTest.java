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
package com.htmlhifive.pitalium.core.config;

import com.htmlhifive.pitalium.common.util.JSONUtils;
import org.junit.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
		assertThat(env.getAutoResizeWindow(), is(true));
		assertThat(env.getMaxThreadCount(), is(100));
		assertThat(env.getMaxThreadExecuteTime(), is(200));
		assertThat(env.getMaxDriverWait(), is(300));
		assertThat(env.getScriptTimeout(), is(10));
		assertThat(env.getCapabilitiesFilePath(), is("test.json"));
		assertThat(env.getPersister(), is("test"));
		assertThat(env.isDebug(), is(true));
	}

	/**
	 * PtlConfigurationPropertyのテスト
	 */
	@Test
	public void testAnnotatedProperties_PtlConfigurationProperty() throws Exception {
		Map<String, String> props = new HashMap<String, String>();
		props.put("environmentConfig", "com/htmlhifive/pitalium/core/config/EnvironmentConfigTest_allProperties.json");
		props.put("execMode", "TAKE_SCREENSHOT");

		PtlTestConfig config = new PtlTestConfig(props);
		EnvironmentConfig env = config.getConfig(EnvironmentConfig.class);

		assertThat(env.getExecMode(), is(ExecMode.TAKE_SCREENSHOT));
	}

	/**
	 * ビルダーテスト。プロパティ設定なし。
	 */
	@Test
	public void builderWithNoProps() throws Exception {
		EnvironmentConfig config = EnvironmentConfig.builder().build();

		assertThat(config.getExecMode(), is(ExecMode.SET_EXPECTED));
		assertThat(config.getHubHost(), is("localhost"));
		assertThat(config.getHubPort(), is(4444));
		assertThat(config.getAutoResizeWindow(), is(false));
		assertThat(config.getMaxThreadCount(), is(16));
		assertThat(config.getMaxThreadExecuteTime(), is(3600));
		assertThat(config.getMaxDriverWait(), is(30));
		assertThat(config.getScriptTimeout(), is(30));
		assertThat(config.getCapabilitiesFilePath(), is("capabilities.json"));
		assertThat(config.getPersister(), is("com.htmlhifive.pitalium.core.io.FilePersister"));
		assertThat(config.getWebDriverSessionLevel(), is(WebDriverSessionLevel.TEST_CASE));
		assertThat(config.isDebug(), is(false));
	}

	/**
	 * ビルダーテスト。全プロパティを設定。
	 */
	@Test
	public void builderWithAllProps() throws Exception {
//@formatter:off
		EnvironmentConfig config = EnvironmentConfig.builder()
				.execMode(ExecMode.TAKE_SCREENSHOT)
				.hubHost("127.0.0.1")
				.hubPort(1234)
				.maxThreadCount(1)
				.maxThreadExecuteTime(2)
				.maxDriverWait(10)
				.scriptTimeout(11)
				.capabilitiesFilePath("cap.json")
				.persister("persister")
				.autoResizeWindow(true)
				.webDriverSessionLevel(WebDriverSessionLevel.GLOBAL)
				.debug(true)
				.build();
//@formatter:on

		assertThat(config.getExecMode(), is(ExecMode.TAKE_SCREENSHOT));
		assertThat(config.getHubHost(), is("127.0.0.1"));
		assertThat(config.getHubPort(), is(1234));
		assertThat(config.getAutoResizeWindow(), is(true));
		assertThat(config.getMaxThreadCount(), is(1));
		assertThat(config.getMaxThreadExecuteTime(), is(2));
		assertThat(config.getMaxDriverWait(), is(10));
		assertThat(config.getScriptTimeout(), is(11));
		assertThat(config.getCapabilitiesFilePath(), is("cap.json"));
		assertThat(config.getPersister(), is("persister"));
		assertThat(config.getWebDriverSessionLevel(), is(WebDriverSessionLevel.GLOBAL));
		assertThat(config.isDebug(), is(true));
	}

}