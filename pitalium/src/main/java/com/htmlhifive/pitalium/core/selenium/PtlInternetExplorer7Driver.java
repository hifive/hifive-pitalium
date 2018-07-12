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

import java.net.URL;

import org.openqa.selenium.remote.CommandExecutor;

/**
 * Internet Explorer 7で利用する{@link org.openqa.selenium.WebDriver}
 */
class PtlInternetExplorer7Driver extends PtlInternetExplorer8Driver {

	/**
	 * コンストラクタ
	 *
	 * @param remoteAddress RemoteWebDriverServerのアドレス
	 * @param capabilities Capability
	 */
	PtlInternetExplorer7Driver(URL remoteAddress, PtlCapabilities capabilities) {
		super(remoteAddress, capabilities);
	}

	/**
	 * コンストラクタ
	 *
	 * @param executor CommandExecutor
	 * @param capabilities Capability
	 */
	PtlInternetExplorer7Driver(CommandExecutor executor, PtlCapabilities capabilities) {
		super(executor, capabilities);
	}

	@Override
	protected boolean canHideBodyScrollbar() {
		return false;
	}

	@Override
	protected boolean canMoveTarget() {
		return false;
	}

	@Override
	protected PtlWebElement newPtlWebElement() {
		return new PtlInternetExplorer7WebElement();
	}

	@Override
	public long getCurrentPageHeight() {
		String docMode = executeJavaScript("return document.compatMode;");
		if ("BackCompat".equals(docMode)) {
			// 互換モード時はbodyのgetBoundingClientRect().topが取得できないため、scrollHeightを返す
			PtlWebElement bodyElement = (PtlWebElement) findElementByTagName("body");
			Number scrollHeight = executeJavaScript(
					"var _scrollHeight = arguments[0].scrollHeight; return _scrollHeight", bodyElement);
			long result = scrollHeight.longValue();
			LOG.trace("(GetCurrentPageHeight) [{}]", result);
			return result;
		}

		return super.getCurrentPageHeight();
	}

}
