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

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.htmlhifive.pitalium.common.exception.TestRuntimeException;
import com.htmlhifive.pitalium.common.util.JSONUtils;
import com.htmlhifive.pitalium.core.config.ExecMode;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import com.htmlhifive.pitalium.core.io.PersistMetadata;
import com.htmlhifive.pitalium.core.io.Persister;
import com.htmlhifive.pitalium.core.model.CompareTarget;
import com.htmlhifive.pitalium.core.model.DomSelector;
import com.htmlhifive.pitalium.core.model.ExecResult;
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
import com.htmlhifive.pitalium.core.selenium.PtlWebDriverManager;
import com.htmlhifive.pitalium.image.model.DiffPoints;
import com.htmlhifive.pitalium.image.model.ImageComparedResult;
import com.htmlhifive.pitalium.image.model.RectangleArea;
import com.htmlhifive.pitalium.image.model.ScreenshotImage;
import com.htmlhifive.pitalium.image.util.ImageUtils;

/**
 * スクリーンショットのassert機能を持ちます。<br/>
 * また、テストメソッド毎に以下の共通処理を行う&#064;Rule用クラスです。<br/>
 * <ul>
 * <li>テスト成功時：期待結果IDの更新</li>
 * <li>テスト終了時：WebDriverのquit</li>
 * </ul>
 * {@link com.htmlhifive.pitalium.core.PtlTestBase}を拡張した場合は、既に定義済みのため指定する必要はありません。
 */
public class AssertionView extends TestWatcher {

	private static final Logger LOG = LoggerFactory.getLogger(AssertionView.class);

	private static final Function<ScreenAreaResult, Rectangle> SCREEN_AREA_RESULT_TO_RECTANGLE_FUNCTION = new Function<ScreenAreaResult, Rectangle>() {
		@Override
		public Rectangle apply(ScreenAreaResult input) {
			return input.getRectangle().toRectangle();
		}
	};

	/**
	 * {@link PtlWebDriver}のコンテナ。<br>
	 * WebDriverの生成と、適切なタイミングでのquitを実行します。
	 */
	protected PtlWebDriverManager.WebDriverContainer webDriverContainer;
	/**
	 * スクリーンショット撮影に用いるWebDriver
	 */
	protected PtlWebDriver driver;

	private final Set<String> screenshotIds = new HashSet<String>();

	private Description description;
	private String className;
	private String methodName;
	private String currentId;

	private PtlCapabilities capabilities;

	private final List<ScreenshotResult> results = new ArrayList<ScreenshotResult>();
	private final List<AssertionError> verifyErrors = new ArrayList<AssertionError>();

	//<editor-fold desc="Watcher methods">

	@Override
	protected void starting(Description desc) {
		LOG.info("[Testcase start] (name: {})", desc.getDisplayName());

		description = desc;
		className = getClassName(desc);
		methodName = getMethodName(desc);

		currentId = TestResultManager.getInstance().getCurrentId();
	}

	@Override
	protected void failed(Throwable e, Description desc) {
		if (e instanceof AssertionError) {
			LOG.info("[Testcase failed] (assertion error)", e);
		} else if (e instanceof WebDriverException) {
			LOG.error("[Testcase failed] (selenium error)", e);
		} else if (e instanceof TestRuntimeException) {
			LOG.error("[Testcase failed] (pitalium runtime error)", e);
		} else {
			LOG.error("[Testcase failed] (unhandled error)", e);
		}

		TestResultManager.getInstance().cancelUpdateExpectedId(className);
	}

	@Override
	protected void succeeded(Description desc) {
		// テストに成功したらExpectedIdを更新
		if (verifyErrors.isEmpty()) {
			LOG.info("[Testcase succeeded]");
			TestResultManager.getInstance().updateExpectedId(className, methodName);
			return;
		}

		LOG.info("[Testcase failed] (verified {} errors)", verifyErrors.size());
		TestResultManager.getInstance().cancelUpdateExpectedId(className);
		String errors = StringUtils
				.join(FluentIterable.from(verifyErrors).transform(new Function<AssertionError, String>() {
					@Override
					public String apply(AssertionError error) {
						return error.getMessage();
					}
				}).filter(new Predicate<String>() {
					@Override
					public boolean apply(String message) {
						return !Strings.isNullOrEmpty(message);
					}
				}), "\n");
		throw new AssertionError(String.format(Locale.US, "Verified %d errors: %s", verifyErrors.size(), errors));
	}

	@Override
	protected void finished(Description desc) {
		LOG.info("[Testcase finished] (name: {})", desc.getDisplayName());

		if (webDriverContainer != null) {
			webDriverContainer.quit();
			driver = null;
		}

		if (results.isEmpty()) {
			return;
		}

		synchronized (AssertionView.class) {
			TestResultManager resultManager = TestResultManager.getInstance();
			for (ScreenshotResult result : results) {
				resultManager.addScreenshotResult(className, result);
			}
		}
	}

	//</editor-fold>

	/**
	 * {@link org.openqa.selenium.Capabilities}に応じた{@link PtlWebDriver}を作成して返します。
	 *
	 * @param cap ブラウザスペック情報
	 * @return ブラウザに対応するWebDriver
	 */
	public PtlWebDriver createDriver(PtlCapabilities cap) {
		if (driver != null) {
			if (!capabilities.equals(cap)) {
				throw new TestRuntimeException("Capabilities not match");
			}
			return driver;
		}

		capabilities = cap;
		webDriverContainer = PtlWebDriverManager.getInstance().getWebDriver(description.getTestClass(), cap,
				new Supplier<WebDriver>() {
					@Override
					public PtlWebDriver get() {
						return PtlWebDriverFactory.getInstance(capabilities).getDriver();
					}
				});

		driver = webDriverContainer.get();
		LOG.debug("[Get WebDriver] Use session ({})", driver);
		return driver;
	}

