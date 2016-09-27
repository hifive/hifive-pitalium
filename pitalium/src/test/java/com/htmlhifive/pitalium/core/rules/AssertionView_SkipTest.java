package com.htmlhifive.pitalium.core.rules;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

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
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.htmlhifive.pitalium.core.config.ExecMode;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import com.htmlhifive.pitalium.core.config.WebDriverSessionLevel;
import com.htmlhifive.pitalium.core.model.IndexDomSelector;
import com.htmlhifive.pitalium.core.model.ScreenArea;
import com.htmlhifive.pitalium.core.model.ScreenAreaResult;
import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.core.model.ScreenshotResult;
import com.htmlhifive.pitalium.core.model.SelectorType;
import com.htmlhifive.pitalium.core.model.TargetResult;
import com.htmlhifive.pitalium.core.result.TestResultManager;
import com.htmlhifive.pitalium.core.selenium.PtlCapabilities;
import com.htmlhifive.pitalium.core.selenium.PtlWebDriver;
import com.htmlhifive.pitalium.core.selenium.PtlWebDriverFactory;
import com.htmlhifive.pitalium.image.model.RectangleArea;
import com.htmlhifive.pitalium.image.model.ScreenshotImage;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PtlTestConfig.class, TestResultManager.class, PtlWebDriverFactory.class })
public class AssertionView_SkipTest {

	@Mock(
			answer = Answers.RETURNS_DEEP_STUBS)
	public PtlWebDriverFactory driverFactory;

	@Mock(
			answer = Answers.RETURNS_DEEP_STUBS)
	public PtlWebDriver driver;

	@Mock(
			answer = Answers.RETURNS_DEEP_STUBS)
	public TestResultManager testResultManager;

	@Mock(
			answer = Answers.RETURNS_DEEP_STUBS)
	public PtlTestConfig testConfig;

	ExpectedException expectedException = ExpectedException.none();
	AssertionView assertionView = new AssertionView();

	@Rule
	public RuleChain chain = RuleChain.outerRule(expectedException).around(new TestWatcher() {
		@Override
		protected void succeeded(Description description) {
			MockitoAnnotations.initMocks(AssertionView_SkipTest.this);

			mockStatic(PtlTestConfig.class);
			when(PtlTestConfig.getInstance()).thenReturn(testConfig);
			when(testConfig.getEnvironment().getWebDriverSessionLevel()).thenReturn(WebDriverSessionLevel.TEST_CASE);

			mockStatic(PtlWebDriverFactory.class);
			when(PtlWebDriverFactory.getInstance(any(PtlCapabilities.class))).thenReturn(driverFactory);
			when(driverFactory.getDriver()).thenReturn(driver);

			mockStatic(TestResultManager.class);
			when(TestResultManager.getInstance()).thenReturn(testResultManager);
			when(testResultManager.getCurrentId()).thenReturn("2016_01_01_01_01_01");

			try {
				BufferedImage image = ImageIO.read(getClass().getClassLoader().getResource("images/hifive_logo.png"));
				screenshotResult = createScreenshotResult(image);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}
	}).around(assertionView);

	@Rule
	public TestName testName = new TestName();

	ScreenshotResult screenshotResult;

	@Before
	public void initializeAssertionView() throws Exception {
		assertionView.createDriver(new PtlCapabilities(new HashMap<String, Object>()));
	}

	/**
	 * assertViewをSKIPモードで実行し、takeScreenshotが呼ばれていないことを確認
	 * 
	 * @throws Exception
	 */
	@Test
	public void assertView_skip() throws Exception {
		when(testConfig.getEnvironment().getExecMode()).thenReturn(ExecMode.SKIP);
		when(driver.takeScreenshot(anyString(), any(List.class), any(List.class)))
				.thenReturn(screenshotResult);

		ScreenshotArgument arg = ScreenshotArgument.builder("ssid").build();
		assertionView.assertView(arg);

		verify(driver, times(0)).takeScreenshot(eq("ssid"), any(List.class), any(List.class));

	}

	/**
	 * verifyViewをSKIPモードで実行し、takeScreenshotが呼ばれていないことを確認
	 * 
	 * @throws Exception
	 */
	@Test
	public void verifyView_skip() throws Exception {
		when(testConfig.getEnvironment().getExecMode()).thenReturn(ExecMode.SKIP);
		when(driver.takeScreenshot(anyString(), any(List.class), any(List.class)))
				.thenReturn(screenshotResult);

		ScreenshotArgument arg = ScreenshotArgument.builder("ssid").build();
		assertionView.verifyView(arg);

		verify(driver, times(0)).takeScreenshot(eq("ssid"), any(List.class), any(List.class));
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
