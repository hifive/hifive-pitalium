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
package com.htmlhifive.testlib.it.assertion.execlude;

import org.junit.Test;
import org.openqa.selenium.Dimension;

import com.htmlhifive.testlib.core.MrtTestBase;
import com.htmlhifive.testlib.core.config.MrtTestConfig;
import com.htmlhifive.testlib.core.model.CompareTarget;
import com.htmlhifive.testlib.core.model.DomSelector;
import com.htmlhifive.testlib.core.model.ScreenArea;
import com.htmlhifive.testlib.core.model.SelectorType;
import com.htmlhifive.testlib.core.selenium.MrtWebDriverWait;

/**
 * ページ全体(body)の比較のテスト
 */
public class CompareEntirePageTest extends MrtTestBase {

	private static final String URL_TOP_PAGE = MrtTestConfig.getInstance().getTestAppConfig().getBaseUrl();

	private static final ScreenArea[] EXCLUDES = new ScreenArea[] { ScreenArea.of(SelectorType.CLASS_NAME,
			"fb-like-box") };

	private static final DomSelector[] HIDDEN_ELEMENTS = new DomSelector[] { new DomSelector(SelectorType.CLASS_NAME,
			"gototop") };

	/**
	 * targetを指定せずにassertViewを実行するテスト.<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：実行したタイムスタンプのフォルダ内にspecifyNoTarget_topPage_WINDOWS_(browser name).pngが生成される<br>
	 * 　　　　　　　仕様通りのresult.jsonと座標用のjsonファイルが生成される<br>
	 * 　　　　　　　RUN_TESTモードの場合、assertViewの比較で一致し、compareResultがtrueとなる
	 */
	@Test
	public void specifyNoTarget() {
		driver.get(URL_TOP_PAGE);

		MrtWebDriverWait wait = new MrtWebDriverWait(driver, 30);
		wait.untilLoad();

		assertionView.assertView("topPage", null, HIDDEN_ELEMENTS);
	}

	/**
	 * bodyタグを指定してassertViewを実行するテスト.<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：実行したタイムスタンプのフォルダ内にspecifyTargetTagBody_topPage_WINDOWS_(browser name).pngが生成される<br>
	 * 　　　　　　　仕様通りのresult.jsonと座標用のjsonファイルが生成される<br>
	 * 　　　　　　　RUN_TESTモードの場合、assertViewの比較で一致し、compareResultがtrueとなる
	 */
	@Test
	public void specifyTargetTagBody() {
		driver.get(URL_TOP_PAGE);

		MrtWebDriverWait wait = new MrtWebDriverWait(driver, 30);
		wait.untilLoad();

		//		CompareTarget[] targets = { new CompareTarget(ScreenArea.of(SelectorType.TAG_NAME, "body")) };
		CompareTarget[] targets = { new CompareTarget(ScreenArea.of(SelectorType.TAG_NAME, "body"), EXCLUDES, true) };
		assertionView.assertView("topPage", targets, HIDDEN_ELEMENTS);
	}

	/**
	 * クラスを指定してbodyのassertViewを実行するテスト.<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：実行したタイムスタンプのフォルダ内にspecifyTargetBodyClass_topPage_WINDOWS_(browser name).pngが生成される<br>
	 * 　　　　　　　仕様通りのresult.jsonと座標用のjsonファイルが生成される<br>
	 * 　　　　　　　RUN_TESTモードの場合、assertViewの比較で一致し、compareResultがtrueとなる
	 */
	@Test
	public void specifyTargetClassBody() {
		driver.get(URL_TOP_PAGE);

		MrtWebDriverWait wait = new MrtWebDriverWait(driver, 30);
		wait.untilLoad();

		// bodyにclass="body"を付加する
		driver.executeJavaScript("document.body.addClassName('body');");

		//		CompareTarget[] targets = { new CompareTarget(ScreenArea.of(SelectorType.CLASS_NAME, "body")) };
		CompareTarget[] targets = { new CompareTarget(ScreenArea.of(SelectorType.CLASS_NAME, "body"), EXCLUDES, true) };
		assertionView.assertView("topPage", targets, HIDDEN_ELEMENTS);
	}

	/**
	 * bodyにmarginがある場合のassertViewを実行するテスト.<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：実行したタイムスタンプのフォルダ内にspecifyTargetBodyWithMargin_topPage_WINDOWS_(browser name).pngが生成される<br>
	 * 　　　　　　　仕様通りのresult.jsonと座標用のjsonファイルが生成される<br>
	 * 　　　　　　　RUN_TESTモードの場合、assertViewの比較で一致し、compareResultがtrueとなる
	 */
	@Test
	public void specifyTargetBodyWithMargin() {
		driver.get(URL_TOP_PAGE);

		MrtWebDriverWait wait = new MrtWebDriverWait(driver, 30);
		wait.untilLoad();

		// bodyにmarginを付加する
		driver.executeJavaScript("document.body.style.margin='10px;'");

		CompareTarget[] targets = { new CompareTarget(ScreenArea.of(SelectorType.TAG_NAME, "body"), EXCLUDES, true) };
		assertionView.assertView("topPage", targets, HIDDEN_ELEMENTS);
	}

	/**
	 * 十分な高さにしてスクロールが出ない状態でbodyのassertViewを実行するテスト.<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：実行したタイムスタンプのフォルダ内にspecifyTargetBodyWithoutScroll_topPage_WINDOWS_(browser name).pngが生成される<br>
	 * 　　　　　　　仕様通りのresult.jsonと座標用のjsonファイルが生成される<br>
	 * 　　　　　　　RUN_TESTモードの場合、assertViewの比較で一致し、compareResultがtrueとなる
	 */
	@Test
	public void specifyTargetBodyWithoutScroll() {
		String platformName = capabilities.getPlatformName();
		if (!"iOS".equals(platformName) && !"Android".equals(platformName)) {
			driver.manage().window().setSize(new Dimension(1280, 2500));
		}

		driver.get(URL_TOP_PAGE);

		MrtWebDriverWait wait = new MrtWebDriverWait(driver, 30);
		wait.untilLoad();

		CompareTarget[] targets = { new CompareTarget(ScreenArea.of(SelectorType.TAG_NAME, "body"), EXCLUDES, true) };
		assertionView.assertView("topPage", targets, HIDDEN_ELEMENTS);
	}
}