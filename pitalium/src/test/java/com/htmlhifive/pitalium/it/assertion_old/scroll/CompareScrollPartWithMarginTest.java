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
package com.htmlhifive.pitalium.it.assertion_old.scroll;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.config.ExecMode;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import com.htmlhifive.pitalium.core.model.CompareTarget;
import com.htmlhifive.pitalium.core.model.ScreenArea;
import com.htmlhifive.pitalium.core.model.SelectorType;

/**
 * ページの特定要素の比較が正しく行われるかのテスト
 */
public class CompareScrollPartWithMarginTest extends PtlTestBase {

	private static final PtlTestConfig config = PtlTestConfig.getInstance();
	private static final String BASE_URL = config.getTestAppConfig().getBaseUrl();

	/**
	 * 同じ位置、マージンの幅が異なる2つの要素を比較するテスト。<br>
	 * 前提条件：このテストをSET_EXPECTEDモードで1度実行し、スクリーンショットを取得している<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：マージンを除いたスクリーンショットが取得され、assertViewが正解と判定される。
	 */
	@Test
	public void comparePartWithMargin() {
		driver.get(BASE_URL);

		if (config.getEnvironment().getExecMode() == ExecMode.RUN_TEST) {
			driver.executeJavaScript("document.getElementById('about-scroll').style.margin = '30px';");
		}

		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.ID, "about-scroll"), null, true, true));
		assertionView.assertView("compareSame", targets);
	}
}
