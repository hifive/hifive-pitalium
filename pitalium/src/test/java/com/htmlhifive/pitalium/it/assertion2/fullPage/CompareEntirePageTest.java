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

package com.htmlhifive.pitalium.it.assertion2.fullPage;

import org.junit.Test;

import com.htmlhifive.pitalium.it.assertion2.PtlItAssertionTestBase;

/**
 * 画面全体の比較を行うテスト
 */
public class CompareEntirePageTest extends PtlItAssertionTestBase {

	/**
	 * スクロールがないページ全体を比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと。
	 */
	@Test
	public void compareNonScrollPage() throws Exception {
		openGradationPage("100%", "100%");
		assertionView.assertView("s");
	}

	/**
	 * 縦スクロール0回、横スクロール1回のページ全体を比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと。
	 */
	@Test
	public void compareScrollPage_v0_h1() throws Exception {
		openGradationPage("160%", "100%");
		assertionView.assertView("s");
	}

	/**
	 * 縦スクロール0回、横スクロール2回のページ全体を比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと。
	 */
	@Test
	public void compareScrollPage_v0_h2() throws Exception {
		openGradationPage("240%", "100%");
		assertionView.assertView("s");
	}

	/**
	 * 縦スクロール1回、横スクロール0回のページ全体を比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと。
	 */
	@Test
	public void compareScrollPage_v1_h0() throws Exception {
		openGradationPage("100%", "160%");
		assertionView.assertView("s");
	}

	/**
	 * 縦スクロール2回、横スクロール0回のページ全体を比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと。
	 */
	@Test
	public void compareScrollPage_v2_h0() throws Exception {
		openGradationPage("100%", "240%");
		assertionView.assertView("s");
	}

	/**
	 * 縦スクロール1回、横スクロール1回のページ全体を比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと。
	 */
	@Test
	public void compareScrollPage_v1_h1() throws Exception {
		openGradationPage("160%", "160%");
		assertionView.assertView("s");
	}

	/**
	 * 縦スクロール2回、横スクロール2回のページ全体を比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと。
	 */
	@Test
	public void compareScrollPage_v2_h2() throws Exception {
		openGradationPage("240%", "240%");
		assertionView.assertView("s");
	}

	/**
	 * BODYにマージンが設定されたページ全体を比較する。
	 * 
	 * @ptl.expect 差分が発生しないこと。
	 */
	@Test
	public void compareBodyWithMargin() throws Exception {
		openGradationPage();
		driver.executeJavaScript("document.body.style.margin = '20px';");
		assertionView.assertView("s");
	}

}