	//<editor-fold desc="assertView">

	/**
	 * 指定の条件でスクリーンショットを撮影します。テスト実行モードが{@link com.htmlhifive.pitalium.core.config.ExecMode#SET_EXPECTED}の時は
	 * 正解状態としてスクリーンショットの画像と座標を保存します。 テスト実行モードが {@link com.htmlhifive.pitalium.core.config.ExecMode#RUN_TEST}の時は、
	 * {@link com.htmlhifive.pitalium.core.config.ExecMode#SET_EXPECTED}で撮影した状態と比較します。
	 *
	 * @param screenshotId スクリーンショットを識別するID
	 */
	public void assertView(String screenshotId) {
		assertView(null, screenshotId);
	}

	/**
	 * 指定の条件でスクリーンショットを撮影します。テスト実行モードが{@link com.htmlhifive.pitalium.core.config.ExecMode#SET_EXPECTED}の時は
	 * 正解状態としてスクリーンショットの画像と座標を保存します。 テスト実行モードが {@link com.htmlhifive.pitalium.core.config.ExecMode#RUN_TEST}の時は、
	 * {@link com.htmlhifive.pitalium.core.config.ExecMode#SET_EXPECTED}で撮影した状態と比較します。
	 *
	 * @param message {@link AssertionError}を識別する文字列
	 * @param screenshotId スクリーンショットを識別するID
	 */
	public void assertView(String message, String screenshotId) {
		assertView(message, screenshotId, asList(new CompareTarget(ScreenArea.of(SelectorType.TAG_NAME, "body"))),
				null);
	}

	/**
	 * 指定の条件でスクリーンショットを撮影します。テスト実行モードが{@link com.htmlhifive.pitalium.core.config.ExecMode#SET_EXPECTED}の時は
	 * 正解状態としてスクリーンショットの画像と座標を保存します。 テスト実行モードが {@link com.htmlhifive.pitalium.core.config.ExecMode#RUN_TEST}の時は、
	 * {@link com.htmlhifive.pitalium.core.config.ExecMode#SET_EXPECTED}で撮影した状態と比較します。
	 *
	 * @param screenshotId スクリーンショットを識別するID
	 * @param compareTargets スクリーンショットの撮影、比較条件
	 */
	public void assertView(String screenshotId, CompareTarget[] compareTargets) {
		assertView(screenshotId, compareTargets, null);
	}

	/**
	 * 指定の条件でスクリーンショットを撮影します。テスト実行モードが{@link com.htmlhifive.pitalium.core.config.ExecMode#SET_EXPECTED}の時は
	 * 正解状態としてスクリーンショットの画像と座標を保存します。 テスト実行モードが {@link com.htmlhifive.pitalium.core.config.ExecMode#RUN_TEST}の時は、
	 * {@link com.htmlhifive.pitalium.core.config.ExecMode#SET_EXPECTED}で撮影した状態と比較します。
	 *
	 * @param screenshotId スクリーンショットを識別するID
	 * @param compareTargets スクリーンショットの撮影、比較条件
	 */
	public void assertView(String screenshotId, List<CompareTarget> compareTargets) {
		assertView(null, screenshotId, compareTargets, null);
	}

	/**
	 * 指定の条件でスクリーンショットを撮影します。テスト実行モードが{@link com.htmlhifive.pitalium.core.config.ExecMode#SET_EXPECTED}の時は
	 * 正解状態としてスクリーンショットの画像と座標を保存します。 テスト実行モードが {@link com.htmlhifive.pitalium.core.config.ExecMode#RUN_TEST}の時は、
	 * {@link com.htmlhifive.pitalium.core.config.ExecMode#SET_EXPECTED}で撮影した状態と比較します。
	 *
	 * @param message {@link AssertionError}を識別する文字列
	 * @param screenshotId スクリーンショットを識別するID
	 * @param compareTargets スクリーンショットの撮影、比較条件
	 */
	public void assertView(String message, String screenshotId, List<CompareTarget> compareTargets) {
		assertView(message, screenshotId, compareTargets, null);
	}

	/**
	 * 指定の条件でスクリーンショットを撮影します。テスト実行モードが{@link com.htmlhifive.pitalium.core.config.ExecMode#SET_EXPECTED}の時は
	 * 正解状態としてスクリーンショットの画像と座標を保存します。 テスト実行モードが {@link com.htmlhifive.pitalium.core.config.ExecMode#RUN_TEST}の時は、
	 * {@link com.htmlhifive.pitalium.core.config.ExecMode#SET_EXPECTED}で撮影した状態と比較します。
	 *
	 * @param screenshotId スクリーンショットを識別するID
	 * @param compareTargets スクリーンショットの撮影、比較条件
	 * @param hiddenElementsSelectors スクリーンショット撮影時に非表示にするDOMを表すセレクターのコレクション
	 */
	public void assertView(String screenshotId, CompareTarget[] compareTargets, DomSelector[] hiddenElementsSelectors) {
		assertView(null, screenshotId, asList(compareTargets), asList(hiddenElementsSelectors));
	}

