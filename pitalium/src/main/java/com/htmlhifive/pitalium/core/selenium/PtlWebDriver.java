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
package com.htmlhifive.pitalium.core.selenium;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.internal.JsonToWebElementConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.htmlhifive.pitalium.common.exception.TestRuntimeException;
import com.htmlhifive.pitalium.core.config.EnvironmentConfig;
import com.htmlhifive.pitalium.core.model.CompareTarget;
import com.htmlhifive.pitalium.core.model.DomSelector;
import com.htmlhifive.pitalium.core.model.IndexDomSelector;
import com.htmlhifive.pitalium.core.model.ScreenArea;
import com.htmlhifive.pitalium.core.model.ScreenAreaResult;
import com.htmlhifive.pitalium.core.model.ScreenAreaWrapper;
import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.core.model.ScreenshotParams;
import com.htmlhifive.pitalium.core.model.ScreenshotResult;
import com.htmlhifive.pitalium.core.model.SelectorType;
import com.htmlhifive.pitalium.core.model.TargetResult;
import com.htmlhifive.pitalium.image.model.RectangleArea;
import com.htmlhifive.pitalium.image.model.ScreenshotImage;
import com.htmlhifive.pitalium.image.util.ImageUtils;

/**
 * WebDriverの実装クラス。{@link org.openqa.selenium.remote.RemoteWebDriver}
 * の機能に加え、表示中のWebページに関する情報取得、ブラウザの差異を吸収したスクリーンショットの撮影を行います。<br/>
 * 各ブラウザの操作に用いるWebDriverは、このクラスを拡張して実装します。
 */
public abstract class PtlWebDriver extends RemoteWebDriver {

	/**
	 * スクリーンショット画像とブラウザの表示領域のサイズ比。デフォルト値は1
	 */
	public static final double DEFAULT_SCREENSHOT_SCALE = 1d;

	//@formatter:off
	// CHECKSTYLE:OFF
	private static final String[] SCRIPTS_SCROLL_TOP = { "document.documentElement.scrollTop",
			"document.body.scrollTop" };
	private static final String[] SCRIPTS_SCROLL_LEFT = { "document.documentElement.scrollLeft",
			"document.body.scrollLeft" };
	private static final String SCRIPT_GET_DEFAULT_DOCUMENT_OVERFLOW = "return {\"overflow\": document.documentElement.style.overflow};";
	private static final String SCRIPT_SET_DOCUMENT_OVERFLOW = "document.documentElement.style.overflow = arguments[0];";
	private static final String SCRIPT_GET_DEFAULT_BODY_STYLE = "return {"
			+ "  \"position\":    document.body.style.position ? document.body.style.position : null,"
			+ "  \"top\":         document.body.style.top      ? document.body.style.top      : null,"
			+ "  \"left\":        document.body.style.left     ? document.body.style.left     : null,"
			+ "  \"width\":       document.body.style.width    ? document.body.style.width    : null,"
			+ "  \"scrollWidth\": document.body.scrollWidth};";

	private static final String SCRIPT_MOVE_BODY = "document.body.style.position = arguments[0];"
			+ "document.body.style.top      = arguments[1];"
			+ "document.body.style.left     = arguments[2];";
	private static final String GET_WINDOW_WIDTH_SCRIPT =
			"if (typeof window.innerWidth != 'undefined') {" +
			"  return window.innerWidth;" +
			"} else if (typeof document.documentElement != 'undefined' && typeof document.documentElement.clientWidth != 'undefined' && document.documentElement.clientWidth != 0) {" +
			"  return document.documentElement.clientWidth;" +
			"} else {" +
			"  return document.getElementsByTagName('body')[0].clientWidth;" +
			"}";
	private static final String GET_WINDOW_HEIGHT_SCRIPT =
			"if (typeof window.innerHeight != 'undefined') {" +
			"  return window.innerHeight;" +
			"} else if (typeof document.documentElement != 'undefined' && typeof document.documentElement.clientHeight != 'undefined' && document.documentElement.clientHeight != 0) {" +
			"  return document.documentElement.clientHeight;" +
			"} else {" +
			"  return document.getElementsByTagName('body')[0].clientHeight;" +
			"}";
	private static final String GET_SCROLL_WIDTH_SCRIPT = "return arguments[0].scrollWidth";
	private static final String GET_SCROLL_HEIGHT_SCRIPT = "return arguments[0].scrollHeight";
	private static final String GET_BODY_LEFT_SCRIPT = "var _bodyLeft = arguments[0].getBoundingClientRect().left; return _bodyLeft;";
	private static final String GET_BODY_TOP_SCRIPT = "var _bodyTop = arguments[0].getBoundingClientRect().top; return _bodyTop;";
	private static final long SCROLL_WAIT_MS = 100L;
	// CHECKSTYLE:ON
	//@formatter:on

	private static final Map<Capabilities, AtomicInteger> DEBUG_SCREENSHOT_COUNTS = new HashMap<Capabilities, AtomicInteger>();

	protected final Logger LOG = LoggerFactory.getLogger(getClass());
	private final PtlCapabilities capabilities;
	private String baseUrl;
	private double scale = DEFAULT_SCREENSHOT_SCALE;
	private EnvironmentConfig environmentConfig;

	/**
	 * コンストラクタ
	 * 
	 * @param remoteAddress RemoteWebDriverServerのアドレス
	 * @param capabilities Capability
	 */
	protected PtlWebDriver(URL remoteAddress, PtlCapabilities capabilities) {
		super(remoteAddress, capabilities);
		this.capabilities = capabilities;

		// JsonToWebElementConverterを上書きすることでfindElementから取得されるRemoteWebElementを差し替え
		setElementConverter(new JsonToPtlWebElementConverter(this));
	}

	/**
	 * テスト対象アプリケーションのベースURLを取得します。
	 * 
	 * @see com.htmlhifive.pitalium.core.config.TestAppConfig#baseUrl
	 * @return テスト対象アプリケーションのベースURL
	 */
	protected String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * テスト対象アプリケーションのベースURLを設定します。
	 * 
	 * @see com.htmlhifive.pitalium.core.config.TestAppConfig#baseUrl
	 * @param baseUrl テスト対象アプリケーションのベースURL
	 */
	protected void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	/**
	 * 環境情報を設定します。
	 * 
	 * @param environmentConfig 環境情報
	 */
	protected final void setEnvironmentConfig(EnvironmentConfig environmentConfig) {
		this.environmentConfig = environmentConfig;
	}

	/**
	 * 指定されたURLをブラウザで開きます。urlがhttp://またはhttps://から始まる場合はそのまま、それ以外の場合は{@link #baseUrl} + urlのアドレスを開きます。
	 * 
	 * @param url 開きたいURL
	 */
	@Override
	public void get(String url) {
		String targetUrl = UrlUtils.getTargetUrl(baseUrl, url);
		LOG.debug("[Get] ({}, base: {}, url: {})", targetUrl, baseUrl, url);

		super.get(targetUrl);
	}

	/**
	 * Capabilityを取得します。
	 * 
	 * @return Capability
	 */
	@Override
	public PtlCapabilities getCapabilities() {
		return capabilities;
	}

	@Override
	public <X> X getScreenshotAs(OutputType<X> outputType) throws WebDriverException {
		X screenshot = super.getScreenshotAs(outputType);
		if (environmentConfig != null && environmentConfig.isDebug()) {
			exportDebugScreenshot(screenshot);
		}
		return screenshot;
	}

	private void exportDebugScreenshot(Object screenshot) {
		File imageFile;
		if (screenshot instanceof String) {
			imageFile = OutputType.FILE.convertFromBase64Png((String) screenshot);
		} else if (screenshot instanceof byte[]) {
			imageFile = OutputType.FILE.convertFromPngBytes((byte[]) screenshot);
		} else if (screenshot instanceof File) {
			imageFile = (File) screenshot;
		} else {
			LOG.warn("Unknown OutputType: \"{}\"", screenshot.getClass().getName());
			return;
		}

		// Filename -> logs/screenshots/firefox/43/0000.png
		// FIXME: 2016/01/04 保存ディレクトリをちゃんとする
		String filename;
		File dir;
		synchronized (DEBUG_SCREENSHOT_COUNTS) {
			AtomicInteger counter = DEBUG_SCREENSHOT_COUNTS.get(capabilities);
			if (counter == null) {
				counter = new AtomicInteger();
				DEBUG_SCREENSHOT_COUNTS.put(capabilities, counter);
			}
			int count = counter.getAndIncrement();

			filename = String.format(Locale.US, "%04d.png", count);
			dir = new File("logs", "screenshots");
			dir = new File(dir, capabilities.getBrowserName());
			if (!Strings.isNullOrEmpty(capabilities.getVersion())) {
				dir = new File(dir, capabilities.getVersion());
			}

			// First time => delete old files
			if (count == 0 && dir.exists()) {
				try {
					FileUtils.deleteDirectory(dir);
				} catch (IOException e) {
					LOG.warn("Cannot delete debug screenshot directory \"" + dir.getAbsolutePath() + "\"", e);
					return;
				}
			}

			if (!dir.exists() && !dir.mkdirs()) {
				LOG.warn("Debug screenshot persist error. Cannot make directory \"{}\"", dir.getAbsolutePath());
				return;
			}
		}

		try {
			Files.copy(imageFile, new File(dir, filename));
		} catch (IOException e) {
			LOG.warn("Debug screenshot persist error", e);
		}
	}

