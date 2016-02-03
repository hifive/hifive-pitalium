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

/**
 * Internet Explorer 8で利用する{@link org.openqa.selenium.WebElement}
 */
class PtlInternetExplorer8WebElement extends PtlInternetExplorerWebElement {

	@Override
	public DoubleValueRect getDoubleValueRect() {
		DoubleValueRect rect = super.getDoubleValueRect();
		double width = rect.getWidth();
		double height = rect.getHeight();
		if ("iframe".equals(getTagName())) {
			width -= 1d;
			height -= 1d;
		}

		return new DoubleValueRect(rect.getLeft(), rect.getTop(), width, height);
	}

	@Override
	public WebElementBorderWidth getBorderWidth() {
		if ("iframe".equals(getTagName())) {
			// IE7・8はiframeのボーダーが2px
			double top = 2;
			double left = 2;
			double bottom = 2;
			double right = 2;

			return new WebElementBorderWidth(top, right, bottom, left);
		}
		return super.getBorderWidth();
	}
}
