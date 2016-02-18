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
package com.htmlhifive.pitalium.it.assertion.partialpage;

import java.awt.image.RasterFormatException;
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
 * 要素が非表示（display : none）の場合に比較が正しく行われるかのテスト
 */
public class CompareNonePartTest extends PtlTestBase {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static final PtlTestConfig config = PtlTestConfig.getInstance();
	private static final String BASE_URL = config.getTestAppConfig().getBaseUrl();

	/**
	 * Actual時に要素が非表示（display : none）の場合に比較が正しく行われることを確認する。<br>
	 * 前提条件：このテストをSET_EXPECTEDモードで1度実行し、スクリーンショットを取得している<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：assertViewが不正解と判定され、差分画像が出力される。
	 */
	@Test
	public void compareNonePart() {
		driver.get(BASE_URL);

		// ACTUALモードで実行時に、意図的に差分を作り出す
		if (config.getEnvironment().getExecMode() == ExecMode.RUN_TEST) {
			driver.executeJavaScript("document.getElementById('about').style.display = 'none';");
			thrown.expect(AssertionError.class);
		}

		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.CSS_SELECTOR, "#about")));
		assertionView.assertView("compareSame", targets);
	}

	/**
	 * 要素がターゲットに追加された後で非表示（display : none）になった場合<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：例外が発生する
	 */
	@Test
	public void takeNonePartAfterAdding() {
		driver.get(BASE_URL);
		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.CSS_SELECTOR, "#about")));

		// ACTUALモードで実行時に、意図的に差分を作り出す
		if (config.getEnvironment().getExecMode() == ExecMode.RUN_TEST) {
			driver.executeJavaScript("document.getElementById('about').style.display = 'none';");
			thrown.expect(AssertionError.class);
		}

		assertionView.assertView("takeNonePartAfterAdding", targets);
	}

	/**
	 * 親要素が非表示（display : none）である場合<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：例外が発生する
	 */
	@Test
	public void takeNoneParent() {
		driver.get(BASE_URL);
		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.CSS_SELECTOR, "#about")));

		// ACTUALモードで実行時に、意図的に差分を作り出す
		if (config.getEnvironment().getExecMode() == ExecMode.RUN_TEST) {
			driver.executeJavaScript("document.getElementById('wrapper').style.display = 'none';");
			thrown.expect(AssertionError.class);
		}

		assertionView.assertView("takeNoneParent", targets);
	}

	/**
	 * 取得対象要素のうち一つを非表示(display : none)にした場合<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：非表示要素を除いた要素のスクリーンショットが取得される。<br>
	 * 　　　　　この場合"2014/07/02"および"2014/06/10"の更新履歴の行が取得される。
	 */
	@Test
	public void takeIncludeNonePart() {
		driver.get(BASE_URL);

		// 取得対象要素のうち一つを非表示にする
		// ACTUALモードで実行時に、意図的に差分を作り出す
		if (config.getEnvironment().getExecMode() == ExecMode.RUN_TEST) {
			driver.executeJavaScript("document.getElementById('news').getElementsByTagName('tr')[0].style.display = 'none'");
			thrown.expect(AssertionError.class);
		}

		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.CSS_SELECTOR, "#news tr")));

		thrown.expect(RasterFormatException.class);
		assertionView.assertView("takeIncludeNonePar", targets);
	}
}
