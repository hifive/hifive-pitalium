/*
 * Copyright (C) 2015-2016 NS Solutions Corporation
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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.config.ExecMode;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import com.htmlhifive.pitalium.core.model.CompareTarget;
import com.htmlhifive.pitalium.core.model.DomSelector;
import com.htmlhifive.pitalium.core.model.ScreenArea;
import com.htmlhifive.pitalium.core.model.SelectorType;
import com.htmlhifive.pitalium.core.selenium.PtlWebDriverWait;

/**
 * 部分スクロール内Excludeありのスクリーンショットが正しくとれているかのテスト
 */
public class CompareScrollPartWithExcludeTest extends PtlTestBase {

	private static final PtlTestConfig config = PtlTestConfig.getInstance();
	private static final String BASE_URL = config.getTestAppConfig().getBaseUrl();

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	/**
	 * excludeを指定しないとdiffが出ることの確認のテスト<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4, 5.1/iOS 8.1, 8.3<br>
	 * 期待結果：assertViewでテストが失敗する
	 */
	@Test
	public void checkFailure() {
		driver.get(BASE_URL);

		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		if (PtlTestConfig.getInstance().getEnvironment().getExecMode() == ExecMode.RUN_TEST) {
			driver.executeJavaScript("document.getElementById('banana').innerHTML='banana'");
			expectedException.expect(AssertionError.class);
		}

		DomSelector tbodySelector = new DomSelector(SelectorType.CSS_SELECTOR, "#table-scroll tbody");
		ScreenArea tbodyScreenArea = ScreenArea.of(tbodySelector.getType(), tbodySelector.getValue());

		CompareTarget[] targets = { new CompareTarget(tbodyScreenArea, null, true, true) };

		assertionView.assertView("DiffTbody", targets);
	}

	/**
	 * 1つのExclude要素を指定してスクリーンショットがとれるかのテスト<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストに成功する。
	 */
	@Test
	public void takeSinglePartExcludeTest() {
		driver.get(BASE_URL);
		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		if (PtlTestConfig.getInstance().getEnvironment().getExecMode() == ExecMode.RUN_TEST) {
			driver.executeJavaScript("document.getElementById('banana').innerHTML='banana'");
		}

		DomSelector tbodySelector = new DomSelector(SelectorType.CSS_SELECTOR, "#table-scroll tbody");
		ScreenArea tbodyScreenArea = ScreenArea.of(tbodySelector.getType(), tbodySelector.getValue());

		ScreenArea excludeArea = ScreenArea.of(SelectorType.ID, "banana");

		CompareTarget[] targets = { new CompareTarget(tbodyScreenArea, new ScreenArea[] { excludeArea }, true, true) };
		assertionView.assertView("SingleExcludeScreenshot", targets);
	}

	/**
	 * スクロールしないと見えない要素をExclude指定してスクリーンショットがとれるかのテスト<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストに成功する。
	 */
	@Test
	public void takeInvisiblePartExcludeTest() {
		driver.get(BASE_URL);
		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		if (PtlTestConfig.getInstance().getEnvironment().getExecMode() == ExecMode.RUN_TEST) {
			driver.executeJavaScript("document.getElementById('banana').innerHTML='banana'");
			driver.executeJavaScript("document.getElementById('grape').innerHTML='grape'");
			driver.executeJavaScript("document.getElementById('pineapple').innerHTML='pineapple'");
		}

		DomSelector tbodySelector = new DomSelector(SelectorType.CSS_SELECTOR, "#table-scroll tbody");
		ScreenArea tbodyScreenArea = ScreenArea.of(tbodySelector.getType(), tbodySelector.getValue());

		ScreenArea excludeArea1 = ScreenArea.of(SelectorType.ID, "banana");
		ScreenArea excludeArea2 = ScreenArea.of(SelectorType.ID, "grape");
		ScreenArea excludeArea3 = ScreenArea.of(SelectorType.ID, "pineapple");

		CompareTarget[] targets = { new CompareTarget(tbodyScreenArea, new ScreenArea[] { excludeArea1, excludeArea2,
				excludeArea3 }, true, true) };
		assertionView.assertView("SingleExcludeScreenshot", targets);
	}
}