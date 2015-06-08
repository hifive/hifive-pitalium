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
package com.htmlhifive.testlib.core.model;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class CompareTargetTest {

	/**
	 * デフォルトコンストラクタはBODYがターゲット
	 */
	@Test
	public void testConstructor_default() throws Exception {
		CompareTarget target = new CompareTarget();

		assertThat(target.getCompareArea(), is(ScreenArea.of(SelectorType.TAG_NAME, "body")));
		assertThat(target.getExcludes().length, is(0));
		assertThat(target.getOptions(), is(nullValue()));
		assertThat(target.isMoveTarget(), is(true));
	}

	/**
	 * デフォルトコンストラクタはBODYがターゲット
	 */
	@Test
	public void testConstructor_single_arg() throws Exception {
		CompareTarget target = new CompareTarget(ScreenArea.of(SelectorType.ID, "main"));

		assertThat(target.getCompareArea(), is(ScreenArea.of(SelectorType.ID, "main")));
		assertThat(target.getExcludes().length, is(0));
		assertThat(target.getOptions(), is(nullValue()));
		assertThat(target.isMoveTarget(), is(true));
	}

	/**
	 * コンストラクタ、excludesがnullではない
	 */
	@Test
	public void testConstructor_excludes_notNull() throws Exception {
		CompareTarget target = new CompareTarget(ScreenArea.of(SelectorType.ID, "main"),
				new ScreenArea[] { ScreenArea.of(SelectorType.CLASS_NAME, "link") }, false);

		assertThat(target.getCompareArea(), is(ScreenArea.of(SelectorType.ID, "main")));
		assertThat(target.getExcludes().length, is(1));
		assertThat(target.getExcludes()[0], is(ScreenArea.of(SelectorType.CLASS_NAME, "link")));
		assertThat(target.getOptions(), is(nullValue()));
		assertThat(target.isMoveTarget(), is(false));
	}

	/**
	 * コンストラクタ、excludesがnull
	 */
	@Test
	public void testConstructor_excludes_null() throws Exception {
		CompareTarget target = new CompareTarget(ScreenArea.of(SelectorType.ID, "main"), null, false);

		assertThat(target.getCompareArea(), is(ScreenArea.of(SelectorType.ID, "main")));
		assertThat(target.getExcludes().length, is(0));
		assertThat(target.getOptions(), is(nullValue()));
		assertThat(target.isMoveTarget(), is(false));
	}

}