	/**
	 * 指定の条件でスクリーンショットを撮影します。テスト実行モードが{@link com.htmlhifive.pitalium.core.config.ExecMode#SET_EXPECTED}の時は
	 * 正解状態としてスクリーンショットの画像と座標を保存します。 テスト実行モードが {@link com.htmlhifive.pitalium.core.config.ExecMode#RUN_TEST}の時は、
	 * {@link com.htmlhifive.pitalium.core.config.ExecMode#SET_EXPECTED}で撮影した状態と比較します。
	 *
	 * @param screenshotId スクリーンショットを識別するID
	 * @param compareTargets スクリーンショットの撮影、比較条件
	 * @param hiddenElementsSelectors スクリーンショット撮影時に非表示にするDOMを表すセレクターのコレクション
	 */
	public void assertView(String screenshotId, List<CompareTarget> compareTargets,
			List<DomSelector> hiddenElementsSelectors) {
		assertView(null, screenshotId, compareTargets, hiddenElementsSelectors);
	}

	/**
	 * 指定の条件でスクリーンショットを撮影します。テスト実行モードが{@link com.htmlhifive.pitalium.core.config.ExecMode#SET_EXPECTED}の時は
	 * 正解状態としてスクリーンショットの画像と座標を保存します。 テスト実行モードが {@link com.htmlhifive.pitalium.core.config.ExecMode#RUN_TEST}の時は、
	 * {@link com.htmlhifive.pitalium.core.config.ExecMode#SET_EXPECTED}で撮影した状態と比較します。
	 *
	 * @param arg スクリーンショットを撮影するための条件
	 */
	public void assertView(ScreenshotArgument arg) {
		assertView(null, arg);
	}

	/**
	 * 指定の条件でスクリーンショットを撮影します。テスト実行モードが{@link com.htmlhifive.pitalium.core.config.ExecMode#SET_EXPECTED}の時は
	 * 正解状態としてスクリーンショットの画像と座標を保存します。 テスト実行モードが {@link com.htmlhifive.pitalium.core.config.ExecMode#RUN_TEST}の時は、
	 * {@link com.htmlhifive.pitalium.core.config.ExecMode#SET_EXPECTED}で撮影した状態と比較します。
	 *
	 * @param message {@link AssertionError}を識別する文字列
	 * @param arg スクリーンショットを撮影するための条件
	 */
	public void assertView(String message, ScreenshotArgument arg) {
		assertView(message, arg.getScreenshotId(), arg.getTargets(), arg.getHiddenElementSelectors());
	}

	/**
	 * 指定の条件でスクリーンショットを撮影します。テスト実行モードが{@link com.htmlhifive.pitalium.core.config.ExecMode#SET_EXPECTED}の時は
	 * 正解状態としてスクリーンショットの画像と座標を保存します。 テスト実行モードが {@link com.htmlhifive.pitalium.core.config.ExecMode#RUN_TEST}の時は、
	 * {@link com.htmlhifive.pitalium.core.config.ExecMode#SET_EXPECTED}で撮影した状態と比較します。
	 *
	 * @param message {@link AssertionError}を識別する文字列
	 * @param screenshotId スクリーンショットを識別するID
	 * @param compareTargets スクリーンショットの撮影、比較条件
	 * @param hiddenElementsSelectors スクリーンショット撮影時に非表示にするDOMを表すセレクターのコレクション
	 */
	public void assertView(String message, String screenshotId, List<CompareTarget> compareTargets,
			List<DomSelector> hiddenElementsSelectors) {
		if (driver == null) {
			throw new TestRuntimeException("Driver is not initialized");
		}

		if (Strings.isNullOrEmpty(screenshotId)) {
			LOG.error("ScreenshotId cannot be null or empty");
			throw new TestRuntimeException("ScreenshotId cannot be null or empty");
		}

		// Check screenshotId
		if (screenshotIds.contains(screenshotId)) {
			LOG.error("Duplicate screenshotId ({})", screenshotId);
			throw new TestRuntimeException("Duplicate screenshotId");
		}

		ExecMode execMode = PtlTestConfig.getInstance().getEnvironment().getExecMode();
		if (execMode.equals(ExecMode.SKIP)) {
			LOG.info("[AssertView skipped] (ssid: {}, Mode:{})", screenshotId, execMode);
			return;
		}

		LOG.info("[AssertView start] (ssid: {}, Mode: {})", screenshotId, execMode);
		LOG.trace("[AssertView start] message: {}, screenshotId: {}, compareTargets: {}, hiddenElementSelectors: {}",
				message, screenshotId, compareTargets, hiddenElementsSelectors);

		List<CompareTarget> targets;
		if (compareTargets == null || compareTargets.isEmpty()) {
			targets = new ArrayList<CompareTarget>();
			targets.add(new CompareTarget());
		} else {
			targets = compareTargets;
		}

		screenshotIds.add(screenshotId);

		// ターゲットのスクリーンショットを撮影
		ScreenshotResult captureResult = takeCaptureAndPersistImage(screenshotId, targets, hiddenElementsSelectors);
		List<TargetResult> targetResults = captureResult.getTargetResults();
		saveTargetResults(screenshotId, targetResults);

		ValidateResult validateResult = validateTargetResults(targetResults, targets);

		// Expected mode
		if (!execMode.isRunTest()) {
			ScreenshotResult screenshotResult = getScreenshotResultForExpectedMode(screenshotId, targetResults,
					validateResult);
			results.add(screenshotResult);

			if (!validateResult.isValid()) {
				LOG.info("[AssertView failed] (validation error, ssid: {})", screenshotId);
				throw new AssertionError("Invalid selector found");
			}

			LOG.info("[AssertView finished] (ssid: {})", screenshotId);
			return;
		}

		// RunTest mode
		LOG.info("[AssertView comparison start] (ssid: {})", screenshotId);

		String expectedId = TestResultManager.getInstance().getExpectedId(className, methodName);
		PersistMetadata expectedMetadata = new PersistMetadata(expectedId, className, methodName, screenshotId,
				capabilities);
		List<TargetResult> expectedTargetResults = TestResultManager.getInstance().getPersister()
				.loadTargetResults(expectedMetadata);

		ScreenshotResult screenshotResult = compareTargetResults(screenshotId, expectedId, targetResults,
				expectedTargetResults, validateResult);
		results.add(screenshotResult);

		if (!validateResult.isValid()) {
			LOG.info("[AssertView failed] (validation error, ssid: {})", screenshotId);
			throw new AssertionError("Invalid selector found");
		}
		if (!screenshotResult.getResult().isSuccess()) {
			LOG.info("[AssertView failed] (ssid: {})", screenshotId);
			throw Strings.isNullOrEmpty(message) ? new AssertionError() : new AssertionError(message);
		}

		LOG.info("[AssertView finished] (ssid: {})", screenshotId);
	}