	/**
	 * 要素を移動可能なブラウザかどうかを取得します。
	 * 
	 * @return 移動可能なブラウザならtrue、不可ならfalse
	 */
	protected boolean canMoveTarget() {
		return true;
	}

	/**
	 * bodyのスクロールバーを非表示にできるブラウザかどうかを取得します。
	 * 
	 * @return 非表示にできる場合はtrue、できない場合はfalse
	 */
	protected boolean canHideBodyScrollbar() {
		return true;
	}

	/**
	 * 要素のスクロールバーを非表示にできるブラウザかどうかを取得します。
	 * 
	 * @return 非表示にできる場合はtrue、できない場合はfalse
	 */
	protected boolean canHideElementScrollbar() {
		return true;
	}

	/**
	 * 要素を非表示にする必要があるブラウザかどうかを取得します。
	 * 
	 * @return 非表示にする場合はtrue、しない場合はfalse
	 */
	protected boolean isHideElementsRequired() {
		return true;
	}

	/**
	 * 要素をリサイズできるブラウザかどうかを取得します。
	 * 
	 * @return リサイズできる場合はtrue、できない場合はfalse
	 */
	protected boolean canResizeElement() {
		return true;
	}

	/**
	 * @see org.openqa.selenium.JavascriptExecutor#executeScript(String, Object...)
	 * @param script 実行するJavaScript
	 * @param params スクリプトに渡すパラメータ
	 * @param <T> 実行結果の型
	 * @return 実行結果
	 */
	@SuppressWarnings("unchecked")
	public <T> T executeJavaScript(String script, Object... params) {
		return (T) executeScript(script, params);
	}

	@Override
	public Object executeScript(String script, Object... args) {
		Object result = super.executeScript(script, args);
		LOG.trace("[Execute script] [{}] (args: {}, result: {})", script, args, result);
		return result;
	}

	//<editor-fold desc="takeScreenshot">

	/**
	 * 画面全体のスクリーンショットを撮影します。
	 * 
	 * @param screenshotId スクリーンショットID
	 * @return スクリーンショット撮影結果
	 */
	public ScreenshotResult takeScreenshot(String screenshotId) {
		return takeScreenshot(screenshotId, Collections.singletonList(new CompareTarget()));
	}

	/**
	 * 指定範囲のスクリーンショットを撮影します。
	 * 
	 * @param screenshotId スクリーンショットID
	 * @param compareTargets 撮影対象とする範囲
	 * @return スクリーンショット撮影結果
	 */
	public ScreenshotResult takeScreenshot(String screenshotId, CompareTarget[] compareTargets) {
		return takeScreenshot(screenshotId, Arrays.asList(compareTargets));
	}

	/**
	 * 指定範囲のスクリーンショットを撮影します。
	 * 
	 * @param screenshotId スクリーンショットID
	 * @param compareTargets 撮影対象とする範囲
	 * @return スクリーンショット撮影結果
	 */
	public ScreenshotResult takeScreenshot(String screenshotId, List<CompareTarget> compareTargets) {
		return takeScreenshot(screenshotId, compareTargets, new ArrayList<DomSelector>());
	}

	/**
	 * 撮影時に非表示にする要素を指定し、指定範囲のスクリーンショットを撮影します。
	 * 
	 * @param screenshotId スクリーンショットID
	 * @param compareTargets 撮影対象とする範囲
	 * @param hiddenElementsSelectors 撮影時に非表示にする要素
	 * @return スクリーンショット撮影結果
	 */
	public ScreenshotResult takeScreenshot(String screenshotId, CompareTarget[] compareTargets,
			DomSelector[] hiddenElementsSelectors) {
		return takeScreenshot(screenshotId, Arrays.asList(compareTargets), Arrays.asList(hiddenElementsSelectors));
	}

	/**
	 * スクリーンショットの撮影条件を指定してスクリーンショットを撮影します。
	 * 
	 * @param arg スクリーンショット撮影条件
	 * @return スクリーンショット撮影結果
	 */
	public ScreenshotResult takeScreenshot(ScreenshotArgument arg) {
		return takeScreenshot(arg.getScreenshotId(), arg.getTargets(), arg.getHiddenElementSelectors());
	}

	/**
	 * 撮影時に非表示にする要素を指定し、指定範囲のスクリーンショットを撮影します。
	 * 
	 * @param screenshotId スクリーンショットID
	 * @param compareTargets 撮影対象とする範囲
	 * @param hiddenElementSelectors 撮影時に非表示にする要素
	 * @return スクリーンショット撮影結果
	 */
	public ScreenshotResult takeScreenshot(String screenshotId, List<CompareTarget> compareTargets,
			List<DomSelector> hiddenElementSelectors) {
		List<CompareTarget> cTarget;
		// CompareTargetsが空の場合、BODYを対象とする
		if (compareTargets == null || compareTargets.isEmpty()) {
			cTarget = new ArrayList<CompareTarget>(1);
			cTarget.add(new CompareTarget());
		} else {
			cTarget = compareTargets;
		}

		LOG.debug("[TakeScreenshot start] (ssid: {})", screenshotId);

		List<PtlWebElement> hiddenElements = findElementsByDomSelectors(hiddenElementSelectors);
		LOG.trace("[TakeScreenshot] compareTargets: {}, hiddenElementSelectors: {}, hiddenElements: {}",
				compareTargets, hiddenElementSelectors, hiddenElements);

		// CompareTarget => ScreenshotParams
		List<Pair<CompareTarget, ScreenshotParams>> moveTargetParams = new ArrayList<Pair<CompareTarget, ScreenshotParams>>();
		List<Pair<CompareTarget, ScreenshotParams>> nonMoveNoScrollTargetParams = new ArrayList<Pair<CompareTarget, ScreenshotParams>>();
		List<Pair<CompareTarget, ScreenshotParams>> nonMoveScrollTargetParams = new ArrayList<Pair<CompareTarget, ScreenshotParams>>();
		for (CompareTarget compareTarget : cTarget) {
			List<ScreenAreaWrapper> targets = ScreenAreaWrapper.fromArea(compareTarget.getCompareArea(), this, null);
			for (int i = 0; i < targets.size(); i++) {
				ScreenAreaWrapper target = targets.get(i);
				List<ScreenAreaWrapper> excludes = new ArrayList<ScreenAreaWrapper>();
				for (ScreenArea exclude : compareTarget.getExcludes()) {
					excludes.addAll(target.getChildWrapper(exclude));
				}

				Pair<CompareTarget, ScreenshotParams> pair = Pair.of(compareTarget, new ScreenshotParams(target,
						excludes, hiddenElements, compareTarget.isMoveTarget(), compareTarget.isScrollTarget(), i));
				if (isMoveTargetRequired(pair.getRight())) {
					LOG.trace("[TakeScreenshot] MoveTarget: {} (index: {})", target.getParent(), i);
					moveTargetParams.add(pair);
				} else if (isScrollTargetRequired(pair.getRight())) {
					LOG.trace("[TakeScreenshot] Non-move scroll target: {} (index: {})", target.getParent(), i);
					nonMoveScrollTargetParams.add(pair);
				} else {
					LOG.trace("[TakeScreenshot] Non-move non-scroll target: {} (index: {})", target.getParent(), i);
					nonMoveNoScrollTargetParams.add(pair);
				}
			}
		}

		// 全体撮影時に渡す追加パラメータ
		ScreenshotParams[] additionalParams = extractScreenshotParams(nonMoveNoScrollTargetParams);

		// 全体スクリーンショットを取得・非部分スクロール要素のパラメータ更新
		ScreenshotParams entireScreenshotParams = new ScreenshotParams(ScreenAreaWrapper.fromArea(
				ScreenArea.of(SelectorType.TAG_NAME, "body"), this, null).get(0), new ArrayList<ScreenAreaWrapper>(),
				hiddenElements, false, false, 0);

		LOG.debug("[TakeScreenshot (entire screenshot start)]");
		TargetResult entireScreenshotResult = getTargetResult(new CompareTarget(), hiddenElementSelectors,
				entireScreenshotParams, additionalParams);
		LOG.debug("[TakeScreenshot (entire screenshot finished)]");
		ScreenshotImage entireScreenshotImage = entireScreenshotResult.getImage();

		List<TargetResult> screenshotResults = new ArrayList<TargetResult>();
		// moveしない・部分スクロールしない要素を撮影
		if (nonMoveNoScrollTargetParams.isEmpty()) {
			LOG.debug("[TakeScreenshot (NO non-move non-scroll screenshots)]");
		} else {
			for (Pair<CompareTarget, ScreenshotParams> pair : nonMoveNoScrollTargetParams) {
				LOG.debug("[TakeScreenshot (non-move non-scroll screenshots start)]");
				screenshotResults.add(getTargetResult(pair.getLeft(), hiddenElementSelectors, pair.getRight(),
						entireScreenshotImage));
				LOG.debug("[TakeScreenshot (non-move non-scroll screenshots finished)]");
			}
		}

		// moveしない・部分スクロールさせる要素を撮影
		if (nonMoveScrollTargetParams.isEmpty()) {
			LOG.debug("[TakeScreenshot (NO non-move scroll screenshots)]");
		} else {
			LOG.debug("[TakeScreenshot (non-move scroll screenshots start)]");
			List<TargetResult> nonMoveScreenshots = takeNonMoveScreenshots(entireScreenshotResult,
					hiddenElementSelectors, entireScreenshotParams, nonMoveScrollTargetParams);
			screenshotResults.addAll(nonMoveScreenshots);
			LOG.debug("[TakeScreenshot (non-move scroll screenshots finished)]");
		}

		// moveして撮る要素を撮影
		if (moveTargetParams.isEmpty()) {
			LOG.debug("[TakeScreenshot (NO move screenshots)]");
		} else {
			LOG.debug("[TakeScreenshot (move screenshots start)]");
			for (Pair<CompareTarget, ScreenshotParams> pair : moveTargetParams) {
				TargetResult moveScreenshot = takeMoveScreenshots(pair.getLeft(), hiddenElementSelectors,
						pair.getRight());
				screenshotResults.add(moveScreenshot);
			}
			LOG.debug("[TakeScreenshot (move screenshots finished)]");
		}

		return new ScreenshotResult(screenshotId, screenshotResults, entireScreenshotImage);
	}

