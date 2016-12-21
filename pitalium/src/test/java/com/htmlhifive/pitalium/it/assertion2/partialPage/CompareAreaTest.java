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
 * 範囲を指定して比較をするテスト
 */
public class CompareAreaTest extends PtlItAssertionTestBase {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void compareSameArea() throws Exception {
		openBasicTextPage();

		Rect rect = getPixelRectById("textColumn0");
		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTarget(rect.x, rect.y, rect.width, rect.height)
				.build();
		assertionView.assertView(arg);
	}

	@Test
	public void compareDifferentArea() throws Exception {
		openBasicTextPage();

		Rect rect = getPixelRectById(isRunTest() ? "textColumn1" : "textColumn0");
		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTarget(rect.x, rect.y, rect.width, rect.height)
				.build();

		if (isRunTest()) {
			expectedException.expect(AssertionError.class);
			assertionView.assertView(arg);
			fail();
			return;
		}

		assertionView.assertView(arg);
	}

}
