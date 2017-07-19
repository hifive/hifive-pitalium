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

package com.htmlhifive.pitalium.it.screenshot.exclude;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.core.model.TargetResult;
import com.htmlhifive.pitalium.image.model.RectangleArea;
import com.htmlhifive.pitalium.it.screenshot.PtlItScreenshotTestBase;

/**
 * 範囲を指定して除外するテスト
 */
public class ExcludeAreaTest extends PtlItScreenshotTestBase {

	/**
	 * 範囲を指定して除外する。moveオプションあり。
	 *
	 * @ptl.expect 除外領域が正しく保存されていること。
	 */
	@Test
	public void move() throws Exception {
		openBasicColorPage();

		Rect rect = getPixelRectById("colorColumn2");
		Rect containerRect = getPixelRectById("container");
		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTarget()
				.addExclude(rect.x, rect.y, rect.width, rect.height).addNewTargetById("container").moveTarget(true)
				.addExclude(rect.x - containerRect.x, rect.y - containerRect.y, rect.width, rect.height)
				.moveTarget(true).build();
		assertionView.assertView(arg);

		// Check
		List<TargetResult> results = loadTargetResults("s");
		assertThat(results, hasSize(2));

		// Body
		TargetResult bodyResult = results.get(0);
		assertThat(bodyResult.getExcludes(), hasSize(1));
		assertThat(bodyResult.getExcludes().get(0).getRectangle(), is(rect.toRectangleArea()));

		// Container
		TargetResult containerResult = results.get(1);
		assertThat(containerResult.getExcludes(), hasSize(1));
		assertThat(containerResult.getExcludes().get(0).getRectangle(),
				is(new RectangleArea(rect.x - containerRect.x, rect.y - containerRect.y, rect.width, rect.height)));
	}

	/**
	 * 範囲を指定して除外する。moveオプションなし。
	 *
	 * @ptl.expect 除外領域が正しく保存されていること。
	 */
	@Test
	public void notMove() throws Exception {
		openBasicColorPage();

		Rect rect = getPixelRectById("colorColumn2");
		Rect containerRect = getPixelRectById("container");
		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTarget()
				.addExclude(rect.x, rect.y, rect.width, rect.height).addNewTargetById("container").moveTarget(false)
				.addExclude(rect.x - containerRect.x, rect.y - containerRect.y, rect.width, rect.height)
				.moveTarget(false).build();
		assertionView.assertView(arg);

		// Check
		List<TargetResult> results = loadTargetResults("s");
		assertThat(results, hasSize(2));

		// Body
		TargetResult bodyResult = results.get(0);
		assertThat(bodyResult.getExcludes(), hasSize(1));
		assertThat(bodyResult.getExcludes().get(0).getRectangle(), is(rect.toRectangleArea()));

		// Container
		TargetResult containerResult = results.get(1);
		assertThat(containerResult.getExcludes(), hasSize(1));

		assertThat(containerResult.getExcludes().get(0).getRectangle(),
				is(new RectangleArea(rect.x - containerRect.x, rect.y - containerRect.y, rect.width, rect.height)));
	}

	/**
	 * DOM指定によるtarget（スクロールなし）の場合、範囲を指定して除外する。
	 *
	 * @ptl.expect 除外領域が正しく保存されていること。
	 */
	@Test
	public void excludeNonScrollTargetByCoordinate() throws Exception {
		openScrollPage();

		Rect containerRect = getPixelRectById("no-overflow-inner");
		WebElement line = driver.executeJavaScript("" + "var div = document.getElementById('no-overflow-inner');"
				+ "var line = div.getElementsByClassName('gradation-row');" + "return line[0];");
		Rect rect = getPixelRect(line);

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("no-overflow-inner")
				.addExclude(rect.x - containerRect.x, rect.y - containerRect.y, rect.width, rect.height).build();
		assertionView.assertView(arg);

		// Check
		List<TargetResult> results = loadTargetResults("s");
		assertThat(results, hasSize(1));

		// Container
		TargetResult containerResult = results.get(0);
		assertThat(containerResult.getExcludes(), hasSize(1));
		assertThat(containerResult.getExcludes().get(0).getRectangle(),
				is(new RectangleArea(rect.x - containerRect.x, rect.y - containerRect.y, rect.width, rect.height)));
	}

	/**
	 * 座標によるtargetの場合、範囲を指定して除外する。
	 *
	 * @ptl.expect 除外領域が正しく保存されていること。
	 */
	@Test
	public void excludeCoordinateTargetByCoordinate() throws Exception {
		openScrollPage();

		Rect containerRect = getPixelRectById("no-overflow-inner");
		WebElement line = driver.executeJavaScript("" + "var div = document.getElementById('no-overflow-inner');"
				+ "var line = div.getElementsByClassName('gradation-row');" + "return line[0];");
		Rect rect = getPixelRect(line);

		ScreenshotArgument arg = ScreenshotArgument.builder("s")
				.addNewTarget(containerRect.x, containerRect.y, containerRect.width, containerRect.height)
				.addExclude(rect.x - containerRect.x, rect.y - containerRect.y, rect.width, rect.height).build();
		assertionView.assertView(arg);

		// Check
		List<TargetResult> results = loadTargetResults("s");
		assertThat(results, hasSize(1));

		// Container
		TargetResult containerResult = results.get(0);
		assertThat(containerResult.getExcludes(), hasSize(1));
		assertThat(containerResult.getExcludes().get(0).getRectangle(),
				is(new RectangleArea(rect.x - containerRect.x, rect.y - containerRect.y, rect.width, rect.height)));
	}
}