	/**
	 * isMoveがfalseに指定されているターゲットを一括で撮影するメソッド。
	 * 
	 * @param entireScreenshotResult 予めページ全体を撮影した際のTargetResult
	 * @param hiddenElementSelectors 撮影時に非表示にする要素のリスト
	 * @param targetParams 撮影対象のターゲットリスト
	 * @param entireScreenshotParams ページ全体撮影用のパラメータ
	 * @return 各ターゲットのTargetResultリスト
	 */
	protected List<TargetResult> takeNonMoveScreenshots(TargetResult entireScreenshotResult,
			List<DomSelector> hiddenElementSelectors, ScreenshotParams entireScreenshotParams,
			List<Pair<CompareTarget, ScreenshotParams>> targetParams) {

		LOG.trace(
				"[TakeNonMoveScrollScreenshots] entireScreenshotResult: {}, hiddenElementSelectors: {}, targetParams: {}, entireScreenshotParams: {}",
				entireScreenshotResult, hiddenElementSelectors, targetParams, entireScreenshotParams);

		String[][] overflowStatus = new String[targetParams.size()][2];
		String[] resizeStatus = new String[targetParams.size()];
		int[] partialScrollNums = new int[targetParams.size()];
		int maxPartialScrollNum = 0;

		for (int i = 0; i < targetParams.size(); i++) {
			Pair<CompareTarget, ScreenshotParams> pair = targetParams.get(i);
			PtlWebElement targetElement = pair.getRight().getTarget().getElement();

			// スクロールバーをhiddenにする
			// スクロールバーの元の状態を覚えておく
			if (canHideElementScrollbar()) {
				overflowStatus[i] = targetElement.getOverflowStatus();
				targetElement.hideScrollBar();
			}
			// リサイズハンドルを消すためリサイズ不可にする
			// 元の状態を覚えておく
			if (canResizeElement()) {
				resizeStatus[i] = targetElement.getResizeStatus();
				targetElement.setNoResizable();
			}

			// 部分スクロールの最大回数を調べる
			partialScrollNums[i] = targetElement.getScrollNum();
			if (maxPartialScrollNum < partialScrollNums[i]) {
				maxPartialScrollNum = partialScrollNums[i];
			}
			LOG.trace("[TakeNonMoveScrollScreenshots] Partial scroll {} times. ({})", partialScrollNums[i], pair
					.getLeft().getCompareArea());

			// スクロールありの場合はスクロール位置をリセット
			if (partialScrollNums[i] > 0) {
				try {
					targetElement.scrollTo(0, 0);
				} catch (InterruptedException e) {
					throw new TestRuntimeException(e);
				}
			}
		}
		LOG.trace("[TakeNonMoveScrollScreenshots] overflow: {}, resize: {}", overflowStatus, resizeStatus);

		// 部分スクロールを含めたターゲットのスクリーンショットを撮影
		List<BufferedImage> screenshots = takeNonMoveScrollTargetScreenshots(hiddenElementSelectors,
				entireScreenshotParams, targetParams, maxPartialScrollNum, partialScrollNums);

		List<TargetResult> nonMoveNoScrollTargetResults = new ArrayList<TargetResult>();
		for (int i = 0; i < targetParams.size(); i++) {
			Pair<CompareTarget, ScreenshotParams> pair = targetParams.get(i);

			// サイズ情報を結合後の高さに更新する
			RectangleArea targetPosition = pair.getRight().getTarget().getArea();
			pair.getRight()
					.getTarget()
					.setArea(
							new RectangleArea(targetPosition.getX(), targetPosition.getY(), targetPosition.getWidth(),
									screenshots.get(i).getHeight()));
			LOG.trace("[TakeNonMoveScrollScreenshots] update target position {}", pair.getRight().getTarget().getArea());

			// 結果セットに追加
			ScreenAreaResult targetAreaResult = createScreenAreaResult(pair.getRight().getTarget(), pair.getRight()
					.getIndex());
			List<ScreenAreaResult> excludes = Lists.transform(pair.getRight().getExcludes(),
					new Function<ScreenAreaWrapper, ScreenAreaResult>() {
						@Override
						public ScreenAreaResult apply(ScreenAreaWrapper input) {
							return createScreenAreaResult(input, null);
						}
					});
			TargetResult tResult = new TargetResult(null, targetAreaResult, excludes,
					isMoveTargetRequired(pair.getRight()), hiddenElementSelectors, new ScreenshotImage(
							screenshots.get(i)), pair.getLeft().getOptions());
			nonMoveNoScrollTargetResults.add(tResult);
		}

		// スクロールバーを元に戻す
		if (canHideElementScrollbar()) {
			for (int i = 0; i < targetParams.size(); i++) {
				PtlWebElement el = targetParams.get(i).getRight().getTarget().getElement();
				el.setOverflowStatus(overflowStatus[i][0], overflowStatus[i][1]);
			}
		}

		// リサイズを元に戻す
		if (canResizeElement()) {
			for (int i = 0; i < targetParams.size(); i++) {
				PtlWebElement el = targetParams.get(i).getRight().getTarget().getElement();
				if (resizeStatus[i] != null) {
					el.setResizeStatus(resizeStatus[i]);
				}
			}
		}

		return nonMoveNoScrollTargetResults;
	}

	/**
	 * isMoveがtrueに指定されているターゲットを撮影するメソッド。
	 * 
	 * @param target 撮影対象のターゲット
	 * @param hiddenElementSelectors 撮影時に非表示にする要素のリスト
	 * @param params 撮影対象のパラメータ
	 * @return ターゲットのTargetResult
	 */
	protected TargetResult takeMoveScreenshots(CompareTarget target, List<DomSelector> hiddenElementSelectors,
			ScreenshotParams params) {

		LOG.trace("[TakeMoveScreenshot] target: {}, hiddenElementSelectors: {}, params: {}", target,
				hiddenElementSelectors, params);

		// 部分スクロールなしの場合
		if (!target.isScrollTarget()) {
			LOG.trace("[TakeMoveScreenshot] partial scroll is not required for ({})", target.getCompareArea());
			return getTargetResult(target, hiddenElementSelectors, params);
		}

		// 部分スクロールありの場合

		PtlWebElement el = params.getTarget().getElement();

		// スクロールバーの元の状態を覚えておく
		String[] overflowStatus = null;
		if (canHideElementScrollbar()) {
			String[] statuses = el.getOverflowStatus();
			overflowStatus = Arrays.copyOf(statuses, statuses.length);
			// スクロールバーを非表示にする
			el.hideScrollBar();
		}

		String resizeStatus = null;
		if (canResizeElement()) {
			// リサイズの元の状態を覚えておく
			resizeStatus = el.getResizeStatus();
			// リサイズ不可にする
			el.setNoResizable();
		}

		// 部分スクロールを含めたターゲットのスクリーンショットを撮影
		BufferedImage screenshot = takeMoveScrollTargetScreenshot(target, hiddenElementSelectors, params);

		// サイズ情報を結合後の高さに更新する
		RectangleArea targetPosition = params.getTarget().getArea();
		params.getTarget().setArea(
				new RectangleArea(targetPosition.getX(), targetPosition.getY(), targetPosition.getWidth(), screenshot
						.getHeight()));
		LOG.trace("[TakeMoveScreenshots] update target position {}", params.getTarget().getArea());

		// TargetResult for target area
		ScreenAreaResult targetAreaResult = createScreenAreaResult(params.getTarget(), params.getIndex());
		// TargetResult for exclude areas
		List<ScreenAreaResult> excludes = Lists.transform(params.getExcludes(),
				new Function<ScreenAreaWrapper, ScreenAreaResult>() {
					@Override
					public ScreenAreaResult apply(ScreenAreaWrapper input) {
						return createScreenAreaResult(input, null);
					}
				});

		// スクロールバーを元の状態に戻す
		if (canHideElementScrollbar()) {
			el.setOverflowStatus(overflowStatus[0], overflowStatus[1]);
		}

		// リサイズを元の状態に戻す
		if (canResizeElement() && resizeStatus != null) {
			el.setResizeStatus(resizeStatus);
		}

		return new TargetResult(null, targetAreaResult, excludes, params.isMoveTarget(), hiddenElementSelectors,
				new ScreenshotImage(screenshot), target.getOptions());
	}

