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
package com.htmlhifive.pitalium.it.screenshot_old.partialapge;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import com.htmlhifive.pitalium.core.model.CompareTarget;
import com.htmlhifive.pitalium.core.model.ScreenArea;
import com.htmlhifive.pitalium.core.model.SelectorType;

/**
 * スクロールが無いページのスクリーンショット取得のテスト
 */
public class TakeNoScrollPageTest extends PtlTestBase {

	private static final String BASE_URL = PtlTestConfig.getInstance().getTestAppConfig().getBaseUrl();

	/**
	 * スクロールが無いページ(ヘッダのみ表示した状態のページ)の<br>
	 * 部分スクリーンショットが正しく取得できることを確認する。<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：スクリーンショット取得が正しく実行され、<br>
	 * 「ヘッダのみが表示されたページ」のスクリーンショットが取得される。
	 */
	@Test
	public void takeNoScrollPage() {
		driver.get(BASE_URL);

		//ヘッダ以外の領域を非表示にする
		driver.executeScript("document.getElementById('contentcontainer').style.display = 'none';");

		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.CSS_SELECTOR, ".download")));
		assertionView.assertView("takeNoScroll", targets);
	}
}