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
package com.htmlhifive.pitalium.it.screenshot.fullpage;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
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
import org.openqa.selenium.WebElement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.model.CompareTarget;
import com.htmlhifive.pitalium.core.model.DomSelector;
import com.htmlhifive.pitalium.core.model.ScreenArea;
import com.htmlhifive.pitalium.core.model.ScreenshotResult;
import com.htmlhifive.pitalium.core.model.SelectorType;
import com.htmlhifive.pitalium.core.result.TestResultManager;
import com.htmlhifive.pitalium.core.selenium.PtlWebDriverWait;
import com.htmlhifive.pitalium.core.selenium.PtlWebElement;
import com.htmlhifive.pitalium.core.selenium.WebElementBorderWidth;
import com.htmlhifive.pitalium.image.util.ImageUtils;

/*
 * 横スクロール・部分スクロールの動作テスト用試作クラス
 */
public class TakeScrollScreenshotTest extends PtlTestBase {
	//	private static final String BASE_URL = "http://localhost/pitalium-test/com/htmlhifive/pitalium/it/screenshot/fullpage/testPartialScrollTest.html";
	private static final String BASE_URL = "http://localhost/pitalium-test/com/htmlhifive/pitalium/it/screenshot/fullpage/frameset.html";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static final String[] SCRIPTS_SCROLL_TOP = {
			"arguments[0].contentWindow.document.documentElement.scrollTop",
			"arguments[0].contentWindow.document.body.scrollTop" };
	private static final String[] SCRIPTS_SCROLL_LEFT = {
			"arguments[0].contentWindow.document.documentElement.scrollLeft",
			"arguments[0].contentWindow.document.body.scrollLeft" };

	private static String resultFolderPath;
	private static String currentId = null;

	@BeforeClass
	public static void beforeClass() throws JsonProcessingException, IOException {
		currentId = TestResultManager.getInstance().getCurrentId();
		resultFolderPath = "results" + File.separator + currentId + File.separator
				+ TakeEntirePageScreenshotTest.class.getSimpleName();
		new File(resultFolderPath).mkdirs();
	}

	/**
	 * 横スクロールのテスト。
	 */
	@Test
	public void takeHorizontalScrollScreenshot() {
		driver.get(BASE_URL);
		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();
		assertionView.assertView("horizontal");
	}

	/**
	 * overflow:scroll/auto/hidden に指定されている要素のスクリーンショットを取得するテスト。<br>
	 * 
	 * @throws IOException
	 */
	@Test
	public void takeOverflowScreenshot() throws InterruptedException {
		driver.get(BASE_URL);
		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		DomSelector divSelector = new DomSelector(SelectorType.ID, "about-scroll");
		ScreenArea divScreenArea = ScreenArea.of(divSelector.getType(), divSelector.getValue());

		DomSelector textareaSelector = new DomSelector(SelectorType.ID, "textarea-scroll");
		ScreenArea textScreenArea = ScreenArea.of(textareaSelector.getType(), textareaSelector.getValue());

		DomSelector tbodySelector = new DomSelector(SelectorType.CSS_SELECTOR, "#table-scroll tbody");
		ScreenArea tbodyScreenArea = ScreenArea.of(tbodySelector.getType(), tbodySelector.getValue());

		CompareTarget[] targets = { new CompareTarget(divScreenArea), new CompareTarget(textScreenArea),
				new CompareTarget(tbodyScreenArea) };
		assertionView.assertView("scrollTest", targets);

		saveCapture(divSelector, "takeOverflowScreenshot_div");
		saveCapture(textareaSelector, "takeOverflowScreenshot_textarea");
		saveCapture(tbodySelector, "takeOverflowScreenshot_tbody");

	}

	/**
	 * iframeのスクリーンショットを取得するテスト。<br>
	 * 
	 * @throws IOException
	 */
	@Test
	public void takeIframeScreenshot() {
		driver.get(BASE_URL);

		DomSelector selector = new DomSelector(SelectorType.ID, "fb-scroll");
		ScreenArea iframeScreenArea = ScreenArea.of(selector.getType(), selector.getValue());
		CompareTarget[] targets = { new CompareTarget(iframeScreenArea) };

		//		assertionView.assertView("iframeTest", targets);
		saveCapture(selector, "takeIframeScreenshot");

	}