	/**
	 * 指定の条件でスクリーンショットを撮影します。テスト実行モードが{@link com.htmlhifive.pitalium.core.config.ExecMode#SET_EXPECTED}の時は
	 * 正解状態としてスクリーンショットの画像と座標を保存します。 テスト実行モードが {@link com.htmlhifive.pitalium.core.config.ExecMode#RUN_TEST}の時は、
	 * {@link com.htmlhifive.pitalium.core.config.ExecMode#SET_EXPECTED}で撮影した状態と比較します。<br />
	 * <br />
	 * {@link #assertView(ScreenshotArgument)}との違いは{@code RUN_TEST}時に比較が失敗してもテストの実行を止めず、テストを最後まで実行します。
	 *
	 * @param arg スクリーンショットを撮影するための条件
	 */
	public void verifyView(ScreenshotArgument arg) {
		try {
			LOG.info("[VerifyView start] (ssid: {})", arg.getScreenshotId());
			assertView(arg);
		} catch (AssertionError e) {
			LOG.info("[VerifyView failed] (ssid: {})", arg.getScreenshotId());
			verifyErrors.add(e);
		}
	}

	/**
	 * 指定の条件でスクリーンショットを撮影します。テスト実行モードが{@link com.htmlhifive.pitalium.core.config.ExecMode#SET_EXPECTED}の時は
	 * 正解状態としてスクリーンショットの画像と座標を保存します。 テスト実行モードが {@link com.htmlhifive.pitalium.core.config.ExecMode#RUN_TEST}の時は、
	 * {@link com.htmlhifive.pitalium.core.config.ExecMode#SET_EXPECTED}で撮影した状態と比較します。<br />
	 * <br />
	 * {@link #assertView(ScreenshotArgument)}との違いは{@code RUN_TEST}時に比較が失敗してもテストの実行を止めず、テストを最後まで実行します。
	 *
	 * @param message {@link AssertionError}を識別する文字列
	 * @param arg スクリーンショットを撮影するための条件
	 */
	public void verifyView(String message, ScreenshotArgument arg) {
		try {
			LOG.info("[VerifyView start] (ssid: {})", arg.getScreenshotId());
			assertView(message, arg);
		} catch (AssertionError e) {
			LOG.info("[VerifyView failed] (ssid: {})", arg.getScreenshotId());
			verifyErrors.add(e);
		}
	}

	/**
	 * スクリーンショットの撮影結果をチェックします。
	 *
	 * @param targetResults 実行結果
	 * @param compareTargets 指定した{@link CompareTarget}
	 * @return バリデーション結果
	 */
	private ValidateResult validateTargetResults(List<TargetResult> targetResults, List<CompareTarget> compareTargets) {
		return new ValidateResult(validateTargetElementHasSize(targetResults),
				validateDomSelectorTargetExists(targetResults, compareTargets));
	}

	/**
	 * CompareTargetで指定したセレクタに対応する要素が大きさを持っているかチェックします。
	 *
	 * @param targetResults 指定した{@link CompareTarget}
	 * @return 大きさを持たない要素のセレクタのリスト
	 */
	private List<IndexDomSelector> validateTargetElementHasSize(List<TargetResult> targetResults) {
		List<IndexDomSelector> selectors = new ArrayList<IndexDomSelector>();
		for (TargetResult targetResult : targetResults) {
			RectangleArea area = targetResult.getTarget().getRectangle();
			IndexDomSelector selector = targetResult.getTarget().getSelector();
			if (selector != null && (area.getWidth() == 0d || area.getHeight() == 0d)) {
				selectors.add(selector);
				LOG.error("[Validation error] element has empty size. ({})", selector);
			}
		}

		return selectors;
	}

	/**
	 * CompareTargetで指定したセレクタに対応する要素が存在したかチェックします。
	 *
	 * @param targetResults 実行結果
	 * @param compareTargets 指定した{@link CompareTarget}
	 * @return 存在しなかった要素のセレクタのリスト
	 */
	private List<DomSelector> validateDomSelectorTargetExists(List<TargetResult> targetResults,
			List<CompareTarget> compareTargets) {
		Set<DomSelector> selectors = new HashSet<DomSelector>(targetResults.size() * 2);
		for (TargetResult targetResult : targetResults) {
			DomSelector selector = targetResult.getTarget().getScreenArea().getSelector();
			if (selector != null) {
				selectors.add(selector);
			}
		}

		List<DomSelector> invalidSelectors = new ArrayList<DomSelector>();
		for (CompareTarget compareTarget : compareTargets) {
			DomSelector selector = compareTarget.getCompareArea().getSelector();
			if (selector == null) {
				continue;
			}

			if (!selectors.contains(selector)) {
				invalidSelectors.add(selector);
				LOG.error("[Validation error] no element found. ({})", selector);
			}
		}

		return invalidSelectors;
	}

