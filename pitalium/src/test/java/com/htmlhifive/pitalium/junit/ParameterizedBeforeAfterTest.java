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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

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
@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(PtlBlockJUnit4ClassRunnerWithParametersFactory.class)
public class ParameterizedBeforeAfterTest {

	/**
	 *
	 */
	private enum Category {
		NONE, PARAM_0, PARAM_1
	}

	private enum State {
		BEFORE_CLASS, PARAMETERIZED_BEFORE_CLASS, BEFORE, METHOD, AFTER, PARAMETERIZED_AFTER_CLASS, AFTER_CLASS
	}

	private static class Container {
		final Category category;
		final State state;

		public Container(Category category, State state) {
			this.category = category;
			this.state = state;
		}
	}

	private static List<Container> testResults = new ArrayList<Container>();

	@BeforeClass
	public static void beforeClass() throws Exception {
		testResults.add(new Container(Category.NONE, State.BEFORE_CLASS));
	}

	@AfterClass
	public static void afterClass() throws Exception {
		testResults.add(new Container(Category.NONE, State.AFTER_CLASS));

		// パラメーター毎のState順番確認
		State[] param0 = FluentIterable.from(testResults).filter(new Predicate<Container>() {
			@Override
			public boolean apply(Container container) {
				return container.category == Category.NONE || container.category == Category.PARAM_0;
			}
		}).transform(new Function<Container, State>() {
			@Override
			public State apply(Container container) {
				return container.state;
			}
		}).toArray(State.class);

		State[] param1 = FluentIterable.from(testResults).filter(new Predicate<Container>() {
			@Override
			public boolean apply(Container container) {
				return container.category == Category.NONE || container.category == Category.PARAM_1;
			}
		}).transform(new Function<Container, State>() {
			@Override
			public State apply(Container container) {
				return container.state;
			}
		}).toArray(State.class);

		State[] expectedStates = { State.BEFORE_CLASS, State.PARAMETERIZED_BEFORE_CLASS, State.BEFORE, State.METHOD,
				State.AFTER, State.BEFORE, State.METHOD, State.AFTER, State.BEFORE, State.METHOD, State.AFTER,
				State.PARAMETERIZED_AFTER_CLASS, State.AFTER_CLASS };

		assertArrayEquals(expectedStates, param0);
		assertArrayEquals(expectedStates, param1);
	}

	@ParameterizedBeforeClass
	public static void parameterizedBeforeClass(Category category, String text) throws Exception {
		assertThat(text, is(category.toString()));
		testResults.add(new Container(category, State.PARAMETERIZED_BEFORE_CLASS));
	}

	@ParameterizedAfterClass
	public static void parameterizedAfterClass(Category category, String text) throws Exception {
		assertThat(text, is(category.toString()));
		testResults.add(new Container(category, State.PARAMETERIZED_AFTER_CLASS));
	}

	@Parameterized.Parameters
	public static Collection<Object[]> params() throws Exception {
		return Arrays.asList(new Object[] { Category.PARAM_0, Category.PARAM_0.name() }, new Object[] {
				Category.PARAM_1, Category.PARAM_1.name() });
	}

	@Parameterized.Parameter(0)
	public Category category;

	@Parameterized.Parameter(1)
	public String text;

	@Before
	public void before() throws Exception {
		testResults.add(new Container(category, State.BEFORE));
	}

	@After
	public void after() throws Exception {
		testResults.add(new Container(category, State.AFTER));
	}

	@Test
	public void method_0() throws Exception {
		testResults.add(new Container(category, State.METHOD));
	}

	@Test
	public void method_1() throws Exception {
		testResults.add(new Container(category, State.METHOD));
	}

	@Test
	public void method_2() throws Exception {
		testResults.add(new Container(category, State.METHOD));
	}

}
