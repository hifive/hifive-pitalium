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

package com.htmlhifive.pitalium.core.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TestAppConfigTest {

	/**
	 * ビルダーテスト。プロパティ設定なし。
	 */
	@Test
	public void builderWithNoProps() throws Exception {
		TestAppConfig config = TestAppConfig.builder().build();

		assertThat(config.getBaseUrl(), is(nullValue()));
		assertThat(config.getWindowWidth(), is(1280));
		assertThat(config.getWindowHeight(), is(800));
	}

	/**
	 * ビルダーテスト。全プロパティを設定。
	 */
	@Test
	public void builderWithAllProps() throws Exception {
		TestAppConfig config = TestAppConfig.builder().baseUrl("http://localhost:8096/").windowWidth(500)
				.windowHeight(600).build();

		assertThat(config.getBaseUrl(), is("http://localhost:8096/"));
		assertThat(config.getWindowWidth(), is(500));
		assertThat(config.getWindowHeight(), is(600));
	}

}