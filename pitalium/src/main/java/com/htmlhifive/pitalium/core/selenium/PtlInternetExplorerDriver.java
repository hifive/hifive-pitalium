/*
 * Copyright (C) 2015-2017 NS Solutions Corporation
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
 * Internet Explorerで利用する{@link org.openqa.selenium.WebDriver}
 */
class PtlInternetExplorerDriver extends SplitScreenshotWebDriver {

	private final int chromeWidth;

	/**
	 * コンストラクタ
	 * 
	 * @param remoteAddress RemoteWebDriverServerのアドレス
	 * @param capabilities Capability
	 */
	PtlInternetExplorerDriver(URL remoteAddress, PtlCapabilities capabilities) {
		super(remoteAddress, capabilities);

		Object chromeWidthCapability = capabilities.getCapability("chromeWidth");
		if (chromeWidthCapability == null) {
			throw new TestRuntimeException("Capability \"chromeWidth\" is required.");
		}

		if (chromeWidthCapability instanceof Number) {
			chromeWidth = ((Number) chromeWidthCapability).intValue();
		} else {
			chromeWidth = Integer.parseInt(chromeWidthCapability.toString());
		}
	}

	@Override
	protected PtlWebElement newPtlWebElement() {
		return new PtlInternetExplorerWebElement();
	}

	@Override
	public BufferedImage getEntirePageScreenshot() {
		//Dimension windowDimension = manage().window().getSize();
		//PtlWebElement bodyElement = (PtlWebElement) findElementByTagName("body");
		//DoubleValueRect bodyRect = bodyElement.getRect();
		//WebElementMargin bodyMargin = bodyElement.getMargin();

		// スクリーンショット取得前の拡大幅を計算
		//int newWidth = (int) (windowDimension.width - bodyRect.width - chromeWidth - bodyMargin.left - bodyMargin.right);

		// IEの場合、スクリーンショット前後で特定のピクセル数分幅を拡大し、元に戻す
		//manage().window().setSize(new Dimension(newWidth, windowDimension.height));

		BufferedImage screenshot = super.getEntirePageScreenshot();

		// 元のサイズに戻す
		//manage().window().setSize(windowDimension);

		return screenshot;
	}

	/**
	 * ウィンドウクロムの幅を取得します。
	 * 
	 * @return クロム幅（整数px）
	 */
	public int getChromeWidth() {
		return chromeWidth;
	}

	@Override
	protected boolean canResizeElement() {
		return false;
	}
}