	/**
	 * 1つのスクリーンショットIDに対応する実行結果を保存します。
	 *
	 * @param screenshotId スクリーンショットID
	 * @param targetResults 実行結果のリスト
	 */
	private void saveTargetResults(String screenshotId, List<TargetResult> targetResults) {
		PersistMetadata currentMetadata = new PersistMetadata(currentId, className, methodName, screenshotId,
				capabilities);
		List<TargetResult> processes = new ArrayList<TargetResult>(targetResults.size());
		for (TargetResult target : targetResults) {
			processes.add(new TargetResult(toTargetForJson(target.getTarget()), toExcludesForJson(target.getExcludes()),
					target.getImage()));
		}

		TestResultManager.getInstance().getPersister().saveTargetResults(currentMetadata, processes);
	}

	/**
	 * スクリーンショットを撮影し、結果画像を保存します。
	 *
	 * @param screenshotId スクリーンショットを識別するID
	 * @param compareTargets スクリーンショットの撮影、比較条件
	 * @param hiddenElementsSelectors スクリーンショット撮影時に非表示にするDOMを表すセレクターのコレクション
	 * @return 撮影結果の{@link ScreenshotResult}
	 */
	private ScreenshotResult takeCaptureAndPersistImage(String screenshotId, List<CompareTarget> compareTargets,
			List<DomSelector> hiddenElementsSelectors) {
		ScreenshotResult screenshotResult = driver.takeScreenshot(screenshotId, compareTargets,
				hiddenElementsSelectors);
		LOG.trace("(takeCaptureAndPersistImage) (ssid: {}) result: {}", screenshotId, screenshotResult);

		// Persist all screenshots
		Persister persister = TestResultManager.getInstance().getPersister();
		ScreenshotImage entireScreenshotImage = screenshotResult.getEntireScreenshotImage();
		if (entireScreenshotImage.isImageCached()) {
			persister.saveScreenshot(
					new PersistMetadata(currentId, className, methodName, screenshotId, null, null, capabilities),
					entireScreenshotImage.get());
		}

		for (TargetResult targetResult : screenshotResult.getTargetResults()) {
			ScreenshotImage image = targetResult.getImage();
			if (!image.isImageCached()) {
				LOG.debug("(takeCaptureAndPersistImage) Screenshot image was not captured. ({})",
						targetResult.getTarget());
				continue;
			}

			ScreenAreaResult target = targetResult.getTarget();
			PersistMetadata metadata;
			if (target.getSelector() == null) {
				metadata = new PersistMetadata(currentId, className, methodName, screenshotId, null,
						target.getRectangle(), capabilities);
			} else {
				metadata = new PersistMetadata(currentId, className, methodName, screenshotId, target.getSelector(),
						null, capabilities);
			}

			persister.saveScreenshot(metadata, image.get());
		}

		return screenshotResult;
	}

	/**
	 * Expectedモード時のScreenshotResultを作成します。
	 *
	 * @param screenshotId スクリーンショットID
	 * @param targetResults 実行結果
	 * @param validateResult バリデーション結果
	 * @return 今回の実行結果{@link ScreenshotResult}
	 */
	private ScreenshotResult getScreenshotResultForExpectedMode(String screenshotId, List<TargetResult> targetResults,
			ValidateResult validateResult) {
		List<TargetResult> processes = new ArrayList<TargetResult>(targetResults.size());
		for (TargetResult target : targetResults) {
			processes.add(
					new TargetResult(null, toTargetForJson(target.getTarget()), toExcludesForJson(target.getExcludes()),
							target.isMoveTarget(), target.getHiddenElementSelectors()));
		}

		for (DomSelector selector : validateResult.noElementSelectors) {
			processes.add(new TargetResult(null, new ScreenAreaResult(null, null, new ScreenArea(selector)), null, null,
					null));
		}

		ExecResult result = validateResult.isValid() ? null : ExecResult.FAILURE;
		return new ScreenshotResult(screenshotId, result, null, processes, className, methodName, capabilities.asMap(),
				null);
	}

