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
package com.htmlhifive.testlib.it.screenshot.fullpage;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriverException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.htmlhifive.testlib.core.MrtTestBase;
import com.htmlhifive.testlib.core.model.CompareTarget;
import com.htmlhifive.testlib.core.model.IndexDomSelector;
import com.htmlhifive.testlib.core.model.ScreenArea;
import com.htmlhifive.testlib.core.model.ScreenAreaResult;
import com.htmlhifive.testlib.core.model.ScreenshotResult;
import com.htmlhifive.testlib.core.model.SelectorType;
import com.htmlhifive.testlib.core.model.TargetResult;
import com.htmlhifive.testlib.core.result.TestResultManager;
import com.htmlhifive.testlib.core.selenium.MrtWebDriverWait;
import com.htmlhifive.testlib.image.model.ScreenshotImage;

/**
 * ページ全体(body)のスクリーンショットの取得のテスト
 */
public class TakeEntirePageScreenshotTest extends MrtTestBase {

	private static final String TEST_TOP_PAGE_URL = "";

	private static String currentId = null;

	private static String resultFolderPath;

	@BeforeClass
	public static void beforeClass() throws JsonProcessingException, IOException {
		currentId = TestResultManager.getInstance().getCurrentId();
		resultFolderPath = "results" + File.separator + currentId + File.separator
				+ TakeEntirePageScreenshotTest.class.getSimpleName();
		new File(resultFolderPath).mkdirs();
	}

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	/**
	 * targetを指定せずにtakeScreenshotを実行するテスト.<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：結果オブジェクト(ScrrenshotResult)に想定通りの値が入っている<br>
	 * 　　　　　　　また、全体の画像とbodyの画像が取得でき、それらをpersistすると画像ファイルが生成される。<br>
	 * 　　　　　　　これらの画像の内容は目視で確認を行うこと。
	 * 
	 * @throws IOException
	 */
	@Test
	public void specifyNoTarget() throws IOException {
		driver.get(TEST_TOP_PAGE_URL);

		MrtWebDriverWait wait = new MrtWebDriverWait(driver, 30);
		wait.untilLoad();

		ScreenshotResult result = driver.takeScreenshot("topPage");
		assertScreenshotResult(result, "specifyNoTarget");
	}

	/**
	 * bodyタグを指定してtakeScreenshotを実行するテスト.<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：結果オブジェクト(ScrrenshotResult)に想定通りの値が入っている<br>
	 * 　　　　　　　また、全体の画像とbodyの画像が取得でき、それらをpersistすると画像ファイルが生成される。<br>
	 * 　　　　　　　これらの画像の内容は目視で確認を行うこと。
	 * 
	 * @throws IOException
	 */
	@Test
	public void specifyTargetTagBody() throws IOException {
		driver.get(TEST_TOP_PAGE_URL);

		MrtWebDriverWait wait = new MrtWebDriverWait(driver, 30);
		wait.untilLoad();

		ScreenArea screenArea = ScreenArea.of(SelectorType.TAG_NAME, "body");
		CompareTarget[] targets = { new CompareTarget(screenArea) };

		ScreenshotResult result = driver.takeScreenshot("topPage", targets);
		assertScreenshotResult(result, "specifyTargetTagBody", SelectorType.TAG_NAME, screenArea);
	}

	/**
	 * クラスを指定してbodyのtakeScreenshotを実行するテスト.<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：結果オブジェクト(ScrrenshotResult)に想定通りの値が入っている<br>
	 * 　　　　　　　また、全体の画像とbodyの画像が取得でき、それらをpersistすると画像ファイルが生成される。<br>
	 * 　　　　　　　これらの画像の内容は目視で確認を行うこと。
	 * 
	 * @throws IOException
	 */
	@Test
	public void specifyTargetClassBody() throws IOException {
		driver.get(TEST_TOP_PAGE_URL);

		MrtWebDriverWait wait = new MrtWebDriverWait(driver, 30);
		wait.untilLoad();

		// bodyにclass="body"を付加する
		driver.executeJavaScript("document.body.addClassName('body');");

		ScreenArea screenArea = ScreenArea.of(SelectorType.CLASS_NAME, "body");
		CompareTarget[] targets = { new CompareTarget(screenArea) };
		ScreenshotResult result = driver.takeScreenshot("topPage", targets);
		assertScreenshotResult(result, "specifyTargetClassBody", SelectorType.CLASS_NAME, screenArea);
	}

