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

package com.htmlhifive.pitalium.it.assertion2.partialPage;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.it.assertion2.PtlItAssertionTestBase;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.fail;

/**
 * 条件が異なる場合の比較テスト（要素の色が異なる）
 */
public class CompareDifferentColorTest extends PtlItAssertionTestBase {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	/**
	 * 同一要素の色を全体的に変更して比較する。
	 * 
	 * @ptl.expect 差分が発生すること。
	 */
	@Test
	public void compareSameElementWhichHasDifferentColor() throws Exception {
		openBasicColorPage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var element = document.getElementById('colorColumn0');"
					+ "element.style.background = '#ffff00';");
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
	 * 同一要素の色を部分的に変更して比較する。
	 * 
	 * @ptl.expect 差分が発生すること。
	 */
	@Test
	public void compareSameElementWhichHasDifferentColorPartially() throws Exception {
		openGradationPage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var container = document.getElementById('container');"
					+ "var row = container.getElementsByClassName('gradation-row')[2];"
					+ "var column = row.getElementsByClassName('gradation-column')[4];"
					+ "column.style.backgroundColor = 'rgb(100, 100, 100)';");
		}

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("container").build();

		if (isRunTest()) {
			expectedException.expect(AssertionError.class);
			assertionView.assertView(arg);
			fail();
			return;
		}

		assertionView.assertView(arg);
	}

	/**
	 * 同一要素の文字色を変更して比較する。
	 * 
	 * @ptl.expect 差分が発生すること。
	 */
	@Test
	public void compareSameTextWhichHasDifferentColor() throws Exception {
		openBasicTextPage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var element = document.querySelector('#textColumn0 > p');"
					+ "element.style.color = '#ff0000';");
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
