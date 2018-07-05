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
package com.htmlhifive.pitalium.core.selenium;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.remote.CommandExecutor;

import com.htmlhifive.pitalium.common.exception.TestRuntimeException;
import com.htmlhifive.pitalium.core.model.CompareTarget;
import com.htmlhifive.pitalium.core.model.DomSelector;
import com.htmlhifive.pitalium.core.model.ScreenshotParams;
import com.htmlhifive.pitalium.core.model.TargetResult;
import com.htmlhifive.pitalium.image.model.RectangleArea;
import com.htmlhifive.pitalium.image.util.ImageUtils;

/**
 * スクリーンショットが可視範囲のみのブラウザ用のWebDriver。このクラスを拡張したdriverは、スクロール毎にスクリーンショットを撮り、結合した画像を返します。
 */
abstract class SplitScreenshotWebDriver extends PtlWebDriver {

	/**
	 * コンストラクタ
	 *
	 * @param remoteAddress RemoteWebDriverServerのアドレス
	 * @param capabilities Capability
	 */
	SplitScreenshotWebDriver(URL remoteAddress, PtlCapabilities capabilities) {
		super(remoteAddress, capabilities);
	}

	SplitScreenshotWebDriver(CommandExecutor executor, PtlCapabilities capabilities) {
		super(executor, capabilities);
	}

	@Override
	protected TargetResult getTargetResult(CompareTarget compareTarget, List<DomSelector> hiddenElementSelectors,
			ScreenshotParams params, ScreenshotParams... additionalParams) {
		resetScreenshotScale();
		return super.getTargetResult(compareTarget, hiddenElementSelectors, params, additionalParams);
	}

	/**
	 * ページ全体のスクリーンショットを取得します。スクロール毎にスクリーンショットを撮り、最後に結合した画像を返します。
	 *
	 * @return 撮影したスクリーンショット
	 */
	@Override
	public BufferedImage getEntirePageScreenshot() {
		return getMinimumScreenshot(null);
	}