	/**
	 * bodyにmarginがある場合のtakeScreenshotを実行するテスト.<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：結果オブジェクト(ScrrenshotResult)に想定通りの値が入っている<br>
	 * 　　　　　　　また、全体の画像とbodyの画像が取得でき、それらをpersistすると画像ファイルが生成される。<br>
	 * 　　　　　　　これらの画像の内容は目視で確認を行うこと。
	 * 
	 * @throws IOException
	 */
	@Test
	public void specifyTargetBodyWithMargin() throws IOException {
		driver.get(TEST_TOP_PAGE_URL);

		MrtWebDriverWait wait = new MrtWebDriverWait(driver, 30);
		wait.untilLoad();

		// bodyにmarginを付加する
		driver.executeJavaScript("document.body.style.margin='10px;'");

		ScreenshotResult result = driver.takeScreenshot("topPage");
		assertScreenshotResult(result, "specifyTargetBodyWithMargin");
	}

	/**
	 * 十分な高さにしてスクロールが出ない状態でbodyのtakeScreenshotを実行するテスト.<br>
	 * 前提条件：なし<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：結果オブジェクト(ScrrenshotResult)に想定通りの値が入っている<br>
	 * 　　　　　　　また、全体の画像とbodyの画像が取得でき、それらをpersistすると画像ファイルが生成される。<br>
	 * 　　　　　　　これらの画像の内容は目視で確認を行うこと。
	 * 
	 * @throws IOException
	 */
	@Test
	public void specifyTargetBodyWithoutScroll() throws IOException {
		String platformName = capabilities.getPlatformName();
		if (!"iOS".equals(platformName) && !"android".equalsIgnoreCase(platformName)) {
			driver.manage().window().setSize(new Dimension(1280, 2500));
		}

		driver.get(TEST_TOP_PAGE_URL);

		MrtWebDriverWait wait = new MrtWebDriverWait(driver, 30);
		wait.untilLoad();

		ScreenshotResult result = driver.takeScreenshot("topPage");
		assertScreenshotResult(result, "specifyTargetBodyWithoutScroll");
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
		driver.get(TEST_TOP_PAGE_URL);
		driver.close();

		expectedException.expect(WebDriverException.class);
		driver.takeScreenshot("topPage");
	}

	private void assertScreenshotResult(ScreenshotResult result, String methodName) throws IOException {
		SelectorType tag = SelectorType.TAG_NAME;
		ScreenArea scrrenArea = ScreenArea.of(tag, "body");
		assertScreenshotResult(result, methodName, tag, scrrenArea);
	}

	private void assertScreenshotResult(ScreenshotResult result, String methodName, SelectorType selectorType,
			ScreenArea screenArea) throws IOException {
		// resultの中身のassert
		assertThat(result.getScreenshotId(), is("topPage"));
		assertNull(result.getResult());
		assertNull(result.getExpectedId());
		assertNull(result.getTestClass());
		assertNull(result.getTestMethod());

		TargetResult targetResult = result.getTargetResults().get(0);
		assertNull(targetResult.getResult());
		assertTrue(targetResult.getExcludes().isEmpty());
		assertFalse(targetResult.isMoveTarget());
		assertTrue(targetResult.getHiddenElementSelectors().isEmpty());
		assertNull(targetResult.getOptions());

		ScreenAreaResult target = targetResult.getTarget();
		IndexDomSelector expectedSelector = new IndexDomSelector(selectorType, "body", 0);
		assertThat(target.getSelector(), is(expectedSelector));
		assertEquals(screenArea, target.getScreenArea());

		ScreenshotImage bodyImage = targetResult.getImage();
		ImageIO.write(bodyImage.get(), "png", new File(getFileName(methodName, SelectorType.TAG_NAME) + ".png"));

		assertNull(result.getCapabilities());

		ScreenshotImage image = result.getEntireScreenshotImage();
		ImageIO.write(image.get(), "png", new File(getFileName(methodName) + ".png"));
	}

	private String getFileName(String methodName) {
		List<String> strs = new ArrayList<String>();
		strs.add(resultFolderPath + File.separator + methodName);
		strs.add("topPage");
		if (capabilities.getPlatform() != null) {
			strs.add(capabilities.getPlatform().name());
		} else {
			strs.add(capabilities.getPlatformName());
		}
		if (!StringUtils.isEmpty(capabilities.getPlatformVersion())) {
			strs.add(capabilities.getPlatformVersion());
		}
		strs.add(capabilities.getBrowserName());
		if (!StringUtils.isEmpty(capabilities.getVersion())) {
			strs.add(capabilities.getVersion());
		}

		return StringUtils.join(strs, "_");
	}

	private String getFileName(String methodName, SelectorType selectorType) {
		return getFileName(methodName) + "_" + selectorType.name() + "_body_[0]";
	}
}
