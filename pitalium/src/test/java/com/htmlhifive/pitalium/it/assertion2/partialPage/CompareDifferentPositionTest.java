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
import static org.junit.Assume.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.it.assertion2.PtlItAssertionTestBase;

/**
 * 条件が異なる場合の比較テスト（内容は等しいが座標が異なる）
 */
public class CompareDifferentPositionTest extends PtlItAssertionTestBase {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	/**
	 * テキストの座標を変更し、移動オプションを指定して比較する。
	 *
	 * @ptl.expect 差分が発生しないこと。
	 */
	@Test
	public void compareTextWithMoveOption() throws Exception {
		openBasicTextPage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var element = document.querySelector('#textColumn0 > h2 > small');"
					+ "var style = element.style;" + "style.marginTop = '5.5px';" + "style.marginLeft = '5.5px'");
		}

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#textColumn0 > h2 > small")
				.moveTarget(true).build();
		assertionView.assertView(arg);
	}

	/**
	 * テキストの座標を変更し、移動オプションを指定せずに比較する。
	 *
	 * @ptl.expect 差分が発生すること。
	 */
	@Test
	public void compareTextWithoutMoveOption() throws Exception {
		openBasicTextPage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var element = document.querySelector('#textColumn0 > h2 > small');"
					+ "var style = element.style;" + "style.marginTop = '5.5px';" + "style.marginLeft = '5.5px'");
		}

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#textColumn0 > h2 > small")
				.moveTarget(false).build();

		if (isRunTest()) {
			expectedException.expect(AssertionError.class);
			assertionView.assertView(arg);
			fail();
			return;
		}

		assertionView.assertView(arg);
	}

	/**
	 * 画像の座標を変更し、移動オプションを指定して比較する。
	 *
	 * @ptl.expect 差分が発生しないこと。
	 */
	@Test
	public void compareImage0WithMoveOption() throws Exception {
		assumeFalse("Skip IE9 test (assertError will occur because of subpixel rendering)", isInternetExplorer9());

		openBasicImagePage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var element = document.querySelector('#imageColumn0 img');"
					+ "var style = element.style;" + "style.marginTop = '5.5px';" + "style.marginLeft = '5.5px'");
		}

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#imageColumn0 img")
				.moveTarget(true).build();
		assertionView.assertView(arg);
	}

	/**
	 * 画像の座標を変更し、移動オプションを指定して比較する。
	 *
	 * @ptl.expect 差分が発生しないこと。
	 */
	@Test
	public void compareImage1WithMoveOption() throws Exception {
		openBasicImagePage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var element = document.querySelector('#imageColumn1 img');"
					+ "var style = element.style;" + "style.marginTop = '3%';" + "style.marginLeft = '3%'");
		}

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#imageColumn1 img")
				.moveTarget(true).build();
		assertionView.assertView(arg);
	}

	/**
	 * 画像の座標を変更し、移動オプションを指定して比較する。
	 *
	 * @ptl.expect 差分が発生しないこと。
	 */
	@Test
	public void compareImage2WithMoveOption() throws Exception {
		assumeFalse("Skip IE9 test (assertError will occur because of subpixel rendering)", isInternetExplorer9());

		openBasicImagePage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var element = document.querySelector('#imageColumn2 img');"
					+ "var style = element.style;" + "style.marginTop = '5.5px';" + "style.marginLeft = '5.5px'");
		}

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#imageColumn2 img")
				.moveTarget(true).build();
		assertionView.assertView(arg);
	}

	/**
	 * 画像の座標を変更し、移動オプションを指定せずに比較する。
	 *
	 * @ptl.expect 差分が発生すること。
	 */
	@Test
	public void compareImage0WithoutMoveOption() throws Exception {
		openBasicImagePage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var element = document.querySelector('#imageColumn0 img');"
					+ "var style = element.style;" + "style.marginTop = '5.5px';" + "style.marginLeft = '5.5px'");
		}

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#imageColumn0 img")
				.moveTarget(false).build();

		if (isRunTest()) {
			expectedException.expect(AssertionError.class);
			assertionView.assertView(arg);
			fail();
			return;
		}

		assertionView.assertView(arg);
	}

	/**
	 * 画像の座標を変更し、移動オプションを指定せずに比較する。
	 *
	 * @ptl.expect 差分が発生すること。
	 */
	@Test
	public void compareImage1WithoutMoveOption() throws Exception {
		openBasicImagePage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var element = document.querySelector('#imageColumn1 img');"
					+ "var style = element.style;" + "style.marginTop = '5.5px';" + "style.marginLeft = '5.5px'");
		}

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#imageColumn1 img")
				.moveTarget(false).build();

		if (isRunTest()) {
			expectedException.expect(AssertionError.class);
			assertionView.assertView(arg);
			fail();
			return;
		}

		assertionView.assertView(arg);
	}

	/**
	 * 画像の座標を変更し、移動オプションを指定せずに比較する。
	 *
	 * @ptl.expect 差分が発生すること。
	 */
	@Test
	public void compareImage2WithoutMoveOption() throws Exception {
		openBasicImagePage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var element = document.querySelector('#imageColumn2 img');"
					+ "var style = element.style;" + "style.marginTop = '5.5px';" + "style.marginLeft = '5.5px'");
		}

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#imageColumn2 img")
				.moveTarget(false).build();

		if (isRunTest()) {
			expectedException.expect(AssertionError.class);
			assertionView.assertView(arg);
			fail();
			return;
		}

		assertionView.assertView(arg);
	}

	// 他のテストで代替できるのでコメントアウト。IEでは必ずサブピクセルレンダリングで差分が検出される
	// (上記が対応できたらコメントインしてよい)
	//	/**
	//	 * 要素の座標を入れ替え、移動オプションを指定して比較する。
	//	 *
	//	 * @ptl.expect 差分が発生しないこと。
	//	 */
	//	@Test
	//	public void compareElementsWhichAreReplacedWithMoveOption() throws Exception {
	//		openBasicTextPage();
	//
	//		// 要素の位置を入れ替える
	//		if (isRunTest()) {
	//			driver.executeJavaScript("" + "var row = document.getElementById('textRow');"
	//					+ "var column0 = document.getElementById('textColumn0');"
	//					+ "var column1 = document.getElementById('textColumn1');" + "row.removeChild(column0);"
	//					+ "row.removeChild(column1);" + "row.appendChild(column1);" + "row.appendChild(column0);");
	//		}
	//
	//		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("textColumn0").moveTarget(true)
	//				.addNewTargetById("textColumn1").moveTarget(true).addNewTargetById("textColumn2").moveTarget(true)
	//				.build();
	//		assertionView.assertView(arg);
	//	}
	//
	//	/**
	//	 * 要素の座標を入れ替え、移動オプションを指定せずに比較する。
	//	 *
	//	 * @ptl.expect 差分が発生しないこと。
	//	 */
	//	@Test
	//	public void compareElementsWhichAreReplacedWithoutMoveOption() throws Exception {
	//		openBasicTextPage();
	//
	//		// 要素の位置を入れ替える
	//		if (isRunTest()) {
	//			driver.executeJavaScript("" + "var row = document.getElementById('textRow');"
	//					+ "var column0 = document.getElementById('textColumn0');"
	//					+ "var column1 = document.getElementById('textColumn1');" + "row.removeChild(column0);"
	//					+ "row.removeChild(column1);" + "row.appendChild(column1);" + "row.appendChild(column0);");
	//		}
	//
	//		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("textColumn0").moveTarget(false)
	//				.addNewTargetById("textColumn1").moveTarget(false).addNewTargetById("textColumn2").moveTarget(false)
	//				.build();
	//		assertionView.assertView(arg);
	//	}

}
