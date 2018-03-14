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

package com.htmlhifive.pitalium.core.rules;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;
import org.junit.rules.TestName;
import org.junit.rules.TestWatcher;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.htmlhifive.pitalium.core.config.ExecMode;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import com.htmlhifive.pitalium.core.config.WebDriverSessionLevel;
import com.htmlhifive.pitalium.core.io.PersistMetadata;
import com.htmlhifive.pitalium.core.model.IndexDomSelector;
import com.htmlhifive.pitalium.core.model.ScreenArea;
import com.htmlhifive.pitalium.core.model.ScreenAreaResult;
import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.core.model.ScreenshotResult;
import com.htmlhifive.pitalium.core.model.SelectorType;
import com.htmlhifive.pitalium.core.model.TargetResult;
import com.htmlhifive.pitalium.core.result.TestResultManager;
import com.htmlhifive.pitalium.core.selenium.PtlCapabilities;
import com.htmlhifive.pitalium.core.selenium.PtlWebDriverFactory;
import com.htmlhifive.pitalium.image.model.RectangleArea;
import com.htmlhifive.pitalium.image.model.ScreenshotImage;

@SuppressWarnings("all")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PtlTestConfig.class, TestResultManager.class, PtlWebDriverFactory.class })
public class AssertionView_VerifyTest {

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	public PtlWebDriverFactory driverFactory;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	public TestResultManager testResultManager;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	public PtlTestConfig testConfig;

	ExpectedException expectedException = ExpectedException.none();
	AssertionView assertionView = new AssertionView();

	@Rule
	public RuleChain chain = RuleChain.outerRule(expectedException).around(new TestWatcher() {
	}).around(assertionView);

	@Rule
	public TestName testName = new TestName();

	ScreenshotResult screenshotResult;

	@Before
	public void initializeAssertionView() throws Exception {
		MockitoAnnotations.initMocks(AssertionView_VerifyTest.this);
		try {
			BufferedImage image = ImageIO.read(getClass().getResource("images/hifive_logo.png"));
			screenshotResult = createScreenshotResult(image);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		mockStatic(PtlWebDriverFactory.class);
		when(PtlWebDriverFactory.getInstance(any(PtlCapabilities.class))).thenReturn(driverFactory);
		when(driverFactory.getDriver().takeScreenshot(anyString(), any(List.class), any(List.class)))
				.thenReturn(screenshotResult);
		when(driverFactory.getDriver().takeScreenshot(anyString())).thenReturn(screenshotResult);

		mockStatic(TestResultManager.class);
		when(TestResultManager.getInstance()).thenReturn(testResultManager);
		when(testResultManager.getCurrentId()).thenReturn("2015_01_01_01_01_01");

		mockStatic(PtlTestConfig.class);
		when(PtlTestConfig.getInstance()).thenReturn(testConfig);
		when(testConfig.getEnvironment().getExecMode()).thenReturn(ExecMode.RUN_TEST);
		when(testConfig.getEnvironment().getWebDriverSessionLevel()).thenReturn(WebDriverSessionLevel.TEST_CASE);
		when(testConfig.getComparisonConfig().getOptions()).thenReturn(new ArrayList<>());

		assertionView.createDriver(new PtlCapabilities(new HashMap<String, Object>()));
	}

	/**
	 * verifyViewをSET_EXPECTEDモードで実行するのみ
	 */
	@Test
	public void verifyView_expected() throws Exception {
		when(testConfig.getEnvironment().getExecMode()).thenReturn(ExecMode.SET_EXPECTED);

		assertionView.verifyView(ScreenshotArgument.builder("ssid").build());
		assertTrue(true);
	}

	/**
	 * verifyViewをRUN_TESTモードで実行 => 検証成功
	 */
	@Test
	public void verifyView_runTest_success() throws Exception {
		when(testResultManager.getPersister().loadTargetResults(any(PersistMetadata.class)))
				.thenReturn(screenshotResult.getTargetResults());

		assertionView.verifyView(ScreenshotArgument.builder("ssid").build());
		assertTrue(true);
	}

	/**
	 * verifyViewをRUN_TESTモードで実行 => 検証失敗
	 */
	@Test
	public void verifyView_runTest_failed() throws Exception {
		BufferedImage image = ImageIO.read(getClass().getResource("images/hifive_logo_part.png"));

		when(testResultManager.getPersister().loadTargetResults(any(PersistMetadata.class)))
				.thenReturn(createScreenshotResult(image).getTargetResults());

		expectedException.expect(AssertionError.class);
		expectedException.expectMessage(startsWith("Verified 1 errors"));

		assertionView.verifyView(ScreenshotArgument.builder("ssid").build());
		assertTrue(true);
	}

	/**
	 * 個別に撮影したScreenshotと保存済みScreenshotの比較 => 検証成功
	 */
	@Test
	public void verifyScreenshot_success() throws Exception {
		when(testResultManager.getPersister().loadTargetResults(any(PersistMetadata.class)))
				.thenReturn(screenshotResult.getTargetResults());

		assertionView.verifyScreenshot(screenshotResult);
		assertTrue(true);
	}

	/**
	 * 個別に撮影したScreenshotと保存済みScreenshotの比較 => 検証失敗
	 */
	@Test
	public void verifyScreenshot_failed() throws Exception {
		BufferedImage image = ImageIO.read(getClass().getResource("images/hifive_logo_part.png"));

		when(testResultManager.getPersister().loadTargetResults(any(PersistMetadata.class)))
				.thenReturn(createScreenshotResult(image).getTargetResults());

		expectedException.expect(AssertionError.class);
		expectedException.expectMessage(startsWith("Verified 1 errors"));

		assertionView.verifyScreenshot(screenshotResult);
		assertTrue(true);
	}

	/**
	 * 個別の画像と撮影したScreenshotの比較 => 検証成功
	 */
	@Test
	public void verifyExists_success() throws Exception {
		BufferedImage image = ImageIO.read(getClass().getResource("images/hifive_logo_part.png"));

		assertionView.verifyExists(image);
		assertTrue(true);
	}

	/**
	 * 個別の画像と撮影したScreenshotの比較 => 検証失敗
	 */
	@Test
	public void verifyExists_failed() throws Exception {
		BufferedImage image = ImageIO.read(getClass().getResource("images/hifive_logo_not_part.png"));

		expectedException.expect(AssertionError.class);
		expectedException.expectMessage(startsWith("Verified 1 errors"));

		assertionView.verifyExists(image);
		assertTrue(true);
	}

	private ScreenshotResult createScreenshotResult(BufferedImage image) {
		ScreenAreaResult screenAreaResult = new ScreenAreaResult(new IndexDomSelector(SelectorType.TAG_NAME, "body", 0),
				new RectangleArea(0, 0, image.getWidth(), image.getHeight()),
				ScreenArea.of(SelectorType.TAG_NAME, "body"));
		TargetResult targetResult = new TargetResult(screenAreaResult, new ArrayList<ScreenAreaResult>(),
				new ScreenshotImage(image));

		return new ScreenshotResult("ssid", null, null, Collections.singletonList(targetResult),
				getClass().getSimpleName(), testName.getMethodName(), new HashMap<String, Object>(),
				new ScreenshotImage(image));
	}

}