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

import com.htmlhifive.pitalium.image.util.ImageUtils;

/**
 * Internet Explorer 8で利用する{@link org.openqa.selenium.WebDriver}
 */
class PtlInternetExplorer8Driver extends PtlInternetExplorerDriver {

	/**
	 * コンストラクタ
	 *
	 * @param remoteAddress RemoteWebDriverServerのアドレス
	 * @param capabilities Capability
	 */
	PtlInternetExplorer8Driver(URL remoteAddress, PtlCapabilities capabilities) {
		super(remoteAddress, capabilities);
	}

	@Override
	protected PtlWebElement newPtlWebElement() {
		return new PtlInternetExplorer8WebElement();
	}

	@Override
	public BufferedImage getEntirePageScreenshot() {
		// IE7,8は上下左右2pxを削る
		BufferedImage screenshot = super.getEntirePageScreenshot();
		return ImageUtils.trim(screenshot, 2, 2, 2, 2);
	}
}
