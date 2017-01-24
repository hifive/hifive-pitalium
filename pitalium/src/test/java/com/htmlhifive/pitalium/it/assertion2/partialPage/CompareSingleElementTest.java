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
import org.junit.Test;

/**
 * 単一要素を同一条件で比較するテスト
 */
public class CompareSingleElementTest extends PtlItAssertionTestBase {

	/**
	 * 単一要素を移動オプションありで比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと
	 */
	@Test
	public void compareWithMoveOption() throws Exception {
		openBasicTextPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("container").moveTarget(true).build();
		assertionView.assertView(arg);
	}

	/**
	 * 単一要素を移動オプションなしで比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと
	 */
	@Test
	public void compareWithoutMoveOption() throws Exception {
		openBasicTextPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("container").moveTarget(false)
				.build();
		assertionView.assertView(arg);
	}

	/**
	 * 文字列を移動オプションありで比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと
	 */
	@Test
	public void compareTextWithMoveOption() throws Exception {
		openBasicTextPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("textColumn0").moveTarget(true)
				.build();
		assertionView.assertView(arg);
	}

	/**
	 * 文字列を移動オプションｍなしで比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと
	 */
	@Test
	public void compareTextWithoutMoveOption() throws Exception {
		openBasicTextPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("textColumn0").moveTarget(false)
				.build();
		assertionView.assertView(arg);
	}

	/**
	 * 特定の色のみを含んだ要素を移動オプションありで比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと
	 */
	@Test
	public void compareColorWithMoveOption() throws Exception {
		openBasicColorPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("colorColumn0").moveTarget(true)
				.build();
		assertionView.assertView(arg);
	}

	/**
	 * 特定の色のみを含んだ要素を移動オプションなしで比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと
	 */
	@Test
	public void compareColorWithoutMoveOption() throws Exception {
		openBasicColorPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("colorColumn0").moveTarget(false)
				.build();
		assertionView.assertView(arg);
	}

	/**
	 * PNG画像を移動オプションありで比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと
	 */
	@Test
	public void compareImagePngWithMoveOption() throws Exception {
		openBasicImagePage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#imageColumn0 img")
				.moveTarget(true).build();
		assertionView.assertView(arg);
	}

	/**
	 * PNG画像を移動オプションなしで比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと
	 */
	@Test
	public void compareImagePngWithoutMoveOption() throws Exception {
		openBasicImagePage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#imageColumn0 img")
				.moveTarget(false).build();
		assertionView.assertView(arg);
	}

	/**
	 * JPEG画像を移動オプションありで比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと
	 */
	@Test
	public void compareImageJpegWithMoveOption() throws Exception {
		openBasicImagePage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#imageColumn1 img")
				.moveTarget(true).build();
		assertionView.assertView(arg);
	}

	/**
	 * JPEG画像を移動オプションなしで比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと
	 */
	@Test
	public void compareImageJpegWithoutMoveOption() throws Exception {
		openBasicImagePage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#imageColumn1 img")
				.moveTarget(false).build();
		assertionView.assertView(arg);
	}

	/**
	 * 圧縮率の高いJPEG画像を移動オプションありで比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと
	 */
	@Test
	public void compareImageBadJpegWithMoveOption() throws Exception {
		openBasicImagePage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#imageColumn2 img")
				.moveTarget(true).build();
		assertionView.assertView(arg);
	}

	/**
	 * 圧縮率の高いJPEG画像を移動オプションなしで比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと
	 */
	@Test
	public void compareImageBadJpegWithoutMoveOption() throws Exception {
		openBasicImagePage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#imageColumn2 img")
				.moveTarget(false).build();
		assertionView.assertView(arg);
	}

}
