/*
 * Copyright (C) 2015-2016 NS Solutions Corporation
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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("all")
public class JSONUtilsTest {

	Map<String, String> data;
	File file;

	@Before
	public void initObject() throws Exception {
		data = new HashMap<>();
		data.put("a", "b");
	}

	@After
	public void deleteTempFile() throws Exception {
		if (file != null) {
			file.delete();
		}
	}

	@Test
	public void testToString() throws Exception {
		String result = JSONUtils.toString(data);
		assertThat(result, is("{\"a\":\"b\"}"));
	}

	@Test
	public void testWriteValue() throws Exception {
		file = File.createTempFile("tmp", null);
		JSONUtils.writeValue(file, data);

		String result = FileUtils.readFileToString(file);
		assertThat(result, is("{\"a\":\"b\"}"));
	}

	@Test
	public void testToStringWithIndent() throws Exception {
		String result = JSONUtils.toStringWithIndent(data);
		assertThat(result.split(System.lineSeparator()), is(new String[] { "{", "  \"a\" : \"b\"", "}" }));
	}

	@Test
	public void testWriteValueWithIndent() throws Exception {
		file = File.createTempFile("tmp", null);
		JSONUtils.writeValueWithIndent(file, data);

		String result = FileUtils.readFileToString(file);
		assertThat(result.split(System.lineSeparator()), is(new String[] { "{", "  \"a\" : \"b\"", "}" }));
	}

}