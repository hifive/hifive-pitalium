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

package com.htmlhifive.pitalium.it.assertion2.excludeInScroll;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.it.assertion2.PtlItAssertionTestBase;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.fail;

/**
 * 要素内スクロールと除外設定を組み合わせるテスト
 */
public class CompareExcludeElementInScrollTest extends PtlItAssertionTestBase {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	/**
	 * 要素内スクロールの撮影において、最初から見えている内容が変更された要素を除外して比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと。
	 */
	@Test
	public void excludeVisibleElement() throws Exception {
		openScrollPage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var table = document.getElementById('table-scroll');"
					+ "var tr = table.getElementsByTagName('tr')[0];" + "var td = tr.childNodes[0];"
					+ "td.style.background = 'rgb(12, 34, 56)';");
		}

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#table-scroll > tbody")
				.addExcludeByCssSelector("tr:nth-of-type(1)").build();
		assertionView.assertView(arg);
	}

	/**
	 * 要素内スクロールの撮影において、最初から見えている内容が変更された要素を除外せずに比較する。
	 * 
	 * @ptl.expect 差分が発生すること。
	 */
	@Test
	public void excludeVisibleElementWithoutExcludeOption() throws Exception {
		openScrollPage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var table = document.getElementById('table-scroll');"
					+ "var tr = table.getElementsByTagName('tr')[0];" + "var td = tr.childNodes[0];"
					+ "td.style.background = 'rgb(12, 34, 56)';");
		}

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#table-scroll > tbody")
				.build();

		if (isRunTest()) {
			expectedException.expect(AssertionError.class);
			assertionView.assertView(arg);
			fail();
			return;
		}

		assertionView.assertView(arg);
	}

	/**
	 * 要素内スクロールの撮影において、最初は見えていない内容が変更された要素を除外して比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと。
	 */
	@Test
	public void excludeNotVisibleElement() throws Exception {
		openScrollPage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var table = document.getElementById('table-scroll');"
					+ "var tr = table.getElementsByTagName('tr');" + "var td = tr[tr.length - 1].childNodes[0];"
					+ "td.style.background = 'rgb(12, 34, 56)';");
		}

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#table-scroll > tbody")
				.addExcludeByCssSelector("tr:nth-last-of-type(1)").build();
		assertionView.assertView(arg);
	}

	/**
	 * 要素内スクロールの撮影において、最初は見えていない内容が変更された要素を除外せずに比較する。
	 * 
	 * @ptl.expect 差分が発生すること。
	 */
	@Test
	public void excludeNotVisibleElementWithoutExcludeOption() throws Exception {
		openScrollPage();

		if (isRunTest()) {
			driver.executeJavaScript("" + "var table = document.getElementById('table-scroll');"
					+ "var tr = table.getElementsByTagName('tr');" + "var td = tr[tr.length - 1].childNodes[0];"
					+ "td.style.background = 'rgb(12, 34, 56)';");
		}

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#table-scroll > tbody")
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