	/**
	 * ページの左上から指定された要素までを含むスクリーンショットを撮影し、{@link BufferedImage}として返します。<br/>
	 * 要素が指定されていない場合はページ全体を撮影します。
	 *
	 * @param params スクリーンショット撮影用パラメータ
	 * @return 撮影したスクリーンショット
	 */
	@Override
	public BufferedImage getMinimumScreenshot(ScreenshotParams params) {
		LOG.trace("[GetMinimumScreenshot start]");

		// 可視範囲のサイズを取得
		LOG.debug("[getMinimumScreenshot>getWindowSize start]");
		long windowWidth = getWindowWidth();
		long windowHeight = getWindowHeight();
		LOG.debug("[getMinimumScreenshot>getWindowSize finished]");

		// 撮影したい要素の位置を取得
		LOG.debug("[getMinimumScreenshot>getArea start]");
		double targetBottom = -1;
		double targetRight = -1;
		if (params != null) {
			RectangleArea elementArea = params.getTarget().getArea();
			targetBottom = elementArea.getY() + elementArea.getHeight();
			targetRight = elementArea.getX() + elementArea.getWidth();
		}
		LOG.trace("[GetMinimumScreenshot] window(w: {}, h: {}), target(right: {}, bottom: {})", windowWidth,
				windowHeight, targetRight, targetBottom);
		LOG.debug("[getMinimumScreenshot>getArea finished]");

		LOG.debug("[getMinimumScreenshot>findBodyElement start]");
		PtlWebElement bodyElement = (PtlWebElement) findElementByTagName("body");
		LOG.debug("[getMinimumScreenshot>findBodyElement finished]");

		long currentVScrollAmount = 0;
		double captureTop = 0d;
		long scrollTop = -1L;
		double currentScale = Double.NaN;
		int imageHeight = -1;

		List<List<BufferedImage>> images = new ArrayList<List<BufferedImage>>();
		try {
			LOG.debug("[getMinimumScreenshot>scrollTo start]");
			// 次の撮影位置までスクロール
			// TODO:
			scrollTo(0d, 0d);
			LOG.debug("[getMinimumScreenshot>scrollTo finished]");

			// スクロール位置を確認
			LOG.debug("[getMinimumScreenshot>getCurrentScrollTop start]");
			long currentScrollTop = Math.round(getCurrentScrollTop());
			LOG.debug("[getMinimumScreenshot>getCurrentScrollTop finished]");

			long scrollNum = -1;
			int currentScrollNum = 0;
			long horizontalScrollNum = -1;

			// Vertical scroll
			while (scrollTop != currentScrollTop) {
				if (scrollNum >= 0 && currentScrollNum > scrollNum) {
					break;
				}
				currentVScrollAmount = currentScrollTop - scrollTop;
				scrollTop = currentScrollTop;
				LOG.trace("[GetMinimumScreenshot] vertical scrollAmount: {}, scrollTop: {}", currentVScrollAmount,
						scrollTop);

				LOG.debug("[getMinimumScreenshot>getHeaderHeight start]");
				int headerHeight = getHeaderHeight(scrollTop);
				int footerHeight = getFooterHeight(scrollTop, captureTop);
				LOG.debug("[getMinimumScreenshot>getHeaderHeight finished]");

				// Horizontal scroll
				double captureLeft = 0d;
				long scrollLeft = -1L;
				long currentHScrollAmount = 0;
				int currentHorizontalScrollNum = 0;
				List<BufferedImage> lineImages = new ArrayList<BufferedImage>();

				// 次の撮影位置までスクロール
				LOG.debug("[getMinimumScreenshot>scrollToNext start]");
				scrollTo(0d, scrollTop);
				LOG.debug("[getMinimumScreenshot>scrollToNext finished]");

				// スクロール位置を確認
				LOG.debug("[getMinimumScreenshot>getCurrentScrollLeft start]");
				long currentScrollLeft = Math.round(getCurrentScrollLeft());
				LOG.debug("[getMinimumScreenshot>getCurrentScrollLeft finished]");
				while (scrollLeft != currentScrollLeft) {
					if (horizontalScrollNum >= 0 && currentHorizontalScrollNum > horizontalScrollNum) {
						break;
					}
					currentHScrollAmount = currentScrollLeft - scrollLeft;
					scrollLeft = currentScrollLeft;
					LOG.trace("[GetMinimumScreenshot] horizontal scrollAmount: {}, scrollLeft: {}",
							currentHScrollAmount, scrollLeft);

					// 可視範囲のスクリーンショットを撮影
					BufferedImage image = getScreenshotAsBufferedImage();
					if (headerHeight > 0 || footerHeight > 0) {
						// ヘッダ・フッタがあれば切り取る
						image = ImageUtils.trim(image, headerHeight, 0, footerHeight, 0);
					}

					// 画像のサイズからscaleを計算（初回のみ）
					LOG.debug("[getMinimumScreenshot>calsScale start]");
					if (Double.isNaN(currentScale)) {
						currentScale = calcScale(windowWidth, image.getWidth());
						setScreenshotScale(currentScale);
						LOG.trace("[GetMinimumScreenshot] scale: {}", currentScale);
					}
					LOG.debug("[getMinimumScreenshot>calsScale finished]");

					// 次の画像と重なる部分を切り取っておく
					LOG.debug("[getMinimumScreenshot>trimOverlap start]");
					if (getScreenshotScale() != DEFAULT_SCREENSHOT_SCALE) {
						image = trimOverlap(captureTop, captureLeft, windowHeight, windowWidth, getScreenshotScale(),
								image);
					}
					LOG.debug("[getMinimumScreenshot>trimOverlap finished]");

					// 今回撮った画像をリストに追加
					lineImages.add(image);
					if (imageHeight < 0) {
						imageHeight = image.getHeight();
					}

					// 1回目のキャプチャの幅さを見てスクロール回数をセット
					LOG.debug("[getMinimumScreenshot>setHorizontalScrollNum start]");
					if (horizontalScrollNum < 0) {
						// TODO: 内部でgetPageWidthを都度呼んでいる→冗長なので上の層で取得して使いまわしたい
						horizontalScrollNum = getHorizontalScrollNum(image.getWidth());
					}
					LOG.debug("[getMinimumScreenshot>setHorizontalScrollNum finished]");

					// 次のキャプチャ開始位置を設定
					LOG.debug("[getMinimumScreenshot>calcHorizontalScrollIncrement start]");
					captureLeft += calcHorizontalScrollIncrement(windowWidth);
					LOG.debug("[getMinimumScreenshot>calcHorizontalScrollIncrement finished]");

					// Targetが写りきっていたら終了
					if (targetRight > 0 && targetRight < captureLeft) {
						LOG.trace("[GetMinimumScreenshot] horizontal scroll break");
						break;
					}
					LOG.trace("[GetMinimumScreenshot] horizontal scroll to ({}, {})", captureLeft, captureTop);

					// 次の撮影位置までスクロール
					LOG.debug("[getMinimumScreenshot>scrollToNextNext start]");
					scrollTo(captureLeft, captureTop);
					currentHorizontalScrollNum++;
					LOG.debug("[getMinimumScreenshot>scrollToNextNext finished]");

					// スクロール位置を確認
					LOG.debug("[getMinimumScreenshot>getCurrentScrollLeft start]");
					currentScrollLeft = Math.round(getCurrentScrollLeft());
					LOG.debug("[getMinimumScreenshot>getCurrentScrollLeft finished]");
				}

				LOG.debug("[getMinimumScreenshot>postProcess start]");
				// 右端の画像の重複部分をトリムする
				trimRightImage(lineImages, currentHScrollAmount, bodyElement, getScreenshotScale());

				images.add(lineImages);

				// 1回目のキャプチャの高さを見てスクロール回数をセット
				if (scrollNum < 0) {
					// TODO: 内部でgetPageHeightを都度呼んでいる→冗長なので上の層で取得して使いまわしたい
					scrollNum = getScrollNum(lineImages.get(0).getHeight());
				}

				// 次のキャプチャ開始位置を設定
				double scrollIncrement = 0;
				if (headerHeight > 0) {
					// HeaderHeightがある場合、画像の高さからスクロール幅を逆算
					scrollIncrement = calcVerticalScrollIncrementWithHeader(imageHeight, getScreenshotScale());
				} else {
					scrollIncrement = calcVerticalScrollIncrement(windowHeight);

				}
				captureTop += scrollIncrement;

				// Targetが写りきっていたら終了
				if (targetBottom > 0 && targetBottom < captureTop) {
					LOG.trace("[GetMinimumScreenshot] vertical scroll break");
					LOG.debug("[getMinimumScreenshot>postProcess finished]");
					break;
				}
				LOG.trace("[GetMinimumScreenshot] vertical scroll to (0, {})", captureTop);

				// 次の撮影位置までスクロール
				scrollTo(0d, captureTop);
				currentScrollNum++;

				// スクロール位置を確認
				currentScrollTop = Math.round(getCurrentScrollTop());
				LOG.debug("[getMinimumScreenshot>postProcess finished]");
			}
		} catch (InterruptedException e) {
			throw new TestRuntimeException(e);
		}

		// 末尾の画像の重複部分をトリムする
		trimBottomImages(images, currentVScrollAmount, bodyElement, getScreenshotScale());

		BufferedImage screenshot = ImageUtils.merge(images);
		LOG.trace("[GetMinimumScreenshot finished]");
		return screenshot;
	}

