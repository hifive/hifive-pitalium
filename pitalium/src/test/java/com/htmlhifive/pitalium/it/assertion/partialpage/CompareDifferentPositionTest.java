/*
 * Copyright (C) 2015 NS Solutions Corporation
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

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.config.ExecMode;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import com.htmlhifive.pitalium.core.model.CompareTarget;
import com.htmlhifive.pitalium.core.model.DomSelector;
import com.htmlhifive.pitalium.core.model.ScreenArea;
import com.htmlhifive.pitalium.core.model.SelectorType;

/**
 * 要素の位置が異なる場合に正しく比較が行われるかのテスト
 */
public class CompareDifferentPositionTest extends PtlTestBase {

	private static final PtlTestConfig config = PtlTestConfig.getInstance();
	private static final String BASE_URL = config.getTestAppConfig().getBaseUrl();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/**
	 * ExpectedとActualで要素の位置が整数ピクセル分異なるが、内容は同じ場合のテスト<br>
	 * 前提条件：このテストをSET_EXPECTEDモードで1度実行し、スクリーンショットを取得している<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：assertViewが正解と判定される。
	 */
	@Test
	public void compareDifferentPositionInt() {
		driver.get(BASE_URL);

		// ACTUALモードで実行時に、意図的に差分を作り出す
		// ヘッダの高さが変わる
		if (config.getEnvironment().getExecMode() == ExecMode.RUN_TEST) {
			final int expendHeight = 1;
			WebElement headerglobal = driver.findElement(By.id("headerglobal"));
			driver.executeJavaScript("document.getElementById('headerglobal').style.height = '"
					+ (Integer.parseInt(headerglobal.getAttribute("clientHeight")) + expendHeight) + "';");
		}

		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.CSS_SELECTOR, "#about")));
		assertionView.assertView("compareDifferentFontColor", targets.toArray(new CompareTarget[] {}),
				new DomSelector[] { new DomSelector(SelectorType.CLASS_NAME, "gototop") });
	}

	/**
	 * ExpectedとActualで要素の位置が実数ピクセル分異なる場合に、movesTarget=trueを指定して比較する<br>
	 * 前提条件：このテストをSET_EXPECTEDモードで1度実行し、スクリーンショットを取得している<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：assertViewが正解と判定される。
	 */
	@Test
	public void compareDifferentPositionDoubleMovesTargetTrue() {
		driver.get(BASE_URL);

		// ACTUALモードで実行時に、意図的に差分を作り出す
		// ヘッダの高さが変わる
		if (config.getEnvironment().getExecMode() == ExecMode.RUN_TEST) {
			final double expendHeight = 0.5;
			WebElement headerglobal = driver.findElement(By.id("headerglobal"));
			driver.executeJavaScript("document.getElementById('headerglobal').style.height = '"
					+ (Integer.parseInt(headerglobal.getAttribute("clientHeight")) + expendHeight) + "';");
		}

		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.CSS_SELECTOR, "#about"), null, true));
		assertionView.assertView("compareDifferentFontColor", targets.toArray(new CompareTarget[] {}),
				new DomSelector[] { new DomSelector(SelectorType.CLASS_NAME, "gototop") });
	}

	/**
	 * ExpectedとActualで要素の位置が実数ピクセル分異なる場合に、movesTarget=falseを指定して比較する<br>
	 * 前提条件：このテストをSET_EXPECTEDモードで1度実行し、スクリーンショットを取得している<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：assertViewが不正解と判定されて、差分画像が出力される。
	 */
	@Test
	public void compareDifferentPositionDoubleMovesTargetFalse() {
		driver.get(BASE_URL);

		// ACTUALモードで実行時に、意図的に差分を作り出す
		// ヘッダの高さが変わる
		if (config.getEnvironment().getExecMode() == ExecMode.RUN_TEST) {
			final double expendHeight = 0.5;
			WebElement headerglobal = driver.findElement(By.id("headerglobal"));
			driver.executeJavaScript("document.getElementById('headerglobal').style.height = '"
					+ (Integer.parseInt(headerglobal.getAttribute("clientHeight")) + expendHeight) + "';");
			//			thrown.expect(AssertionError.class);
		}

		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.CSS_SELECTOR, "#about"), null, false));
		assertionView.assertView("compareDifferentFontColor", targets.toArray(new CompareTarget[] {}),
				new DomSelector[] { new DomSelector(SelectorType.CLASS_NAME, "gototop") });
	}

	/**
	 * body全体の位置が異なる場合に、movesTarget=trueを指定して比較する<br>
	 * 前提条件：このテストをSET_EXPECTEDモードで1度実行し、スクリーンショットを取得している<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：assertViewが正解と判定される
	 */
	@Test
	public void compareDifferentBodyPositionMovesTargetTrue() {
		driver.get(BASE_URL);

		// ACTUALモードで実行時に、意図的に差分を作り出す
		// Body全体がずれる
		if (config.getEnvironment().getExecMode() == ExecMode.RUN_TEST) {
			final double bodyTop = -0.5;
			driver.executeJavaScript("document.body.style.top ='" + bodyTop + "';");
		}

		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.CSS_SELECTOR, "#about"), null, false));
		assertionView.assertView("compareDifferentFontColor", targets.toArray(new CompareTarget[] {}),
				new DomSelector[] { new DomSelector(SelectorType.CLASS_NAME, "gototop") });
	}
}
