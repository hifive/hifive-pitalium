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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.ImmutableMap;
import com.htmlhifive.pitalium.common.exception.TestRuntimeException;

public class TextReplacerTest {

	@Rule
	public ExpectedException expected = ExpectedException.none();

	/**
	 * 置換前の文字列はNonNull
	 */
	@Test
	public void replace_baseNonNull() throws Exception {
		expected.expect(NullPointerException.class);
		TextReplacer.replace(null, new HashMap<String, Object>());
	}

	/**
	 * プレースホルダを置換するパラメーター一覧はNonNull
	 */
	@Test
	public void replace_parametersNonNull() throws Exception {
		expected.expect(NullPointerException.class);
		TextReplacer.replace("", null);
	}

	/**
	 * プレースホルダ文字列が空の場合TestRuntimeException
	 */
	@Test
	public void replace_emptyPlaceholder() throws Exception {
		expected.expect(TestRuntimeException.class);
		TextReplacer.replace("${}", new HashMap<String, Object>());
	}

	/**
	 * プレースホルダを置換するパラメーターが存在しない場合TestRuntimeException
	 */
	@Test
	public void replace_parameterNotFound() throws Exception {
		expected.expect(TestRuntimeException.class);
		TextReplacer.replace("${hoge}", new HashMap<String, Object>());
	}

	@Test
	public void replace() throws Exception {
		String base = "Lorem ipsum dolor ${sit} amet, ${consectetur} adipisicing elit";
		Map<String, ?> params = ImmutableMap.of("sit", "SIT", "consectetur", "CONSECTETUR");
		String result = TextReplacer.replace(base, params);
		String expected = "Lorem ipsum dolor SIT amet, CONSECTETUR adipisicing elit";

		assertThat(result, is(expected));
	}

}