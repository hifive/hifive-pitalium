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

import com.htmlhifive.pitalium.common.exception.TestRuntimeException;

/**
 * iPhoneのSafariで利用する{@link org.openqa.selenium.WebDriver}
 */
class PtlIPhoneDriver extends SplitScreenshotWebDriver {

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
	protected boolean canHideScrollbar() {
		return false;
	}

	@Override
	protected int getHeaderHeight(long pageHeight, long scrollTop) {

		int currentHeaderHeight = headerHeight;
		if (scrollTop > 0) {
			// 最上部以外はヘッダを影ごと切り取る
			currentHeaderHeight += 2;
		}
		return currentHeaderHeight;
	}

	@Override
	protected int getFooterHeight(long pageHeight, long scrollTop, long windowHeight) {
		int currentFooterHeight = footerHeight;
		if (scrollTop + windowHeight < pageHeight) {
			// 最下部以外はフッタを影ごと切り取る
			currentFooterHeight += 2;
		}
		return currentFooterHeight;
	}

	@Override
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

		long calculatedRightValue = Math.round((captureLeft + windowWidth) * scale);
		long actualRightValue = Math.round(captureLeft * scale) + img.getWidth();
		// 余分にキャプチャに写っていたら切り取っておく
		if (calculatedRightValue < actualRightValue) {
			image = image.getSubimage(0, 0, (int) (image.getWidth() - (actualRightValue - calculatedRightValue)),
					image.getHeight());
		}

		return image;
	}

	@Override
	protected double calcVerticalScrollIncrementWithHeader(int imageHeight, double scale) {
		double scrollIncrement = super.calcVerticalScrollIncrementWithHeader(imageHeight, scale);
		return scrollIncrement - 1;
	}

	@Override
	protected int calcTrimTop(int imageNum, long windowHeight, long pageHeight, double scale) {
		// スクロール幅をずらした分、切り取り位置を調整する
		// 1スクロールにつき2pxずつずれていく
		return (int) Math.round((windowHeight - (pageHeight % windowHeight) - (imageNum - 1) * 2) * scale);
	}

	@Override
	protected double calcScale(double windowWidth, double imageWidth) {
		return imageWidth / windowWidth;
	}

	@Override
	protected PtlWebElement newPtlWebElement() {
		return new PtlIPhoneWebElement();
	}

}