	@Test
	public void takeFrameScreenshot() {
		driver.get(BASE_URL);

		DomSelector selector = new DomSelector(SelectorType.NAME, "left");
		ScreenArea frameScreenArea = ScreenArea.of(selector.getType(), selector.getValue());
		CompareTarget[] targets = { new CompareTarget(frameScreenArea) };
		saveCapture(selector, "takeFrameScreenshot");
		assertionView.assertView("frameTest");
	}

	/**
	 * domSelectorで指定したDOM要素の、部分スクロールを展開したスクリーンショットを撮影して保存する。
	 * 
	 * @param domSelector DOM要素のセレクタ
	 * @param methodName メソッド名
	 */
	public void saveCapture(DomSelector domSelector, String methodName) {
		WebElement el = domSelector.getType().findElement(driver, domSelector.getValue());

		//TODO: 元の状態を覚えておいて復元する
		// スクロールバーをhiddenにする
		if (!capabilities.getPlatformName().equals("ANDROID")) {
			if (el.getTagName().equals("iframe") || el.getTagName().equals("frame")) {
				driver.executeScript("arguments[0].contentWindow.document.documentElement.style.overflow='hidden'", el);
			} else {
				driver.executeJavaScript("arguments[0].style.overflow='hidden'", el);
			}
		}
		// textareaの場合はリサイズ不可にする
		if (el.getTagName().equals("textarea")) {
			driver.executeScript("arguments[0].style.resize = 'none'", el);
		}

		// 可視範囲のサイズを調べる
		long clientHeight = getClientHeight(el);
		long clientWidth = getClientWidth(el);

		// コンテンツ全体のサイズを調べる
		long scrollHeight = getScrollHeight(el);
		long scrollWidth = getScrollWidth(el);

		double captureTop = 0d;
		long scrollTop = -1L;
		int totalHeight = 0;
		int totalWidth = -1;
		double currentScale = Double.NaN;
		int imageHeight = -1;

		List<List<BufferedImage>> images = new ArrayList<List<BufferedImage>>();
		try {
			// Vertical scroll
			while (scrollTop < scrollHeight) {
				// 次の開始位置へ縦スクロール
				scrollTo(el, 0d, captureTop);
				Thread.sleep(100L);

				// スクロール位置の更新
				long currentScrollTop = Math.round(getCurrentScrollTop(el));
				// 変わっていなければ下端とみなして終了
				if (scrollTop == currentScrollTop) {
					break;
				}
				scrollTop = currentScrollTop;

				// Horizontal scroll
				List<BufferedImage> lineImages = new ArrayList<BufferedImage>();
				double captureLeft = 0d;
				long scrollLeft = -1L;
				int lineWidth = 0;
				while (scrollLeft < clientWidth) {
					// 次の開始位置へ横スクロール
					scrollTo(el, captureLeft, captureTop);
					Thread.sleep(100L);

					// スクロール位置の更新
					long currentScrollLeft = Math.round(getCurrentScrollLeft(el));
					// 変わっていなければ右端とみなして終了
					if (scrollLeft == currentScrollLeft) {
						break;
					}
					scrollLeft = currentScrollLeft;

					// ターゲットのスクリーンショットを取得
					ScreenArea screenArea = ScreenArea.of(domSelector.getType(), domSelector.getValue());
					CompareTarget[] targets = { new CompareTarget(screenArea) };
					ScreenshotResult sr = driver.takeScreenshot("test", targets);
					BufferedImage image = sr.getTargetResults().get(0).getImage().get();

					// scaleの取得
					if (Double.isNaN(currentScale)) {
						// TODO: モバイルの場合はdriverからscaleを取得する
						currentScale = 1.0;
					}

					// 周囲にborderがあれば切り取る
					WebElementBorderWidth bWidth = ((PtlWebElement) el).getBorderWidth();
					int top = (int) Math.round(bWidth.getTop() * currentScale);
					int left = (int) Math.round(bWidth.getLeft() * currentScale);
					int bottom = (int) Math.round(bWidth.getBottom() * currentScale);
					int right = (int) Math.round(bWidth.getRight() * currentScale);
					// IE7, 8のiframeはボーダーが写りこむので削る
					if (el.getTagName().equals("iframe")) {
						int frameBorder = Integer.parseInt(driver.executeScript(
								"return parseInt(arguments[0].frameBorder)", el).toString());
						if (frameBorder > 0 && capabilities.getBrowserName().equals("internet explorer")
								&& (capabilities.getVersion().equals("7") || capabilities.getVersion().equals("8"))) {
							top += 2;
							left += 2;
							bottom += 2;
							right += 2;
						}
					}
					image = ImageUtils.trim(image, top, left, bottom, right);

					// 次の画像と重なる部分を切り取る
					image = trimOverlap(captureTop, captureLeft, clientHeight, clientWidth, currentScale, image);

					if (imageHeight < 0) {
						imageHeight = image.getHeight();
					}

					// 結果セットに追加
					lineImages.add(image);
					lineWidth += image.getWidth();

					// 次のキャプチャ開始位置を設定
					captureLeft += clientWidth;
				}

				// 右端の重複をトリム
				if (lineImages.size() > 1) {
					BufferedImage rightImage = lineImages.get(lineImages.size() - 1);
					int trimLeft = (int) Math.round((clientWidth - scrollWidth % clientWidth) * currentScale);
					if (trimLeft < rightImage.getWidth()) {
						lineImages.set(lineImages.size() - 1, ImageUtils.trim(rightImage, 0, trimLeft, 0, 0));
						lineWidth -= trimLeft;
					}
				}

				images.add(lineImages);
				totalHeight += imageHeight;
				if (totalWidth < 0) {
					totalWidth = lineWidth;
				}

				// 次のキャプチャ開始位置を設定
				captureTop += clientHeight;
			}
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		// 末尾の重複をトリム
		if (images.size() > 1) {
			for (int i = 0; i < images.get(images.size() - 1).size(); i++) {
				BufferedImage lastImage = images.get(images.size() - 1).get(i);
				int trimTop = (int) Math.round((clientHeight - scrollHeight % clientHeight) * currentScale);

				if (trimTop < lastImage.getHeight()) {
					images.get(images.size() - 1).set(i, ImageUtils.trim(lastImage, trimTop, 0, 0, 0));
					if (i == 0) {
						totalHeight -= trimTop;
					}
				}
			}
		}

		// 画像の結合
		BufferedImage screenshot = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
		Graphics graphics = screenshot.getGraphics();
		int nextTop = 0;
		for (List<BufferedImage> lineImage : images) {
			int imgHeight = -1;
			int nextLeft = 0;
			for (BufferedImage image : lineImage) {
				graphics.drawImage(image, nextLeft, nextTop, null);
				nextLeft += image.getWidth();
				if (imageHeight < 0) {
					imageHeight = image.getHeight();
				}
			}
			nextTop += imageHeight;
		}

		// 保存
		try {
			ImageIO.write(screenshot, "png", new File(getFileName(methodName, domSelector.getType()) + ".png"));
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	/**
	 * スクロールを含む要素全体の高さを取得する。
	 * 
	 * @param el 取得する要素
	 * @return 高さ（整数px）
	 */
	public long getScrollHeight(WebElement el) {
		String result;
		if (el.getTagName().equals("iframe") || el.getTagName().equals("frame")) {
			result = driver
					.executeScript("return arguments[0].contentWindow.document.documentElement.scrollHeight", el)
					.toString();
		} else {
			result = driver.executeScript("return arguments[0].scrollHeight", el).toString();
		}
		return Long.parseLong(result);
	}

	/**
	 * スクロールを含む要素全体の幅を取得する。
	 * 
	 * @param el 取得する要素
	 * @return 幅（整数px）
	 */
	public long getScrollWidth(WebElement el) {
		String result;
		if (el.getTagName().equals("iframe") || el.getTagName().equals("frame")) {
			result = driver.executeScript("return arguments[0].contentWindow.document.documentElement.scrollWidth", el)
					.toString();
		} else {
			result = driver.executeScript("return arguments[0].scrollWidth", el).toString();
		}
		return Long.parseLong(result);
	}

	/**
	 * 要素の可視範囲の高さを取得する。
	 * 
	 * @param el 取得する要素
	 * @return 高さ（整数px）
	 */
	public long getClientHeight(WebElement el) {
		String result;
		if (el.getTagName().equals("iframe") || el.getTagName().equals("frame")) {
			result = driver
					.executeScript("return arguments[0].contentWindow.document.documentElement.clientHeight", el)
					.toString();
		} else {
			if (capabilities.getBrowserName().equals("internet explorer") && capabilities.getVersion().equals("7")
					&& el.getTagName().equals("tbody")) {
				result = driver.executeScript("return arguments[0].offsetHeight", el).toString();
			} else {
				result = driver.executeScript("return arguments[0].clientHeight", el).toString();
			}
		}
		return Long.parseLong(result);
	}

	/**
	 * 要素の可視範囲の幅を取得する。
	 * 
	 * @param el 取得する要素
	 * @return 幅（整数px）
	 */
	public long getClientWidth(WebElement el) {
		String result;
		if (el.getTagName().equals("iframe") || el.getTagName().equals("frame")) {
			result = driver.executeScript("return arguments[0].contentWindow.document.documentElement.clientWidth", el)
					.toString();
		} else {
			if (capabilities.getBrowserName().equals("internet explorer") && capabilities.getVersion().equals("7")
					&& el.getTagName().equals("tbody")) {
				result = driver.executeScript("return arguments[0].offsetWidth", el).toString();
			} else {
				result = driver.executeScript("return arguments[0].clientWidth", el).toString();
			}
		}
		return Long.parseLong(result);
	}

	/**
	 * 指定位置までスクロールする。
	 * 
	 * @param el スクロール対象の要素
	 * @param x x座標
	 * @param y y座標
	 */
	public void scrollTo(WebElement el, double x, double y) {
		if (el.getTagName().equals("iframe") || el.getTagName().equals("frame")) {
			driver.executeScript("arguments[0].contentWindow.scrollTo(arguments[1], arguments[2])", el, x, y);
		} else {
			driver.executeScript("arguments[0].scrollLeft = arguments[1]", el, x);
			driver.executeScript("arguments[0].scrollTop = arguments[1]", el, y);
		}

	}

	/**
	 * 指定要素の現在のスクロール位置（y座標）を取得する。
	 * 
	 * @param element 取得する要素
	 * @return スクロール位置（実数px）
	 */
	double getCurrentScrollTop(WebElement element) {
		double top = 0;
		if (element.getTagName().equals("iframe") || element.getTagName().equals("frame")) {
			double max = 0d;
			for (String value : SCRIPTS_SCROLL_TOP) {
				try {
					double current = Double.parseDouble(driver.executeScript("return " + value, element).toString());
					max = Math.max(max, current);
				} catch (Exception e) {
				}
			}
			top = max;
		} else {
			top = Double.parseDouble(driver.executeScript("return arguments[0].scrollTop", element).toString());
		}
		return top;
	}

	/**
	 * 指定要素の現在のスクロール位置（x座標）を取得する。
	 * 
	 * @param element 取得する要素
	 * @return スクロール位置（実数px）
	 */
	double getCurrentScrollLeft(WebElement element) {
		double top = 0;
		if (element.getTagName().equals("iframe") || element.getTagName().equals("frame")) {
			double max = 0d;
			for (String value : SCRIPTS_SCROLL_LEFT) {
				try {
					double current = Double.parseDouble(driver.executeScript("return " + value, element).toString());
					max = Math.max(max, current);
				} catch (Exception e) {
				}
			}
			top = max;
		} else {
			top = Double.parseDouble(driver.executeScript("return arguments[0].scrollLeft", element).toString());
		}
		return top;
	}

	protected BufferedImage trimOverlap(double captureTop, double captureLeft, long windowHeight, long windowWidth,
			double scale, BufferedImage img) {
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
