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

package com.htmlhifive.pitalium.junit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * <p>
 * {@link Parameterized}と{@link ParameterizedBeforeClass}、{@link ParameterizedAfterClass}のテスト
 * </p>
 * JUnitのサイクルが以下の順で回ることを確認する。
 * <ul>
 * <li>BEFORE_CLASS</li>
 * <li>PARAMETERIZED_BEFORE_CLASS</li>
 * <li>BEFORE</li>
 * <li>METHOD</li>
 * <li>AFTER</li>
 * <li>PARAMETERIZED_AFTER_CLASS</li>
 * <li>AFTER_CLASS</li>
 * </ul>
 */
public class ParameterizedBeforeAfterTest {

	static String[] logs = { "", "" };

	@RunWith(Parameterized.class)
	@Parameterized.UseParametersRunnerFactory(PtlBlockJUnit4ClassRunnerWithParametersFactory.class)
	public static class ParameterizedBeforeAndAfterTestClass {

		@Parameterized.Parameters
		public static Collection<Object[]> parameters() {
			return Arrays.asList(new Object[] { 0 }, new Object[] { 1 });
		}

		@BeforeClass
		public static void beforeClass() throws Exception {
			logs[0] += "beforeClass ";
			logs[1] += "beforeClass ";
		}

		@AfterClass
		public static void afterClass() throws Exception {
			logs[0] += "afterClass ";
			logs[1] += "afterClass ";
		}

		@ParameterizedBeforeClass
		public static void parameterizedBeforeClass(int param) throws Exception {
			logs[param] += "parameterizedBeforeClass ";
		}

		@ParameterizedAfterClass
		public static void parameterizedAfterClass(int param) throws Exception {
			logs[param] += "parameterizedAfterClass ";
		}

		@Parameterized.Parameter
		public int param;

		@Before
		public void before() throws Exception {
			logs[param] += "before ";
		}

		@After
		public void after() throws Exception {
			logs[param] += "after ";
		}

		@Test
		public void test1() throws Exception {
			logs[param] += "test ";
		}

		@Test
		public void test2() throws Exception {
			logs[param] += "test ";
		}

	}

	@Test
	public void testParameterizedBeforeClassAndAfterClass() throws Exception {
		Result result = JUnitCore.runClasses(ParameterizedBeforeAndAfterTestClass.class);
		assertThat(result.getRunCount(), is(4));

		final String expectedLog = "beforeClass parameterizedBeforeClass before test after before test after parameterizedAfterClass afterClass ";
		assertThat(logs[0], is(expectedLog));
		assertThat(logs[1], is(expectedLog));
	}

}
