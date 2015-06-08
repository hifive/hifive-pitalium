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
package com.htmlhifive.testlib.core.selenium;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.htmlhifive.testlib.common.exception.TestRuntimeException;

/**
 * URL操作を行うユーティリティクラス
 */
final class UrlUtils {

	private static final Logger LOG = LoggerFactory.getLogger(UrlUtils.class);

	private static final Pattern HTTP_PREFIX_PATTERN = Pattern.compile("^https?://");

	private UrlUtils() {
	}

	/**
	 * BaseURLとpathからURLを取得します。ただし{@code path}がhttp、httpsから始まる場合はBaseURLを使用せずpathをそのまま返します。
	 * 
	 * @param baseUrl 基本URL
	 * @param path パス
	 * @return URL
	 */
	static String getTargetUrl(String baseUrl, String path) {
		boolean baseUrlIsEmpty = Strings.isNullOrEmpty(baseUrl);
		boolean pathIsEmpty = Strings.isNullOrEmpty(path);

		if (baseUrlIsEmpty && pathIsEmpty) {
			String message = "Both \"baseUrl\" and \"path\" are empty.";
			LOG.error(message);
			throw new TestRuntimeException(message);
		}

		if (baseUrlIsEmpty) {
			return path;
		}

		if (pathIsEmpty) {
			return baseUrl;
		}

		// pathが http:// または https:// から始まる場合はpathをそのまま利用する
		if (HTTP_PREFIX_PATTERN.matcher(path).find()) {
			return path;
		}

		return baseUrl + path;
	}

}
