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
 * 条件が異なる場合の比較テスト（要素のボーダーが異なる）
 */
public class CompareDifferentBorderTest extends PtlItAssertionTestBase {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	/**
	 * 同一要素のボーダーの幅を変更して比較する。
	 * 
	 * @ptl.expect 差分が発生すること。
	 */
	@Test
	public void compareSameElementWhichHasDifferentBorderWidth() throws Exception {
		openBasicTextPage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var element = document.getElementById('textColumn0');"
					+ "element.style.border = '5px black solid';");
		} else {
			driver.executeJavaScript("" + "var element = document.getElementById('textColumn0');"
					+ "element.style.border = '1px black solid';");
		}

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("textColumn0").build();

		if (isRunTest()) {
			expectedException.expect(AssertionError.class);
			assertionView.assertView(arg);
			fail();
			return;
		}

		assertionView.assertView(arg);
	}

	/**
	 * 同一要素からボーダーを削除して比較する。
	 * 
	 * @ptl.expect 差分が発生すること。
	 */
	@Test
	public void compareSameElementWhichHasNoBorderWidth() throws Exception {
		openBasicTextPage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var element = document.getElementById('textColumn0');"
					+ "element.style.border = '0px';");
		} else {
			driver.executeJavaScript("" + "var element = document.getElementById('textColumn0');"
					+ "element.style.border = '1px black solid';");
		}

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("textColumn0").build();

		if (isRunTest()) {
			expectedException.expect(AssertionError.class);
			assertionView.assertView(arg);
			fail();
			return;
		}

		assertionView.assertView(arg);
	}

}