	/**
	 * ターゲットをスクロールしながら撮影し、結合した画像を返します。
	 * 
	 * @param hiddenElementSelectors 撮影時に非表示にする要素のリスト
	 * @param entireScreenshotParams ページ全体を撮影するためのパラメータ
	 * @param targetParams 撮影対象のパラメータ群
	 * @param maxPartialScrollNum 要素の最大スクロール回数
	 * @param partialScrollNums 各要素のスクロール回数
	 * @return ターゲットの画像リスト
	 */
	private List<BufferedImage> takeNonMoveScrollTargetScreenshots(List<DomSelector> hiddenElementSelectors,
			ScreenshotParams entireScreenshotParams, List<Pair<CompareTarget, ScreenshotParams>> targetParams,
			int maxPartialScrollNum, int[] partialScrollNums) {
		LOG.debug("[TakeNonMoveScrollScreenshots (capture start)] maximum partial scroll times: {}",
				maxPartialScrollNum);

		List<List<BufferedImage>> allTargetScreenshots = new ArrayList<List<BufferedImage>>();
		int targetSize = targetParams.size();
		for (int i = 0; i < targetSize; i++) {
			allTargetScreenshots.add(new ArrayList<BufferedImage>());
		}
		long[] partialScrollAmounts = new long[targetSize];
		long[] partialScrollTops = new long[targetSize];

		// 全体撮影時に渡す追加パラメータ
		ScreenshotParams[] additionalParams = extractScreenshotParams(targetParams);

		// 全てのtargetがスクロールし終わるまで、全体撮影→切り抜き→各targetスクロール を繰り返す
		for (int i = 0; i <= maxPartialScrollNum; i++) {
			// 全体スクリーンショットを撮影
			TargetResult entireResult = getTargetResult(new CompareTarget(), hiddenElementSelectors,
					entireScreenshotParams, additionalParams);
			ScreenshotImage entireScreenshotImage = entireResult.getImage();
			LOG.trace("[TakeNonMoveScrollScreenshots (captured)] ({}) TargetResult: {}", entireScreenshotImage,
					entireResult);

			// scaleを計算（初回のみ）
			if (i == 0) {
				scale = calcScale(getCurrentPageWidth(), entireScreenshotImage.get().getWidth());
				LOG.trace("[TakeNonMoveScrollScreenshots] scale: {}", scale);
			}

			// 各targetの処理
			for (int j = 0; j < targetSize; j++) {
				Pair<CompareTarget, ScreenshotParams> pair = targetParams.get(j);
				PtlWebElement targetElement = pair.getRight().getTarget().getElement();

				// 自身の必要スクロール回数に達していなければ切り抜いて保存
				if (i <= partialScrollNums[j]) {
					// 全体画像から切り抜く（同時に座標値を切り抜き後の値に更新）
					TargetResult targetPartResult = getTargetResult(pair.getLeft(), hiddenElementSelectors,
							pair.getRight(), entireScreenshotImage);
					allTargetScreenshots.get(j).add(targetPartResult.getImage().get());
				}

				// 必要スクロール回数に達していなければスクロールしておく
				if (i < partialScrollNums[j]) {
					try {
						long currentScrollAmount = targetElement.scrollNext();
						if (currentScrollAmount == 0) {
							partialScrollNums[j] = i;
						} else {
							partialScrollAmounts[j] = currentScrollAmount;
						}
						LOG.debug("[TakeNonMoveScrollScreenshots] Partial scroll amount({}): {}", j,
								currentScrollAmount);
					} catch (InterruptedException e) {
						throw new TestRuntimeException(e);
					}

				} else if (i != 0) {
					// 最後までスクロールしたらスクロール位置を保持しておく
					partialScrollTops[j] = Math.round(targetElement.getCurrentScrollTop());
				}
			}

		}
		LOG.debug("[TakeNonMoveScrollScreenshots (capture finished)]");

		// 必要に応じてborderを切り取る
		trimNonMoveBorder(allTargetScreenshots, targetParams, scale);

		// 必要に応じてpaddingを切り取る
		trimNonMovePadding(allTargetScreenshots, targetParams);

		LOG.debug("[TakeNonMoveScrollScreenshots (image processing start)]");

		// 撮影した全ターゲットの末尾をそれぞれtrim
		for (int i = 0; i < allTargetScreenshots.size(); i++) {
			List<BufferedImage> targetScreenshots = allTargetScreenshots.get(i);
			PtlWebElement targetElement = targetParams.get(i).getRight().getTarget().getElement();
			trimBottomImage(targetScreenshots, partialScrollAmounts[i], targetElement, scale);
		}

		List<BufferedImage> screenshots = new ArrayList<>();
		for (int i = 0; i < targetSize; i++) {
			Pair<CompareTarget, ScreenshotParams> pair = targetParams.get(i);
			// Exclude領域の座標を更新
			for (ScreenAreaWrapper wrapper : pair.getRight().getExcludes()) {
				wrapper.setArea(wrapper.getArea().move(0, partialScrollTops[i] * scale));
			}
			// 画像の結合
			List<BufferedImage> targetScreenshots = allTargetScreenshots.get(i);
			screenshots.add(ImageUtils.verticalMerge(targetScreenshots));
		}

		LOG.debug("[TakeNonMoveScrollScreenshots (image processing finished)]");

		return screenshots;
	}

	/**
	 * ターゲットをスクロールしながら撮影し、結合した画像を返します。
	 * 
	 * @param target 撮影対象のターゲット
	 * @param hiddenElementSelectors 撮影時に非表示にする要素
	 * @param params 撮影対象のパラメータ
	 * @return ターゲットの画像
	 */
	private BufferedImage takeMoveScrollTargetScreenshot(CompareTarget target,
			List<DomSelector> hiddenElementSelectors, ScreenshotParams params) {

		PtlWebElement el = params.getTarget().getElement();

		// 可視範囲のサイズを調べる
		long clientHeight = el.getClientHeight();
		LOG.debug("[TakeMoveScreenshot (capture start)] clientHeight: {}", clientHeight);

		double captureTop = 0d;
		long scrollTop = -1L;
		long currentScrollAmount = -1;
		List<Double> allCaptureTop = new ArrayList<Double>();

		List<BufferedImage> images = new ArrayList<BufferedImage>();
		try {
			// 最上部までスクロール
			el.scrollTo(0d, 0d);
			// スクロール位置を確認
			long currentScrollTop = Math.round(el.getCurrentScrollTop());

			// 要素のスクロール回数を調べておく
			long scrollNum = el.getScrollNum();
			int currentScrollNum = 0;

			while (scrollTop != currentScrollTop && currentScrollNum <= scrollNum) {
				if (currentScrollAmount < 0) {
					currentScrollAmount = 0;
				} else {
					currentScrollAmount = currentScrollTop - scrollTop;
				}
				scrollTop = currentScrollTop;
				LOG.trace("[TakeMoveScreenshot] scrollTop: {}, scrollAmount: {}", scrollTop, currentScrollAmount);

				// 可視範囲のスクリーンショットを撮影
				BufferedImage image = getTargetResult(target, hiddenElementSelectors, params).getImage().get();
				allCaptureTop.add(captureTop);

				// scaleを計算（初回のみ）
				if (currentScrollNum == 0) {
					scale = calcScale(el.getDoubleValueRect().getWidth(), image.getWidth());
					LOG.trace("[TakeMoveScreenshot] scale: {}", scale);
				}

				// 結果セットに追加
				images.add(image);

				// 次のキャプチャ開始位置を設定
				double scrollIncrement = 0;
				scrollIncrement = clientHeight;
				captureTop += scrollIncrement;
				LOG.debug("[TakeMoveScreenshot] Scroll increment: {}", scrollIncrement);

				// 次の撮影位置までスクロール
				el.scrollNext();
				currentScrollNum++;

				// スクロール位置を確認
				currentScrollTop = Math.round(el.getCurrentScrollTop());
			}
		} catch (InterruptedException e) {
			throw new TestRuntimeException(e);
		}
		LOG.debug("[TakeMoveScreenshot (capture finished)]");

		// borderがある場合は切り取る
		trimMoveBorder(el, images, scale);

		// paddingがある場合は切り取る
		trimMovePadding(el, images);

		// 末尾の重複をトリム
		trimBottomImage(images, currentScrollAmount, el, scale);

		// Exclude領域の座標を更新
		for (ScreenAreaWrapper wrapper : params.getExcludes()) {
			wrapper.setArea(wrapper.getArea().move(0, scrollTop * scale));
		}

		LOG.debug("[TakeMoveScreenshot (image processing start)]");
		BufferedImage screenshot = ImageUtils.verticalMerge(images);
		LOG.debug("[TakeMoveScreenshot (image processing finished)]");

		return screenshot;
	}

	/**
	 * 指定されたパラメータ群から、{@link ScreenshotParams}だけを抜き出して配列にします。
	 * 
	 * @param params 対象のパラメータ群
	 * @return {@link ScreenshotParams}の配列
	 */
	private ScreenshotParams[] extractScreenshotParams(List<Pair<CompareTarget, ScreenshotParams>> params) {
		int size = params.size();
		ScreenshotParams[] extractedParams = new ScreenshotParams[size];
		for (int i = 0; i < size; i++) {
			extractedParams[i] = params.get(i).getRight();
		}
		return extractedParams;
	}

