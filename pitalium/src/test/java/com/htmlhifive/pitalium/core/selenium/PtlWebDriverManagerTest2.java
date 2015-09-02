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
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.annotation.PtlWebDriverStrategy;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;

public class PtlWebDriverManagerTest2 {

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
			driverInstanceIds.get(capabilities).put(Pair.of(classId, 1), driver.toString());
		}

		@Test
		public void test2() throws Exception {
			driverInstanceIds.get(capabilities).put(Pair.of(classId, 2), driver.toString());
		}

	}

	public static class Class1 extends BaseClass {
		public Class1() {
			super(1);
		}
	}

	@PtlWebDriverStrategy(reuse = true)
	public static class Class2 extends BaseClass {
		public Class2() {
			super(2);
		}
	}

	@PtlWebDriverStrategy(reuse = false)
	public static class Class3 extends BaseClass {
		public Class3() {
			super(3);
		}
	}

	public static class Class4 extends BaseClass {
		public Class4() {
			super(4);
		}
	}

	//</editor-fold>

	static Map<Capabilities, Map<Pair<Integer, Integer>, String>> driverInstanceIds;
	static Collection<WebDriver> drivers;

	@Before
	public void prepare() throws Exception {
		drivers = new HashSet<WebDriver>();
		driverInstanceIds = new HashMap<Capabilities, Map<Pair<Integer, Integer>, String>>();
		for (PtlCapabilities[] capabilities : PtlCapabilities.readCapabilities()) {
			driverInstanceIds.put(capabilities[0], new HashMap<Pair<Integer, Integer>, String>());
		}
	}

	@After
	public void reset() throws Exception {
		for (WebDriver driver : drivers) {
			try {
				driver.quit();
			} catch (Exception e) {
			}
		}

		drivers.clear();
		driverInstanceIds.clear();
	}

	@AfterClass
	public static void resetDriverManager() throws Exception {
		PtlWebDriverManager.getInstance()
				.resetCache(PtlTestConfig.getInstance().getEnvironment().isReuseDriverForAllClasses());
	}

	/**
	 * <p>
	 * 全クラスでWebDriverを再利用するテスト。<br />
	 * WebDriverStrategy::reuse = true/false が明示されているクラスでは再利用は行われない。
	 * </p>
	 * 以下が同じWebDriverのインスタンスIDになる。
	 * <ul>
	 * <li>1-1, 1-2, 4-1, 4-2</li>
	 * <li>2-1, 2-2</li>
	 * <li>3-1</li>
	 * <li>3-2</li>
	 * </ul>
	 */
	@Test
	public void reuseDriverOnAllClasses() throws Exception {
		PtlWebDriverManager.getInstance().resetCache(true);
		JUnitCore.runClasses(Class1.class, Class2.class, Class3.class, Class4.class);

		for (Map<Pair<Integer, Integer>, String> instanceIds : driverInstanceIds.values()) {
			String id11 = instanceIds.get(Pair.of(1, 1));
			String id21 = instanceIds.get(Pair.of(2, 1));
			String id31 = instanceIds.get(Pair.of(3, 1));
			String id41 = instanceIds.get(Pair.of(4, 1));

			assertThat(id11, is(not(anyOf(equalTo(id21), equalTo(id31), equalTo(instanceIds.get(Pair.of(3, 2)))))));
			assertThat(id11, is(allOf(equalTo(instanceIds.get(Pair.of(1, 2))), equalTo(instanceIds.get(Pair.of(4, 1))),
					equalTo(instanceIds.get(Pair.of(4, 2))))));

			assertThat(instanceIds.get(Pair.of(2, 2)), is(id21));
			assertThat(instanceIds.get(Pair.of(3, 2)), is(not(id31)));
		}
	}

	/**
	 * <p>
	 * 全クラスでWebDriverを再利用しないテスト。
	 * </p>
	 * 以下が同じWebDriverのインスタンスIDになる。
	 * <ul>
	 * <li>1-1</li>
	 * <li>1-2</li>
	 * <li>2-1, 2-2</li>
	 * <li>3-1</li>
	 * <li>3-2</li>
	 * <li>4-1</li>
	 * <li>4-2</li>
	 * </ul>
	 */
	@Test
	public void notReuseDriverOnAllClasses() throws Exception {
		PtlWebDriverManager.getInstance().resetCache(false);
		JUnitCore.runClasses(Class1.class, Class2.class, Class3.class, Class4.class);

		for (Map<Pair<Integer, Integer>, String> instanceIds : driverInstanceIds.values()) {
			Collection<String> ids = new HashSet<String>(instanceIds.values());
			assertThat(ids.size(), is(7));
			assertThat(instanceIds.get(Pair.of(2, 1)), is(instanceIds.get(Pair.of(2, 2))));
		}
	}

}
