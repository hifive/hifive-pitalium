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
package com.htmlhifive.pitalium.core.model;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class ScreenAreaResultTest {

	//<editor-fold desc="areaEquals">

	/**
	 * セレクタが一致するテスト
	 */
	@Test
	public void testAreaEquals_selector_equals() throws Exception {
		ScreenAreaResult result1 = new ScreenAreaResult(new IndexDomSelector(SelectorType.TAG_NAME, "body", 0), null,
				null);
		ScreenAreaResult result2 = new ScreenAreaResult(new IndexDomSelector(SelectorType.TAG_NAME, "body", 0), null,
				null);

		assertThat(result1.areaEquals(result2), is(true));
	}

	/**
	 * セレクタが一致しないテスト
	 */
	@Test
	public void testAreaEquals_selector_not_equals() throws Exception {
		ScreenAreaResult result1 = new ScreenAreaResult(new IndexDomSelector(SelectorType.TAG_NAME, "body", 0), null,
				null);
		ScreenAreaResult result2 = new ScreenAreaResult(new IndexDomSelector(SelectorType.TAG_NAME, "body", 1), null,
				null);

		assertThat(result1.areaEquals(result2), is(false));
	}

	/**
	 * 矩形指定範囲が一致するテスト
	 */
	@Test
	public void testAreaEquals_rectangle_equals() throws Exception {
		ScreenAreaResult result1 = new ScreenAreaResult(null, null, ScreenArea.of(1d, 1d, 1d, 1d));
		ScreenAreaResult result2 = new ScreenAreaResult(null, null, ScreenArea.of(1d, 1d, 1d, 1d));

		assertThat(result1.areaEquals(result2), is(true));
	}

	/**
	 * 矩形指定範囲が一致しないテスト
	 */
	@Test
	public void testAreaEquals_rectangle_not_equals() throws Exception {
		ScreenAreaResult result1 = new ScreenAreaResult(null, null, ScreenArea.of(1d, 1d, 1d, 1d));
		ScreenAreaResult result2 = new ScreenAreaResult(null, null, ScreenArea.of(2d, 1d, 1d, 1d));

		assertThat(result1.areaEquals(result2), is(false));
	}

	//</editor-fold>

}