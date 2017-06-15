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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.htmlhifive.pitalium.common.exception.TestRuntimeException;

@RunWith(Enclosed.class)
public class UrlUtilsTest {

	/**
	 * 両方が空以外のパラメーターのテスト
	 */
	@RunWith(Parameterized.class)
	public static class NonEmptyParameterTest {

		@Parameterized.Parameters(name = "{0}")
		public static Collection<Fixture> parameters() throws Exception {
			return Arrays.asList(new Fixture(null, "path", "path"), new Fixture("", "path", "path"), new Fixture(
					"base", null, "base"), new Fixture("base", "", "base"), new Fixture("base", "path", "basepath"),
					new Fixture("base", "http://localhost/", "http://localhost/"), new Fixture("base",
							"https://localhost/", "https://localhost/"));
		}

		@Parameterized.Parameter
		public Fixture fixture;

		@Test
		public void testGetTargetUrl() throws Exception {
			assertThat(UrlUtils.getTargetUrl(fixture.baseUrl, fixture.path), is(fixture.targetUrl));
		}

	}

	/**
	 * 両方とも空パラメーターのテスト
	 */
	@RunWith(Parameterized.class)
	public static class EmptyParameterTest {

		@Parameterized.Parameters(name = "{0}")
		public static Collection<Fixture> parameters() throws Exception {
			return Arrays.asList(new Fixture(null, null, null), new Fixture("", null, null),
					new Fixture(null, "", null), new Fixture("", "", null));
		}

		@Rule
		public ExpectedException expected = ExpectedException.none();

		@Parameterized.Parameter
		public Fixture fixture;

		@Test
		public void testGetTargetUrl() throws Exception {
			expected.expect(TestRuntimeException.class);
			UrlUtils.getTargetUrl(fixture.baseUrl, fixture.path);
		}
	}

	static class Fixture {
		final String baseUrl;
		final String path;
		final String targetUrl;

		public Fixture(String baseUrl, String path, String targetUrl) {
			this.baseUrl = baseUrl;
			this.path = path;
			this.targetUrl = targetUrl;
		}

		@Override
		public String toString() {
			return "Fixture{" + "baseUrl='" + baseUrl + '\'' + ", path='" + path + '\'' + ", targetUrl='" + targetUrl
					+ '\'' + '}';
		}
	}

}