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

package com.htmlhifive.pitalium.it.assertion.exclude;

import org.junit.Test;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.it.assertion.PtlItAssertionTestBase;

/**
 * 複数要素比較時に除外設定をするテスト
 */
public class CompareExcludeMultipleElementTest extends PtlItAssertionTestBase {

	/**
	 * 変更がない部位を複数除外して比較を行う。
	 * 
	 * @ptl.expect 差分が発生しないこと。
	 */
	@Test
	public void compareNotChangedElementWithExcludeOption() throws Exception {
		openBasicTextPage(false);

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByClassName("text-column")
				.addExcludeByCssSelector("h2").build();

		assertionView.assertView(arg);
	}

	/**
	 * 変更された部位を複数除外して比較を行う。
	 * 
	 * @ptl.expect 差分が発生しないこと。
	 */
	@Test
	public void compareChangedElementWithExcludeOption() throws Exception {
		openBasicTextPage(true);

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByClassName("text-column")
				.addExcludeByCssSelector("h2").build();

		assertionView.assertView(arg);
	}

}
