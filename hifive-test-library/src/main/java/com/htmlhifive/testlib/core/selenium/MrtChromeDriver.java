/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.core.selenium;

import java.net.URL;

/**
 * Google Chromeで利用する{@link org.openqa.selenium.WebDriver}
 */
class MrtChromeDriver extends SplitScreenshotWebDriver {

	/**
	 * コンストラクタ
	 * 
	 * @param remoteAddress RemoteWebDriverServerのアドレス
	 * @param capabilities Capability
	 */
	MrtChromeDriver(URL remoteAddress, MrtCapabilities capabilities) {
		super(remoteAddress, capabilities);
	}

	@Override
	protected MrtWebElement newMrtWebElement() {
		return new MrtChromeWebElement();
	}
}
