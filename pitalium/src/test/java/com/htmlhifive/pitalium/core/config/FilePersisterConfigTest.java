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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class FilePersisterConfigTest {

	/**
	 * ビルダーテスト（デフォルト値）
	 */
	@Test
	public void testBuilder_defaultValue() throws Exception {
		FilePersisterConfig expected = new FilePersisterConfig();
		FilePersisterConfig actual = FilePersisterConfig.builder().build();

		assertThat(actual, is(expected));
	}

	/**
	 * ビルダーテスト（全プロパティ）
	 */
	@Test
	public void testBuilder_setAllProperties() throws Exception {
		FilePersisterConfig expected = new FilePersisterConfig();
		expected.setResultDirectory("1");
		expected.setTargetResultFileName("2");
		expected.setScreenshotFileName("3");
		expected.setDiffFileName("4");

		FilePersisterConfig actual = FilePersisterConfig.builder().resultDirectory("1").targetResultFileName("2")
				.screenshotFileName("3").diffFileName("4").build();

		assertThat(actual, is(expected));
	}

}