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

import org.openqa.selenium.WebElement;

import com.htmlhifive.pitalium.image.util.ImageUtils;

/**
 * Internet Explorer 8で利用する{@link org.openqa.selenium.WebDriver}
 */
class PtlInternetExplorer8Driver extends PtlInternetExplorerDriver {

	private static final String GET_FRAMEBORDER_INT_SCRIPT = "return parseInt(arguments[0].frameBorder)";

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

	@Override
	protected BufferedImage trimTargetBorder(WebElement el, BufferedImage image) {
		BufferedImage trimedImage = super.trimTargetBorder(el, image);

		// IE7・8はiframeに2pxのボーダーが写りこむため削る
		if (el.getTagName().equals("iframe")) {
			int frameBorder = Integer.parseInt(executeScript(GET_FRAMEBORDER_INT_SCRIPT, el).toString());
			if (frameBorder > 0) {
				int top = 2;
				int left = 2;
				int bottom = 2;
				int right = 2;

				return ImageUtils.trim(trimedImage, top, left, bottom, right);
			}
		}
		return trimedImage;
	}
}
