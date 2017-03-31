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

package com.htmlhifive.pitalium.it.assertion2.iframe;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.it.assertion2.PtlItAssertionTestBase;

/**
 * iframe内の要素を除外設定とするテスト
 */
public class CompareExcludeInFrameTest extends PtlItAssertionTestBase {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	/**
	 * BODYを撮影する際にIFRAME内外の要素を除外して比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと。
	 */
	@Test
	public void excludeInsideAndOutsideIFrame() throws Exception {
		openIFramePage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var el0 = document.getElementsByClassName('exclude-target')[0];"
					+ "el0.style.background = 'blue';"
					+ "var el1 = window.frames[0].document.getElementsByClassName('content-left')[0];"
					+ "el1.style.background = '#123456';");
		}

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTarget().moveTarget(true).scrollTarget(false)
				.addExcludeByClassName("exclude-target").addExcludeByClassName("content-left")
				.inFrameByClassName("content").build();
		assertionView.assertView(arg);

		// 除外しないと差分
		ScreenshotArgument arg2 = ScreenshotArgument.builder("s2").addNewTarget().moveTarget(true).scrollTarget(false)
				.build();
		if (isRunTest()) {
			expectedException.expect(AssertionError.class);
			assertionView.assertView(arg2);
			fail();
			return;
		}

		assertionView.assertView(arg2);
	}

	/**
	 * IFRAMEを撮影する際にIFRAME内の要素を除外して比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと。
	 */
	@Test
	public void excludeInsideIFrame() throws Exception {
		openIFramePage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var el0 = document.getElementsByClassName('exclude-target')[0];"
					+ "el0.style.background = 'blue';"
					+ "var el1 = window.frames[0].document.getElementsByClassName('content-left')[0];"
					+ "el1.style.background = '#123456';");
		}

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByClassName("content").moveTarget(true)
				.scrollTarget(true).addExcludeByClassName("content-left").build();
		assertionView.assertView(arg);

		// 除外しないと差分
		ScreenshotArgument arg2 = ScreenshotArgument.builder("s2").addNewTargetByClassName("content").moveTarget(true)
				.scrollTarget(true).build();
		if (isRunTest()) {
			expectedException.expect(AssertionError.class);
			assertionView.assertView(arg2);
			fail();
			return;
		}

		assertionView.assertView(arg2);
	}

}
