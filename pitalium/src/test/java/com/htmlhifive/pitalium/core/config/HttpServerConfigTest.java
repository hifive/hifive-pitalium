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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class HttpServerConfigTest {

	/**
	 * ビルダーでインスタンス作成するテスト（デフォルト値）
	 */
	@Test
	public void buildWithDefaultProperties() throws Exception {
		HttpServerConfig config = HttpServerConfig.builder().build();
		assertThat(config.getHostname(), is("localhost"));
		assertThat(config.getPort(), is(8080));
	}

	/**
	 * ビルダーでインスタンスを作成するテスト（プロパティ設定）
	 */
	@Test
	public void buildWithCustomProperties() throws Exception {
		HttpServerConfig config = HttpServerConfig.builder().hostname("hostname").port(1234).build();
		assertThat(config.getHostname(), is("hostname"));
		assertThat(config.getPort(), is(1234));
	}

}