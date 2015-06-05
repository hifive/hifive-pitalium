/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.core.selenium;

import java.net.URL;

/**
 * Internet Explorer 7で利用する{@link org.openqa.selenium.WebDriver}
 */
class MrtInternetExplorer7Driver extends MrtInternetExplorer8Driver {

	/**
	 * コンストラクタ
	 * 
	 * @param remoteAddress RemoteWebDriverServerのアドレス
	 * @param capabilities Capability
	 */
	MrtInternetExplorer7Driver(URL remoteAddress, MrtCapabilities capabilities) {
		super(remoteAddress, capabilities);
	}

	@Override
	protected boolean canHideScrollbar() {
		return false;
	}

	@Override
	protected boolean canMoveTarget() {
		return false;
	}

	@Override
	protected MrtWebElement newMrtWebElement() {
		return new MrtInternetExplorer7WebElement();
	}

	@Override
	public long getCurrentPageHeight() {
		String docMode = executeJavaScript("return document.compatMode;");
		if (docMode.equals("BackCompat")) {
			// 互換モード時はbodyのgetBoundingClientRect().topが取得できないため、scrollHeightを返す
			MrtWebElement bodyElement = (MrtWebElement) findElementByTagName("body");
			Number scrollHeight = executeJavaScript(
					"var _scrollHeight = arguments[0].scrollHeight; return _scrollHeight", bodyElement);
			return scrollHeight.longValue();
		}

		return super.getCurrentPageHeight();
	}

}
