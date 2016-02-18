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

import com.google.common.base.CaseFormat;

/**
 * Selendroid（Android>4.3）で利用する{@link org.openqa.selenium.WebElement}
 */
class PtlSelendroidWebElement extends PtlAndroidWebElement {

	private static final String GET_CSS_VALUE_SCRIPT = "var _val = arguments[0].style.%s; return _val;";

	@Override
	public String getCssValue(String propertyName) {
		String name = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, propertyName);
		String script = String.format(GET_CSS_VALUE_SCRIPT, name);
		return getWrappedDriver().executeJavaScript(script, this);
	}

}
