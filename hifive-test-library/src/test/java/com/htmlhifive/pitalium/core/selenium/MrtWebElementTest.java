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
package com.htmlhifive.pitalium.core.selenium;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class MrtWebElementTest {

	/**
	 * Double変換テスト
	 */
	@Test
	public void testGetDoubleOrDefault() throws Exception {
		double value = MrtWebElement.getDoubleOrDefault(1d, Double.NaN);
		assertThat(value, is(1d));
	}

	/**
	 * Double変換テスト。数字、文字の混合
	 */
	@Test
	public void testGetDoubleOrDefault_mixed() throws Exception {
		double value = MrtWebElement.getDoubleOrDefault("1.0px", Double.NaN);
		assertThat(value, is(1d));
	}

	/**
	 * Double変換テスト。文字のみ
	 */
	@Test
	public void testGetDoubleOrDefault_not_number() throws Exception {
		double value = MrtWebElement.getDoubleOrDefault("middle", Double.NaN);
		assertThat(value, is(Double.NaN));
	}

	/**
	 * Double変換テスト・マイナスの値。
	 */
	@Test
	public void testGetDoubleOrDefault_minus_number() throws Exception {
		double value = MrtWebElement.getDoubleOrDefault("-1.5px", Double.NaN);
		assertThat(value, is(-1.5d));
	}

}