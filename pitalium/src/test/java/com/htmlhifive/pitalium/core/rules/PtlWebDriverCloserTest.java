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

package com.htmlhifive.pitalium.core.rules;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.htmlhifive.pitalium.common.util.JSONUtils;
import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.annotation.PtlWebDriverStrategy;
import com.htmlhifive.pitalium.core.selenium.PtlCapabilities;

/**
 * PtlWebDriverCloserでWebDriverを適切にクローズしているかのテスト
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.net.ssl.*")
@PrepareForTest(PtlCapabilities.class)
public class PtlWebDriverCloserTest {

	private static Map<Capabilities, WebDriver> drivers = new HashMap<Capabilities, WebDriver>();

	public static abstract class TestCaseBase extends PtlTestBase {
		@After
		public void addDriverForReleaseResources() {
			drivers.put(capabilities, driver);
		}

		@Test
		public void test1() throws Exception {
			assertTrue(true);
		}
	}

	@Before
	public void prepare() throws Exception {
		// Mock capabilities
		List<PtlCapabilities[]> mockCapabilities = new ArrayList<PtlCapabilities[]>(2);
		mockStatic(PtlCapabilities.class);
		when(PtlCapabilities.readCapabilities()).thenReturn(mockCapabilities);

		// Read capabilities from file
		InputStream in = null;
		try {
			in = getClass().getResourceAsStream("PtlWebDriverCloserTest_capabilities.json");
			List<Map<String, Object>> maps = JSONUtils.readValue(in, new TypeReference<List<Map<String, Object>>>() {
			});
			for (Map<String, Object> map : maps) {
				mockCapabilities.add(new PtlCapabilities[] { new PtlCapabilities(map) });
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	@After
	public void reset() throws Exception {
		for (WebDriver driver : drivers.values()) {
			try {
				driver.quit();
			} catch (Exception e) {
				// Do nothing
			}
		}

		drivers.clear();
	}

	@PtlWebDriverStrategy(sessionLevel = PtlWebDriverStrategy.SessionLevel.GLOBAL)
	public static class GlobalTestCase extends TestCaseBase {
	}

	@Test
	public void globalTest() throws Exception {
		JUnitCore.runClasses(GlobalTestCase.class);

		for (WebDriver driver : drivers.values()) {
			driver.getWindowHandle();
		}
	}

	@PtlWebDriverStrategy(sessionLevel = PtlWebDriverStrategy.SessionLevel.TEST_CLASS)
	public static class TestClassTestCase extends TestCaseBase {
	}

	@Test
	public void testClassTest() throws Exception {
		JUnitCore.runClasses(TestClassTestCase.class);

		for (WebDriver driver : drivers.values()) {
			try {
				driver.getWindowHandle();
				fail();
			} catch (WebDriverException e) {
				assertTrue(true);
			}
		}
	}

	@PtlWebDriverStrategy(sessionLevel = PtlWebDriverStrategy.SessionLevel.TEST_CASE)
	public static class TestCaseTestCase extends TestCaseBase {
	}

	@Test
	public void testCaseTest() throws Exception {
		JUnitCore.runClasses(TestCaseTestCase.class);

		for (WebDriver driver : drivers.values()) {
			try {
				driver.getWindowHandle();
				fail();
			} catch (WebDriverException e) {
				assertTrue(true);
			}
		}
	}

}