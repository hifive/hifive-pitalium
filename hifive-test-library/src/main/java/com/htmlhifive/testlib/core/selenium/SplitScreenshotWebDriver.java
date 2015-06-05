/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.core.selenium;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.htmlhifive.testlib.common.exception.TestRuntimeException;
import com.htmlhifive.testlib.core.model.CompareTarget;
import com.htmlhifive.testlib.core.model.DomSelector;
import com.htmlhifive.testlib.core.model.ScreenshotParams;
import com.htmlhifive.testlib.core.model.TargetResult;
import com.htmlhifive.testlib.image.util.ImageUtils;

/**
 * スクリーンショットが可視範囲のみのブラウザ用のWebDriver。このクラスを拡張したdriverは、スクロール毎にスクリーンショットを撮り、結合した画像を返します。
 */
abstract class SplitScreenshotWebDriver extends MrtWebDriver {

	private static final Logger LOG = LoggerFactory.getLogger(SplitScreenshotWebDriver.class);

	private double scale = DEFAULT_SCREENSHOT_SCALE;

	/**
	 * コンストラクタ
	 * 
	 * @param remoteAddress RemoteWebDriverServerのアドレス
	 * @param capabilities Capability
	 */
	SplitScreenshotWebDriver(URL remoteAddress, MrtCapabilities capabilities) {
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

		// 移動しているTOP分を引く
		MrtWebElement bodyElement = (MrtWebElement) findElementByTagName("body");
		WebElementRect bodyRect = bodyElement.getRect();
		if (bodyRect.getTop() < 0) {
			pageHeight += bodyRect.getTop();
		}

		double captureTop = 0d;
		long scrollTop = -1L;
		int totalHeight = 0;
		double currentScale = Double.NaN;
		int imageHeight = -1;

		List<BufferedImage> images = new ArrayList<BufferedImage>();
		try {
			while (scrollTop < pageHeight) {
				scrollTo(0d, captureTop);
				// Wait until scroll finished
				Thread.sleep(100L);

				long currentScrollTop = Math.round(getCurrentScrollTop());
				if (scrollTop == currentScrollTop) {
					break;
				}

				scrollTop = currentScrollTop;

				// Screenshot
				int headerHeight = getHeaderHeight(pageHeight, scrollTop);
				int footerHeight = getFooterHeight(pageHeight, scrollTop, windowHeight);
				BufferedImage image = getScreenshotAsBufferedImage();
				if (headerHeight > 0 || footerHeight > 0) {
					image = ImageUtils.trim(image, headerHeight, 0, footerHeight, 0);
				}

				// scale
				if (Double.isNaN(currentScale)) {
					currentScale = calcScale(windowWidth, image.getWidth());
					scale = currentScale;
					LOG.debug("pageWidth: {}, pageHeight: {}, windowWidth: {}, windowHeight: {}, scale: {}", pageWidth,
							pageHeight, windowWidth, windowHeight, scale);
				}

				// 次の画像と重なる部分を切り取っておく
				image = trimCaptureTop(captureTop, windowHeight, scale, image);

				if (imageHeight < 0) {
					imageHeight = image.getHeight();
				}

				images.add(image);
				totalHeight += imageHeight;

				// 次のキャプチャ開始位置を設定
				// HeaderHeightがある場合、画像の高さからスクロール幅を逆算
				if (headerHeight > 0) {
					captureTop += calcScrollIncrementWithHeader(imageHeight, scale);
				} else {
					captureTop += calcScrollIncrement(windowHeight);
				}
			}
		} catch (InterruptedException e) {
			throw new TestRuntimeException(e);
		}

		// 末尾の画像の重複部分をトリムする
		if (images.size() > 1) {
			BufferedImage lastImage = images.get(images.size() - 1);
			int trimTop = calcTrimTop(images.size(), windowHeight, pageHeight, scale);
			LOG.debug("trimTop: " + trimTop);

			if (trimTop < lastImage.getHeight()) {
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
	protected BufferedImage trimCaptureTop(double captureTop, long windowHeight, double currentScale, BufferedImage img) {
		return img;
	}

}