	/**
	 * 指定されたリスト内の最後の画像の高さ、スクロール量、ボーダー幅から<br>
	 * トリム量を計算し、縦の重複をトリムします。
	 * 
	 * @param images 対象の画像
	 * @param lastScrollAmount 最後のスクロール量
	 * @param el 撮影対象の要素
	 * @param currentScale 表示領域とスクリーンショットのサイズ比
	 */
	protected void trimBottomImage(List<BufferedImage> images, long lastScrollAmount, PtlWebElement el,
			double currentScale) {
		LOG.trace("(trimBottomImage) lastScrollAmount: {}, el: {}, currentScroll: {}", lastScrollAmount, el,
				currentScale);

		int size = images.size();
		// 画像が1枚しかないときは何もしない
		if (size <= 1) {
			return;
		}

		BufferedImage lastImage = images.get(size - 1);
		int trimTop = calcTrimTop(lastImage.getHeight(), lastScrollAmount, el, currentScale);
		LOG.trace("(trimBottomImage) trim: {}", trimTop);

		if (trimTop > 0 && trimTop < lastImage.getHeight()) {
			images.set(size - 1, ImageUtils.trim(lastImage, trimTop, 0, 0, 0));
		}
	}

	/**
	 * 指定されたリスト内の最後の画像の幅、スクロール量、ボーダー幅から<br>
	 * トリム量を計算し、横の重複をトリムします。
	 * 
	 * @param images 対象の画像
	 * @param lastScrollAmount 最後のスクロール量
	 * @param el 撮影対象の要素
	 * @param currentScale 表示領域とスクリーンショットのサイズ比
	 */
	protected void trimRightImage(List<BufferedImage> images, long lastScrollAmount, PtlWebElement el,
			double currentScale) {
		LOG.trace("(trimRightImage) lastScrollAmount: {}, el: {}, currentScroll: {}", lastScrollAmount, el,
				currentScale);

		int size = images.size();
		// 画像が1枚しかないときは何もしない
		if (size <= 1) {
			return;
		}

		BufferedImage lastImage = images.get(size - 1);
		int trimLeft = calcTrimLeft(lastImage.getWidth(), lastScrollAmount, el, currentScale);
		LOG.trace("(trimRightImage) trim: {}", trimLeft);

		if (trimLeft > 0 && trimLeft < lastImage.getWidth()) {
			images.set(size - 1, ImageUtils.trim(lastImage, 0, trimLeft, 0, 0));
		}
	}

	/**
	 * 要素の部分スクロール時、元画像からボーダーを切り取ります。
	 * 
	 * @param el 対象の要素
	 * @param image 元画像
	 * @param num 何スクロール目の画像化
	 * @param size 全体のスクロール数
	 * @return ボーダーを切り取ったBufferedImage
	 */
	protected BufferedImage trimTargetBorder(WebElement el, BufferedImage image, int num, int size, double currentScale) {
		LOG.trace("(trimTargetBorder) el: {}; image[w: {}, h: {}], num: {}, size: {}", el, image.getWidth(),
				image.getHeight(), num, size);

		WebElementBorderWidth targetBorder = ((PtlWebElement) el).getBorderWidth();

		int trimTop = 0;
		int trimBottom = 0;
		if (size > 1) {
			if (num <= 0) {
				trimBottom = (int) Math.round(targetBorder.getBottom() * currentScale);
			} else if (num >= size - 1) {
				trimTop = (int) Math.round(targetBorder.getTop() * currentScale);
			} else {
				trimBottom = (int) Math.round(targetBorder.getBottom() * currentScale);
				trimTop = (int) Math.round(targetBorder.getTop() * currentScale);
			}
		}

		LOG.trace("(trimTargetBorder) top: {}, bottom: {}", trimTop, trimBottom);
		return ImageUtils.trim(image, trimTop, 0, trimBottom, 0);
	}

	/**
	 * 要素の部分スクロール時、元画像からPaddingを切り取ります。<br>
	 * スクロール
	 * 
	 * @param el 対象の要素
	 * @param image 元画像
	 * @param num 何スクロール目の画像か
	 * @param size 全体のスクロール数
	 * @return Paddingを切り取ったBufferedImage
	 */
	protected BufferedImage trimTargetPadding(WebElement el, BufferedImage image, int num, int size) {
		LOG.trace("(trimTargetPadding) el: {}, image[w: {}, h: {}], num: {}, size: {}", el, image.getWidth(),
				image.getHeight(), num, size);

		WebElementPadding targetPadding = ((PtlWebElement) el).getPadding();

		int trimTop = 0;
		int trimBottom = 0;
		if (size > 1) {
			if (num <= 0) {
				trimBottom = (int) Math.round(targetPadding.getBottom() * scale);
			} else if (num >= size - 1) {
				trimTop = (int) Math.round(targetPadding.getTop() * scale);
			} else {
				trimBottom = (int) Math.round(targetPadding.getBottom() * scale);
				trimTop = (int) Math.round(targetPadding.getTop() * scale);
			}
		}

		LOG.trace("(trimTargetPadding) top: {}, bottom: {}", trimTop, trimBottom);
		return ImageUtils.trim(image, trimTop, 0, trimBottom, 0);
	}

	/**
	 * isMoveがfalseのとき、要素のborderを切り取る処理をします。
	 * 
	 * @param allTargetScreenshots 撮影した全スクリーンショット
	 * @param targetParams 撮影対象のターゲットリスト
	 */
	protected void trimNonMoveBorder(List<List<BufferedImage>> allTargetScreenshots,
			List<Pair<CompareTarget, ScreenshotParams>> targetParams, double currentScale) {
		LOG.trace("[Trim non-move elements' border]");
		for (int i = 0; i < allTargetScreenshots.size(); i++) {
			PtlWebElement targetElement = targetParams.get(i).getRight().getTarget().getElement();
			// 部分スクロール有の場合はborderを切り取る
			if (!targetElement.isBody() && targetParams.get(i).getLeft().isScrollTarget()) {
				List<BufferedImage> targetScreenshots = allTargetScreenshots.get(i);
				for (int j = 0; j < targetScreenshots.size(); j++) {
					targetScreenshots.set(
							j,
							trimTargetBorder(targetElement, targetScreenshots.get(j), j, targetScreenshots.size(),
									currentScale));
				}
			}

		}
	}

	/**
	 * isMoveがtrueのとき、要素のborderを切り取る処理をします。
	 * 
	 * @param el ターゲットの要素
	 * @param images 撮影したスクリーンショット
	 */
	protected void trimMoveBorder(WebElement el, List<BufferedImage> images, double currentScale) {
		LOG.trace("[Trim move element's border]");
		for (int i = 0; i < images.size(); i++) {
			images.set(i, trimTargetBorder(el, images.get(i), i, images.size(), currentScale));
		}
	}

	/**
	 * isMoveがfalseのとき、要素のpaddingを切り取る処理をします。
	 * 
	 * @param allTargetScreenshots 撮影した全スクリーンショット
	 * @param targetParams 撮影対象のターゲットリスト
	 */
	protected void trimNonMovePadding(List<List<BufferedImage>> allTargetScreenshots,
			List<Pair<CompareTarget, ScreenshotParams>> targetParams) {
		LOG.trace("[Trim non-move elements' padding]");
	}

	/**
	 * isMoveがtrueのとき、要素のpaddingを切り取る処理をします。
	 * 
	 * @param el ターゲットの要素
	 * @param images 撮影したスクリーンショット
	 */
	protected void trimMovePadding(WebElement el, List<BufferedImage> images) {
		LOG.trace("[Trim move element's padding]");
	}

	/**
	 * 次のキャプチャ開始位置と今回キャプチャした範囲を比較し、重なる部分がある場合は切り取ります。
	 * 
	 * @param captureTop 今回のキャプチャ開始位置（上）
	 * @param captureLeft 今回のキャプチャ開始位置（左）
	 * @param windowHeight ウィンドウ（viewport内の表示領域）の高さ
	 * @param windowWidth ウィンドウ（viewport内の表示領域）の幅
	 * @param currentScale ウィンドウとスクリーンショットのサイズ比
	 * @param img スクリーンショット画像
	 * @return 重複を切り取った画像
	 */
	protected BufferedImage trimOverlap(double captureTop, double captureLeft, long windowHeight, long windowWidth,
			double currentScale, BufferedImage img) {
		LOG.trace("(TrimOverlap) image[w: {}, h:{}], top: {}, left: {}, windowWidth: {}, windowHeight: {}",
				img.getWidth(), img.getHeight(), captureTop, captureLeft, windowWidth, windowHeight);

		BufferedImage image = img;

		// 右端の推定位置（次スクロール時に左に来る位置）と、実際のキャプチャに写っている右端の位置を比較
		long calculatedRightValue = Math.round((captureLeft + windowWidth) * currentScale);
		long actualRightValue = Math.round(captureLeft * currentScale) + img.getWidth();
		int trimWidth = calculatedRightValue < actualRightValue ? (int) (actualRightValue - calculatedRightValue) : 0;

		// 下端の推定位置（次スクロール時にトップに来る位置）と、実際のキャプチャに写っている下端の位置を比較
		long calculatedBottomValue = Math.round((captureTop + windowHeight) * currentScale);
		long actualBottomValue = Math.round(captureTop * currentScale) + img.getHeight();
		int trimHeight = calculatedBottomValue < actualBottomValue ? (int) (actualBottomValue - calculatedBottomValue)
				: 0;

		// 余分にキャプチャに写っていたら切り取っておく
		LOG.trace("(TrimOverlap) right(calc: {}, actual: {}), bottom(calc: {}, actual: {})", calculatedRightValue,
				actualRightValue, calculatedBottomValue, actualBottomValue);
		if (trimWidth > 0 || trimHeight > 0) {
			image = image.getSubimage(0, 0, image.getWidth() - trimWidth, image.getHeight() - trimHeight);
		}

		return image;
	}

