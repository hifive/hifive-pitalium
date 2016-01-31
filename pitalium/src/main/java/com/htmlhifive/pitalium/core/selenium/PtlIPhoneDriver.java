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
	protected boolean canHideBodyScrollbar() {
		return false;
	}

	@Override
	protected int getHeaderHeight(long scrollTop) {

		int currentHeaderHeight = headerHeight;
		if (scrollTop > 0) {
			// 最上部以外はヘッダを影ごと切り取る
			currentHeaderHeight += 2;
		}
		return currentHeaderHeight;
	}

	@Override
	protected int getFooterHeight(long scrollTop, double captureTop) {
		int currentFooterHeight = footerHeight;
		if (scrollTop < Math.round(captureTop)) {
			// 最下部以外はフッタを影ごと切り取る
			currentFooterHeight += 2;
		}
		return currentFooterHeight;
	}

	@Override
	protected double calcVerticalScrollIncrementWithHeader(int imageHeight, double scale) {
		double scrollIncrement = super.calcVerticalScrollIncrementWithHeader(imageHeight, scale);
		return scrollIncrement - 1;
	}

	/**
	 * 下端の切り取り位置を調整します。
	 *
	 * @param scrollNum スクロール回数
	 * @param trimTop 調整前の切り取り量
	 * @param scale スケール
	 * @return 調整後の切り取り量
	 */
	protected int adjustTrimTop(int scrollNum, int trimTop, double scale) {
		// スクロール幅をずらした分、切り取り位置を調整する
		// 1スクロールにつき2pxずつずれていく
		return trimTop - (int) Math.round((scrollNum - 1) * 2 * scale);
	}

	@Override
	protected int calcSplitScrollTrimTop(int imageHeight, long scrollAmount, PtlWebElement targetElement,
			double currentScale, int scrollNum) {
		int trimTop = super.calcSplitScrollTrimTop(imageHeight, scrollAmount, targetElement, currentScale, scrollNum);
		trimTop = adjustTrimTop(scrollNum, trimTop, currentScale);

		return trimTop;
	};

	@Override
	protected double calcScale(double windowWidth, double imageWidth) {
		return imageWidth / windowWidth;
	}

	@Override
	protected PtlWebElement newPtlWebElement() {
		return new PtlIPhoneWebElement();
	}

}
