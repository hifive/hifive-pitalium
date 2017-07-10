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

package com.htmlhifive.pitalium.it.screenshot.excludeInScroll;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeFalse;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.core.model.TargetResult;
import com.htmlhifive.pitalium.image.model.RectangleArea;
import com.htmlhifive.pitalium.it.screenshot.PtlItScreenshotTestBase;

/**
 * 要素内スクロールと除外設定を組み合わせるテスト
 */
public class ExcludeAreaInScrollTest extends PtlItScreenshotTestBase {

	/**
	 * 要素内スクロールの撮影において、最初から見えている領域を指定して除外する。
	 *
	 * @ptl.expect 除外領域が正しく保存されていること。
	 */
	@Test
	public void excludeVisibleArea_move() throws Exception {
		assumeFalse("Skip IE8 table test.", isInternetExplorer8());
		assumeFalse("Skip IE9 table test.", isInternetExplorer9());

		openScrollPage();

		// 一つ目のTRを除外する
		WebElement row = driver.executeJavaScript("" + "var table = document.getElementById('table-scroll');"
				+ "return table.getElementsByTagName('tr')[0];");
		Rect tableRect = getRectById("table-scroll");
		Rect rowRect = getPixelRect(row);

		double x = Math.round(rowRect.x - tableRect.x);
		double y = Math.round(rowRect.y - tableRect.y);
		double width = Math.round(rowRect.width);
		double height = Math.round(rowRect.height);

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#table-scroll > tbody")
				.scrollTarget(true).moveTarget(true).addExclude(x, y, width, height).build();
		assertionView.assertView(arg);

		// Check
		TargetResult result = loadTargetResults("s").get(0);
		assertThat(result.getExcludes(), hasSize(1));

		assertThat(result.getExcludes().get(0).getRectangle(), is(new RectangleArea(x, y, width, height)));
	}

	/**
	 * 要素内スクロールの撮影において、最初は見えていない領域を指定して除外する。
	 *
	 * @ptl.expect 除外領域が正しく保存されていること。
	 */
	@Test
	public void excludeNotVisibleArea_move() throws Exception {
		assumeFalse("Skip IE8 table test.", isInternetExplorer8());
		assumeFalse("Skip IE9 table test.", isInternetExplorer9());

		openScrollPage();

		// 最後のTRを除外する
		WebElement row = driver.executeJavaScript("" + "var table = document.getElementById('table-scroll');"
				+ "var tr = table.getElementsByTagName('tr');" + "return tr[tr.length - 1];");
		Rect rowRect = getPixelRect(row);
		Rect tableRect = getPixelRectById("table-scroll");
		double x = Math.round(rowRect.x - tableRect.x);
		double y = Math.round(rowRect.y - tableRect.y);
		double width = Math.round(rowRect.width);
		double height = Math.round(rowRect.height);

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#table-scroll > tbody")
				.scrollTarget(true).moveTarget(true).addExclude(x, y, width, height).build();
		assertionView.assertView(arg);

		// Check
		TargetResult result = loadTargetResults("s").get(0);
		assertThat(result.getExcludes(), hasSize(1));

		assertThat(result.getExcludes().get(0).getRectangle(), is(new RectangleArea(x, y, width, height)));
	}

	/**
	 * 要素内スクロールの撮影において、最初から見えている領域を指定して除外する。
	 *
	 * @ptl.expect 除外領域が正しく保存されていること。
	 */
	@Test
	public void excludeVisibleArea_notMove() throws Exception {
		assumeFalse("Skip IE8 table test.", isInternetExplorer8());
		assumeFalse("Skip IE9 table test.", isInternetExplorer9());

		openScrollPage();

		// 一つ目のTRを除外する
		WebElement row = driver.executeJavaScript("" + "var table = document.getElementById('table-scroll');"
				+ "return table.getElementsByTagName('tr')[0];");
		Rect tableRect = getPixelRectById("table-scroll");
		Rect rowRect = getPixelRect(row);

		double x = Math.round(rowRect.x - tableRect.x);
		double y = Math.round(rowRect.y - tableRect.y);
		double width = Math.round(rowRect.width);
		double height = Math.round(rowRect.height);

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#table-scroll > tbody")
				.scrollTarget(true).moveTarget(false).addExclude(x, y, width, height).build();
		assertionView.assertView(arg);

		// Check
		TargetResult result = loadTargetResults("s").get(0);
		assertThat(result.getExcludes(), hasSize(1));
		assertThat(result.getExcludes().get(0).getRectangle(), is(new RectangleArea(x, y, width, height)));
	}

	/**
	 * 要素内スクロールの撮影において、最初は見えていない領域を指定して除外する。
	 *
	 * @ptl.expect 除外領域が正しく保存されていること。
	 */
	@Test
	public void excludeNotVisibleArea_notMove() throws Exception {
		assumeFalse("Skip IE8 table test.", isInternetExplorer8());
		assumeFalse("Skip IE9 table test.", isInternetExplorer9());

		openScrollPage();

		// 最後のTRを除外する
		WebElement row = driver.executeJavaScript("" + "var table = document.getElementById('table-scroll');"
				+ "var tr = table.getElementsByTagName('tr');" + "return tr[tr.length - 1];");
		Rect tableRect = getPixelRectById("table-scroll");
		Rect rowRect = getPixelRect(row);

		double x = Math.round(rowRect.x - tableRect.x);
		double y = Math.round(rowRect.y - tableRect.y);
		double width = Math.round(rowRect.width);
		double height = Math.round(rowRect.height);

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#table-scroll > tbody")
				.scrollTarget(true).moveTarget(false).addExclude(x, y, width, height).build();
		assertionView.assertView(arg);

		// Check
		TargetResult result = loadTargetResults("s").get(0);
		assertThat(result.getExcludes(), hasSize(1));
		assertThat(result.getExcludes().get(0).getRectangle(), is(new RectangleArea(x, y, width, height)));
	}

}
