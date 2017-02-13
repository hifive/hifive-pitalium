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

import com.htmlhifive.pitalium.image.util.ImageUtils;

/**
 * Android端末で利用する{@link org.openqa.selenium.WebDriver}。Appium + Chrome向け。
 */
class PtlAndroidDriver extends PtlAbsAndroidDriver {
	private static final Logger LOG = LoggerFactory.getLogger(PtlAndroidDriver.class);

	/**
	 * コンストラクタ
	 * 
	 * @param remoteAddress RemoteWebDriverServerのアドレス
	 * @param capabilities Capability
	 */
	PtlAndroidDriver(URL remoteAddress, PtlCapabilities capabilities) {
		super(remoteAddress, capabilities);
	}

	@Override
	protected boolean canHideBodyScrollbar() {
		return true;
	}

	@Override
	protected boolean canHideElementScrollbar() {
		return false;
	};

	@Override
	protected PtlWebElement newPtlWebElement() {
		return new PtlAndroidWebElement();
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
		if (targetElement.isBody()) {
			return trimTop;
		}

		trimTop = adjustTrimTop(trimTop, currentScale);

		return trimTop;
	};

}