	/**
	 * Actualモード時のScreenshotResultを作成します。
	 *
	 * @param screenshotId スクリーンショットID
	 * @param expectedId 期待結果ID
	 * @param currents 今回の実行結果
	 * @param expects 期待結果
	 * @param validateResult バリデーション結果
	 * @return 画像比較の結果を含む{@link ScreenshotResult}
	 */
	private ScreenshotResult compareTargetResults(String screenshotId, String expectedId, List<TargetResult> currents,
			List<TargetResult> expects, ValidateResult validateResult) {
		boolean assertFail = !validateResult.isValid();

		List<TargetResult> processes = new ArrayList<TargetResult>(currents.size());
		for (final TargetResult current : currents) {
			// Target element's area was nothing
			IndexDomSelector selector = current.getTarget().getSelector();
			if (selector != null && validateResult.noAreaElementSelectors.contains(selector)) {
				processes.add(new TargetResult(ExecResult.FAILURE, toTargetForJson(current.getTarget()),
						toExcludesForJson(current.getExcludes()), current.isMoveTarget(),
						current.getHiddenElementSelectors()));
				LOG.debug("[Comparison skipped] ({})", current.getTarget());
				continue;
			}

			TargetResult expected;
			try {
				expected = Iterators.find(expects.iterator(), new Predicate<TargetResult>() {
					@Override
					public boolean apply(TargetResult input) {
						return current.getTarget().areaEquals(input.getTarget());
					}
				});
			} catch (NoSuchElementException e) {
				LOG.error("[Comparison failed] No element found for target ({}).", current.getTarget());
				processes.add(new TargetResult(null, toTargetForJson(current.getTarget()),
						toExcludesForJson(current.getExcludes()), current.isMoveTarget(),
						current.getHiddenElementSelectors()));
				assertFail = true;

				continue;
			}

			// 画像比較
			LOG.debug("[Comparison start] ({})", current);
			ImageRectanglePair currentImage = prepareScreenshotImageForCompare(current);
			ImageRectanglePair expectedImage = prepareScreenshotImageForCompare(expected);

			//			if (current.getOptions() == null || current.getOptions().length == 0) {
			//				current.
			//			}

			ImageComparedResult compareResult = ImageUtils.compare(currentImage.image, currentImage.rectangle,
					expectedImage.image, expectedImage.rectangle, current.getOptions());
			assertFail |= compareResult.isFailed();
			if (compareResult.isFailed()) {
				LOG.error("[Comparison failed] ({})", current);
			} else {
				LOG.debug("[Comparison success] ({})", current);
			}

			processes.add(new TargetResult(compareResult.isSucceeded() ? ExecResult.SUCCESS : ExecResult.FAILURE,
					toTargetForJson(current.getTarget()), toExcludesForJson(current.getExcludes()),
					current.isMoveTarget(), current.getHiddenElementSelectors()));

			// 比較でFailだった場合、差分の画像を作成
			if (compareResult.isFailed()) {
				LOG.debug("[Create diff image] ({})", current);
				BufferedImage diffImage = ImageUtils.getDiffImage(expectedImage.image, currentImage.image,
						(DiffPoints) compareResult);

				// Metadata作成して保存
				ScreenAreaResult target = current.getTarget();
				PersistMetadata metadata;
				if (target.getSelector() == null) {
					metadata = new PersistMetadata(currentId, className, methodName, screenshotId, null,
							target.getRectangle(), capabilities);
				} else {
					metadata = new PersistMetadata(currentId, className, methodName, screenshotId, target.getSelector(),
							null, capabilities);
				}
				TestResultManager.getInstance().getPersister().saveDiffImage(metadata, diffImage);
			}
		}

		for (DomSelector selector : validateResult.noElementSelectors) {
			processes.add(new TargetResult(null, new ScreenAreaResult(null, null, new ScreenArea(selector)), null, null,
					null));
		}

		return new ScreenshotResult(screenshotId, assertFail ? ExecResult.FAILURE : ExecResult.SUCCESS, expectedId,
				processes, className, methodName, capabilities.asMap(), null);
	}

	/**
	 * スクリーンショット比較の前準備として、除外領域をマスクし、座標情報とペアにして返します。
	 *
	 * @param target スクリーンショット撮影結果
	 * @return マスク済の画像と座標情報のペア
	 */
	private ImageRectanglePair prepareScreenshotImageForCompare(TargetResult target) {
		BufferedImage image = target.getImage().get();

		// Mask
		List<ScreenAreaResult> excludes = target.getExcludes();
		if (!excludes.isEmpty()) {
			List<Rectangle> maskAreas = Lists.transform(toExcludesForJson(excludes),
					SCREEN_AREA_RESULT_TO_RECTANGLE_FUNCTION);
			image = ImageUtils.getMaskedImage(image, maskAreas);
		}

		return new ImageRectanglePair(image, target.getTarget().getRectangle().toRectangle());
	}

	/**
	 * JSON出力用に小数点以下を丸める。target部分は指定した領域に収まる最大の短径となるようにする。
	 *
	 * @param target targetに指定した要素結果
	 * @return JSON出力用のtargetのScreenAreaResult
	 */
	private ScreenAreaResult toTargetForJson(ScreenAreaResult target) {
		ScreenArea screenArea = target.getScreenArea();
		if (screenArea.getSelector() == null) {
			return target;
		}

		RectangleArea area = target.getRectangle();
		double x = Math.ceil(area.getX());
		double y = Math.ceil(area.getY());
		double height = Math.floor(area.getY() + area.getHeight()) - y;
		double width = Math.floor(area.getX() + area.getWidth()) - x;
		RectangleArea newArea = new RectangleArea(x, y, width, height);

		return new ScreenAreaResult(target.getSelector(), newArea, screenArea);
	}

	/**
	 * JSON出力用に小数点以下を丸める。exclude部分は指定した領域が収まる最小の短径となるようにする。
	 *
	 * @param excludes excludesに指定した要素結果のリスト
	 * @return JSON出力用のexcludesのScreenAreaResultのリスト
	 */
	private List<ScreenAreaResult> toExcludesForJson(List<ScreenAreaResult> excludes) {
		List<ScreenAreaResult> ret = new ArrayList<>();
		for (ScreenAreaResult exclude : excludes) {
			ScreenArea screenArea = exclude.getScreenArea();
			if (screenArea.getSelector() == null) {
				ret.add(exclude);
				continue;
			}

			RectangleArea area = exclude.getRectangle();
			double x = Math.floor(area.getX());
			double y = Math.floor(area.getY());
			double height = Math.ceil(area.getY() + area.getHeight()) - y;
			double width = Math.ceil(area.getX() + area.getWidth()) - x;
			RectangleArea newArea = new RectangleArea(x, y, width, height);

			ret.add(new ScreenAreaResult(exclude.getSelector(), newArea, screenArea));
		}
		return ret;
	}

