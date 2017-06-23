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

package com.htmlhifive.pitalium.it.assertion.scroll;

import org.junit.Test;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.it.assertion.PtlItAssertionTestBase;

/**
 * 要素無いスクロールがある単一要素を比較するテスト
 */
public class CompareSingleScrollElementTest extends PtlItAssertionTestBase {

	/**
	 * 要素無いスクロールがあるDIV要素を比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと。
	 */
	@Test
	public void compareScrollableDivElement() throws Exception {
		openScrollPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("div-scroll").scrollTarget(true)
				.build();
		assertionView.assertView(arg);
	}

	/**
	 * 要素無いスクロールがあるTEXTAREA要素を比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと。
	 */
	@Test
	public void compareScrollableTextareaElement() throws Exception {
		openScrollPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("textarea-scroll").scrollTarget(true)
				.build();
		assertionView.assertView(arg);
	}

	/**
	 * 要素無いスクロールがあるTABLE要素を比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと。
	 */
	@Test
	public void compareScrollableTableElement() throws Exception {
		openScrollPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#table-scroll > tbody")
				.scrollTarget(true).build();
		assertionView.assertView(arg);
	}

	/**
	 * 要素無いスクロールがあるIFRAME要素を比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと。
	 */
	@Test
	public void compareScrollableIFrameElement() throws Exception {
		openScrollPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByName("iframe-scroll").scrollTarget(true)
				.build();
		assertionView.assertView(arg);
	}

}
