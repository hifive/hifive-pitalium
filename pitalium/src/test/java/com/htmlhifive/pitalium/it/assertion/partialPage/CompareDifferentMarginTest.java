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

import org.junit.Test;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.it.assertion.PtlItAssertionTestBase;

/**
 * 条件が異なる場合の比較テスト（要素のマージンが異なる）
 */
public class CompareDifferentMarginTest extends PtlItAssertionTestBase {

	/**
	 * 比較時に要素のマージンを変更して比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと。
	 */
	@Test
	public void compareSameElementWhichHasDifferentMargin() throws Exception {
		openBasicTextPage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var element = document.getElementById('textColumn0');"
					+ "element.style.margin = '25px';" + "element.style.width = '250px';"
					+ "element.style.height = '250px';");
		} else {
			driver.executeJavaScript("" + "var element = document.getElementById('textColumn0');"
					+ "element.style.width = '250px';" + "element.style.height = '250px';");
		}

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("textColumn0").build();

		assertionView.assertView(arg);
	}

}