	//</editor-fold>

	//<editor-fold desc="assertScreenshot">

	/**
	 * すでに取得したスクリーンショットについて、期待画像と一致するか検証します。一致しなければdiff画像を生成し、テストを失敗として中断します。
	 *
	 * @param screenshotResult {@link PtlWebDriver#takeScreenshot(String) takeScreenshot}を実行して取得した結果オブジェクト
	 */
	public void assertScreenshot(ScreenshotResult screenshotResult) {
		assertScreenshot(null, screenshotResult);
	}

	/**
	 * すでに取得したスクリーンショットについて、期待画像と一致するか検証します。一致しなければdiff画像を生成し、テストを失敗として中断します。
	 *
	 * @param message 失敗時に表示するメッセージ
	 * @param screenshotResult {@link PtlWebDriver#takeScreenshot(String) takeScreenshot}を実行して取得した結果オブジェクト
	 */
	public void assertScreenshot(String message, ScreenshotResult screenshotResult) {
		ExecMode execMode = PtlTestConfig.getInstance().getEnvironment().getExecMode();
		String screenshotId = screenshotResult.getScreenshotId();
		if (!execMode.isRunTest()) {
			LOG.debug("[AssertScreenshot skipped] (ssid: {}, mode: {})", screenshotId, execMode);
			return;
		}

		String expectedId = TestResultManager.getInstance().getExpectedId(className, methodName);
		PersistMetadata expectedMetadata = new PersistMetadata(expectedId, className, methodName, screenshotId,
				capabilities);
		List<TargetResult> expectedTargetResults = TestResultManager.getInstance().getPersister()
				.loadTargetResults(expectedMetadata);

		LOG.info("[AssertScreenshot comparison start] (ssid: {})", screenshotId);
		ScreenshotResult result = compareTargetResults(screenshotId, expectedId, screenshotResult.getTargetResults(),
				expectedTargetResults, new ValidateResult());

		if (!result.getResult().isSuccess()) {
			LOG.info("[AssertScreenshot failed] (ssid: {})", screenshotId);
			throw Strings.isNullOrEmpty(message) ? new AssertionError() : new AssertionError(message);
		}

		LOG.info("[AssertScreenshot succeeded] (ssid: {})", screenshotId);
	}

	/**
	 * すでに取得したスクリーンショットについて、期待画像と一致するか検証します。一致しなければdiff画像を生成し、テストを失敗とします。<br />
	 * <br />
	 * {@link #assertScreenshot(ScreenshotResult)}との違いは比較が失敗してもテストの実行を止めず、テストを最後まで実行します。
	 *
	 * @param screenshotResult {@link PtlWebDriver#takeScreenshot(String) takeScreenshot}を実行して取得した結果オブジェクト
	 */
	public void verifyScreenshot(ScreenshotResult screenshotResult) {
		try {
			LOG.info("[VerifyScreenshot start] (ssid: {})", screenshotResult.getExpectedId());
			assertScreenshot(screenshotResult);
		} catch (AssertionError e) {
			LOG.info("[VerifyScreenshot failed] (ssid: {})", screenshotResult.getScreenshotId());
			verifyErrors.add(e);
		}
	}

	/**
	 * すでに取得したスクリーンショットについて、期待画像と一致するか検証します。一致しなければdiff画像を生成し、テストを失敗とします。<br />
	 * <br />
	 * {@link #assertScreenshot(ScreenshotResult)}との違いは比較が失敗してもテストの実行を止めず、テストを最後まで実行します。
	 *
	 * @param message 失敗時に表示するメッセージ
	 * @param screenshotResult {@link PtlWebDriver#takeScreenshot(String) takeScreenshot}を実行して取得した結果オブジェクト
	 */
	public void verifyScreenshot(String message, ScreenshotResult screenshotResult) {
		try {
			LOG.info("[VerifyScreenshot start] (ssid: {})", screenshotResult.getExpectedId());
			assertScreenshot(message, screenshotResult);
		} catch (AssertionError e) {
			LOG.info("[VerifyScreenshot failed] (ssid: {})", screenshotResult.getScreenshotId());
			verifyErrors.add(e);
		}
	}

	//</editor-fold>

	//<editor-fold desc="assertExists">

	/**
	 * 画面全体のスクリーンショットを撮影し、指定の画像が現在のページ上に存在するかどうか検証します。<br />
	 * ただし、テスト実行モードが{@link com.htmlhifive.pitalium.core.config.ExecMode#SET_EXPECTED}の場合、検証は行われません。
	 *
	 * @param image 検証に使用する画像
	 */
	public void assertExist(BufferedImage image) {
		assertExist(null, image);
	}

