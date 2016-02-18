/*
 * Copyright (C) 2016 NS Solutions Corporation
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Android端末で利用する{@link org.openqa.selenium.WebDriver}の抽象クラス。
 */
abstract class PtlAbsAndroidDriver extends SplitScreenshotWebDriver {

	private static final Logger LOG = LoggerFactory.getLogger(PtlAbsAndroidDriver.class);

	/**
	 * コンストラクタ
	 * 
	 * @param remoteAddress RemoteWebDriverServerのアドレス
	 * @param capabilities Capability
	 */
	PtlAbsAndroidDriver(URL remoteAddress, PtlCapabilities capabilities) {
		super(remoteAddress, capabilities);
	}

	@Override
	protected double calcScale(double windowWidth, double imageWidth) {
		return imageWidth / windowWidth;
	}

}
