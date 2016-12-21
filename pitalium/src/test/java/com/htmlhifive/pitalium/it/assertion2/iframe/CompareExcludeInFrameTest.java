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

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.it.assertion2.PtlItAssertionTestBase;
import org.junit.Test;

/**
 * iframe内の要素を除外設定とするテスト
 */
public class CompareExcludeInFrameTest extends PtlItAssertionTestBase {

	@Test
	public void captureBody_excludeInsideAndOutsideFrame() throws Exception {
		openIFramePage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTarget().moveTarget(true).scrollTarget(false)
				.addExcludeByClassName("exclude-target").addExcludeByClassName("content-left")
				.inFrameByClassName("content").build();
		assertionView.assertView(arg);
	}

	@Test
	public void captureIFrame() throws Exception {
		openIFramePage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByClassName("content").moveTarget(true)
				.scrollTarget(true).addExcludeByClassName("content-left").build();
		assertionView.assertView(arg);
	}

}
