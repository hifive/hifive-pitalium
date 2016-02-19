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
package com.htmlhifive.pitalium.it.screenshot.scroll;

import org.junit.Test;

import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import com.htmlhifive.pitalium.core.model.CompareTarget;
import com.htmlhifive.pitalium.core.selenium.PtlWebDriverWait;

/**
 * 横スクロールのスクリーンショットが正しくとれているかのテスト
 */
public class TakeHorizontalScreenshotTest extends PtlTestBase {

	private static final PtlTestConfig config = PtlTestConfig.getInstance();
	private static final String BASE_URL = config.getTestAppConfig().getBaseUrl();

	/**
	 * 横スクロールのスクリーンショットが正しくとれているかのテスト<br>
	 * 前提：ウィンドウサイズを横300px未満に設定して実行する 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、スクリーンショットが正しくとれていることを目視で確認する
	 */
	@Test
	public void takeHorizontalScrollScreenshotTest() {
		driver.get(BASE_URL);
		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();
		CompareTarget[] targets = { new CompareTarget() };
		assertionView.assertView("HorizontalScrollScreenshot", targets);
	}
}