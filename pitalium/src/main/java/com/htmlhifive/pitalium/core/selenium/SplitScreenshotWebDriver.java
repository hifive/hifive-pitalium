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
import com.htmlhifive.pitalium.image.model.RectangleArea;
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
		// 可視範囲のサイズを取得
		long windowWidth = getWindowWidth();
		long windowHeight = getWindowHeight();

		// 撮影したい要素の位置を取得
		double targetBottom = -1;
		if (params != null) {
			RectangleArea elementArea = params.getTarget().getArea();
			targetBottom = elementArea.getY() + elementArea.getHeight();
		}

		List<BufferedImage> images = new ArrayList<BufferedImage>();
		long currentScrollAmount = 0;
		double captureTop = 0d;
		long scrollTop = -1L;
		double currentScale = Double.NaN;
		int imageHeight = -1;
		int totalHeight = 0;
		try {
			// 次の撮影位置までスクロール
			scrollTo(0d, 0d);
			// Wait until scroll finished
			Thread.sleep(100L);

			// スクロール位置を確認
			long currentScrollTop = Math.round(getCurrentScrollTop());
			while (scrollTop != currentScrollTop) {
				currentScrollAmount = currentScrollTop - scrollTop;
				scrollTop = currentScrollTop;

				// 可視範囲のスクリーンショットを撮影
				BufferedImage image = getScreenshotAsBufferedImage();
				int headerHeight = getHeaderHeight(scrollTop);
				int footerHeight = getFooterHeight(scrollTop, captureTop);
				if (headerHeight > 0 || footerHeight > 0) {
					// ヘッダ・フッタがあれば切り取る
					image = ImageUtils.trim(image, headerHeight, 0, footerHeight, 0);
				}

				// 画像のサイズからscaleを計算（初回のみ）
				if (Double.isNaN(currentScale)) {
					currentScale = calcScale(windowWidth, image.getWidth());
					scale = currentScale;
				}

				// 次の画像と重なる部分を切り取っておく
				image = trimCaptureTop(captureTop, windowHeight, scale, image);

				// 今回撮った画像をリストに追加
				images.add(image);
				if (imageHeight < 0) {
					imageHeight = image.getHeight();
				}
				totalHeight += imageHeight;

				// 次のキャプチャ開始位置を設定
				double scrollIncrement = 0;
				if (headerHeight > 0) {
					// HeaderHeightがある場合、画像の高さからスクロール幅を逆算
					scrollIncrement = calcScrollIncrementWithHeader(imageHeight, scale);
					captureTop += scrollIncrement;
				} else {
					scrollIncrement = calcScrollIncrement(windowHeight);
					captureTop += scrollIncrement;
				}

				// Targetが写りきっていたら終了
				if (targetBottom > 0 && targetBottom < captureTop) {
					break;
				}

				// 次の撮影位置までスクロール
				scrollTo(0d, captureTop);
				// Wait until scroll finished
				Thread.sleep(100L);

				// スクロール位置を確認
				currentScrollTop = Math.round(getCurrentScrollTop());
			}
		} catch (InterruptedException e) {
			throw new TestRuntimeException(e);
		}

		// 末尾の画像の重複部分をトリムする
		if (images.size() > 1) {
			BufferedImage lastImage = images.get(images.size() - 1);
			int trimTop = lastImage.getHeight() - (int) Math.round(currentScrollAmount * scale);
			LOG.debug("trimTop: " + trimTop);

			if (trimTop > 0 && trimTop < lastImage.getHeight()) {
				images.set(images.size() - 1, ImageUtils.trim(lastImage, trimTop, 0, 0, 0));
				totalHeight -= trimTop;
			}
		}

		// 全キャプチャを結合
		BufferedImage screenshot = new BufferedImage(images.get(0).getWidth(), totalHeight, BufferedImage.TYPE_INT_RGB);
		Graphics graphics = screenshot.getGraphics();
		int nextTop = 0;
		for (BufferedImage image : images) {
			graphics.drawImage(image, 0, nextTop, null);
			nextTop += image.getHeight();
		}

		return screenshot;
	}

	/**
	 * ヘッダがある場合のスクロール量を計算します。
	 * 
	 * @param imageHeight 前回撮った画像の高さ
	 * @param currentScale スケール
	 * @return スクロール量
	 */
	protected double calcScrollIncrementWithHeader(int imageHeight, double currentScale) {
		return imageHeight / currentScale;
	}

	/**
	 * ヘッダがない場合のスクロール量を計算します。
	 * 
	 * @param windowHeight ウィンドウの高さ
	 * @return スクロール量
	 */
	protected double calcScrollIncrement(long windowHeight) {
		return windowHeight;
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
	 * スクリーンショットに含まれるウィンドウのヘッダーの高さを取得します。
	 * 
	 * @param pageHeight ページの高さ
	 * @param scrollTop 現在のスクロール位置
	 * @return ヘッダの高さ（整数px）
	 */
	protected int getHeaderHeight(long scrollTop) {
		return 0;
	}

	/**
	 * スクリーンショットに含まれるウィンドウのフッタの高さを取得します。
	 * 
	 * @param pageHeight ページの高さ
	 * @param scrollTop 現在のスクロール位置
	 * @return フッタの高さ（整数px）
	 */
	protected int getFooterHeight(long scrollTop, double captureTop) {
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
	protected BufferedImage trimCaptureTop(double captureTop, long windowHeight, double currentScale, BufferedImage img) {
		return img;
	}

}
