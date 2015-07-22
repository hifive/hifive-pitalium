/*
 * Copyright (C) 2015 NS Solutions Corporation
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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.htmlhifive.pitalium.common.exception.TestRuntimeException;

public class MrtTestConfigTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	//<editor-fold desc="Convert">
	@Test
	public void testConvertFromString_string() throws Exception {
		String value = "hoge";
		Class type = String.class;
		Object result = MrtTestConfig.convertFromString(type, value);

		assertThat(result, is(instanceOf(String.class)));
		assertThat((String) result, is("hoge"));
	}

	@Test
	public void testConvertFromString_int() throws Exception {
		String value = "10";
		Class type = int.class;
		Object result = MrtTestConfig.convertFromString(type, value);

		assertThat(result, is(instanceOf(Integer.class)));
		assertThat((Integer) result, is(10));
	}

	@Test
	public void testConvertFromString_double() throws Exception {
		String value = "10.55";
		Class type = double.class;
		Object result = MrtTestConfig.convertFromString(type, value);

		assertThat(result, is(instanceOf(double.class)));
		assertThat((Double) result, is(10.55d));
	}

	@Test
	public void testConvertFromString_enum() throws Exception {
		String value = "RUNTIME";
		Class type = RetentionPolicy.class;
		Object result = MrtTestConfig.convertFromString(type, value);

		assertThat(result, is(instanceOf(RetentionPolicy.class)));
		assertThat((RetentionPolicy) result, is(RetentionPolicy.RUNTIME));
	}

	@Test
	public void testConvertFromString_enum_fail() throws Exception {
		expectedException.expect(IllegalArgumentException.class);

		String value = "HOGE";
		Class type = RetentionPolicy.class;
		MrtTestConfig.convertFromString(type, value);
	}

	//</editor-fold>

	//<editor-fold desc="LoadConfig">

	/**
	 * LoadConfig フィールドの自動保管
	 */
	@Test
	public void testLoadConfig() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("prop_0", "hoge");
		map.put("prop1", "fuga");
		map.put("prop2", "100");

		MrtTestConfig config = new MrtTestConfig(map);
		ConfigParent c = config.getConfig(ConfigParent.class);

		assertThat(c.prop0, is("hoge"));
		assertThat(c.object, is(nullValue()));
		assertThat(c.child.prop1, is("prop1"));
		assertThat(c.child.prop2, is(10));
	}

	/**
	 * LoadConfig 入れ子のフィールドがnullの場合
	 */
	@Test
	public void testLoadConfig_childNullValue() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("prop_0", "hoge");
		map.put("prop1", "fuga");
		map.put("prop2", "100");

		MrtTestConfig config = new MrtTestConfig(map);
		ConfigParent c = config.getConfig(ConfigParentEx.class);

		assertThat(c.prop0, is("hoge"));
		assertThat(c.object, is(nullValue()));
		assertThat(c.child, is(nullValue()));
	}

	@MrtConfiguration
	static class ConfigParent {

		@MrtConfigurationProperty("prop_0")
		String prop0 = "prop0";
		ConfigChild child = new ConfigChild();
		Object object = null;

	}

	@MrtConfiguration
	static class ConfigParentEx extends ConfigParent {
		ConfigParentEx() {
			child = null;
		}
	}

	@MrtConfiguration
	static class ConfigChild {

		@MrtConfigurationProperty("prop_1")
		String prop1 = "prop1";
		int prop2 = 10;

	}

	//</editor-fold>

	//<editor-fold desc="GetEnvironment">

	/**
	 * 環境設定読み込みテスト
	 */
	@Test
	public void testGetEnvironment_startupArguments() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("environmentConfig", "com/htmlhifive/testlib/core/config/MrtTestConfigTest_Env.json");
		map.put("execMode", "TAKE_SCREENSHOT");

		MrtTestConfig config = new MrtTestConfig(map);
		EnvironmentConfig env = config.getEnvironment();

		assertThat(env.getExecMode(), is(ExecMode.TAKE_SCREENSHOT));
		assertThat(env.getHubHost(), is("255.255.255.255"));
	}

	/**
	 * 環境設定読み込みテスト ファイルが存在しない場合
	 */
	@Test
	public void testGetEnvironment_fileNotExists() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("environmentConfig", "com/htmlhifive/testlib/core/config/FILE_NOT_EXISTS");
		map.put("execMode", "TAKE_SCREENSHOT");

		expectedException.expect(TestRuntimeException.class);

		MrtTestConfig config = new MrtTestConfig(map);
		EnvironmentConfig env = config.getEnvironment();
	}

	//</editor-fold>

}