	/**
	 * 画面全体のスクリーンショットを撮影し、指定の画像が現在のページ上に存在するかどうか検証します。<br />
	 * ただし、テスト実行モードが{@link com.htmlhifive.pitalium.core.config.ExecMode#SET_EXPECTED}の場合、検証は行われません。
	 *
	 * @param message {@link AssertionError}を識別する文字列
	 * @param image 検証に使用する画像
	 */
	public void assertExist(String message, BufferedImage image) {
		ExecMode execMode = PtlTestConfig.getInstance().getEnvironment().getExecMode();
		if (!execMode.isRunTest()) {
			LOG.debug("[AssertExist skipped] (mode: {})", execMode);
			return;
		}

		// Capture body
		LOG.debug("[AssertExist capture start]");
		ScreenshotImage screenshot = driver.takeScreenshot("assertExists").getTargetResults().get(0).getImage();
		BufferedImage entireScreenshotImage = screenshot.get();
		LOG.debug("[AssertExist capture finished]");

		if (ImageUtils.isContained(entireScreenshotImage, image)) {
			LOG.info("[AssertExist succeeded]");
			return;
		}

		LOG.info("[AssertExist failed]");
		throw Strings.isNullOrEmpty(message) ? new AssertionError() : new AssertionError(message);
	}

	/**
	 * 画面全体のスクリーンショットを撮影し、指定の画像が現在のページ上に存在するかどうか検証します。<br />
	 * ただし、テスト実行モードが{@link com.htmlhifive.pitalium.core.config.ExecMode#SET_EXPECTED}の場合、検証は行われません。<br />
	 * <br />
	 * {@link #assertExist(BufferedImage)}との違いは比較が失敗してもテストの実行を止めず、テストを最後まで実行します。
	 *
	 * @param image 検証に使用する画像
	 */
	public void verifyExists(BufferedImage image) {
		try {
			LOG.info("[VerifyExist start]");
			assertExist(image);
		} catch (AssertionError e) {
			LOG.info("[VerifyExist failed]");
			verifyErrors.add(e);
		}
	}

	/**
	 * 画面全体のスクリーンショットを撮影し、指定の画像が現在のページ上に存在するかどうか検証します。<br />
	 * ただし、テスト実行モードが{@link com.htmlhifive.pitalium.core.config.ExecMode#SET_EXPECTED}の場合、検証は行われません。<br />
	 * <br />
	 * {@link #assertExist(BufferedImage)}との違いは比較が失敗してもテストの実行を止めず、テストを最後まで実行します。
	 *
	 * @param message {@link AssertionError}を識別する文字列
	 * @param image 検証に使用する画像
	 */
	public void verifyExists(String message, BufferedImage image) {
		try {
			LOG.info("[VerifyExist start]");
			assertExist(message, image);
		} catch (AssertionError e) {
			LOG.info("[VerifyExist failed]");
			verifyErrors.add(e);
		}
	}

	//</editor-fold>

	/**
	 * 実行中のテストクラス名を取得します。
	 *
	 * @param description 実行中のテストのDescription
	 * @return テストクラス名
	 */
	private static String getClassName(Description description) {
		return description.getTestClass().getSimpleName();
	}

	/**
	 * 実行中のテストメソッド名を取得します。
	 *
	 * @param description 実行中のテストのDescription
	 * @return テストメソッド名
	 */
	private static String getMethodName(Description description) {
		return description.getMethodName().split("\\[")[0];
	}

	/**
	 * 配列をListに変換します。
	 *
	 * @param <T> 対象の配列の型
	 * @param array 変換元配列
	 * @return 変換後List
	 */
	@SafeVarargs
	private static <T> List<T> asList(T... array) {
		if (array == null || array.length == 0) {
			return Collections.emptyList();
		} else if (array.length == 1) {
			return Collections.singletonList(array[0]);
		} else {
			return Arrays.asList(array);
		}
	}

	/**
	 * 画像と矩形情報のペアを保持するクラス
	 */
	private static class ImageRectanglePair {
		private final BufferedImage image;
		private final Rectangle rectangle;

		/**
		 * 画像と矩形情報のペアを持ったオブジェクトを生成します。
		 *
		 * @param image 画像
		 * @param rectangle 矩形情報
		 */
		public ImageRectanglePair(BufferedImage image, Rectangle rectangle) {
			this.image = image;
			this.rectangle = rectangle;
		}
	}

	/**
	 * バリデーション結果を保持するクラス
	 */
	private static class ValidateResult {
		/**
		 * 大きさが無い要素を指定したセレクタ一覧
		 */
		@JsonInclude
		private final Collection<IndexDomSelector> noAreaElementSelectors;
		/**
		 * 存在しない要素を指定したセレクタ一覧
		 */
		@JsonInclude
		private final Collection<DomSelector> noElementSelectors;

		/**
		 * 空のバリデーション結果オブジェクトを生成します。
		 */
		public ValidateResult() {
			this(new ArrayList<IndexDomSelector>(), new ArrayList<DomSelector>());
		}

		/**
		 * 大きさが無い要素、存在しない要素を指定したセレクタ一覧を持った<br>
		 * バリデーション結果オブジェクトを生成します。
		 *
		 * @param noAreaElementSelectors 大きさが無い要素を指定したセレクタ一覧
		 * @param noElementSelectors 存在しない要素を指定したセレクタ一覧
		 */
		public ValidateResult(Collection<IndexDomSelector> noAreaElementSelectors,
				Collection<DomSelector> noElementSelectors) {
			this.noAreaElementSelectors = noAreaElementSelectors;
			this.noElementSelectors = noElementSelectors;
		}

		/**
		 * バリデーションの結果がValidか否かを取得します。
		 *
		 * @return Validならtrue、Invalidならfalse
		 */
		public boolean isValid() {
			return noAreaElementSelectors.isEmpty() && noElementSelectors.isEmpty();
		}

		@Override
		public String toString() {
			return "ValidateResult: " + JSONUtils.toString(this);
		}
	}

}
