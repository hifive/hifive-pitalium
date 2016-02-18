/*
 * Copyright (C) 2016 NS Solutions Corporation
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
package com.htmlhifive.pitalium.it.screenshot.partialapge;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import com.htmlhifive.pitalium.core.model.CompareTarget;
import com.htmlhifive.pitalium.core.model.ScreenArea;
import com.htmlhifive.pitalium.core.model.SelectorType;

/**
 * ページの特定要素のスクリーンショットが正しくとれているかのテスト
 */
public class TakeMultiplePartByMultipleTargetTest extends PtlTestBase {

	private static final String BASE_URL = PtlTestConfig.getInstance().getTestAppConfig().getBaseUrl();

	/**
	 * 複数のセレクタで複数要素選択時にスクリーンショットが正しくとれているかのテスト<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、スクリーンショットが正しくとれていることを目視で確認する
	 */
	@Test
	public void takeMultiplePartByMultipleTarget() {
		driver.get(BASE_URL);
		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.CSS_SELECTOR, "#news tr")));
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.CSS_SELECTOR, "#firstNav>ul>li")));
		assertionView.assertView("multipleElements", targets);
	}
}