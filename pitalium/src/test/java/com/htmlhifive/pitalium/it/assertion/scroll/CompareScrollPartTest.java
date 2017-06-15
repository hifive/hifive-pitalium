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

import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import com.htmlhifive.pitalium.core.model.CompareTarget;
import com.htmlhifive.pitalium.core.model.DomSelector;
import com.htmlhifive.pitalium.core.model.ScreenArea;
import com.htmlhifive.pitalium.core.model.SelectorType;
import com.htmlhifive.pitalium.core.selenium.PtlWebDriverWait;

/**
 * 部分スクロール要素が正しく比較できるかのテスト
 */
public class CompareScrollPartTest extends PtlTestBase {

	private static final PtlTestConfig config = PtlTestConfig.getInstance();
	private static final String BASE_URL = config.getTestAppConfig().getBaseUrl();

	/**
	 * 部分スクロール要素が正しく比較できるかのテスト<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが成功する
	 */
	@Test
	public void compareOverflowScreenshotTest() {
		driver.get(BASE_URL);
		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		DomSelector divSelector = new DomSelector(SelectorType.ID, "about-scroll");
		ScreenArea divScreenArea = ScreenArea.of(divSelector.getType(), divSelector.getValue());

		DomSelector textareaSelector = new DomSelector(SelectorType.ID, "textarea-scroll");
		ScreenArea textScreenArea = ScreenArea.of(textareaSelector.getType(), textareaSelector.getValue());

		DomSelector tbodySelector = new DomSelector(SelectorType.CSS_SELECTOR, "#table-scroll tbody");
		ScreenArea tbodyScreenArea = ScreenArea.of(tbodySelector.getType(), tbodySelector.getValue());

		DomSelector selector = new DomSelector(SelectorType.NAME, "fb-scroll");
		ScreenArea iframeScreenArea = ScreenArea.of(selector.getType(), selector.getValue());

		CompareTarget[] targets = { new CompareTarget(divScreenArea, null, true, true),
				new CompareTarget(textScreenArea, null, true, true),
				new CompareTarget(tbodyScreenArea, null, true, true),
				new CompareTarget(iframeScreenArea, null, true, true) };
		assertionView.assertView("overflowScreenshot", targets);
	}
}