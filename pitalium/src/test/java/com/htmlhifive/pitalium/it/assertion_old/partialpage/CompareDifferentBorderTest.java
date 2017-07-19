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
package com.htmlhifive.pitalium.it.assertion_old.partialpage;

import java.util.ArrayList;
import java.util.List;

import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.config.ExecMode;
import com.htmlhifive.pitalium.core.model.CompareTarget;
import com.htmlhifive.pitalium.core.model.ScreenArea;
import com.htmlhifive.pitalium.core.model.SelectorType;

/**
 * 要素のボーダーが異なる場合に正しく比較が行われるかのテスト
 */
public class CompareDifferentBorderTest extends PtlTestBase {

	private static final PtlTestConfig config = PtlTestConfig.getInstance();
	private static final String BASE_URL = config.getTestAppConfig().getBaseUrl();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/**
	 * ExpectedとActualで要素のborderが違う場合に正しく比較が行われるかのテスト<br>
	 * 前提条件：このテストをSET_EXPECTEDモードで1度実行し、スクリーンショットを取得している<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：assertViewが不正解と判定されて、差分画像が生成される。
	 */
	@Test
	public void compareDifferentBorderWidth() {
		driver.get(BASE_URL);

		// ACTUALモードで実行時に、意図的に差分を作り出す
		if (config.getEnvironment().getExecMode() == ExecMode.RUN_TEST) {
			driver.executeJavaScript("document.getElementById('about').style.border = '1px solid black';");
			thrown.expect(AssertionError.class);
		}

		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.CSS_SELECTOR, "#about")));
		assertionView.assertView("compareDifferentFontColor", targets);
	}

	/**
	 * ExpectedとActualで要素のborderが違う場合に正しく比較が行われるかのテスト<br>
	 * 前提条件：このテストをSET_EXPECTEDモードで1度実行し、スクリーンショットを取得している<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：assertViewが不正解と判定されて、差分画像が生成される。
	 */
	@Test
	public void compareDifferentBorderWidth2() {
		driver.get(BASE_URL);

		// ACTUALモードで実行時に、意図的に差分を作り出す
		if (config.getEnvironment().getExecMode() == ExecMode.RUN_TEST) {
			driver.executeJavaScript("document.getElementById('about').style.border = '500px solid black';");
			thrown.expect(AssertionError.class);
		}

		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.CSS_SELECTOR, "#about")));
		assertionView.assertView("compareDifferentFontColor", targets);
	}
}
