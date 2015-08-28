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

package com.htmlhifive.pitalium.common.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.util.Strings;

import com.htmlhifive.pitalium.common.exception.TestRuntimeException;

/**
 * &quot;${}&quot;を含む文字列を適切な文字列で置換
 *
 * @author nakatani
 */
public class TextReplacer {

	public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{(.*?)\\}");

	public static String replace(String base, Map<String, ?> parameters) {
		if (base == null || parameters == null) {
			throw new NullPointerException();
		}

		Matcher m = PLACEHOLDER_PATTERN.matcher(base);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			String placeholderText = m.group(1).trim();
			if (Strings.isEmpty(placeholderText)) {
				throw new TestRuntimeException("Placeholder text cannot be empty.");
			}

			Object param = parameters.get(placeholderText);
			if (param == null) {
				throw new TestRuntimeException("Parameter for [" + placeholderText + "] not found.");
			}

			m.appendReplacement(sb, param.toString());
		}

		m.appendTail(sb);

		return sb.toString();
	}

}
