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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.htmlhifive.pitalium.common.exception.TestRuntimeException;
import com.htmlhifive.pitalium.core.model.CompareTarget;
import com.htmlhifive.pitalium.core.model.DomSelector;
import com.htmlhifive.pitalium.core.model.ScreenshotParams;
import com.htmlhifive.pitalium.core.model.TargetResult;
import com.htmlhifive.pitalium.image.util.ImageUtils;

/**
 * スクリーンショットが可視範囲のみのブラウザ用のWebDriver。このクラスを拡張したdriverは、スクロール毎にスクリーンショットを撮り、結合した画像を返します。
 */
abstract class SplitScreenshotWebDriver extends PtlWebDriver {

	private static final Logger LOG = LoggerFactory.getLogger(SplitScreenshotWebDriver.class);

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
		// ウィンドウの最大サイズを取得
		long pageWidth = getCurrentPageWidth();
		long pageHeight = getCurrentPageHeight();

		// 可視範囲のサイズを取得
		long windowWidth = getWindowWidth();
		long windowHeight = getWindowHeight();

		double captureTop = 0d;
		long scrollTop = -1L;
		int totalHeight = 0;
		int totalWidth = -1;
		double currentScale = Double.NaN;
		int imageHeight = -1;

		List<List<BufferedImage>> images = new ArrayList<List<BufferedImage>>();
		try {
			// Vertical scroll
			while (scrollTop < pageHeight) {
				scrollTo(0d, captureTop);
				// Wait until scroll finished
				Thread.sleep(100L);

				long currentScrollTop = Math.round(getCurrentScrollTop());
				if (scrollTop == currentScrollTop) {
					break;
				}
				scrollTop = currentScrollTop;

				int headerHeight = getHeaderHeight(pageHeight, scrollTop);
				int footerHeight = getFooterHeight(pageHeight, scrollTop, windowHeight);
				List<BufferedImage> lineImages = new ArrayList<BufferedImage>();

				// Horizontal scroll
				double captureLeft = 0d;
				long scrollLeft = -1L;
				int lineWidth = 0;
				while (scrollLeft < pageWidth) {
					scrollTo(captureLeft, captureTop);
					// Wait until scroll finished
					Thread.sleep(100L);

					long currentScrollLeft = Math.round(getCurrentScrollLeft());
					if (scrollLeft == currentScrollLeft) {
						break;
					}
					scrollLeft = currentScrollLeft;

					// Take screenshot
					BufferedImage image = getScreenshotAsBufferedImage();
					// Trim header or footer
					if (headerHeight > 0 || footerHeight > 0) {
						image = ImageUtils.trim(image, headerHeight, 0, footerHeight, 0);
					}

					// Calc scale
					if (Double.isNaN(currentScale)) {
						currentScale = calcScale(windowWidth, image.getWidth());
						scale = currentScale;
						LOG.debug("pageWidth: {}, pageHeight: {}, windowWidth: {}, windowHeight: {}, scale: {}",
								pageWidth, pageHeight, windowWidth, windowHeight, scale);
					}

					// 次の画像と重なる部分を切り取っておく
					image = trimOverlap(captureTop, captureLeft, windowHeight, windowWidth, scale, image);

					if (imageHeight < 0) {
						imageHeight = image.getHeight();
					}

					lineImages.add(image);
					lineWidth += image.getWidth();
					captureLeft += calcHorizontalScrollIncrement(windowWidth);
				}

				// 右端の画像の重複部分をトリムする
				if (lineImages.size() > 1) {
					BufferedImage rImg = lineImages.get(lineImages.size() - 1);
					int trimLeft = calcTrimTop(lineImages.size(), windowWidth, pageWidth, scale);
					LOG.debug("trimLeft: " + trimLeft);

					if (trimLeft < rImg.getWidth()) {
						lineImages.set(lineImages.size() - 1, ImageUtils.trim(rImg, 0, trimLeft, 0, 0));
						lineWidth -= trimLeft;
					}
				}

				images.add(lineImages);
				totalHeight += imageHeight;
				if (totalWidth < 0) {
					totalWidth = lineWidth;
				}

				// 次のキャプチャ開始位置を設定
				// HeaderHeightがある場合、画像の高さからスクロール幅を逆算
				if (headerHeight > 0) {
					captureTop += calcVerticalScrollIncrementWithHeader(imageHeight, scale);
				} else {
					captureTop += calcVerticalScrollIncrement(windowHeight);
				}
			}
		} catch (InterruptedException e) {
			throw new TestRuntimeException(e);
		}

