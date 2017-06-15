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
package com.htmlhifive.pitalium.it.assertion.exclude;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.config.ExecMode;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import com.htmlhifive.pitalium.core.model.CompareTarget;
import com.htmlhifive.pitalium.core.model.DomSelector;
import com.htmlhifive.pitalium.core.model.ScreenArea;
import com.htmlhifive.pitalium.core.model.SelectorType;
import com.htmlhifive.pitalium.core.result.TestResultManager;
import com.htmlhifive.pitalium.core.selenium.PtlWebDriverWait;

/**
 * excludeのテスト
 */
public class CompareWithExcludesTest extends PtlTestBase {

	private static final String URL_TOP_PAGE = PtlTestConfig.getInstance().getTestAppConfig().getBaseUrl();

	private static final ScreenArea[] EXCLUDES = new ScreenArea[] { ScreenArea.of(SelectorType.CLASS_NAME,
			"fb-like-box") };

	private static final DomSelector[] HIDDEN_ELEMENTS = new DomSelector[] { new DomSelector(SelectorType.CLASS_NAME,
			"gototop") };

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	/**
	 * excludeを指定しないとdiffが出ることの確認のテスト<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4, 5.1/iOS 8.1, 8.3<br>
	 * 期待結果：assertViewでテストが失敗する
	 */
	@Test
	public void checkFailure() {
		driver.get(URL_TOP_PAGE);

		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		if (PtlTestConfig.getInstance().getEnvironment().getExecMode() == ExecMode.RUN_TEST) {
			driver.executeJavaScript("document.getElementsByClassName('fb-like-box')[0].style.backgroundColor='red'");
			expectedException.expect(AssertionError.class);
		}

		assertionView.assertView("topPage", null, HIDDEN_ELEMENTS);
	}

	/**
	 * bodyをtargetにexcludeを指定するテスト.<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4, 5.1/iOS 8.1, 8.3<br>
	 * 期待結果：テストに成功する。jsonファイルにexcludeの情報が出力される
	 */
	@Test
	public void excludeForBody() {
		driver.get(URL_TOP_PAGE);

		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		if (PtlTestConfig.getInstance().getEnvironment().getExecMode() == ExecMode.RUN_TEST) {
			driver.executeJavaScript("document.getElementsByClassName('fb-like-box')[0].style.backgroundColor='red'");
		}

		CompareTarget[] targets = { new CompareTarget(ScreenArea.of(SelectorType.TAG_NAME, "body"), EXCLUDES, true) };
		assertionView.assertView("topPage", targets, HIDDEN_ELEMENTS);
	}

	/**
	 * bodyにmarginがある場合にexcludeを指定するテスト<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4, 5.1/iOS 8.1, 8.3<br>
	 * 期待結果：テストに成功する
	 */
	@Test
	public void excludeForBodyWithMargin() {
		driver.get(URL_TOP_PAGE);

		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		// bodyにmarginを付加する
		driver.executeJavaScript("document.body.style.margin='100px'");

		if (PtlTestConfig.getInstance().getEnvironment().getExecMode() == ExecMode.RUN_TEST) {
			driver.executeJavaScript("document.getElementsByClassName('fb-like-box')[0].style.backgroundColor='red'");
		}

		CompareTarget[] targets = { new CompareTarget(ScreenArea.of(SelectorType.TAG_NAME, "body"), EXCLUDES, true) };
		assertionView.assertView("topPage", targets, HIDDEN_ELEMENTS);
	}

	/**
	 * 十分な高さにしてスクロールが出ない状態でexcludeを指定するテスト<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4, 5.1/iOS 8.1, 8.3<br>
	 * 期待結果：テストに成功する
	 */
	@Test
	public void excludeForBodyWithoutScroll() {
		String platformName = capabilities.getPlatformName();
		if (!"ios".equalsIgnoreCase(platformName) && !"android".equalsIgnoreCase(platformName)) {
			driver.manage().window().setSize(new Dimension(1280, 2500));
		}

		driver.get(URL_TOP_PAGE);

		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		if (PtlTestConfig.getInstance().getEnvironment().getExecMode() == ExecMode.RUN_TEST) {
			wait.until(ExpectedConditions.presenceOfElementLocated(By.className("fb-like-box")));
			driver.executeJavaScript("document.getElementsByClassName('fb-like-box')[0].style.backgroundColor='red'");
		}

		CompareTarget[] targets = { new CompareTarget(ScreenArea.of(SelectorType.TAG_NAME, "body"), EXCLUDES, true) };
		assertionView.assertView("topPage", targets, HIDDEN_ELEMENTS);
	}

	/**
	 * bodyでないtargetの要素の内部の要素をexcludeするテスト<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4, 5.1/iOS 8.1, 8.3<br>
	 * 期待結果：diffが発生する
	 */
	@Test
	public void excludeElementInTarget() {
		driver.get(URL_TOP_PAGE);

		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		if (PtlTestConfig.getInstance().getEnvironment().getExecMode() == ExecMode.RUN_TEST) {
			driver.executeJavaScript("document.getElementsByClassName('fb-like-box')[0].style.backgroundColor='blue'");
		}

		CompareTarget[] targets = { new CompareTarget(ScreenArea.of(SelectorType.ID, "wrapper"), EXCLUDES, true) };

		assertionView.assertView("topPage", targets, HIDDEN_ELEMENTS);
	}

	/**
	 * 異なる位置の要素をexclude対象とすると、エラーになるテスト<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4, 5.1/iOS 8.1, 8.3<br>
	 * 期待結果：diffが発生する
	 */
	@Test
	public void excludeDifferentPositionElement() {
		driver.get(URL_TOP_PAGE);

		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		if (PtlTestConfig.getInstance().getEnvironment().getExecMode() == ExecMode.RUN_TEST) {
			driver.executeJavaScript("document.getElementsByClassName('fb-like-box')[0].style.backgroundColor='red'");
			driver.executeJavaScript("document.getElementById('wrapper').style.textAlign='left'");
			expectedException.expect(AssertionError.class);
		}

		CompareTarget[] targets = { new CompareTarget(ScreenArea.of(SelectorType.TAG_NAME, "body"), EXCLUDES, true) };

		assertionView.assertView("topPage", targets, HIDDEN_ELEMENTS);
	}

	@AfterClass
	public static void saveExpectedId() throws IOException {
		if (PtlTestConfig.getInstance().getEnvironment().getExecMode() != ExecMode.SET_EXPECTED) {
			return;
		}

		File file = new File(PtlTestConfig.getInstance().getPersisterConfig().getFile().getResultDirectory()
				+ File.separator + CompareWithExcludesTest.class.getSimpleName() + ".json");

		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter pw = new PrintWriter(bw);

		pw.print(TestResultManager.getInstance().getCurrentId());
		pw.close();
	}
}