	/**
	 * 指定されたリスト内の末尾1列の画像の重複部分をトリムします。
	 *
	 * @param images 対象のリスト
	 * @param lastScrollAmount 最後のスクロール量
	 * @param el 撮影対象の要素
	 * @param currentScale スケール
	 */
	private void trimBottomImages(List<List<BufferedImage>> images, long lastScrollAmount, PtlWebElement el,
			double currentScale) {
		int size = images.size();
		// 画像が1列しかないときは何もしない
		if (size <= 1) {
			return;
		}

		List<BufferedImage> bottomLineImages = images.get(size - 1);
		for (int i = 0; i < bottomLineImages.size(); i++) {
			BufferedImage bottomImage = bottomLineImages.get(i);
			int trimTop = calcTrimTop(bottomImage.getHeight(), lastScrollAmount, el, currentScale);

			if (trimTop > 0 && trimTop < bottomImage.getHeight()) {
				bottomLineImages.set(i, ImageUtils.trim(bottomImage, trimTop, 0, 0, 0));
			}
		}
	}

	/**
	 * ヘッダがある場合のスクロール量を計算します。
	 *
	 * @param imageHeight 前回撮った画像の高さ
	 * @param currentScale スケール
	 * @return スクロール量
	 */
	protected double calcVerticalScrollIncrementWithHeader(int imageHeight, double currentScale) {
		return imageHeight / currentScale;
	}

	/**
	 * ヘッダがない場合のスクロール量を計算します。
	 *
	 * @param windowHeight ウィンドウの高さ
	 * @return スクロール量
	 */
	protected double calcVerticalScrollIncrement(long windowHeight) {
		return windowHeight;
	}

	/**
	 * 横スクロール量を計算します。
	 *
	 * @param windowWidth ウィンドウの幅
	 * @return スクロール量
	 */
	protected double calcHorizontalScrollIncrement(long windowWidth) {
		return windowWidth;
	}

	/**
	 * スクリーンショットに含まれるウィンドウのヘッダーの高さを取得します。
	 *
	 * @param scrollTop 現在のスクロール位置
	 * @return ヘッダの高さ（整数px）
	 */
	protected int getHeaderHeight(long scrollTop) {
		return 0;
	}

	/**
	 * スクリーンショットに含まれるウィンドウのフッタの高さを取得します。
	 *
	 * @param scrollTop 現在の（実際の）スクロール位置
	 * @param captureTop 現在の（計算上の）スクロール位置
	 * @return フッタの高さ（整数px）
	 */
	protected int getFooterHeight(long scrollTop, double captureTop) {
		return 0;
	}

}
