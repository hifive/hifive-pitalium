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

package com.htmlhifive.pitalium.it.screenshot;

import static org.hamcrest.Matchers.*;
import static org.junit.Assume.*;

import org.junit.Before;

import com.htmlhifive.pitalium.core.config.ExecMode;
import com.htmlhifive.pitalium.it.PtlItTestBase;

public abstract class PtlItScreenshotTestBase extends PtlItTestBase {

	@Before
	@Override
	public void setUp() {
		// RUN_TESTだったらスキップする
		assumeThat(getCurrentMode(), is(not(ExecMode.RUN_TEST)));

		super.setUp();
	}

	/**
	 * @return
	 */
	protected boolean isIgnoreCorners() {
		return isMacOS() && isChrome();
	}
}
