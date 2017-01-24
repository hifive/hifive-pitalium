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

package com.htmlhifive.pitalium.it.screenshot2.excludeInScroll;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.core.model.TargetResult;
import com.htmlhifive.pitalium.image.model.RectangleArea;
import com.htmlhifive.pitalium.it.screenshot2.PtlItScreenshotTestBase;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

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
	public void excludeVisibleArea() throws Exception {
		openScrollPage();

		// 一つ目のTRを除外する
		Rect tableRect = getRectById("table-scroll");
		Rect rowRect = getRect((WebElement) driver.executeJavaScript(""
				+ "var table = document.getElementById('table-scroll');"
				+ "return table.getElementsByTagName('tr')[0];"));

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#table-scroll > tbody")
				.addExclude(rowRect.x, rowRect.y, rowRect.width, rowRect.height).build();
		assertionView.assertView(arg);

		// Check
		TargetResult result = loadTargetResults("s").get(0);
		assertThat(result.getExcludes(), hasSize(1));

		double x = Math.round(rowRect.x - tableRect.x);
		double y = Math.round(rowRect.y - tableRect.y);
		double width = Math.round(rowRect.width);
		double height = Math.round(rowRect.height);
		assertThat(result.getExcludes().get(0).getRectangle(), is(new RectangleArea(x, y, width, height)));
	}

	/**
	 * 要素内スクロールの撮影において、最初は見えていない領域を指定して除外する。
	 * 
	 * @ptl.expect 除外領域が正しく保存されていること。
	 */
	@Test
	public void excludeNotVisibleArea() throws Exception {
		openScrollPage();

		// 最後のTRを除外する
		Rect tableRect = getRectById("table-scroll");
		Rect rowRect = getRect((WebElement) driver.executeJavaScript(""
				+ "var table = document.getElementById('table-scroll');" + "var tr = table.getElementsByTagName('tr');"
				+ "return tr[tr.length - 1];"));

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#table-scroll > tbody")
				.addExclude(rowRect.x, rowRect.y, rowRect.width, rowRect.height).build();
		assertionView.assertView(arg);

		// Check
		TargetResult result = loadTargetResults("s").get(0);
		assertThat(result.getExcludes(), hasSize(1));

		double x = Math.round(rowRect.x - tableRect.x);
		double y = Math.round(rowRect.y - tableRect.y);
		double width = Math.round(rowRect.width);
		double height = Math.round(rowRect.height);
		assertThat(result.getExcludes().get(0).getRectangle(), is(new RectangleArea(x, y, width, height)));
	}

}
