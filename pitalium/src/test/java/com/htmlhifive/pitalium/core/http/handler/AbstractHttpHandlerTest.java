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

package com.htmlhifive.pitalium.core.http.handler;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.sun.net.httpserver.HttpExchange;

public class AbstractHttpHandlerTest {

	/**
	 * QueryStringをパースするテスト
	 */
	@Test
	public void parseQueryString() throws Exception {
		AbstractHttpHandler handler = new MockHttpHandler();
		Map<String, String> queryStrings = handler.parseQueryString("aaa=AAA&bbb=BBB&ccc=CCC");
		assertThat(queryStrings.size(), is(3));
		assertThat(queryStrings.get("aaa"), is("AAA"));
		assertThat(queryStrings.get("bbb"), is("BBB"));
		assertThat(queryStrings.get("ccc"), is("CCC"));
	}

	/**
	 * 空のQueryStringをパースするテスト
	 */
	@Test
	public void parseEmptyQueryString() throws Exception {
		AbstractHttpHandler handler = new MockHttpHandler();
		Map<String, String> queryStrings = handler.parseQueryString("");
		assertThat(queryStrings.size(), is(0));
	}

	/**
	 * nullのQueryStringをパースするテスト
	 */
	@Test
	public void parseNullQueryString() throws Exception {
		AbstractHttpHandler handler = new MockHttpHandler();
		Map<String, String> queryStrings = handler.parseQueryString(null);
		assertThat(queryStrings.size(), is(0));
	}

	private static class MockHttpHandler extends AbstractHttpHandler {
		@Override
		protected void handle(HttpExchange httpExchange, Map<String, String> queryStrings) throws IOException {
		}
	}

}