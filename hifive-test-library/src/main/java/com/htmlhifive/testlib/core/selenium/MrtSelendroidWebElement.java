/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.core.selenium;

import com.google.common.base.CaseFormat;

class MrtSelendroidWebElement extends MrtAndroidWebElement {

	private static final String GET_CSS_VALUE_SCRIPT = "var _val = arguments[0].style.%s; return _val;";

	@Override
	public String getCssValue(String propertyName) {
		String name = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, propertyName);
		String script = String.format(GET_CSS_VALUE_SCRIPT, name);
		return getWrappedDriver().executeJavaScript(script, this);
	}

}
