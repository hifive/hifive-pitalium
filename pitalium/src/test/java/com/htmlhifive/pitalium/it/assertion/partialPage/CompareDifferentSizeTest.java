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

package com.htmlhifive.pitalium.it.assertion.partialPage;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.it.assertion.PtlItAssertionTestBase;

/**
 * 条件が異なる場合の比較テスト（要素の大きさが異なる）
 */
public class CompareDifferentSizeTest extends PtlItAssertionTestBase {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	/**
	 * 要素の幅を変更して比較する。
	 * 
	 * @ptl.expect 差分が発生すること。
	 */
	@Test
	public void compareSameElementWhichHasDifferentWidth() throws Exception {
		openBasicColorPage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var element = document.getElementById('colorColumn0');"
					+ "element.style.width = '200px';");
		}

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("colorColumn0").build();

		if (isRunTest()) {
			expectedException.expect(AssertionError.class);
			assertionView.assertView(arg);
			fail();
			return;
		}

		assertionView.assertView(arg);
	}

	/**
	 * 要素の高さを変更して比較する。
	 * 
	 * @ptl.expect 差分が発生すること。
	 */
	@Test
	public void compareSameElementWhichHasDifferentHeight() throws Exception {
		openBasicColorPage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var element = document.getElementById('colorColumn0');"
					+ "element.style.height = '200px';");
		}

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("colorColumn0").build();

		if (isRunTest()) {
			expectedException.expect(AssertionError.class);
			assertionView.assertView(arg);
			fail();
			return;
		}

		assertionView.assertView(arg);
	}

	/**
	 * 要素の幅と高さを変更して比較する。
	 * 
	 * @ptl.expect 差分が発生すること。
	 */
	@Test
	public void compareSameElementWhichHasDifferentSize() throws Exception {
		openBasicColorPage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var element = document.getElementById('colorColumn0');"
					+ "element.style.width = '200px';" + "element.style.height = '200px';");
		}

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("colorColumn0").build();

		if (isRunTest()) {
			expectedException.expect(AssertionError.class);
			assertionView.assertView(arg);
			fail();
			return;
		}

		assertionView.assertView(arg);
	}

}
