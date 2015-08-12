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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.google.common.base.Supplier;
import com.htmlhifive.pitalium.core.annotation.PtlWebDriverStrategy;
import com.htmlhifive.pitalium.core.rules.PtlWebDriverCloser;
import com.htmlhifive.pitalium.junit.ParameterizedClassRule;
import com.htmlhifive.pitalium.junit.PtlBlockJUnit4ClassRunnerWithParametersFactory;

public class PtlWebDriverManagerTest {

	@Before
	public void reset() {
		MockWebDriver.createCount = 0;
	}

	//<editor-fold desc="MockWebDriver">

	static class MockWebDriver implements WebDriver {

		static int createCount;
		boolean quit;

		MockWebDriver() {
			createCount++;
		}

		@Override
		public void get(String s) {
		}

		@Override
		public String getCurrentUrl() {
			return null;
		}

		@Override
		public String getTitle() {
			return null;
		}

		@Override
		public List<WebElement> findElements(By by) {
			return null;
		}

		@Override
		public WebElement findElement(By by) {
			return null;
		}

		@Override
		public String getPageSource() {
			return null;
		}

		@Override
		public void close() {
			quit = true;
		}

		@Override
		public void quit() {
			quit = true;
		}

		@Override
		public Set<String> getWindowHandles() {
			return null;
		}

		@Override
		public String getWindowHandle() {
			return null;
		}

		@Override
		public TargetLocator switchTo() {
			return null;
		}

		@Override
		public Navigation navigate() {
			return null;
		}

		@Override
		public Options manage() {
			return null;
		}
	}

	//</editor-fold>

	//<editor-fold desc="NoAnnotationTest">

	@RunWith(Parameterized.class)
	@Parameterized.UseParametersRunnerFactory(PtlBlockJUnit4ClassRunnerWithParametersFactory.class)
	public static class NoAnnotationTest {

		@Parameterized.Parameters
		public static Collection<Object[]> parameters() {
			PtlCapabilities capabilities1 = new PtlCapabilities(new HashMap<String, Object>());
			capabilities1.setBrowserName("chrome");

			return Collections.singletonList(new Object[] { capabilities1 });
		}

		@ParameterizedClassRule
		public static PtlWebDriverCloser webDriverCloser = new PtlWebDriverCloser() {
			@Override
			protected void finished(Description description, Object[] params) {
				super.finished(description, params);

				assertThat(MockWebDriver.createCount, is(2));
			}
		};

		@Parameterized.Parameter
		public PtlCapabilities capabilities;

		PtlWebDriverManager.WebDriverContainer container;
		MockWebDriver driver;

		@Before
		public void initDriver() throws Exception {
			container = PtlWebDriverManager.getInstance().getWebDriver(getClass(), capabilities,
					new Supplier<WebDriver>() {
						@Override
						public WebDriver get() {
							return new MockWebDriver();
						}
					});
			driver = container.get();
		}

		@After
		public void after() throws Exception {
			container.quit();
			assertThat(driver.quit, is(true));
		}

		@Test
		public void test1() throws Exception {
			assertThat(container.reuse, is(false));
		}

		@Test
		public void test2() throws Exception {
		}

	}

	/**
	 * {@link com.htmlhifive.pitalium.core.annotation.PtlWebDriverStrategy}を設定しない場合のWebDriver寿命テスト<br />
	 * 毎回ドライバーが生成、クローズされる。
	 */
	@Test
	public void noAnnotation() throws Exception {
		Result result = JUnitCore.runClasses(NoAnnotationTest.class);
		assertThat(result.getRunCount(), is(2));
		assertThat(result.getFailureCount(), is(0));
	}

	//</editor-fold>

	//<editor-fold desc="WithAnnotationNoReuse">

	@PtlWebDriverStrategy(reuse = false)
	public static class WithAnnotationNoReuse extends NoAnnotationTest {
	}

	/**
	 * {@link com.htmlhifive.pitalium.core.annotation.PtlWebDriverStrategy}を設定した場合のWebDriver寿命テスト<br />
	 * reuse = false とすることで毎回ドライバーが生成、クローズされる。
	 */
	@Test
	public void withAnnotationNoReuse() throws Exception {
		Result result = JUnitCore.runClasses(WithAnnotationNoReuse.class);
		assertThat(result.getRunCount(), is(2));
		assertThat(result.getFailureCount(), is(0));
	}

	//</editor-fold>

	//<editor-fold desc="WithAnnotationReuse">

	@RunWith(Parameterized.class)
	@Parameterized.UseParametersRunnerFactory(PtlBlockJUnit4ClassRunnerWithParametersFactory.class)
	@PtlWebDriverStrategy(reuse = true)
	public static class WithAnnotationReuse {

		@Parameterized.Parameters
		public static Collection<Object[]> parameters() {
			PtlCapabilities capabilities1 = new PtlCapabilities(new HashMap<String, Object>());
			capabilities1.setBrowserName("chrome");

			return Collections.singletonList(new Object[] { capabilities1 });
		}

		@ParameterizedClassRule
		public static PtlWebDriverCloser webDriverCloser = new PtlWebDriverCloser() {
			@Override
			protected void finished(Description description, Object[] params) {
				assertThat(MockWebDriver.createCount, is(1));
				assertThat(driverInstance.quit, is(false));

				super.finished(description, params);

				assertThat(driverInstance.quit, is(true));
			}
		};

		static MockWebDriver driverInstance;

		@Parameterized.Parameter
		public PtlCapabilities capabilities;

		PtlWebDriverManager.WebDriverContainer container;
		MockWebDriver driver;

		@Before
		public void initDriver() throws Exception {
			container = PtlWebDriverManager.getInstance().getWebDriver(getClass(), capabilities,
					new Supplier<WebDriver>() {
						@Override
						public WebDriver get() {
							return driverInstance = new MockWebDriver();
						}
					});
			driver = container.get();
		}

		@After
		public void after() throws Exception {
			container.quit();
			assertThat(driver.quit, is(false));
		}

		@Test
		public void test1() throws Exception {
			assertThat(container.reuse, is(true));
		}

		@Test
		public void test2() throws Exception {
		}

	}

	/**
	 * {@link com.htmlhifive.pitalium.core.annotation.PtlWebDriverStrategy}を設定した場合のWebDriver寿命テスト<br />
	 * reuse = true とすることでドライバーがクローズされずに再利用される。
	 */
	@Test
	public void withAnnotationReuse() throws Exception {
		Result result = JUnitCore.runClasses(WithAnnotationReuse.class);
		assertThat(result.getRunCount(), is(2));
		assertThat(result.getFailureCount(), is(0));
	}

	//</editor-fold>

}