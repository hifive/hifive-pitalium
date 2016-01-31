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
 * Firefoxで利用する{@link org.openqa.selenium.WebElement}
 */
class PtlFirefoxWebElement extends PtlWebElement {

	/**
	 * コンストラクタ
	 */
	PtlFirefoxWebElement() {
	}

	@Override
	public long getClientHeight() {
		long clientHeight = super.getClientHeight();
		// firefoxのtextareaは上下paddingが常に表示されるため、padding分を除く
		if ("textarea".equals(getTagName())) {
			WebElementPadding padding = getPadding();
			clientHeight -= (int) Math.round(padding.getTop()) + (int) Math.round(padding.getBottom());
		}
		return clientHeight;
	}

	@Override
	protected int getContainedPaddingHeight(int i, int size) {
		int padding = super.getContainedPaddingHeight(i, size);
		if ("textarea".equals(getTagName())) {
			WebElementPadding targetPadding = getPadding();
			if (i <= 0) {
				padding += (int) Math.round(targetPadding.getTop());
			} else if (i >= size - 1) {
				padding += (int) Math.round(targetPadding.getBottom());
			}
		}
		return padding;
	}
}
