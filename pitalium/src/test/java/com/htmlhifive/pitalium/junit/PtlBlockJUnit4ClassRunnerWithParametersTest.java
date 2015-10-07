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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runners.Parameterized;

public class PtlBlockJUnit4ClassRunnerWithParametersTest {

	//<editor-fold desc="ValidateParameterizedClassRule">

	@RunWith(Parameterized.class)
	@Parameterized.UseParametersRunnerFactory(PtlBlockJUnit4ClassRunnerWithParametersFactory.class)
	public static class ValidateParameterizedClassRule {

		@Parameterized.Parameters
		public static Collection<Object[]> parameters() {
			return Collections.singletonList(new Object[] { "" });
		}

		@ParameterizedClassRule
		Object rule = new Object();

		@ParameterizedClassRule
		Object classRule(Object param) {
			return null;
		}

		@Parameterized.Parameter
		public String param;

		@Test
		public void test1() {
		}

	}

	/**
	 * {@link ParameterizedClassRule}を設定したフィールドまたはメソッドのバリデーションテスト
	 */
	@Test
	public void testValidateParameterizedClassRule() throws Exception {
		Result result = JUnitCore.runClasses(ValidateParameterizedClassRule.class);
		assertThat(result.getFailureCount(), is(7));

		for (Failure failure : result.getFailures()) {
			assertThat(
					failure.getMessage(),
					is(anyOf(equalTo("Method classRule() should be static"),
							equalTo("Method classRule() should be public"),
							equalTo("Method classRule() should have no parameters"),
							equalTo("Method classRule() must return an implementation of ParameterizedTestRule"),
							equalTo("Field rule should be static"), equalTo("Field rule should be public"),
							equalTo("Field rule must implement ParameterizedTestRule"))));
		}
	}

	//</editor-fold>

	//<editor-fold desc="ValidateParameterizedBeforeClass">

	@RunWith(Parameterized.class)
	@Parameterized.UseParametersRunnerFactory(PtlBlockJUnit4ClassRunnerWithParametersFactory.class)
	public static class ValidateParameterizedBeforeClass {

		@Parameterized.Parameters
		public static Collection<Object[]> parameters() {
			return Collections.singletonList(new Object[] { "" });
		}

		@ParameterizedBeforeClass
		Object beforeClass() throws Exception {
			return null;
		}

		@Parameterized.Parameter
		public String param;

		@Test
		public void test1() {
		}

	}

	/**
	 * {@link ParameterizedBeforeClass}を設定したメソッドのバリデーションテスト
	 */
	@Test
	public void testValidateParameterizedBeforeClass() throws Exception {
		Result result = JUnitCore.runClasses(ValidateParameterizedBeforeClass.class);
		assertThat(result.getFailureCount(), is(3));

		for (Failure failure : result.getFailures()) {
			assertThat(
					failure.getMessage(),
					is(anyOf(equalTo("Method beforeClass() should be static"),
							equalTo("Method beforeClass() should be public"),
							//							equalTo("Method beforeClass() should have 1 parameters"),
							equalTo("Method beforeClass() should be void"))));
		}
	}

	//</editor-fold>

	//<editor-fold desc="ValidateParameterizedAfterClass">

	@RunWith(Parameterized.class)
	@Parameterized.UseParametersRunnerFactory(PtlBlockJUnit4ClassRunnerWithParametersFactory.class)
	public static class ValidateParameterizedAfterClass {

		@Parameterized.Parameters
		public static Collection<Object[]> parameters() {
			return Collections.singletonList(new Object[] { "" });
		}

		@ParameterizedAfterClass
		Object afterClass() throws Exception {
			return null;
		}

		@Parameterized.Parameter
		public String param;

		@Test
		public void test1() {
		}

	}

	/**
	 * {@link ParameterizedAfterClass}を設定したメソッドのバリデーションテスト
	 */
	@Test
	public void testValidateParameterizedAfterClass() throws Exception {
		Result result = JUnitCore.runClasses(ValidateParameterizedAfterClass.class);
		assertThat(result.getFailureCount(), is(3));

		for (Failure failure : result.getFailures()) {
			assertThat(
					failure.getMessage(),
					is(anyOf(equalTo("Method afterClass() should be static"),
							equalTo("Method afterClass() should be public"),
							//							equalTo("Method afterClass() should have 1 parameters"),
							equalTo("Method afterClass() should be void"))));
		}
	}

	//</editor-fold>
}