		// 末尾の画像の重複部分をトリムする
		if (images.size() > 1) {
			for (int i = 0; i < images.get(images.size() - 1).size(); i++) {
				BufferedImage lastImage = images.get(images.size() - 1).get(i);
				int trimTop = calcTrimTop(images.size(), windowHeight, pageHeight, scale);
				LOG.debug("trimTop: " + trimTop);

				if (trimTop < lastImage.getHeight()) {
					images.get(images.size() - 1).set(i, ImageUtils.trim(lastImage, trimTop, 0, 0, 0));
					if (i == 0) {
						totalHeight -= trimTop;
					}
				}
			}
		}

		// 全キャプチャを結合
		BufferedImage screenshot = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
		Graphics graphics = screenshot.getGraphics();
		int nextTop = 0;
		for (List<BufferedImage> lineImage : images) {
			int imgHeight = -1;
			int nextLeft = 0;
			for (BufferedImage img : lineImage) {
				graphics.drawImage(img, nextLeft, nextTop, null);
				nextLeft += img.getWidth();
				if (imgHeight < 0) {
					imgHeight = img.getHeight();
				}
			}
			nextTop += imgHeight;
		}

		//		for (BufferedImage image : images) {
		//			graphics.drawImage(image, 0, nextTop, null);
		//			nextTop += image.getHeight();
		//		}

		return screenshot;
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
	 * 最下部のスクリーンショットのトリム幅を計算します。
	 * 
	 * @param imageNum キャプチャした画像の数
	 * @param windowHeight ウィンドウ（viewport内の表示領域）の幅
	 * @param pageHeight ページ全体の高さ
	 * @param currentScale スケール
	 * @return トリム幅（整数px）
	 */
	protected int calcTrimTop(int imageNum, long windowHeight, long pageHeight, double currentScale) {
		return (int) Math.round((windowHeight - pageHeight % windowHeight) * currentScale);
	}

	@Override
	protected double getScreenshotScale() {
		return scale;
	}

	/**
	 * スクリーンショットに含まれるウィンドウのヘッダーの高さを取得します。
	 * 
	 * @param pageHeight ページの高さ
	 * @param scrollTop 現在のスクロール位置
	 * @return ヘッダの高さ（整数px）
	 */
	protected int getHeaderHeight(long pageHeight, long scrollTop) {
		return 0;
	}

	/**
	 * スクリーンショットに含まれるウィンドウのフッタの高さを取得します。
	 * 
	 * @param pageHeight ページの高さ
	 * @param scrollTop 現在のスクロール位置
	 * @return フッタの高さ（整数px）
	 */
	protected int getFooterHeight(long pageHeight, long scrollTop, long windowHeight) {
		return 0;
	}

	/**
	 * 次のキャプチャ開始位置と今回キャプチャした範囲を比較し、重なる部分がある場合は切り取ります。
	 * 
	 * @param captureTop 今回のキャプチャ開始位置
	 * @param windowHeight ウィンドウ（viewport内の表示領域）の高さ
	 * @param currentScale ウィンドウとスクリーンショットのサイズ比
	 * @param img スクリーンショット画像
	 * @return 重複を切り取った画像
	 */
	protected BufferedImage trimOverlap(double captureTop, double captureLeft, long windowHeight, long windowWidth,
			double currentScale, BufferedImage img) {
		return img;
	}

}
