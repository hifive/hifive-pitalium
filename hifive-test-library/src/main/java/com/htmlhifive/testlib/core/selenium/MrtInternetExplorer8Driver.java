/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.core.selenium;

import java.awt.image.BufferedImage;
import java.net.URL;

import com.htmlhifive.testlib.image.util.ImageUtils;

/**
 * Internet Explorer 8で利用する{@link org.openqa.selenium.WebDriver}
 */
class MrtInternetExplorer8Driver extends MrtInternetExplorerDriver {

	/**
	 * コンストラクタ
	 * 
	 * @param remoteAddress RemoteWebDriverServerのアドレス
	 * @param capabilities Capability
	 */
	MrtInternetExplorer8Driver(URL remoteAddress, MrtCapabilities capabilities) {
		super(remoteAddress, capabilities);
	}

	@Override
	protected MrtWebElement newMrtWebElement() {
		return new MrtInternetExplorer8WebElement();
	}

	@Override
	public BufferedImage getEntirePageScreenshot() {
		// IE7,8は上下左右2pxを削る
		BufferedImage screenshot = super.getEntirePageScreenshot();
		return ImageUtils.trim(screenshot, 2, 2, 2, 2);
	}

}
