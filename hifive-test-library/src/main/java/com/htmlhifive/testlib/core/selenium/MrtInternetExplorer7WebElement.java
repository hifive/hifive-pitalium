/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.core.selenium;

/**
 * Internet Explorer 7で利用する{@link org.openqa.selenium.WebElement}
 */
class MrtInternetExplorer7WebElement extends MrtInternetExplorerWebElement {

	@Override
	public WebElementRect getRect() {
		// IE7と8はbody以外座標を-2する
		WebElementRect rect = super.getRect();
		if ("body".equals(getTagName())) {
			return rect;
		}

		return new WebElementRect(rect.getLeft() - 2d, rect.getTop() - 2d, rect.getWidth(), rect.getHeight());
	}
}
