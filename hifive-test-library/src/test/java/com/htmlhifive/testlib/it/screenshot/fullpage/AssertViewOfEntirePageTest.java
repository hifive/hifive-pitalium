/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.it.screenshot.fullpage;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriverException;

import com.htmlhifive.testlib.core.MrtTestBase;
import com.htmlhifive.testlib.core.model.CompareTarget;
import com.htmlhifive.testlib.core.model.ScreenArea;
import com.htmlhifive.testlib.core.model.SelectorType;
import com.htmlhifive.testlib.core.selenium.MrtWebDriverWait;

/**
 * ページ全体(body)の比較のテスト
 */
public class AssertViewOfEntirePageTest extends MrtTestBase {

	private static final String URL_TOP_PAGE = "";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

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

		assertionView.assertView("topPage");
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

		CompareTarget[] targets = { new CompareTarget(ScreenArea.of(SelectorType.TAG_NAME, "body")) };
		assertionView.assertView("topPage", targets);
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

		CompareTarget[] targets = { new CompareTarget(ScreenArea.of(SelectorType.CLASS_NAME, "body")) };
		assertionView.assertView("topPage", targets);
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

		CompareTarget[] targets = { new CompareTarget(ScreenArea.of(SelectorType.TAG_NAME, "body")) };
		assertionView.assertView("topPage", targets);
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
		if (!"iOS".equals(platformName) && !"android".equalsIgnoreCase(platformName)) {
			driver.manage().window().setSize(new Dimension(1280, 2500));
		}

		driver.get(URL_TOP_PAGE);

		MrtWebDriverWait wait = new MrtWebDriverWait(driver, 30);
		wait.untilLoad();

		CompareTarget[] targets = { new CompareTarget(ScreenArea.of(SelectorType.TAG_NAME, "body")) };
		assertionView.assertView("topPage", targets);
	}

	/**
	 * driverを閉じたあとにtakeScreenshotを実行するテスト.<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：WebDriverExceptionが発生する
	 * 
	 * @throws IOException
	 */
	@Test
	public void driverQuitTest() throws IOException {
		driver.get(URL_TOP_PAGE);
		driver.close();

		expectedException.expect(WebDriverException.class);
		assertionView.assertView("topPage");
	}
}