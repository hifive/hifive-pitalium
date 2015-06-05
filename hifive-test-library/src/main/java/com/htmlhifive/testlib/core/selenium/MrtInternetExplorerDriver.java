/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.core.selenium;

import java.awt.image.BufferedImage;
import java.net.URL;

import com.htmlhifive.testlib.common.exception.TestRuntimeException;

/**
 * Internet Explorerで利用する{@link org.openqa.selenium.WebDriver}
 */
class MrtInternetExplorerDriver extends MrtWebDriver {

	private final int chromeWidth;

	/**
	 * コンストラクタ
	 * 
	 * @param remoteAddress RemoteWebDriverServerのアドレス
	 * @param capabilities Capability
	 */
	MrtInternetExplorerDriver(URL remoteAddress, MrtCapabilities capabilities) {
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
	protected MrtWebElement newMrtWebElement() {
		return new MrtInternetExplorerWebElement();
	}

	@Override
	public BufferedImage getEntirePageScreenshot() {
		//Dimension windowDimension = manage().window().getSize();
		//MrtWebElement bodyElement = (MrtWebElement) findElementByTagName("body");
		//WebElementRect bodyRect = bodyElement.getRect();
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
}
