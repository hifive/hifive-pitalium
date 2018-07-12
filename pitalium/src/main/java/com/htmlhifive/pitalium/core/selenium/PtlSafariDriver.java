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
 * Mac OS XのSafariで利用する{@link org.openqa.selenium.WebDriver}
 */
class PtlSafariDriver extends SplitScreenshotWebDriver {

	/**
	 * コンストラクタ
	 *
	 * @param remoteAddress RemoteWebDriverServerのアドレス
	 * @param capabilities Capability
	 */
	PtlSafariDriver(URL remoteAddress, PtlCapabilities capabilities) {
		super(remoteAddress, capabilities);
	}

	/**
	 * コンストラクタ
	 *
	 * @param executor CommandExecutor
	 * @param capabilities Capability
	 */
	PtlSafariDriver(CommandExecutor executor, PtlCapabilities capabilities) {
		super(executor, capabilities);
	}

	@Override
	protected PtlWebElement newPtlWebElement() {
		return new PtlSafariWebElement();
	}

}
