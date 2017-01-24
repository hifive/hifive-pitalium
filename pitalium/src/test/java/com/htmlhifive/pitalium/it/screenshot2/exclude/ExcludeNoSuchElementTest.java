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

package com.htmlhifive.pitalium.it.screenshot2.exclude;

import com.htmlhifive.pitalium.core.model.ScreenAreaResult;
import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.core.model.TargetResult;
import com.htmlhifive.pitalium.it.screenshot2.PtlItScreenshotTestBase;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * 存在しない要素を除外するテスト
 */
public class ExcludeNoSuchElementTest extends PtlItScreenshotTestBase {

	/**
	 * 存在しない要素を除外する。
	 * 
	 * @ptl.expect エラーが発生せず、除外領域が保存されていないこと。
	 */
	@Test
	public void noSuchElement() throws Exception {
		openBasicColorPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTarget().addExcludeById("noSuchElement").build();
		assertionView.assertView(arg);

		// Check
		List<ScreenAreaResult> excludes = loadTargetResults("s").get(0).getExcludes();
		assertThat(excludes, is(empty()));
	}

	/**
	 * 複数のスクリーンショット撮影時に、そのいずれかで存在しない要素を除外する。
	 * 
	 * @ptl.expect エラーが発生せず、存在する要素の除外領域が保存されていること。
	 */
	@Test
	public void noSuchElementInMultiTargets() throws Exception {
		openBasicColorPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTarget().addExcludeById("container")
				.addNewTarget().addExcludeById("noSuchElement").build();
		assertionView.assertView(arg);

		// Check
		List<TargetResult> results = loadTargetResults("s");
		assertThat(results, hasSize(2));
		assertThat(results.get(0).getExcludes(), hasSize(1));
		assertThat(results.get(1).getExcludes(), is(empty()));
	}

}
