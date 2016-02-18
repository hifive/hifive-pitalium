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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

	private double scale = DEFAULT_SCREENSHOT_SCALE;

	/**
	 * コンストラクタ
	 * 
	 * @param remoteAddress RemoteWebDriverServerのアドレス
	 * @param capabilities Capability
	 */
	SplitScreenshotWebDriver(URL remoteAddress, PtlCapabilities capabilities) {
		super(remoteAddress, capabilities);
	}

	@Override
	protected TargetResult getTargetResult(CompareTarget compareTarget, List<DomSelector> hiddenElementSelectors,
			ScreenshotParams params, ScreenshotParams... additionalParams) {
		scale = DEFAULT_SCREENSHOT_SCALE;
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
		long windowWidth = getWindowWidth();
		long windowHeight = getWindowHeight();

		// 撮影したい要素の位置を取得
		double targetBottom = -1;
		double targetRight = -1;
		if (params != null) {
			RectangleArea elementArea = params.getTarget().getArea();
			targetBottom = elementArea.getY() + elementArea.getHeight();
			targetRight = elementArea.getX() + elementArea.getWidth();
		}
		LOG.trace("[GetMinimumScreenshot] window(w: {}, h: {}), target(right: {}, bottom: {})", windowWidth,
				windowHeight, targetRight, targetBottom);

		PtlWebElement bodyElement = (PtlWebElement) findElementByTagName("body");

		long currentVScrollAmount = 0;
		double captureTop = 0d;
		long scrollTop = -1L;
		double currentScale = Double.NaN;
		int imageHeight = -1;

		List<List<BufferedImage>> images = new ArrayList<List<BufferedImage>>();
		try {
			// 次の撮影位置までスクロール
			scrollTo(0d, 0d);

			// スクロール位置を確認
			long currentScrollTop = Math.round(getCurrentScrollTop());

			// スクロール回数を調べておく
			long scrollNum = getScrollNum();
			int currentScrollNum = 0;

			// Vertical scroll
			while (scrollTop != currentScrollTop && currentScrollNum <= scrollNum) {
				currentVScrollAmount = currentScrollTop - scrollTop;
				scrollTop = currentScrollTop;
				LOG.trace("[GetMinimumScreenshot] vertical scrollAmount: {}, scrollTop: {}", currentVScrollAmount,
						scrollTop);

				int headerHeight = getHeaderHeight(scrollTop);
				int footerHeight = getFooterHeight(scrollTop, captureTop);

				// Horizontal scroll
				double captureLeft = 0d;
				long scrollLeft = -1L;
				long currentHScrollAmount = 0;
				List<BufferedImage> lineImages = new ArrayList<BufferedImage>();

				// 次の撮影位置までスクロール
				scrollTo(0d, scrollTop);

				// スクロール位置を確認
				long currentScrollLeft = Math.round(getCurrentScrollLeft());
				while (scrollLeft != currentScrollLeft) {
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
					if (Double.isNaN(currentScale)) {
						currentScale = calcScale(windowWidth, image.getWidth());
						scale = currentScale;
						LOG.trace("[GetMinimumScreenshot] scale: {}", scale);
					}

					// 次の画像と重なる部分を切り取っておく
					if (scale != DEFAULT_SCREENSHOT_SCALE) {
						image = trimOverlap(captureTop, captureLeft, windowHeight, windowWidth, scale, image);
					}

					// 今回撮った画像をリストに追加
					lineImages.add(image);
					if (imageHeight < 0) {
						imageHeight = image.getHeight();
					}

					// 次のキャプチャ開始位置を設定
					captureLeft += calcHorizontalScrollIncrement(windowWidth);

					// Targetが写りきっていたら終了
					if (targetRight > 0 && targetRight < captureLeft) {
						LOG.trace("[GetMinimumScreenshot] horizontal scroll break");
						break;
					}
					LOG.trace("[GetMinimumScreenshot] horizontal scroll to ({}, {})", captureLeft, captureTop);

					// 次の撮影位置までスクロール
					scrollTo(captureLeft, captureTop);

					// スクロール位置を確認
					currentScrollLeft = Math.round(getCurrentScrollLeft());
				}

				// 右端の画像の重複部分をトリムする
				trimRightImage(lineImages, currentHScrollAmount, bodyElement, scale);

				images.add(lineImages);

				// 次のキャプチャ開始位置を設定
				double scrollIncrement = 0;
				if (headerHeight > 0) {
					// HeaderHeightがある場合、画像の高さからスクロール幅を逆算
					scrollIncrement = calcVerticalScrollIncrementWithHeader(imageHeight, scale);
				} else {
					scrollIncrement = calcVerticalScrollIncrement(windowHeight);

				}
				captureTop += scrollIncrement;

				// Targetが写りきっていたら終了
				if (targetBottom > 0 && targetBottom < captureTop) {
					LOG.trace("[GetMinimumScreenshot] vertical scroll break");
					break;
				}
				LOG.trace("[GetMinimumScreenshot] vertical scroll to (0, {})", captureTop);

				// 次の撮影位置までスクロール
				scrollTo(0d, captureTop);
				currentScrollNum++;

				// スクロール位置を確認
				currentScrollTop = Math.round(getCurrentScrollTop());
			}
		} catch (InterruptedException e) {
			throw new TestRuntimeException(e);
		}

		// 末尾の画像の重複部分をトリムする
		trimBottomImages(images, currentVScrollAmount, bodyElement, scale);

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

	@Override
	protected double getScreenshotScale() {
		return scale;
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
