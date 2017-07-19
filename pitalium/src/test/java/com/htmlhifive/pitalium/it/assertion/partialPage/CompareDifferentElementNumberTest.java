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
 * 比較時に要素数が異なる場合のテスト
 */
public class CompareDifferentElementNumberTest extends PtlItAssertionTestBase {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	/**
	 * 比較時に一部の要素が存在しない状態で比較する。
	 * 
	 * @ptl.expect 差分が発生すること。
	 */
	@Test
	public void compareRemovedElement() throws Exception {
		openBasicTextPage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var element = document.getElementById('textColumn2');"
					+ "var parent = document.getElementById('textRow');" + "parent.removeChild(element);");
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

	/**
	 * 比較時に要素を追加して比較する。
	 * 
	 * @ptl.expect 差分が発生すること。
	 */
	@Test
	public void compareAddedElement() throws Exception {
		openBasicTextPage();

		if (!isRunTest()) {
			driver.executeJavaScript("" + "var element = document.getElementById('textColumn2');"
					+ "var parent = document.getElementById('textRow');" + "parent.removeChild(element);");
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