	/**
	 * 一つのターゲットに対するスクリーンショットを撮影し、{@link TargetResult}として取得します。
	 * 
	 * @param compareTarget 撮影対象とする範囲
	 * @param hiddenElementSelectors 撮影時に非表示にする要素のセレクタのリスト
	 * @param params スクリーンショットを撮影するためのパラメーター
	 * @param additionalParams 追加のスクリーンショット撮影用パラメータ。スクリーンショットは{@code params}
	 *            に設定した情報から撮影しますが、同時にこの引数に設定したパラメータの座標情報が更新されます。
	 * @return スクリーンショット撮影結果
	 */
	protected TargetResult getTargetResult(CompareTarget compareTarget, List<DomSelector> hiddenElementSelectors,
			ScreenshotParams params, ScreenshotParams... additionalParams) {
		ScreenshotImage image = getScreenshotImage(params, additionalParams);

		// TargetResult for target area
		ScreenAreaResult targetAreaResult = createScreenAreaResult(params.getTarget(), params.getIndex());

		// TargetResult for exclude areas
		List<ScreenAreaResult> excludes = Lists.transform(params.getExcludes(),
				new Function<ScreenAreaWrapper, ScreenAreaResult>() {
					@Override
					public ScreenAreaResult apply(ScreenAreaWrapper input) {
						return createScreenAreaResult(input, null);
					}
				});

		return new TargetResult(null, targetAreaResult, excludes, isMoveTargetRequired(params), hiddenElementSelectors,
				image, compareTarget.getOptions());
	}

	/**
	 * スクリーンショットを受け取り、ターゲットの領域を切り抜いて{@link TargetResult}として返します。
	 * 
	 * @param compareTarget 撮影対象の範囲
	 * @param hiddenElementSelectors 撮影時に非表示にする要素のセレクタのリスト
	 * @param params スクリーンショットを撮影するためのパラメーター
	 * @param image スクリーンショット画像
	 * @return {@link TargetResult}オブジェクト
	 */
	protected TargetResult getTargetResult(CompareTarget compareTarget, List<DomSelector> hiddenElementSelectors,
			ScreenshotParams params, ScreenshotImage image) {
		BufferedImage bi = image.get();
		ScreenshotImage targetImage;
		RectangleArea targetArea = params.getTarget().getArea();
		if (targetArea.getX() != 0d || targetArea.getY() != 0d || targetArea.getWidth() != bi.getWidth()
				|| targetArea.getHeight() != bi.getHeight()) {
			if (targetArea.getWidth() == 0d || targetArea.getHeight() == 0d) {
				targetImage = new ScreenshotImage();
			} else {
				// Crop image and reset elements' area
				targetImage = new ScreenshotImage(cropScreenshotImage(bi, params));
			}
		} else {
			targetImage = image;
		}

		// TargetResult for target area
		ScreenAreaResult targetAreaResult = createScreenAreaResult(params.getTarget(), params.getIndex());

		// TargetResult for exclude areas
		List<ScreenAreaResult> excludes = Lists.transform(params.getExcludes(),
				new Function<ScreenAreaWrapper, ScreenAreaResult>() {
					@Override
					public ScreenAreaResult apply(ScreenAreaWrapper input) {
						return createScreenAreaResult(input, null);
					}
				});

		return new TargetResult(null, targetAreaResult, excludes, isMoveTargetRequired(params), hiddenElementSelectors,
				targetImage, compareTarget.getOptions());
	}

	/**
	 * {@link ScreenAreaWrapper}から{@link ScreenAreaResult}を生成します。
	 * 
	 * @param target 元となるScreenAreaWrapper
	 * @param index 対象とする要素のインデックス
	 * @return {@link ScreenAreaResult}
	 */
	protected ScreenAreaResult createScreenAreaResult(ScreenAreaWrapper target, Integer index) {
		DomSelector selector = target.getSelector();
		// Rectangle
		if (selector == null) {
			return new ScreenAreaResult(null, target.getArea(), target.getParent());
		}

		// DOM
		return new ScreenAreaResult(new IndexDomSelector(selector, index), target.getArea(), target.getParent());
	}

	/**
	 * スクリーンショットを撮影し、{@link ScreenshotImage}として取得します。
	 * 
	 * @param params スクリーンショット撮影用パラメータ
	 * @param additionalParams 追加のスクリーンショット撮影用パラメータ。スクリーンショットは{@code params}
	 *            に設定した情報から撮影しますが、同時にこの引数に設定したパラメータの座標情報が更新されます。
	 * @return 撮影したスクリーンショット
	 */
	protected ScreenshotImage getScreenshotImage(ScreenshotParams params, ScreenshotParams... additionalParams) {
		LOG.trace("[GetScreenshotImage start]");

		Object documentOverflow = null;

		// Hide scrollbar
		if (canHideBodyScrollbar()) {
			// Backup default overflow value
			Map<String, Object> object = executeJavaScript(SCRIPT_GET_DEFAULT_DOCUMENT_OVERFLOW);
			documentOverflow = object.get("overflow");
			LOG.trace("[GetScreenshotImage] Hide scrollbar. origin: {}", documentOverflow);

			executeScript(SCRIPT_SET_DOCUMENT_OVERFLOW, "hidden");
		}

		updateScreenWrapperStatus(0d, 0d, params, additionalParams);
		params.updateInitialArea();

		// Check target element size
		RectangleArea area = params.getTarget().getArea().floor();
		LOG.trace("[GetScreenshotImage] target area size: {}", area);
		if (area.getWidth() == 0d || area.getHeight() == 0d) {
			LOG.debug("[GetScreenshotImage] Target element is empty. (target: {}, index: {})", params.getTarget()
					.getParent(), params.getIndex());
			if (canHideBodyScrollbar()) {
				executeScript(SCRIPT_SET_DOCUMENT_OVERFLOW, documentOverflow);
			}

			return new ScreenshotImage();
		}

		if (isHideElementsRequired()) {
			for (PtlWebElement element : params.getHiddenElements()) {
				element.hide();
			}
		}

		// Do not move if the target is "body" element.
		LOG.debug("[GetScreenshotImage (capture start)]");
		BufferedImage fullScreenshot;
		if (isMoveTargetRequired(params)) {
			fullScreenshot = getScreenshotInternal(params);
		} else {
			fullScreenshot = getScreenshotInternalWithoutMoving(params, additionalParams);
		}
		LOG.debug("[GetScreenshotImage (capture finished)] w: {}, h: {}", fullScreenshot.getWidth(),
				fullScreenshot.getHeight());

		if (isHideElementsRequired()) {
			for (PtlWebElement element : params.getHiddenElements()) {
				element.show();
			}
		}

		// Restore scrollbar
		if (canHideBodyScrollbar()) {
			executeScript(SCRIPT_SET_DOCUMENT_OVERFLOW, documentOverflow);
		}

		// Crop screenshot
		BufferedImage targetImage = cropScreenshotImage(fullScreenshot, params);

		LOG.trace("[GetScreenshotImage finished]");

		// MEMO Driverからではテスト実行クラス情報が分からないので、この時点ではメタデータに何も設定しない
		return new ScreenshotImage(targetImage);
	}

	/**
	 * スクリーンショット画像をスクリーンショットパラメータで指定されたターゲット要素を切り抜き、除外領域の座標も切り抜き後の値に更新します。
	 * 
	 * @param image スクリーンショット画像
	 * @param params スクリーンショットパラメータ
	 * @return 切り抜いた画像
	 */
	private BufferedImage cropScreenshotImage(BufferedImage image, ScreenshotParams params) {
		RectangleArea targetArea = params.getTarget().getArea();
		RectangleArea floorTargetArea = targetArea.round();
		LOG.debug("[CropScreenshot] image[w: {}, h: {}], target: {} ({})", image.getWidth(), image.getHeight(),
				floorTargetArea, targetArea);

		// Don't crop image when the target element is "body"
		if (params.getTarget().isBody()) {
			int width = image.getWidth();
			if (width < floorTargetArea.getX() + floorTargetArea.getWidth()) {
				width -= (int) floorTargetArea.getX();
			} else {
				width = (int) floorTargetArea.getWidth();
			}
			int height = image.getHeight();
			if (height < floorTargetArea.getY() + floorTargetArea.getHeight()) {
				height -= (int) floorTargetArea.getY();
			} else {
				height = (int) floorTargetArea.getHeight();
			}
			params.getTarget()
					.setArea(new RectangleArea(floorTargetArea.getX(), floorTargetArea.getY(), width, height));
			LOG.trace("[CropScreenshot] Did not crop image (element is body). Image area: {}", params.getTarget()
					.getArea());
			return image;
		}

		// (width + x) と (height + y) が画像サイズを超えないようにする
		int maxCropWidth = (int) Math.min(floorTargetArea.getX() + floorTargetArea.getWidth(), image.getWidth());
		int maxCropHeight = (int) Math.min(floorTargetArea.getY() + floorTargetArea.getHeight(), image.getHeight());
		LOG.trace("[CropScreenshot] ({})", new RectangleArea((int) floorTargetArea.getX(),
				(int) floorTargetArea.getY(), maxCropWidth - (int) floorTargetArea.getX(), maxCropHeight
						- (int) floorTargetArea.getY()));

		BufferedImage targetImage = image.getSubimage((int) floorTargetArea.getX(), (int) floorTargetArea.getY(),
				maxCropWidth - (int) floorTargetArea.getX(), maxCropHeight - (int) floorTargetArea.getY());

		double deltaX = -floorTargetArea.getX();
		double deltaY = -floorTargetArea.getY();

		params.getTarget().setArea(new RectangleArea(0d, 0d, targetImage.getWidth(), targetImage.getHeight()));
		LOG.trace("[CropScreenshot] new area: {}", params.getTarget().getArea());

		LOG.trace("[CropScreenshot] Move excludes. (deltaX: {}, deltaY: {})", deltaX, deltaY);
		for (ScreenAreaWrapper wrapper : params.getExcludes()) {
			wrapper.setArea(wrapper.getArea().move(deltaX, deltaY));
		}

		return targetImage;
	}

