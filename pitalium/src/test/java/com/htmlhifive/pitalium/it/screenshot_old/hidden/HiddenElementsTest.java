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
package com.htmlhifive.pitalium.it.screenshot_old.hidden;

import org.junit.Test;

import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.model.DomSelector;
import com.htmlhifive.pitalium.core.model.SelectorType;
import com.htmlhifive.pitalium.core.selenium.PtlWebDriverWait;

/**
 * ページ全体(body)の比較のテスト
 */
public class HiddenElementsTest extends PtlTestBase {

	private static final String URL_TOP_PAGE = "";

	/**
	 * hiddenElementsを指定するテスト<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：指定した要素が現れないスクリーンショットが撮れる。
	 */
	@Test
	public void specifyHiddenElement() {
		driver.get(URL_TOP_PAGE);

		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		assertionView.assertView("topPage", null, new DomSelector[] { new DomSelector(SelectorType.CLASS_NAME,
				"gototop") });
	}

	/**
	 * hiddenElementsを複数指定するテスト<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：指定した要素が現れないスクリーンショットが撮れる。
	 */
	@Test
	public void specifyMultipleHiddenElements() {
		driver.get(URL_TOP_PAGE);

		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		DomSelector[] hiddenElements = new DomSelector[] { new DomSelector(SelectorType.CLASS_NAME, "gototop"),
				new DomSelector(SelectorType.ID, "about") };

		assertionView.assertView("topPage", null, hiddenElements);
	}

	/**
	 * 1つのセレクタで複数のhiddenElementsを指定するテスト<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：指定した要素が現れないスクリーンショットが撮れる。
	 */
	@Test
	public void specifyHiddenMultipleElements() {
		driver.get(URL_TOP_PAGE);

		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		DomSelector[] hiddenElements = new DomSelector[] { new DomSelector(SelectorType.CLASS_NAME, "putCursol") };

		assertionView.assertView("topPage", null, hiddenElements);
	}

	/**
	 * 存在しない要素をhiddenElementsで指定するテスト<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：スクリーンショットが撮れる
	 */
	@Test
	public void specifyNotExistingHiddenElement() {
		driver.get(URL_TOP_PAGE);

		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		DomSelector[] hiddenElements = new DomSelector[] { new DomSelector(SelectorType.CLASS_NAME, "dummyClass") };

		assertionView.assertView("topPage", null, hiddenElements);
	}
}