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

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.it.assertion2.PtlItAssertionTestBase;

/**
 * 条件が異なる場合の比較テスト（要素の可視性が異なる）
 */
public class CompareDifferentVisibilityTest extends PtlItAssertionTestBase {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	/**
	 * 同一要素を比較時にvisibility: hiddenに変更して比較する。
	 * 
	 * @ptl.expect 差分が発生すること。
	 */
	@Test
	public void compareSameElementWhichVisibilityIsHidden() throws Exception {
		openBasicTextPage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var element = document.getElementById('textColumn0');"
					+ "element.style.visibility = 'hidden';");
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
	 * 同一要素の一部を比較時にvisibility: hiddenに変更して比較する。
	 * 
	 * @ptl.expect 差分が発生すること。
	 */
	@Test
	public void compareSameElementWhichVisibilityIsHiddenPartially() throws Exception {
		openBasicTextPage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var element = document.querySelector('#textColumn0 h2');"
					+ "element.style.visibility = 'hidden';");
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
	 * 同一要素を比較時にdisplay: noneに変更して比較する。
	 * 
	 * @ptl.expect AssertionErrorが発生すること。
	 */
	@Test
	public void compareSameElementWhichIsDisplayNone() throws Exception {
		openBasicTextPage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var element = document.getElementById('textColumn0');"
					+ "element.style.display = 'none';");
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
	 * 同一要素を比較時に、親要素をdisplay: noneに変更して比較する。
	 * 
	 * @ptl.expect AssertionErrorが発生すること。
	 */
	@Test
	public void compareSameElementWhoseParentIsDisplayNone() throws Exception {
		openBasicTextPage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var element = document.getElementById('textRow');"
					+ "element.style.display = 'none';");
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
	 * 同一要素を比較時に要素の一部をdisplay: noneに変更して比較する。
	 * 
	 * @ptl.expect 差分が発生すること。
	 */
	@Test
	public void compareMultipleElementsWhichIsDisplayNonePartially() throws Exception {
		openBasicTextPage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var element = document.getElementById('textColumn0');"
					+ "element.style.display = 'none';");
		}

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByClassName("text-column").build();

		if (isRunTest()) {
			expectedException.expect(AssertionError.class);
			assertionView.assertView(arg);
			fail();
			return;
		}

		assertionView.assertView(arg);
	}

}
