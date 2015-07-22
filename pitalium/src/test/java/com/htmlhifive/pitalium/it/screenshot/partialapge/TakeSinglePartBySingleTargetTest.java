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
package com.htmlhifive.pitalium.it.screenshot.partialapge;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.htmlhifive.pitalium.core.MrtTestBase;
import com.htmlhifive.pitalium.core.config.MrtTestConfig;
import com.htmlhifive.pitalium.core.model.CompareTarget;
import com.htmlhifive.pitalium.core.model.ScreenArea;
import com.htmlhifive.pitalium.core.model.SelectorType;

/**
 * ページの特定要素のスクリーンショットが正しくとれているかのテスト
 */
public class TakeSinglePartBySingleTargetTest extends MrtTestBase {

	private static final String BASE_URL = MrtTestConfig.getInstance().getTestAppConfig().getBaseUrl();

	/**
	 * XPathセレクタによる単一要素のスクリーンショットが正しくとれているかのテスト<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、「hifiveとは」部分のスクリーンショットが正しくとれていることを目視で確認する
	 */
	@Test
	public void takeScreenshotByXPath() {
		driver.get(BASE_URL);
		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.XPATH, "id(\"about\")"), null, true));
		assertionView.assertView("XPathSelector", targets);
	}

	/**
	 * cssセレクタによる単一要素のスクリーンショットが正しくとれているかのテスト<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、「hifiveとは」部分のスクリーンショットが正しくとれていることを目視で確認する
	 */
	@Test
	public void takeScreenshotByCssSelector() {
		driver.get(BASE_URL);
		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.CSS_SELECTOR, "#about")));
		assertionView.assertView("CssSelector", targets);
	}

	/**
	 * Nameの指定による単一要素のスクリーンショットが正しくとれているかのテスト<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、「検索入力欄」のスクリーンショットが正しくとれていることを目視で確認する
	 */
	@Test
	public void takeScreenshotByName() {
		driver.get(BASE_URL);
		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.NAME, "text"), null, true));
		assertionView.assertView("NameSelector", targets);
	}

	/**
	 * TagNameの指定による単一要素のスクリーンショットが正しくとれているかのテスト<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、「更新履歴」赤文字のスクリーンショットが正しくとれていることを目視で確認する
	 */
	@Test
	public void takeScreenshotByTagName() {
		driver.get(BASE_URL);
		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.TAG_NAME, "em"), null, true));
		assertionView.assertView("TagNameSelector", targets);
	}

	/**
	 * ClassNameの指定による単一要素のスクリーンショットが正しくとれているかのテスト<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、「Download」ボタンのスクリーンショットが正しくとれていることを目視で確認する
	 */
	@Test
	public void takeScreenshotByClassName() {
		driver.get(BASE_URL);
		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.CLASS_NAME, "dlbtn"), null, true));
		assertionView.assertView("ClassNameSelector", targets);
	}

	/**
	 * ClassNameの指定による単一要素のスクリーンショットが正しくとれているかのテスト<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、「hifiveとは」部分のスクリーンショットが正しくとれていることを目視で確認する
	 */
	@Test
	public void takeScreenshotById() {
		driver.get(BASE_URL);
		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.ID, "about"), null, true));
		assertionView.assertView("IdSelector", targets);
	}

	/**
	 * PartialLinkの指定による単一要素のスクリーンショットが正しくとれているかのテスト<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、「ダウンロードはこちら」部分のスクリーンショットが正しくとれていることを目視で確認する
	 */
	@Test
	public void takeScreenshotByPartialLink() {
		driver.get(BASE_URL);
		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.PARTIAL_LINK, "ダウンロードは"), null, true));
		assertionView.assertView("PartialLinkSelector", targets);
	}

	/**
	 * linkTextの指定による単一要素のスクリーンショットが正しくとれているかのテスト<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、「ダウンロードはこちら」部分のスクリーンショットが正しくとれていることを目視で確認する
	 */
	@Test
	public void takeScreenshotByLinkText() {
		driver.get(BASE_URL);
		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.LINK_TEXT, "ダウンロードはこちら"), null, true));
		assertionView.assertView("LinkTextSelector", targets);
	}
}