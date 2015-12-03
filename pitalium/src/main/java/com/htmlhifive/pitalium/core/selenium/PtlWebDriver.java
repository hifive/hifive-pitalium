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

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.internal.JsonToWebElementConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.htmlhifive.pitalium.common.exception.TestRuntimeException;
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
	// CHECKSTYLE:ON
	//@formatter:on

	protected final Logger LOG = LoggerFactory.getLogger(getClass());
	private final PtlCapabilities capabilities;
	private String baseUrl;
	private double scale = DEFAULT_SCREENSHOT_SCALE;

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
	 * 指定されたURLをブラウザで開きます。urlがhttp://またはhttps://から始まる場合はそのまま、それ以外の場合は{@link #baseUrl} + urlのアドレスを開きます。
	 *
	 * @param url 開きたいURL
	 */
	@Override
	public void get(String url) {
		String targetUrl = UrlUtils.getTargetUrl(baseUrl, url);
		LOG.debug("BaseUrl: {}, url: {}, targetUrl: {}", baseUrl, url, targetUrl);

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

	/**
	 * 要素を移動可能なブラウザかどうかを取得します。
	 *
	 * @return 移動可能なブラウザならtrue、不可ならfalse
	 */
	protected boolean canMoveTarget() {
		return true;
	}

	/**
	 * スクロールバーを非表示にできるブラウザかどうかを取得します。
	 *
	 * @return 非表示にできる場合はtrue、できない場合はfalse
	 */
	protected boolean canHideScrollbar() {
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

		List<PtlWebElement> hiddenElements = findElementsByDomSelectors(hiddenElementSelectors);

		// CompareTarget => ScreenshotParams
		List<Pair<CompareTarget, ScreenshotParams>> moveTargetParams = new ArrayList<Pair<CompareTarget, ScreenshotParams>>();
		List<Pair<CompareTarget, ScreenshotParams>> nonMoveTargetParams = new ArrayList<Pair<CompareTarget, ScreenshotParams>>();
		for (CompareTarget compareTarget : cTarget) {
			List<ScreenAreaWrapper> targets = ScreenAreaWrapper.fromArea(compareTarget.getCompareArea(), this, null);
			for (int i = 0; i < targets.size(); i++) {
				ScreenAreaWrapper target = targets.get(i);
				List<ScreenAreaWrapper> excludes = new ArrayList<ScreenAreaWrapper>();
				for (ScreenArea exclude : compareTarget.getExcludes()) {
					excludes.addAll(target.getChildWrapper(exclude));
				}

				Pair<CompareTarget, ScreenshotParams> pair = Pair.of(compareTarget, new ScreenshotParams(target,
						excludes, hiddenElements, compareTarget.isMoveTarget(), i));
				if (isMoveTargetRequired(pair.getRight())) {
					moveTargetParams.add(pair);
				} else {
					nonMoveTargetParams.add(pair);
				}
			}
		}

		// 全体撮影時に渡す追加パラメータ
		int nonMoveTargetSize = nonMoveTargetParams.size();
		ScreenshotParams[] additionalParams = new ScreenshotParams[nonMoveTargetSize];
		for (int i = 0; i < nonMoveTargetSize; i++) {
			additionalParams[i] = nonMoveTargetParams.get(i).getRight();
		}

		// 最後に返す用の全体スクリーンショットを取得・パラメータ更新
		// TODO: NonMoveのときに撮ったものを使いまわす
		ScreenshotParams entireScreenshotParams = new ScreenshotParams(ScreenAreaWrapper.fromArea(
				ScreenArea.of(SelectorType.TAG_NAME, "body"), this, null).get(0), new ArrayList<ScreenAreaWrapper>(),
				hiddenElements, false, 0);
		TargetResult entireScreenshotResult = getTargetResult(new CompareTarget(), hiddenElementSelectors,
				entireScreenshotParams, additionalParams);
		ScreenshotImage entireScreenshotImage = entireScreenshotResult.getImage();

		List<TargetResult> screenshotResults = new ArrayList<TargetResult>();
		// moveせずに撮る要素を撮影
		List<TargetResult> nonMoveScreenshots = takeNonMoveScreenshots(hiddenElementSelectors, nonMoveTargetParams,
				entireScreenshotParams, additionalParams);
		screenshotResults.addAll(nonMoveScreenshots);

		// moveして撮る要素を撮影
		for (Pair<CompareTarget, ScreenshotParams> pair : moveTargetParams) {
			TargetResult moveScreenshot = takeMoveScreenshots(pair.getLeft(), hiddenElementSelectors, pair.getRight());
			screenshotResults.add(moveScreenshot);
		}

		return new ScreenshotResult(screenshotId, screenshotResults, entireScreenshotImage);
	}

	/**
	 * isMoveがfalseに指定されているターゲットを一括で撮影するメソッド。
	 *
	 * @param hiddenElementSelectors 撮影時に非表示にする要素のリスト
	 * @param targetParams 撮影対象のターゲットリスト
	 * @param entireScreenshotParams ページ全体撮影用のパラメータ
	 * @param additionalParams 更新用パラメータ
	 * @return 各ターゲットのTargetResultリスト
	 */
	protected List<TargetResult> takeNonMoveScreenshots(List<DomSelector> hiddenElementSelectors,
			List<Pair<CompareTarget, ScreenshotParams>> targetParams, ScreenshotParams entireScreenshotParams,
			ScreenshotParams... additionalParams) {

		int nonMoveTargetSize = targetParams.size();
		double currentScale = Double.NaN;

		// TODO: 元の状態を覚えておいて復元する
		// スクロールバーをhiddenにする
		for (Pair<CompareTarget, ScreenshotParams> pair : targetParams) {
			WebElement el = pair.getLeft().getCompareArea().getSelector().getType()
					.findElement(this, pair.getLeft().getCompareArea().getSelector().getValue());
			if (!capabilities.getPlatformName().equals("ANDROID")) {
				((PtlWebElement) el).hideScrollBar();
			}
			// textareaの場合はリサイズ不可にする
			((PtlWebElement) el).setNoResizable();
		}

		// 部分スクロールの最大回数を調べる
		int partialScrollNums[] = new int[nonMoveTargetSize];
		int maxPartialScrollNum = 0;
		for (int i = 0; i < nonMoveTargetSize; i++) {
			PtlWebElement targetElement = (PtlWebElement) (targetParams.get(i).getLeft().getCompareArea().getSelector()
					.getType().findElement(this, targetParams.get(i).getLeft().getCompareArea().getSelector()
					.getValue()));
			partialScrollNums[i] = targetElement.getScrollNum();
			if (maxPartialScrollNum < partialScrollNums[i]) {
				maxPartialScrollNum = partialScrollNums[i];
			}
		}

		// 全てのtargetがスクロールし終わるまで、全体撮影→切り抜き→各targetスクロール を繰り返す
		List<List<TargetResult>> allTargetScreenshots = new ArrayList<List<TargetResult>>();
		for (int i = 0; i < targetParams.size(); i++) {
			allTargetScreenshots.add(new ArrayList<TargetResult>());
		}
		long partialScrollAmounts[] = new long[nonMoveTargetSize];
		// スクロール位置をリセット
		for (int i = 0; i < nonMoveTargetSize; i++) {
			PtlWebElement targetElement = (PtlWebElement) (targetParams.get(i).getLeft().getCompareArea().getSelector()
					.getType().findElement(this, targetParams.get(i).getLeft().getCompareArea().getSelector()
					.getValue()));
			targetElement.scrollTo(0, 0);
		}

		for (int i = 0; i <= maxPartialScrollNum; i++) {
			// 全体スクリーンショットを撮影
			ScreenshotImage entireScreenshotImage = getTargetResult(new CompareTarget(), hiddenElementSelectors,
					entireScreenshotParams, additionalParams).getImage();

			// scaleを計算（初回のみ）
			if (Double.isNaN(currentScale)) {
				currentScale = calcScale(getCurrentPageWidth(), entireScreenshotImage.get().getWidth());
				scale = currentScale;
			}

			// 各targetの処理
			for (int j = 0; j < targetParams.size(); j++) {
				Pair<CompareTarget, ScreenshotParams> pair = targetParams.get(j);
				// 自身の必要スクロール回数に達していなければ切り抜いて保存
				if (i <= partialScrollNums[j]) {
					// 全体画像から切り抜く
					TargetResult targetPartResult = getTargetResult(pair.getLeft(), hiddenElementSelectors,
							pair.getRight(), entireScreenshotImage);

					// 結果セットに追加
					allTargetScreenshots.get(j).add(targetPartResult);
				}
			}

			// 各targetをスクロール
			for (int j = 0; j < targetParams.size(); j++) {
				if (i < partialScrollNums[j]) {
					PtlWebElement targetElement = (PtlWebElement) (targetParams.get(j).getLeft().getCompareArea()
							.getSelector().getType().findElement(this, targetParams.get(j).getLeft().getCompareArea()
							.getSelector().getValue()));
					int scrollAmount = targetElement.scrollNext();
					partialScrollAmounts[j] = scrollAmount;
				}
			}

		}

		// 必要に応じてborderを切り取る
		trimNonMoveBorder(allTargetScreenshots, targetParams);

		// 必要に応じてpaddingを切り取る
		trimNonMovePadding(allTargetScreenshots, targetParams);

		// 撮影した全ターゲットの末尾をそれぞれtrim
		for (int i = 0; i < allTargetScreenshots.size(); i++) {
			List<TargetResult> targetScreenshots = allTargetScreenshots.get(i);
			if (targetScreenshots.size() > 1) {
				TargetResult lastResult = targetScreenshots.get(targetScreenshots.size() - 1);
				BufferedImage lastImage = lastResult.getImage().get();
				PtlWebElement targetElement = (PtlWebElement) (targetParams.get(i).getLeft().getCompareArea()
						.getSelector().getType().findElement(this, targetParams.get(i).getLeft().getCompareArea()
						.getSelector().getValue()));
				int trimTop = calcTrimTop(lastImage.getHeight(), partialScrollAmounts[i], targetElement);
				if (trimTop == lastImage.getHeight()) {
					trimTop = 0;
				}
				LOG.debug("trimTop: " + trimTop);
				targetScreenshots.set(targetScreenshots.size() - 1, new TargetResult(null, lastResult.getTarget(),
						lastResult.getExcludes(), lastResult.isMoveTarget(), lastResult.getHiddenElementSelectors(),
						new ScreenshotImage(ImageUtils.trim(lastImage, trimTop, 0, 0, 0)), lastResult.getOptions()));
			}
		}

		// 結果画像の結合
		List<TargetResult> nonMoveTargetResults = new ArrayList<TargetResult>();
		for (int i = 0; i < targetParams.size(); i++) {
			Pair<CompareTarget, ScreenshotParams> pair = targetParams.get(i);
			List<TargetResult> targetScreenshots = allTargetScreenshots.get(i);
			// 全画像を結合したときの幅・高さを調べる
			int imageWidth = 0;
			int imageHeight = 0;
			for (TargetResult result : targetScreenshots) {
				if (imageWidth == 0) {
					imageWidth = result.getImage().get().getWidth();
				}
				imageHeight += result.getImage().get().getHeight();
			}

			// 画像の結合
			BufferedImage screenshot = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
			Graphics graphics = screenshot.getGraphics();
			int nextTop = 0;
			for (TargetResult result : targetScreenshots) {
				graphics.drawImage(result.getImage().get(), 0, nextTop, null);
				nextTop += result.getImage().get().getHeight();
			}
			ScreenshotImage image = new ScreenshotImage(screenshot);

			// TargetResult for target areas
			ScreenAreaResult targetAreaResult = createScreenAreaResult(pair.getRight().getTarget(), pair.getRight()
					.getIndex());
			// TargetResult for exclude areas
			List<ScreenAreaResult> excludes = Lists.transform(pair.getRight().getExcludes(),
					new Function<ScreenAreaWrapper, ScreenAreaResult>() {
						@Override
						public ScreenAreaResult apply(ScreenAreaWrapper input) {
							return createScreenAreaResult(input, null);
						}
					});
			TargetResult tResult = new TargetResult(null, targetAreaResult, excludes,
					isMoveTargetRequired(pair.getRight()), hiddenElementSelectors, image, pair.getLeft().getOptions());
			nonMoveTargetResults.add(tResult);
		}

		return nonMoveTargetResults;
	}

	/**
	 * isMoveがtrueに指定されているターゲットを撮影するメソッド。
	 *
	 * @param target 撮影対象のターゲット
	 * @param hiddenElementSelectors 撮影時に非表示にする要素のリスト
	 * @param params 撮影対象のターゲットリスト
	 * @return 各ターゲットのTargetResultリスト
	 */
	protected TargetResult takeMoveScreenshots(CompareTarget target, List<DomSelector> hiddenElementSelectors,
			ScreenshotParams params) {
		WebElement el = target.getCompareArea().getSelector().getType()
				.findElement(this, target.getCompareArea().getSelector().getValue());
		WebElementPadding targetPadding = ((PtlWebElement) el).getPadding();

		// TODO: 元の状態を覚えておいて復元する
		// スクロールバーをhiddenにする
		if (!capabilities.getPlatformName().equals("ANDROID")) {
			((PtlWebElement) el).hideScrollBar();
		}
		// textareaの場合はリサイズ不可にする
		((PtlWebElement) el).setNoResizable();

		// 可視範囲のサイズを調べる
		long clientHeight = ((PtlWebElement) el).getClientHeight();
		long clientWidth = ((PtlWebElement) el).getClientWidth();

		double captureTop = 0d;
		long scrollTop = -1L;
		double currentScale = Double.NaN;
		long currentScrollAmount = -1;
		List<Double> allCaptureTop = new ArrayList<Double>();

		List<BufferedImage> images = new ArrayList<BufferedImage>();
		try {
			// 次の撮影位置までスクロール
			((PtlWebElement) el).scrollTo(0d, 0d);
			// スクロール完了を待つためのwait
			Thread.sleep(100L);

			// スクロール位置を確認
			long currentScrollTop = ((PtlWebElement) el).getCurrentScrollTop();
			while (scrollTop != currentScrollTop) {
				if (currentScrollAmount < 0) {
					currentScrollAmount = 0;
				} else {
					currentScrollAmount = currentScrollTop - scrollTop;
				}
				scrollTop = currentScrollTop;

				// 可視範囲のスクリーンショットを撮影
				BufferedImage image = getScreenshotImage(params).get();
				allCaptureTop.add(captureTop);

				// scaleを計算（初回のみ）
				if (Double.isNaN(currentScale)) {
					currentScale = calcScale(clientWidth, image.getWidth());
					scale = currentScale;
				}

				// 結果セットに追加
				images.add(image);

				// 次のキャプチャ開始位置を設定
				double scrollIncrement = 0;
				scrollIncrement = clientHeight;
				captureTop += scrollIncrement;

				// 次の撮影位置までスクロール
				((PtlWebElement) el).scrollNext();
				Thread.sleep(100L);

				// スクロール位置を確認
				currentScrollTop = ((PtlWebElement) el).getCurrentScrollTop();
			}
		} catch (InterruptedException e) {
			throw new TestRuntimeException(e);
		}

		// borderがある場合は切り取る
		trimMoveBorder(el, images);

		// paddingがある場合は切り取る
		trimMovePadding(el, images);

		// 画像間の重なりを切り取る
		if (scale != DEFAULT_SCREENSHOT_SCALE) {
			for (int i = 0; i < images.size(); i++) {
				long windowHeight = clientHeight;
				long windowWidth = clientWidth;
				if (el.getTagName().equals("textarea") && capabilities.getBrowserName().equals("firefox")) {
					if (i <= 0) {
						windowHeight += (int) Math.round(targetPadding.getTop());
					} else if (i >= images.size() - 1) {
						windowHeight += (int) Math.round(targetPadding.getBottom());
					}
				}
				images.set(i, trimOverlap(allCaptureTop.get(i), 0, windowHeight, windowWidth, images.get(i)));
			}
		}

		// 末尾の重複をトリム
		if (images.size() > 1) {
			BufferedImage lastImage = images.get(images.size() - 1);
			int trimTop = calcTrimTop(lastImage.getHeight(), currentScrollAmount, (PtlWebElement) el);
			LOG.debug("trimTop: " + trimTop);

			if (trimTop > 0 && trimTop < lastImage.getHeight()) {
				images.set(images.size() - 1, ImageUtils.trim(lastImage, trimTop, 0, 0, 0));
			}
		}

		// 結合後の画像サイズを調べる
		int totalHeight = 0;
		int totalWidth = -1;
		for (BufferedImage image : images) {
			totalHeight += image.getHeight();
			if (totalWidth < 0) {
				totalWidth = image.getWidth();
			}
		}

		// 画像の結合
		BufferedImage screenshot = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
		Graphics graphics = screenshot.getGraphics();
		int nextTop = 0;
		for (BufferedImage image : images) {
			graphics.drawImage(image, 0, nextTop, null);
			nextTop += image.getHeight();
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

		return new TargetResult(null, targetAreaResult, excludes, params.isMoveTarget(), hiddenElementSelectors,
				new ScreenshotImage(screenshot), target.getOptions());
	}

	/**
	 * 元画像からボーダーを切り取る。
	 *
	 * @param el 対象の要素
	 * @param originalResult
	 * @param image 元画像
	 * @return ボーダーを切り取ったBufferedImage
	 */
	protected BufferedImage trimTargetBorder(WebElement el, BufferedImage image, int num, int size) {
		WebElementBorderWidth targetBorder = ((PtlWebElement) el).getBorderWidth();

		int trimTop = 0;
		int trimBottom = 0;
		if (size > 1) {
			if (num <= 0) {
				trimBottom = (int) Math.round(targetBorder.getBottom() * scale);
			} else if (num >= size - 1) {
				trimTop = (int) Math.round(targetBorder.getTop() * scale);
			} else {
				trimBottom = (int) Math.round(targetBorder.getBottom() * scale);
				trimTop = (int) Math.round(targetBorder.getTop() * scale);
			}
		}

		return ImageUtils.trim(image, trimTop, 0, trimBottom, 0);
	}

	/**
	 * 元画像からPaddingを切り取る。<br>
	 * スクロール
	 *
	 * @param el 対象の要素
	 * @param image 元画像
	 * @param num 何スクロール目の画像か
	 * @param size 全体のスクロール数
	 * @return Paddingを切り取ったBufferedImage
	 */
	protected BufferedImage trimTargetPadding(WebElement el, BufferedImage image, int num, int size) {
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

		return ImageUtils.trim(image, trimTop, 0, trimBottom, 0);
	}

	/**
	 * isMoveがfalseのとき、要素のborderを切り取る処理をする。
	 *
	 * @param allTargetScreenshots 撮影した全スクリーンショット
	 * @param targetParams 撮影対象のターゲットリスト
	 */
	protected void trimNonMoveBorder(List<List<TargetResult>> allTargetScreenshots,
			List<Pair<CompareTarget, ScreenshotParams>> targetParams) {
		for (int i = 0; i < allTargetScreenshots.size(); i++) {
			PtlWebElement targetElement = (PtlWebElement) (targetParams.get(i).getLeft().getCompareArea().getSelector()
					.getType().findElement(this, targetParams.get(i).getLeft().getCompareArea().getSelector()
					.getValue()));
			List<TargetResult> targetScreenshots = allTargetScreenshots.get(i);
			for (int j = 0; j < targetScreenshots.size(); j++) {
				TargetResult oldResult = targetScreenshots.get(j);
				targetScreenshots.set(
						j,
						new TargetResult(null, oldResult.getTarget(), oldResult.getExcludes(),
								oldResult.isMoveTarget(), oldResult.getHiddenElementSelectors(), new ScreenshotImage(
										trimTargetBorder(targetElement, oldResult.getImage().get(), j,
												targetScreenshots.size())), oldResult.getOptions()));
			}

		}
	}

	/**
	 * isMoveがtrueのとき、要素のborderを切り取る処理をする。
	 *
	 * @param el ターゲットの要素
	 * @param images 撮影したスクリーンショット
	 */
	protected void trimMoveBorder(WebElement el, List<BufferedImage> images) {
		for (int i = 0; i < images.size(); i++) {
			images.set(i, trimTargetBorder(el, images.get(i), i, images.size()));
		}
	}

	/**
	 * isMoveがfalseのとき、要素のpaddingを切り取る処理をする。
	 *
	 * @param allTargetScreenshots 撮影した全スクリーンショット
	 * @param targetParams 撮影対象のターゲットリスト
	 */
	protected void trimNonMovePadding(List<List<TargetResult>> allTargetScreenshots,
			List<Pair<CompareTarget, ScreenshotParams>> targetParams) {
	}

	/**
	 * isMoveがtrueのとき、要素のpaddingを切り取る処理をする。
	 *
	 * @param el ターゲットの要素
	 * @param images 撮影したスクリーンショット
	 */
	protected void trimMovePadding(WebElement el, List<BufferedImage> images) {
	}

	protected BufferedImage trimOverlap(double captureTop, double captureLeft, long windowHeight, long windowWidth,
			BufferedImage img) {
		BufferedImage image = img;
		// 下端の推定位置（次スクロール時にトップに来る位置）と、実際のキャプチャに写っている下端の位置を比較
		long calculatedBottomValue = Math.round((captureTop + windowHeight) * scale);
		long actualBottomValue = Math.round(captureTop * scale) + img.getHeight();
		// 余分にキャプチャに写っていたら切り取っておく
		if (calculatedBottomValue < actualBottomValue) {
			image = image.getSubimage(0, 0, image.getWidth(),
					(int) (image.getHeight() - (actualBottomValue - calculatedBottomValue)));
		}

		// 右端の推定位置（次スクロール時に左に来る位置）と、実際のキャプチャに写っている右端の位置を比較
		long calculatedRightValue = Math.round((captureLeft + windowWidth) * scale);
		long actualRightValue = Math.round(captureLeft * scale) + img.getWidth();
		// 余分にキャプチャに写っていたら切り取っておく
		if (calculatedRightValue < actualRightValue) {
			image = image.getSubimage(0, 0, (int) (image.getWidth() - (actualRightValue - calculatedRightValue)),
					image.getHeight());
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
		Object documentOverflow = null;

		// Hide scrollbar
		if (canHideScrollbar()) {
			// Backup default overflow value
			Map<String, Object> object = executeJavaScript(SCRIPT_GET_DEFAULT_DOCUMENT_OVERFLOW);
			documentOverflow = object.get("overflow");

			executeScript(SCRIPT_SET_DOCUMENT_OVERFLOW, "hidden");
		}

		updateScreenWrapperStatus(0d, 0d, params, additionalParams);
		params.updateInitialArea();

		// Check target element size
		RectangleArea area = params.getTarget().getArea().floor();
		if (area.getWidth() == 0d || area.getHeight() == 0d) {
			if (canHideScrollbar()) {
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
		BufferedImage fullScreenshot;
		if (isMoveTargetRequired(params)) {
			fullScreenshot = getScreenshotInternal(params);
		} else {
			fullScreenshot = getScreenshotInternalWithoutMoving(params, additionalParams);
		}

		if (isHideElementsRequired()) {
			for (PtlWebElement element : params.getHiddenElements()) {
				element.show();
			}
		}

		// Restore scrollbar
		if (canHideScrollbar()) {
			executeScript(SCRIPT_SET_DOCUMENT_OVERFLOW, documentOverflow);
		}

		// Crop screenshot
		BufferedImage targetImage = cropScreenshotImage(fullScreenshot, params);

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
		RectangleArea floorTargetArea = targetArea.floor();
		//		RectangleArea floorTargetArea = targetArea.round();

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
			return image;
		}

		// (width + x) と (height + y) が画像サイズを超えないようにする
		int maxCropWidth = (int) Math.min(floorTargetArea.getX() + floorTargetArea.getWidth(), image.getWidth());
		int maxCropHeight = (int) Math.min(floorTargetArea.getY() + floorTargetArea.getHeight(), image.getHeight());

		if (LOG.isDebugEnabled()) {
			RectangleArea cropRect = new RectangleArea((int) floorTargetArea.getX(), (int) floorTargetArea.getY(),
					maxCropWidth - (int) floorTargetArea.getX(), maxCropHeight - (int) floorTargetArea.getY());
			LOG.debug("Screenshot: width={}, height={}, Target: {} CropRect: {}", image.getWidth(), image.getHeight(),
					targetArea, cropRect);
		}

		BufferedImage targetImage = image.getSubimage((int) floorTargetArea.getX(), (int) floorTargetArea.getY(),
				maxCropWidth - (int) floorTargetArea.getX(), maxCropHeight - (int) floorTargetArea.getY());

		double deltaX = -floorTargetArea.getX();
		double deltaY = -floorTargetArea.getY();

		params.getTarget().setArea(new RectangleArea(0d, 0d, targetImage.getWidth(), targetImage.getHeight()));
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
		// Backup default body style values
		Map<String, Object> originalStyle = executeJavaScript(SCRIPT_GET_DEFAULT_BODY_STYLE);

		// Set body width
		executeScript("document.body.style.width = arguments[0]", originalStyle.get("scrollWidth") + "px");

		executeScript(SCRIPT_MOVE_BODY, "absolute", "", "");

		// Get target element position
		ScreenAreaWrapper target = params.getTarget();
		target.updatePosition(getScreenshotScale());
		RectangleArea moveAmount = target.getArea();

		// Move body position
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
		scrollTo(0d, 0d);

		double scale = getScreenshotScale();
		updateScreenWrapperStatus(moveX, moveY, scale, params);
		for (ScreenshotParams p : additionalParams) {
			updateScreenWrapperStatus(moveX, moveY, scale, p);
		}
	}

	private void updateScreenWrapperStatus(double moveX, double moveY, double scale, ScreenshotParams params) {
		params.getTarget().updatePosition(scale, moveX, moveY);

		for (ScreenAreaWrapper wrapper : params.getExcludes()) {
			wrapper.updatePosition(scale, moveX, moveY);
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
				LOG.debug("ScrollTop unexpected error", e);
			}
		}
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
				LOG.debug("ScrollLeft unexpected error", e);
			}
		}
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
	 * 現在のページの幅を取得します。
	 *
	 * @return ページの幅（整数px）
	 */
	public long getCurrentPageWidth() {
		// 現在のスクロール位置を取得
		double scrollTop = getCurrentScrollTop();
		double scrollLeft = getCurrentScrollLeft();

		// bodyの絶対座標を取得
		PtlWebElement bodyElement = (PtlWebElement) findElementByTagName("body");
		scrollTo(0d, 0d);
		Number bodyLeft = executeJavaScript(
				"var _bodyLeft = arguments[0].getBoundingClientRect().left; return _bodyLeft;", bodyElement);

		// 一番右までスクロールした状態からbodyの相対座標を取得
		String scrollWidth = executeScript("return arguments[0].scrollWidth", bodyElement).toString();
		WebElementMargin margin = bodyElement.getMargin();
		// ページの幅 + 小数点以下の誤差対策で+1した座標へスクロール
		double totalWidth = Double.parseDouble(scrollWidth) + margin.getLeft() + margin.getRight() + 1;
		scrollTo(totalWidth, 0d);
		Number relativeBodyLeft = executeJavaScript(
				"var _bodyLeft = arguments[0].getBoundingClientRect().left; return _bodyLeft;", bodyElement);

		// leftの移動量とウィンドウ幅からページ全体の高さを計算
		LOG.debug("relativeBodyLeft: {}, bodyLeft: {}, windowWidth: {}, margin: {}", relativeBodyLeft, bodyLeft,
				getWindowWidth(), margin.getLeft());
		double pageWidth = -relativeBodyLeft.doubleValue() + bodyLeft.doubleValue() + getWindowWidth();

		// スクロール位置を元に戻す
		scrollTo(scrollLeft, scrollTop);

		return Math.round(pageWidth);
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

		// bodyの絶対座標を取得
		PtlWebElement bodyElement = (PtlWebElement) findElementByTagName("body");
		scrollTo(0d, 0d);
		Number bodyTop = executeJavaScript("var _bodyTop = arguments[0].getBoundingClientRect().top; return _bodyTop;",
				bodyElement);

		// 一番下までスクロールした状態からbodyの相対座標を取得
		String scrollHeight = executeScript("return arguments[0].scrollHeight", bodyElement).toString();
		WebElementMargin margin = bodyElement.getMargin();
		// ページの高さ + 小数点以下の誤差対策で+1した座標へスクロール
		double totalHeight = Double.parseDouble(scrollHeight) + margin.getTop() + margin.getBottom() + 1;
		scrollTo(0d, totalHeight);
		Number relativeBodyTop = executeJavaScript(
				"var _bodyTop = arguments[0].getBoundingClientRect().top; return _bodyTop;", bodyElement);

		// topの移動量とウィンドウ高さからページ全体の高さを計算
		LOG.debug("relativeBodyTop: {}, bodyTop: {}, windowheight: {}, margin: {}", relativeBodyTop, bodyTop,
				getWindowHeight(), margin.getTop());
		double pageHeight = -relativeBodyTop.doubleValue() + bodyTop.doubleValue() + getWindowHeight();

		// スクロール位置を元に戻す
		scrollTo(scrollLeft, scrollTop);

		return Math.round(pageHeight);
	}

	/**
	 * 画面上の特定の位置にスクロールします。
	 *
	 * @param x スクロール先のx座標（実数px）
	 * @param y スクロール先のy座標（実数px）
	 */
	public void scrollTo(double x, double y) {
		executeScript("window.scrollTo(arguments[0], arguments[1])", x, y);
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
	 * @param imageHeight 元画像の高さ
	 * @param scrollAmount 最後のスクロール量
	 * @param targetElement ターゲット
	 * @return trim量
	 */
	protected int calcTrimTop(int imageHeight, long scrollAmount, PtlWebElement targetElement) {
		WebElementBorderWidth border = targetElement.getBorderWidth();
		int trimTop = imageHeight - (int) Math.round(scrollAmount * scale) - (int) Math.round(border.getTop());
		return trimTop;
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
