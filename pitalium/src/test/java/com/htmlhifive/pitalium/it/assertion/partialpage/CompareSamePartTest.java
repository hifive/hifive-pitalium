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
package com.htmlhifive.pitalium.it.assertion.partialpage;

import java.util.ArrayList;
import java.util.List;

import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import org.junit.Test;

import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.model.CompareTarget;
import com.htmlhifive.pitalium.core.model.ScreenArea;
import com.htmlhifive.pitalium.core.model.SelectorType;

/**
 * ページの特定要素の比較が正しく行われるかのテスト
 */
public class CompareSamePartTest extends PtlTestBase {

	private static final String BASE_URL = PtlTestConfig.getInstance().getTestAppConfig().getBaseUrl();

	/**
	 * 同じ位置、同じ内容の要素を比較するテスト。<br>
	 * 前提条件：このテストをSET_EXPECTEDモードで1度実行し、スクリーンショットを取得している<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：assertViewが正解と判定される。
	 */
	@Test
	public void compareSamePartByXPath() {
		driver.get(BASE_URL);
		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.XPATH, "id(\"about\")"), null, true));
		assertionView.assertView("XPathSelector", targets);
	}

	/**
	 * 同じ位置、同じ内容の要素を比較するテスト。<br>
	 * 前提条件：このテストをSET_EXPECTEDモードで1度実行し、スクリーンショットを取得している<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：assertViewが正解と判定される。
	 */
	@Test
	public void compareSamePartByCssSelector() {
		driver.get(BASE_URL);
		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.CSS_SELECTOR, "#about")));
		assertionView.assertView("compareSame", targets);
	}

	/**
	 * 同じ位置、同じ内容の要素を比較するテスト。<br>
	 * 前提条件：このテストをSET_EXPECTEDモードで1度実行し、スクリーンショットを取得している<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、「検索入力欄」のスクリーンショットが正しくとれていることを目視で確認する
	 */
	@Test
	public void compareSamePartByName() {
		driver.get(BASE_URL);
		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.NAME, "text"), null, true));
		assertionView.assertView("NameSelector", targets);
	}

	/**
	 * 同じ位置、同じ内容の要素を比較するテスト。<br>
	 * 前提条件：このテストをSET_EXPECTEDモードで1度実行し、スクリーンショットを取得している<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、「更新履歴」赤文字のスクリーンショットが正しくとれていることを目視で確認する
	 */
	@Test
	public void compareSamePartByTagName() {
		driver.get(BASE_URL);
		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.TAG_NAME, "em"), null, true));
		assertionView.assertView("TagNameSelector", targets);
	}

	/**
	 * 同じ位置、同じ内容の要素を比較するテスト。<br>
	 * 前提条件：このテストをSET_EXPECTEDモードで1度実行し、スクリーンショットを取得している<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、「Download」ボタンのスクリーンショットが正しくとれていることを目視で確認する
	 */
	@Test
	public void compareSamePartByClassName() {
		driver.get(BASE_URL);
		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.CLASS_NAME, "dlbtn"), null, true));
		assertionView.assertView("ClassNameSelector", targets);
	}

	/**
	 * 同じ位置、同じ内容の要素を比較するテスト。<br>
	 * 前提条件：このテストをSET_EXPECTEDモードで1度実行し、スクリーンショットを取得している<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、「hifiveとは」部分のスクリーンショットが正しくとれていることを目視で確認する
	 */
	@Test
	public void compareSamePartById() {
		driver.get(BASE_URL);
		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.ID, "about"), null, true));
		assertionView.assertView("IdSelector", targets);
	}

	/**
	 * 同じ位置、同じ内容の要素を比較するテスト。<br>
	 * 前提条件：このテストをSET_EXPECTEDモードで1度実行し、スクリーンショットを取得している<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、「ダウンロードはこちら」部分のスクリーンショットが正しくとれていることを目視で確認する
	 */
	@Test
	public void compareSamePartByPartialLink() {
		driver.get(BASE_URL);
		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.PARTIAL_LINK, "ダウンロードは"), null, true));
		assertionView.assertView("PartialLinkSelector", targets);
	}

	/**
	 * 同じ位置、同じ内容の要素を比較するテスト。<br>
	 * 前提条件：このテストをSET_EXPECTEDモードで1度実行し、スクリーンショットを取得している<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、「ダウンロードはこちら」部分のスクリーンショットが正しくとれていることを目視で確認する
	 */
	@Test
	public void compareSamePartByLinkText() {
		driver.get(BASE_URL);
		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.LINK_TEXT, "ダウンロードはこちら"), null, true));
		assertionView.assertView("LinkTextSelector", targets);
	}
}
