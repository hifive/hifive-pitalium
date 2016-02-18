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
package com.htmlhifive.pitalium.it.screenshot.scroll;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import com.htmlhifive.pitalium.core.model.CompareTarget;
import com.htmlhifive.pitalium.core.model.DomSelector;
import com.htmlhifive.pitalium.core.model.ScreenArea;
import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.core.model.SelectorType;
import com.htmlhifive.pitalium.core.selenium.PtlWebDriverWait;

/**
 * スクロールなしの要素をスクロールオプションをつけて撮影したときのテスト
 */
public class TakeNoScrollPartWithScrollOptionTest extends PtlTestBase {

	private static final PtlTestConfig config = PtlTestConfig.getInstance();
	private static final String BASE_URL = config.getTestAppConfig().getBaseUrl();

	/**
	 * moveTarget=trueのとき、linkTextの指定によるスクリーンショットが正しくとれているかのテスト<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、スクリーンショットが正しくとれていることを目視で確認する
	 */
	@Test
	public void takeMoveScreenshotByLinkText() {
		driver.get(BASE_URL);
		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.LINK_TEXT, "りんご"), null, false, true));
		assertionView.assertView("MoveLinkTextSelector", targets);
	}

	/**
	 * moveTarget=falseのとき、linkTextの指定によるスクリーンショットが正しくとれているかのテスト<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、スクリーンショットが正しくとれていることを目視で確認する
	 */
	@Test
	public void takeNonMoveScreenshotByLinkText() {
		driver.get(BASE_URL);
		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.LINK_TEXT, "りんご"), null, false, true));
		assertionView.assertView("NonMoveLinkTextSelector", targets);
	}

	/**
	 * moveTarget=trueのとき、スクロールなしの要素のスクリーンショットが正しく撮れているかのテスト<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、スクリーンショットが正しくとれていることを目視で確認する
	 */
	@Test
	public void takeMoveNoscrollElement() {
		driver.get(BASE_URL);
		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.ID, "no-overflow"), null, true, true));
		assertionView.assertView("MoveNoScrollElement", targets);
	}

	/**
	 * moveTarget=falseのとき、スクロールなしの要素のスクリーンショットが正しく撮れているかのテスト<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、スクリーンショットが正しくとれていることを目視で確認する
	 */
	@Test
	public void takeNonMoveNoscrollElement() {
		driver.get(BASE_URL);
		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.ID, "no-overflow"), null, false, true));
		assertionView.assertView("NonMoveNoScrollElement", targets);
	}

	/**
	 * moveTarget=trueのとき、スクロールなし（overflow: visible）で、子要素のサイズ＞親要素のサイズに<br>
	 * なっているとき、正しく撮影できるか確認するテスト。 <br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、スクリーンショットが正しくとれていることを目視で確認する
	 */
	@Test
	public void takeMoveVisibleOverflowScreenshotTest() {
		driver.get(BASE_URL);
		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		// Targetの設定
		DomSelector divSelector = new DomSelector(SelectorType.ID, "about-noscroll");
		ScreenArea divScreenArea = ScreenArea.of(divSelector.getType(), divSelector.getValue());
		CompareTarget[] targets = { new CompareTarget(divScreenArea, null, true, true) };
		assertionView.assertView("moveVisibleOverflowScreenshot", targets);

	}

	/**
	 * moveTarget=falseのとき、スクロールなし（overflow: visible）で、子要素のサイズ＞親要素のサイズに<br>
	 * なっているとき、正しく撮影できるか確認するテスト。 <br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、スクリーンショットが正しくとれていることを目視で確認する
	 */
	@Test
	public void takeNonMoveVisibleOverflowScreenshotTest() {
		driver.get(BASE_URL);
		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		// Targetの設定
		DomSelector divSelector = new DomSelector(SelectorType.ID, "about-noscroll");
		ScreenArea divScreenArea = ScreenArea.of(divSelector.getType(), divSelector.getValue());
		CompareTarget[] targets = { new CompareTarget(divScreenArea, null, false, true) };
		assertionView.assertView("nonMoveVisibleOverflowScreenshot", targets);

	}

	/**
	 * moveTarget=trueのとき、スクロールなし（overflow: hidden）で、子要素のサイズ＞親要素のサイズに<br>
	 * なっているとき、正しく撮影できるか確認するテスト。 <br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、スクリーンショットが正しくとれていることを目視で確認する
	 */
	@Test
	public void takeMoveHiddenOverflowScreenshotTest() {
		driver.get(BASE_URL);
		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		// Targetの設定
		DomSelector divSelector = new DomSelector(SelectorType.ID, "hidden-overflow");
		ScreenArea divScreenArea = ScreenArea.of(divSelector.getType(), divSelector.getValue());
		CompareTarget[] targets = { new CompareTarget(divScreenArea, null, true, true) };
		assertionView.assertView("moveHiddenOverflowScreenshot", targets);

	}

	/**
	 * moveTarget=falseのとき、スクロールなし（overflow: hidden）で、子要素のサイズ＞親要素のサイズに<br>
	 * なっているとき、正しく撮影できるか確認するテスト。 <br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、スクリーンショットが正しくとれていることを目視で確認する
	 */
	@Test
	public void takeNonMoveHiddenOverflowScreenshotTest() {
		driver.get(BASE_URL);
		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		// Targetの設定
		DomSelector divSelector = new DomSelector(SelectorType.ID, "hidden-overflow");
		ScreenArea divScreenArea = ScreenArea.of(divSelector.getType(), divSelector.getValue());
		CompareTarget[] targets = { new CompareTarget(divScreenArea, null, false, true) };
		assertionView.assertView("nonMoveHiddenOverflowScreenshot", targets);

	}

	/**
	 * bodyを部分スクロールありオプションで撮影するテスト。<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、スクリーンショットが正しくとれていることを目視で確認する
	 */
	public void takeBodyScreenshotTest() {
		driver.get(BASE_URL);
		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		assertionView.assertView(ScreenshotArgument.builder("body").addNewTarget().scrollTarget(true).build());
	}
}