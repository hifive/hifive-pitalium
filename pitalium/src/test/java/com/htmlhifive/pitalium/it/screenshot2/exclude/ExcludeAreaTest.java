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

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.core.model.TargetResult;
import com.htmlhifive.pitalium.image.model.RectangleArea;
import com.htmlhifive.pitalium.it.screenshot2.PtlItScreenshotTestBase;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * 範囲を指定して除外するテスト
 */
public class ExcludeAreaTest extends PtlItScreenshotTestBase {

	/**
	 * 範囲を指定して除外する。moveオブションあり。
	 * 
	 * @ptl.expect 除外領域が正しく保存されていること。
	 */
	@Test
	public void move() throws Exception {
		openBasicColorPage();

		Rect rect = getRectById("colorColumn2");
		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTarget()
				.addExclude(rect.x, rect.y, rect.width, rect.height).addNewTargetById("container").moveTarget(true)
				.addExclude(rect.x, rect.y, rect.width, rect.height).moveTarget(true).build();
		assertionView.assertView(arg);

		// Check
		List<TargetResult> results = loadTargetResults("s");
		assertThat(results, hasSize(2));

		// Body
		Rect pixelRect = getPixelRectById("colorColumn2");
		TargetResult bodyResult = results.get(0);
		assertThat(bodyResult.getExcludes(), hasSize(1));
		assertThat(bodyResult.getExcludes().get(0).getRectangle(), is(pixelRect.toRectangleArea()));

		// Container
		TargetResult containerResult = results.get(1);
		assertThat(containerResult.getExcludes(), hasSize(1));

		Rect containerRect = getPixelRectById("container");
		assertThat(containerResult.getExcludes().get(0).getRectangle(), is(new RectangleArea(pixelRect.x
				- containerRect.x, pixelRect.y - containerRect.y, pixelRect.width, pixelRect.height)));
	}

	/**
	 * 範囲を指定して除外する。moveオブションなし。
	 * 
	 * @ptl.expect 除外領域が正しく保存されていること。
	 */
	@Test
	public void notMove() throws Exception {
		openBasicColorPage();

		Rect rect = getRectById("colorColumn2");
		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTarget()
				.addExclude(rect.x, rect.y, rect.width, rect.height).addNewTargetById("container").moveTarget(false)
				.addExclude(rect.x, rect.y, rect.width, rect.height).moveTarget(false).build();
		assertionView.assertView(arg);

		// Check
		List<TargetResult> results = loadTargetResults("s");
		assertThat(results, hasSize(2));

		// Body
		Rect pixelRect = getPixelRectById("colorColumn2");
		TargetResult bodyResult = results.get(0);
		assertThat(bodyResult.getExcludes(), hasSize(1));
		assertThat(bodyResult.getExcludes().get(0).getRectangle(), is(pixelRect.toRectangleArea()));

		// Container
		TargetResult containerResult = results.get(1);
		assertThat(containerResult.getExcludes(), hasSize(1));

		Rect containerRect = getPixelRectById("container");
		assertThat(containerResult.getExcludes().get(0).getRectangle(), is(new RectangleArea(pixelRect.x
				- containerRect.x, pixelRect.y - containerRect.y, pixelRect.width, pixelRect.height)));
	}

}
