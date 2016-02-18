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

package com.htmlhifive.pitalium.core.selenium;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.htmlhifive.pitalium.common.util.JSONUtils;
import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.annotation.PtlWebDriverStrategy;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import com.htmlhifive.pitalium.core.config.WebDriverSessionLevel;

/**
 * WebDriverManagerとWebDriverStrategy、reuseDriverForAllClassesのテスト
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.net.ssl.*")
@PrepareForTest(PtlCapabilities.class)
public class PtlWebDriverManagerTest {

	//<editor-fold desc="TestClasses">

	public static abstract class BaseClass extends PtlTestBase {

		final int classId;

		public BaseClass(int classId) {
			this.classId = classId;
		}

		@After
		public void addDriverForReleaseResources() {
			drivers.add(driver);
		}

		@Test
		public void test1() throws Exception {
			driverInstanceIds.get(capabilities).put(classId | TEST_1, System.identityHashCode(driver));
		}

		@Test
		public void test2() throws Exception {
			driverInstanceIds.get(capabilities).put(classId | TEST_2, System.identityHashCode(driver));
		}

	}

	public static final int NO_ANNOTATION_1 = 0x01;
	public static final int NO_ANNOTATION_2 = 0x02;
	public static final int ANNOTATED_ONLY = 0x04;
	public static final int ANNOTATED_USE_CONFIG = 0x08;
	public static final int ANNOTATED_TEST_CASE = 0x10;
	public static final int ANNOTATED_TEST_CLASS = 0x20;
	public static final int ANNOTATED_GLOBAL = 0x40;

	public static final int TEST_1 = 0x1 << 8;
	public static final int TEST_2 = 0x2 << 8;

	public static class NoAnnotation1 extends BaseClass {
		public NoAnnotation1() {
			super(NO_ANNOTATION_1);
		}
	}

	public static class NoAnnotation2 extends BaseClass {
		public NoAnnotation2() {
			super(NO_ANNOTATION_2);
		}
	}

	@PtlWebDriverStrategy
	public static class AnnotatedOnly extends BaseClass {
		public AnnotatedOnly() {
			super(ANNOTATED_ONLY);
		}
	}

	@PtlWebDriverStrategy(sessionLevel = PtlWebDriverStrategy.SessionLevel.USE_CONFIG)
	public static class AnnotatedUseConfig extends BaseClass {
		public AnnotatedUseConfig() {
			super(ANNOTATED_USE_CONFIG);
		}
	}

	@PtlWebDriverStrategy(sessionLevel = PtlWebDriverStrategy.SessionLevel.TEST_CASE)
	public static class AnnotatedTestCase extends BaseClass {
		public AnnotatedTestCase() {
			super(ANNOTATED_TEST_CASE);
		}
	}

	@PtlWebDriverStrategy(sessionLevel = PtlWebDriverStrategy.SessionLevel.TEST_CLASS)
	public static class AnnotatedTestClass extends BaseClass {
		public AnnotatedTestClass() {
			super(ANNOTATED_TEST_CLASS);
		}
	}

	@PtlWebDriverStrategy(sessionLevel = PtlWebDriverStrategy.SessionLevel.GLOBAL)
	public static class AnnotatedGlobal extends BaseClass {
		public AnnotatedGlobal() {
			super(ANNOTATED_GLOBAL);
		}
	}

	//</editor-fold>

	static final Class[] TEST_CLASSES = { NoAnnotation1.class, NoAnnotation2.class, AnnotatedOnly.class,
			AnnotatedUseConfig.class, AnnotatedTestCase.class, AnnotatedTestClass.class, AnnotatedGlobal.class };

	static Map<Capabilities, Map<Integer, Integer>> driverInstanceIds;
	static Collection<WebDriver> drivers;

	@Before
	public void prepare() throws Exception {
		// Mock capabilities
		List<PtlCapabilities[]> mockCapabilities = new ArrayList<PtlCapabilities[]>(2);
		mockStatic(PtlCapabilities.class);
		when(PtlCapabilities.readCapabilities()).thenReturn(mockCapabilities);

		// Read capabilities from file
		InputStream in = null;
		try {
			in = getClass().getResourceAsStream("PtlWebDriverManagerTest_capabilities.json");
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

		drivers = new HashSet<WebDriver>();
		driverInstanceIds = new HashMap<Capabilities, Map<Integer, Integer>>();
		for (PtlCapabilities[] capabilities : PtlCapabilities.readCapabilities()) {
			driverInstanceIds.put(capabilities[0], new HashMap<Integer, Integer>());
		}
	}

	@After
	public void reset() throws Exception {
		for (WebDriver driver : drivers) {
			try {
				driver.quit();
			} catch (Exception e) {
				// Do nothing
			}
		}

		drivers.clear();
		driverInstanceIds.clear();
	}

	@AfterClass
	public static void resetDriverManager() throws Exception {
		PtlWebDriverManager.getInstance().resetCache(
				PtlTestConfig.getInstance().getEnvironment().getWebDriverSessionLevel());
	}

	/**
	 * <p>
	 * 全クラスでWebDriverを再利用するテスト。<br />
	 * {@link PtlWebDriverStrategy#sessionLevel()}が設定されていない、またはデフォルト値の場合全体で再利用される。
	 * </p>
	 * 以下が同じWebDriverのインスタンスIDになる。
	 * <ul>
	 * <li>NO_ANNOTATED_1 - 1,2 / NO_ANNOTATED_2 - 1,2 / ANNOTATED_ONLY - 1,2 / ANNOTATED_USE_CONFIG - 1,2 /
	 * ANNOTATED_GLOBAL - 1,2</li>
	 * <li>ANNOTATED_TEST_CLASS - 1,2</li>
	 * <li>ANNOTATED_TEST_CASE - 1</li>
	 * <li>ANNOTATED_TEST_CASE - 2</li>
	 * </ul>
	 */
	@Test
	public void sessionLevelGlobal() throws Exception {
		PtlWebDriverManager.getInstance().resetCache(WebDriverSessionLevel.GLOBAL);
		JUnitCore.runClasses(TEST_CLASSES);

		for (Map<Integer, Integer> instanceIds : driverInstanceIds.values()) {
			assertThat(instanceIds.size(), is(14));
			assertThat(new HashSet<Integer>(instanceIds.values()).size(), is(4));

			// NO_ANNOTATED_1 - 1,2 / NO_ANNOTATED_2 - 1,2 / ANNOTATED_ONLY - 1,2 /
			// ANNOTATED_USE_CONFIG - 1,2 / ANNOTATED_GLOBAL - 1,2
			int[] classIds = { NO_ANNOTATION_1, NO_ANNOTATION_2, ANNOTATED_ONLY, ANNOTATED_USE_CONFIG, ANNOTATED_GLOBAL };

			int expect1 = instanceIds.get(NO_ANNOTATION_1 | TEST_1);
			for (int classId : classIds) {
				assertThat(instanceIds.get(classId | TEST_1), is(expect1));
				assertThat(instanceIds.get(classId | TEST_2), is(expect1));
			}

			// ANNOTATED_TEST_CLASS - 1,2
			int expect2 = instanceIds.get(ANNOTATED_TEST_CLASS | TEST_1);
			assertThat(expect2, is(not(expect1)));
			assertThat(instanceIds.get(ANNOTATED_TEST_CLASS | TEST_2), is(expect2));

			// ANNOTATED_TEST_CASE - 1,2
			int testCase1 = instanceIds.get(ANNOTATED_TEST_CASE | TEST_1);
			int testCase2 = instanceIds.get(ANNOTATED_TEST_CASE | TEST_2);
			assertThat(testCase1, is(not(expect1)));
			assertThat(testCase1, is(not(expect2)));
			assertThat(testCase1, is(not(testCase2)));
		}
	}

	/**
	 * <p>
	 * テストクラス毎にWebDriverを再利用するテスト。
	 * </p>
	 * 以下が同じWebDriverのインスタンスIDになる。
	 * <ul>
	 * <li>NO_ANNOTATED_1 - 1,2</li>
	 * <li>NO_ANNOTATED_2 - 1,2</li>
	 * <li>ANNOTATED_ONLY - 1,2</li>
	 * <li>ANNOTATED_USE_CONFIG - 1,2</li>
	 * <li>ANNOTATED_GLOBAL - 1,2</li>
	 * <li>ANNOTATED_TEST_CLASS - 1,2</li>
	 * <li>ANNOTATED_TEST_CASE - 1</li>
	 * <li>ANNOTATED_TEST_CASE - 2</li>
	 * </ul>
	 */
	@Test
	public void sessionLevelTestClass() throws Exception {
		PtlWebDriverManager.getInstance().resetCache(WebDriverSessionLevel.TEST_CLASS);
		JUnitCore.runClasses(TEST_CLASSES);

		for (Map<Integer, Integer> instanceIds : driverInstanceIds.values()) {
			assertThat(instanceIds.size(), is(14));
			assertThat(new HashSet<Integer>(instanceIds.values()).size(), is(8));

			assertThat(instanceIds.get(NO_ANNOTATION_1 | TEST_1), is(instanceIds.get(NO_ANNOTATION_1 | TEST_2)));
			assertThat(instanceIds.get(NO_ANNOTATION_2 | TEST_1), is(instanceIds.get(NO_ANNOTATION_2 | TEST_2)));
			assertThat(instanceIds.get(ANNOTATED_ONLY | TEST_1), is(instanceIds.get(ANNOTATED_ONLY | TEST_2)));
			assertThat(instanceIds.get(ANNOTATED_USE_CONFIG | TEST_1),
					is(instanceIds.get(ANNOTATED_USE_CONFIG | TEST_2)));
			assertThat(instanceIds.get(ANNOTATED_GLOBAL | TEST_1), is(instanceIds.get(ANNOTATED_GLOBAL | TEST_2)));
			assertThat(instanceIds.get(ANNOTATED_TEST_CLASS | TEST_1),
					is(instanceIds.get(ANNOTATED_TEST_CLASS | TEST_2)));
			assertThat(instanceIds.get(ANNOTATED_TEST_CASE | TEST_1),
					is(not(instanceIds.get(ANNOTATED_TEST_CASE | TEST_2))));
		}
	}

	/**
	 * <p>
	 * 全クラスでWebDriverを再利用しないテスト。
	 * </p>
	 * 以下が同じWebDriverのインスタンスIDになる。
	 * <ul>
	 * <li>ANNOTATED_GLOBAL - 1,2</li>
	 * <li>ANNOTATED_TEST_CLASS - 1,2</li>
	 * </ul>
	 */
	@Test
	public void sessionLevelTestCase() throws Exception {
		PtlWebDriverManager.getInstance().resetCache(WebDriverSessionLevel.TEST_CASE);
		JUnitCore.runClasses(TEST_CLASSES);

		for (Map<Integer, Integer> instanceIds : driverInstanceIds.values()) {
			assertThat(instanceIds.size(), is(14));
			assertThat(new HashSet<Integer>(instanceIds.values()).size(), is(12));

			assertThat(instanceIds.get(ANNOTATED_GLOBAL | TEST_1), is(instanceIds.get(ANNOTATED_GLOBAL | TEST_2)));
			assertThat(instanceIds.get(ANNOTATED_TEST_CLASS | TEST_1),
					is(instanceIds.get(ANNOTATED_TEST_CLASS | TEST_2)));
		}
	}

}