	/**
	 * スクリーンショット撮影時に、対象領域を定位置に移動させるか否かを調べます。
	 * 
	 * @param params スクリーンショット撮影用パラメータ
	 * @return 移動するか否か。移動する場合はtrue。
	 */
	protected boolean isMoveTargetRequired(ScreenshotParams params) {
		return params.isMoveTarget() && !params.getTarget().isBody() && canMoveTarget();
	}

	/**
	 * スクリーンショット撮影時に、対象領域をスクロールさせるか否かを調べます。
	 * 
	 * @param params スクリーンショット撮影用パラメータ
	 * @return スクロールするか否か。スクロールする場合はtrue。
	 */
	protected boolean isScrollTargetRequired(ScreenshotParams params) {
		return params.isScrollTarget() && !params.getTarget().isBody();
	}

	/**
	 * 対象領域を移動せずにスクリーンショットを撮影します。
	 * 
	 * @param params スクリーンショット撮影用パラメータ
	 * @param additionalParams スクリーンショット撮影用パラメータ
	 * @return 撮影したスクリーンショット
	 */
	protected BufferedImage getScreenshotInternalWithoutMoving(ScreenshotParams params,
			ScreenshotParams... additionalParams) {
		BufferedImage image = getMinimumScreenshot(params);
		updateScreenWrapperStatus(0d, 0d, params, additionalParams);

		return image;
	}

	/**
	 * 対象領域を定位置（0, 0)に移動し、スクリーンショットを撮影します。
	 * 
	 * @param params スクリーンショット撮影用パラメータ
	 * @param additionalParams スクリーンショット撮影用パラメータ
	 * @return 撮影したスクリーンショット
	 */
	protected BufferedImage getScreenshotInternal(ScreenshotParams params, ScreenshotParams... additionalParams) {
		LOG.debug("[GetMoveScreenshot] target: {}; index: {}", params.getTarget().getParent(), params.getIndex());

		// Backup default body style values
		Map<String, Object> originalStyle = executeJavaScript(SCRIPT_GET_DEFAULT_BODY_STYLE);
		LOG.trace("[GetMoveScreenshot] Original style: {}", originalStyle);

		// Set body width
		executeScript("document.body.style.width = arguments[0]", originalStyle.get("scrollWidth") + "px");

		executeScript(SCRIPT_MOVE_BODY, "absolute", "", "");

		// Get target element position
		ScreenAreaWrapper target = params.getTarget();
		target.updatePosition(getScreenshotScale());
		RectangleArea moveAmount = target.getArea();

		// Move body position
		LOG.debug("[GetMoveScreenshot] Move body (amount = x: {}, y: {})", -moveAmount.getX(), -moveAmount.getY());
		executeScript(SCRIPT_MOVE_BODY, "absolute", String.format("%spx", -moveAmount.getY()),
				String.format("%spx", -moveAmount.getX()));

		BufferedImage image = getMinimumScreenshot(params);

		updateScreenWrapperStatus(moveAmount.getX(), moveAmount.getY(), params, additionalParams);

		// Restore body width
		String width = originalStyle.get("width") == null ? "" : originalStyle.get("width").toString();
		executeScript("document.body.style.width = arguments[0]", width);

		// Restore body position
		String pos = originalStyle.get("position") == null ? "" : originalStyle.get("position").toString();
		String top = originalStyle.get("top") == null ? "" : originalStyle.get("top").toString();
		String left = originalStyle.get("left") == null ? "" : originalStyle.get("left").toString();

		LOG.debug("[GetMoveScreenshot] Restore body position. (width: {}, position: {}, top: {}, left: {})", width,
				pos, top, left);
		executeScript(SCRIPT_MOVE_BODY, pos, top, left);

		return image;
	}

	/**
	 * {@link DomSelector}を指定して対応する要素の一覧を取得します。
	 * 
	 * @param selectors 取得する要素のセレクタのリスト
	 * @return 取得した要素のリスト
	 */
	protected List<PtlWebElement> findElementsByDomSelectors(List<DomSelector> selectors) {
		List<PtlWebElement> elements = new ArrayList<PtlWebElement>();
		if (selectors == null || selectors.isEmpty()) {
			return elements;
		}

		for (DomSelector selector : selectors) {
			for (WebElement element : selector.getType().findElements(this, selector.getValue())) {
				elements.add((PtlWebElement) element);
			}
		}
		return elements;
	}

	/**
	 * {@code params}、{@code additionalParams}内の座標値を、表示スケール、移動量を加味して更新します。
	 * 
	 * @param moveX x方向の移動量
	 * @param moveY y方向の移動量
	 * @param params スクリーンショットの更新元パラメータ
	 * @param additionalParams スクリーンショットの更新元パラメータ
	 */
	private void updateScreenWrapperStatus(double moveX, double moveY, ScreenshotParams params,
			ScreenshotParams... additionalParams) {
		// Scroll to top
		try {
			scrollTo(0d, 0d);
		} catch (InterruptedException e) {
			throw new TestRuntimeException(e);
		}

		double currentScale = getScreenshotScale();
		LOG.debug("(UpdateScreenWrapperStatus) moveX: {}, moveY: {}, scale: {}", moveX, moveY, currentScale);

		updateScreenWrapperStatus(moveX, moveY, currentScale, params);
		for (ScreenshotParams p : additionalParams) {
			updateScreenWrapperStatus(moveX, moveY, currentScale, p);
		}
	}

	/**
	 * {@code params}、{@code additionalParams}内の座標値を、表示スケール、移動量を加味して更新します。
	 * 
	 * @param moveX x方向の移動量
	 * @param moveY y方向の移動量
	 * @param currentScale 表示スケール
	 * @param params スクリーンショットの更新元パラメータ
	 */
	private void updateScreenWrapperStatus(double moveX, double moveY, double currentScale, ScreenshotParams params) {
		LOG.trace("(UpdateScreenWrapperStatus) moveX: {}, moveY: {}, scale: {} => {}", moveX, moveY, currentScale,
				params.getTarget().getParent());
		params.getTarget().updatePosition(currentScale, moveX, moveY);

		for (ScreenAreaWrapper wrapper : params.getExcludes()) {
			wrapper.updatePosition(currentScale, moveX, moveY);
		}
	}

	/**
	 * スクリーンショットとviewportのサイズ比を取得します。
	 * 
	 * @return サイズ比。PCの場合は1.0
	 */
	protected double getScreenshotScale() {
		return DEFAULT_SCREENSHOT_SCALE;
	}

	/**
	 * 画面全体のスクリーンショットを撮影し、{@link BufferedImage}として返します。
	 * 
	 * @return 撮影したスクリーンショット
	 */
	public BufferedImage getEntirePageScreenshot() {
		return getScreenshotAsBufferedImage();
	}

	/**
	 * パラメータで指定された要素を含む最小範囲でスクリーンショットを撮影し、{@link BufferedImage}として返します。<br>
	 * 
	 * @param params スクリーンショット撮影用パラメータ
	 * @return 撮影したスクリーンショット
	 */
	protected BufferedImage getMinimumScreenshot(ScreenshotParams params) {
		return getEntirePageScreenshot();
	}

	/**
	 * スクリーンショットを撮影し、{@link BufferedImage}として取得します。
	 * 
	 * @return 撮影したスクリーンショット
	 */
	protected final BufferedImage getScreenshotAsBufferedImage() {
		try {
			byte[] data = getScreenshotAs(OutputType.BYTES);
			return ImageIO.read(new ByteArrayInputStream(data));
		} catch (IOException e) {
			throw new TestRuntimeException("Screenshot capture error", e);
		}
	}

	//</editor-fold>

	//<editor-fold desc="getWidth/Height">

	/**
	 * 現在表示位置の上端座標を取得します。
	 * 
	 * @return 座標のy値（実数px）
	 */
	public double getCurrentScrollTop() {
		double max = 0d;
		for (String value : SCRIPTS_SCROLL_TOP) {
			try {
				double current = Double.parseDouble(executeScript("return " + value).toString());
				max = Math.max(max, current);
			} catch (Exception e) {
				LOG.debug("(GetCurrentScrollTop) unexpected error", e);
			}
		}
		LOG.trace("(GetCurrentScrollTop) [{}]", max);
		return max;
	}

