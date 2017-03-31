/*
 * Copyright (C) 2015-2016 NS Solutions Corporation
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

import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.htmlhifive.pitalium.common.exception.TestRuntimeException;
import com.htmlhifive.pitalium.core.model.ScreenshotParams;
import com.htmlhifive.pitalium.image.util.ImageUtils;

/**
 * iPhoneのSafariで利用する{@link org.openqa.selenium.WebDriver}
 */
class PtlIPhoneDriver extends SplitScreenshotWebDriver {

	private static final Logger LOG = LoggerFactory.getLogger(PtlIPhoneDriver.class);

	private final int headerHeight;
	private final int footerHeight;

	/**
	 * コンストラクタ
	 * 
	 * @param remoteAddress RemoteWebDriverServerのアドレス
	 * @param capabilities Capability
	 */
	PtlIPhoneDriver(URL remoteAddress, PtlCapabilities capabilities) {
		super(remoteAddress, capabilities);

		Object headerHeightCapability = capabilities.getCapability("headerHeight");
		if (headerHeightCapability == null) {
			throw new TestRuntimeException("Capability \"headerHeight\" is required");
		} else {
			if (headerHeightCapability instanceof Number) {
				headerHeight = ((Number) headerHeightCapability).intValue();
			} else {
				headerHeight = Integer.parseInt(headerHeightCapability.toString());
			}
		}

		Object footerHeightCapability = capabilities.getCapability("footerHeight");
		if (footerHeightCapability == null) {
			throw new TestRuntimeException("Capability \"footerHeight\" is required");
		} else {
			if (footerHeightCapability instanceof Number) {
				footerHeight = ((Number) footerHeightCapability).intValue();
			} else {
				footerHeight = Integer.parseInt(footerHeightCapability.toString());
			}
		}
	}

	@Override
	protected boolean canHideBodyScrollbar() {
		return false;
	}

	@Override
	public BufferedImage getMinimumScreenshot(ScreenshotParams params) {
		// move直後はwindowWidthの値が変わるためwaitを挟む
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			throw new TestRuntimeException(e);
		}

		return super.getMinimumScreenshot(params);
	};

	@Override
	protected int getHeaderHeight(long scrollTop) {

		int currentHeaderHeight = headerHeight;
		if (scrollTop > 0) {
			// 最上部以外はヘッダを影ごと切り取る
			currentHeaderHeight += 2;
		}
		LOG.trace("(GetHeaderHeight) [{}] ({})", currentHeaderHeight, this);
		return currentHeaderHeight;
	}

	@Override
	protected int getFooterHeight(long scrollTop, double captureTop) {
		int currentFooterHeight = footerHeight;
		if (scrollTop >= Math.round(captureTop)) {
			// 最下部以外はフッタを影ごと切り取る
			currentFooterHeight += 2;
		}
		LOG.trace("(GetFooterHeight) [{}] ({})", currentFooterHeight, this);
		return currentFooterHeight;
	}

	@Override
	protected double calcVerticalScrollIncrementWithHeader(int imageHeight, double scale) {
		double scrollIncrement = super.calcVerticalScrollIncrementWithHeader(imageHeight, scale);
		double result = scrollIncrement - 1;
		LOG.trace("(CalcVerticalScrollIncrementWithHeader) (imageHeight: {}, scale: {}) => {}", imageHeight, scale,
				result);
		return result;
	}

	@Override
	protected double calcScale(double windowWidth, double imageWidth) {
		return imageWidth / windowWidth;
	}

	@Override
	protected PtlWebElement newPtlWebElement() {
		return new PtlIPhoneWebElement();
	}

	@Override
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
		// 影を切り取った分高さを-1
		long calculatedBottomValue = Math.round((captureTop + windowHeight - 1) * currentScale);
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

	@Override
	protected BufferedImage trimTargetBorder(WebElement el, BufferedImage image, int num, int size, double currentScale) {
		LOG.trace("(trimTargetBorder) el: {}; image[w: {}, h: {}], num: {}, size: {}", el, image.getWidth(),
				image.getHeight(), num, size);

		WebElementBorderWidth targetBorder = ((PtlWebElement) el).getBorderWidth();

		int trimTop = 0;
		int trimBottom = 0;
		if (size > 1) {
			// 外枠の影が入るため1px多く切り取る
			if (num <= 0) {
				trimBottom = (int) Math.round(targetBorder.getBottom() * currentScale)
						+ (int) Math.round(1 * currentScale);
			} else if (num >= size - 1) {
				trimTop = (int) Math.round(targetBorder.getTop() * currentScale);
			} else {
				trimBottom = (int) Math.round(targetBorder.getBottom() * currentScale)
						+ (int) Math.round(1 * currentScale);
				trimTop = (int) Math.round(targetBorder.getTop() * currentScale);
			}
		}

		LOG.trace("(trimTargetBorder) top: {}, bottom: {}", trimTop, trimBottom);
		return ImageUtils.trim(image, trimTop, 0, trimBottom, 0);
	}

	/**
	 * 下端の切り取り位置を調整します。
	 * 
	 * @param trimTop 調整前の切り取り量
	 * @param scale スケール
	 * @return 調整後の切り取り量
	 */
	protected int adjustTrimTop(int trimTop, double scale) {
		// スクロール幅をずらした分、切り取り位置を調整する
		return trimTop - (int) Math.round(1 * scale);
	}

	@Override
	protected int calcTrimTop(int imageHeight, long scrollAmount, PtlWebElement targetElement, double currentScale) {
		int trimTop = super.calcTrimTop(imageHeight, scrollAmount, targetElement, currentScale);
		trimTop = adjustTrimTop(trimTop, currentScale);

		return trimTop;
	}

	@Override
	long getScrollNum(double clientHeight) {
		return super.getScrollNum();
	}

}
