/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.core.selenium;

import java.net.URL;

/**
 * Firefoxで利用する{@link org.openqa.selenium.WebDriver}
 */
class MrtFirefoxDriver extends MrtWebDriver {

	/**
	 * コンストラクタ
	 * 
	 * @param remoteAddress RemoteWebDriverServerのアドレス
	 * @param capabilities Capability
	 */
	MrtFirefoxDriver(URL remoteAddress, MrtCapabilities capabilities) {
		super(remoteAddress, capabilities);
	}

	@Override
	protected MrtWebElement newMrtWebElement() {
		return new MrtFirefoxWebElement();
	}
}