	/**
	 * 現在表示位置の左端座標を取得します。
	 * 
	 * @return 座標のx値（実数px）
	 */
	public double getCurrentScrollLeft() {
		double max = 0d;
		for (String value : SCRIPTS_SCROLL_LEFT) {
			try {
				double current = Double.parseDouble(executeScript("return " + value).toString());
				max = Math.max(max, current);
			} catch (Exception e) {
				LOG.debug("(GetCurrentScrollLeft) unexpected error", e);
			}
		}
		LOG.trace("(GetCurrentScrollLeft) [{}]", max);
		return max;
	}

	/**
	 * 可視領域の幅を取得します。
	 * 
	 * @return 可視領域の幅（整数px）
	 */
	public long getWindowWidth() {
		return executeJavaScript(GET_WINDOW_WIDTH_SCRIPT);
	}

	/**
	 * 可視領域の高さを取得します。
	 * 
	 * @return 可視領域の高さ（整数px）
	 */
	public long getWindowHeight() {
		return executeJavaScript(GET_WINDOW_HEIGHT_SCRIPT);
	}

	/**
	 * ページのscrollWidthを取得します。
	 * 
	 * @return scrollWidth（整数px）
	 */
	public long getScrollWidth() {
		PtlWebElement bodyElement = (PtlWebElement) findElementByTagName("body");
		return executeJavaScript(GET_SCROLL_WIDTH_SCRIPT, bodyElement);
	}

	/**
	 * ページのscrollHeightを取得します。
	 * 
	 * @return scrollHeight（整数px）
	 */
	public long getScrollHeight() {
		PtlWebElement bodyElement = (PtlWebElement) findElementByTagName("body");
		return executeJavaScript(GET_SCROLL_HEIGHT_SCRIPT, bodyElement);
	}

	/**
	 * ページのスクロール回数を取得します。
	 * 
	 * @return スクロール回数
	 */
	public long getScrollNum() {
		double clientHeight = getWindowHeight();
		double scrollHeight = getScrollHeight() + 1;

		if (clientHeight >= scrollHeight) {
			return 0;
		}

		return (int) (Math.ceil(scrollHeight / clientHeight)) - 1;
	}

	/**
	 * 現在のページの幅を取得します。
	 * 
	 * @return ページの幅（整数px）
	 */
	public long getCurrentPageWidth() {
		// 現在のスクロール位置を取得
		double scrollTop = getCurrentScrollTop();
		double scrollLeft = getCurrentScrollLeft();
		double pageWidth = 0;

		try {
			// bodyの絶対座標を取得
			PtlWebElement bodyElement = (PtlWebElement) findElementByTagName("body");
			scrollTo(0d, 0d);
			Number bodyLeft = executeJavaScript(GET_BODY_LEFT_SCRIPT, bodyElement);

			// 一番右までスクロールした状態からbodyの相対座標を取得
			long scrollWidth = getScrollWidth();
			WebElementMargin margin = bodyElement.getMargin();
			// ページの幅 + 小数点以下の誤差対策で+1した座標へスクロール
			double totalWidth = scrollWidth + margin.getLeft() + margin.getRight() + 1;
			scrollTo(totalWidth, 0d);
			Number relativeBodyLeft = executeJavaScript(GET_BODY_LEFT_SCRIPT, bodyElement);

			// leftの移動量とウィンドウ幅からページ全体の高さを計算
			LOG.trace("(GetCurrentPageWidth) relativeBodyLeft: {}, bodyLeft: {}, margin: {}", relativeBodyLeft,
					bodyLeft, margin.getLeft());
			pageWidth = -relativeBodyLeft.doubleValue() + bodyLeft.doubleValue() + getWindowWidth();

			// スクロール位置を元に戻す
			scrollTo(scrollLeft, scrollTop);
		} catch (InterruptedException e) {
			throw new TestRuntimeException(e);
		}

		long result = Math.round(pageWidth);
		LOG.trace("(GetCurrentPageWidth) [{}] ({})", result, pageWidth);
		return result;
	}

	/**
	 * 現在のページの高さを取得します。
	 * 
	 * @return ページの高さ（整数px）
	 */
	public long getCurrentPageHeight() {
		// 現在のスクロール位置を取得
		double scrollTop = getCurrentScrollTop();
		double scrollLeft = getCurrentScrollLeft();
		double pageHeight = 0;

		try {
			// bodyの絶対座標を取得
			PtlWebElement bodyElement = (PtlWebElement) findElementByTagName("body");
			scrollTo(0d, 0d);
			Number bodyTop = executeJavaScript(GET_BODY_TOP_SCRIPT, bodyElement);

			// 一番下までスクロールした状態からbodyの相対座標を取得
			long scrollHeight = getScrollHeight();
			WebElementMargin margin = bodyElement.getMargin();
			// ページの高さ + 小数点以下の誤差対策で+1した座標へスクロール
			double totalHeight = scrollHeight + margin.getTop() + margin.getBottom() + 1;
			scrollTo(0d, totalHeight);
			Number relativeBodyTop = executeJavaScript(GET_BODY_TOP_SCRIPT, bodyElement);

			// topの移動量とウィンドウ高さからページ全体の高さを計算
			LOG.trace("(GetCurrentPageHeight) relativeBodyTop: {}, bodyTop: {}, margin: {}", relativeBodyTop, bodyTop,
					margin.getTop());
			pageHeight = -relativeBodyTop.doubleValue() + bodyTop.doubleValue() + getWindowHeight();

			// スクロール位置を元に戻す
			scrollTo(scrollLeft, scrollTop);
		} catch (InterruptedException e) {
			throw new TestRuntimeException(e);
		}

		long result = Math.round(pageHeight);
		LOG.trace("(GetCurrentPageHeight) [{}] ({})", result, pageHeight);
		return result;
	}

	/**
	 * 画面上の特定の位置にスクロールします。
	 * 
	 * @param x スクロール先のx座標（実数px）
	 * @param y スクロール先のy座標（実数px）
	 * @throws InterruptedException スクロール中に例外が発生した場合
	 */
	public void scrollTo(double x, double y) throws InterruptedException {
		executeScript("window.scrollTo(arguments[0], arguments[1])", x, y);
		Thread.sleep(SCROLL_WAIT_MS);
	}

	/**
	 * viewport内の表示領域とスクリーンショットのサイズ比を計算します。
	 * 
	 * @param windowWidth ウィンドウ（viewport内の表示領域）の幅
	 * @param imageWidth スクリーンショットの幅
	 * @return サイズ比。PCの場合は1
	 */
	protected double calcScale(double windowWidth, double imageWidth) {
		return DEFAULT_SCREENSHOT_SCALE;
	}

	/**
	 * 画像の高さ、スクロール量、要素のボーダー幅から縦のトリム量を計算します。
	 * 
	 * @param imageHeight 元画像の高さ
	 * @param scrollAmount 最後のスクロール量
	 * @param targetElement ターゲット
	 * @param currentScale 表示領域とスクリーンショットのサイズ比
	 * @return trim量
	 */
	protected int calcTrimTop(int imageHeight, long scrollAmount, PtlWebElement targetElement, double currentScale) {
		int borderWidth = 0;
		if (!targetElement.isBody()) {
			WebElementBorderWidth border = targetElement.getBorderWidth();
			borderWidth = (int) Math.round(border.getTop());
		}
		int trimTop = imageHeight - (int) Math.round(scrollAmount * currentScale)
				- (int) Math.round(borderWidth * currentScale);
		LOG.trace("(CalcTrimTop) imageHeight: {}, scrollAmount: {}, element: {} => {}", imageHeight, scrollAmount,
				targetElement, trimTop);
		return trimTop;
	}

	/**
	 * 画像の幅、スクロール量、要素のボーダー幅から横のトリム量を計算します。
	 * 
	 * @param imageWidth 元画像の幅
	 * @param scrollAmount 最後のスクロール量
	 * @param targetElement ターゲット（nullの場合は無視）
	 * @param currentScale 表示領域とスクリーンショットのサイズ比
	 * @return trim量
	 */
	protected int calcTrimLeft(int imageWidth, long scrollAmount, PtlWebElement targetElement, double currentScale) {
		int borderWidth = 0;
		if (!targetElement.isBody()) {
			WebElementBorderWidth border = targetElement.getBorderWidth();
			borderWidth = (int) Math.round(border.getLeft());
		}
		int trimLeft = imageWidth - (int) Math.round(scrollAmount * currentScale)
				- (int) Math.round(borderWidth * currentScale);
		return trimLeft;
	}

	/**
	 * driverに対応するWebElementを生成します。
	 * 
	 * @return WebElement
	 */
	protected abstract PtlWebElement newPtlWebElement();

	//</editor-fold>

	//<editor-fold desc="JsonToPtlWebElementConverter">

	/**
	 * JSONデータをWebElementに変換するクラス
	 */
	static class JsonToPtlWebElementConverter extends JsonToWebElementConverter {

		private final PtlWebDriver driver;

		/**
		 * コンストラクタ
		 * 
		 * @param driver 親WebDriver
		 */
		JsonToPtlWebElementConverter(PtlWebDriver driver) {
			super(driver);
			this.driver = driver;
		}

		@Override
		protected PtlWebElement newRemoteWebElement() {
			PtlWebElement element = driver.newPtlWebElement();
			element.setParent(driver);
			return element;
		}
	}

	//</editor-fold>

}
