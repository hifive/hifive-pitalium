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
package com.htmlhifive.testlib.core.selenium;

import java.awt.image.BufferedImage;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Android端末で利用する{@link org.openqa.selenium.WebDriver}
 */
class MrtAndroidDriver extends SplitScreenshotWebDriver {
	private static final Logger LOG = LoggerFactory.getLogger(MrtAndroidDriver.class);

	/**
	 * コンストラクタ
	 * 
	 * @param remoteAddress RemoteWebDriverServerのアドレス
	 * @param capabilities Capability
	 */
	MrtAndroidDriver(URL remoteAddress, MrtCapabilities capabilities) {
		super(remoteAddress, capabilities);
	}

	@Override
	protected boolean canHideScrollbar() {
		return false;
	}

	@Override
	protected double calcScale(double windowWidth, double imageWidth) {
		return imageWidth / windowWidth;
	}

	@Override
	protected BufferedImage trimCaptureTop(double captureTop, long windowHeight, double scale, BufferedImage img) {
		// 下端の推定位置（次スクロール時にトップに来る位置）と、実際のキャプチャに写っている下端の位置を比較
		long calculatedBottomValue = Math.round((captureTop + windowHeight) * scale);
		long actualBottomValue = Math.round(captureTop * scale) + img.getHeight();
		// 余分にキャプチャに写っていたら切り取っておく
		if (calculatedBottomValue < actualBottomValue) {
			return img.getSubimage(0, 0, img.getWidth(),
					(int) (img.getHeight() - (actualBottomValue - calculatedBottomValue)));
		} else {
			return img;
		}
	}

	@Override
	protected MrtWebElement newMrtWebElement() {
		return new MrtAndroidWebElement();
	}

}
