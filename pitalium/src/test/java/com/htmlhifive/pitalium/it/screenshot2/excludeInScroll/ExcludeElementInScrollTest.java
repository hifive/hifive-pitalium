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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.core.model.TargetResult;
import com.htmlhifive.pitalium.image.model.RectangleArea;
import com.htmlhifive.pitalium.it.screenshot2.PtlItScreenshotTestBase;

/**
 * 要素内スクロールと除外設定を組み合わせるテスト
 */
public class ExcludeElementInScrollTest extends PtlItScreenshotTestBase {

	/**
	 * 要素内スクロールの撮影において、最初から見えている要素を指定して除外する。
	 *
	 * @ptl.expect 除外領域が正しく保存されていること。
	 */
	@Test
	public void excludeVisibleElement() throws Exception {
		assumeFalse("Skip IE8 table test.", isInternetExplorer8());
		assumeFalse("Skip IE9 table test.", isInternetExplorer9());

		openScrollPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#table-scroll > tbody")
				.addExcludeByCssSelector("tr:nth-of-type(1)").build();
		assertionView.assertView(arg);

		// Check
		TargetResult result = loadTargetResults("s").get(0);
		assertThat(result.getExcludes(), hasSize(1));

		Rect tableRect = getRectById("table-scroll");
		Rect rowRect = getRect(
				(WebElement) driver.executeJavaScript("" + "var table = document.getElementById('table-scroll');"
						+ "return table.getElementsByTagName('tr')[0];"));

		RectangleArea expectedRect = createExpectedRect(tableRect, rowRect);
		assertThat(result.getExcludes().get(0).getRectangle(), is(expectedRect));
	}

	/**
	 * 要素内スクロールの撮影において、最初は見えていない要素を指定して除外する。
	 *
	 * @ptl.expect 除外領域が正しく保存されていること。
	 */
	@Test
	public void excludeNotVisibleElement() throws Exception {
		assumeFalse("Skip IE8 table test.", isInternetExplorer8());
		assumeFalse("Skip IE9 table test.", isInternetExplorer9());

		openScrollPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#table-scroll > tbody")
				.addExcludeByCssSelector("tr:nth-last-of-type(1)").build();
		assertionView.assertView(arg);

		// Check
		TargetResult result = loadTargetResults("s").get(0);
		assertThat(result.getExcludes(), hasSize(1));

		Rect tableRect = getRectById("table-scroll");
		Rect rowRect = getRect(
				(WebElement) driver.executeJavaScript("" + "var table = document.getElementById('table-scroll');"
						+ "var tr = table.getElementsByTagName('tr');" + "return tr[tr.length - 1];"));

		RectangleArea expectedRect = createExpectedRect(tableRect, rowRect);
		assertThat(result.getExcludes().get(0).getRectangle(), is(expectedRect));
	}

	/**
	 * 要素内スクロールの撮影において、存在しない要素を指定して除外する。
	 *
	 * @ptl.expect エラーが発生せず、除外領域が保存されていないこと。
	 */
	@Test
	public void excludeNotExistElement() throws Exception {
		assumeFalse("Skip IE8 table test.", isInternetExplorer8());
		assumeFalse("Skip IE9 table test.", isInternetExplorer9());

		openScrollPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#table-scroll > tbody")
				.addExcludeById("not-exists").build();
		assertionView.assertView(arg);

		// Check
		TargetResult result = loadTargetResults("s").get(0);
		assertThat(result.getExcludes(), is(empty()));
	}

	private RectangleArea createExpectedRect(Rect targetRect, Rect excludeRect) {

		double x = excludeRect.x - (!isMicrosoftBrowser() ? targetRect.x : Math.floor(targetRect.x * 100) / 100);
		double y = excludeRect.y - (!isMicrosoftBrowser() ? targetRect.y : Math.floor(targetRect.y * 100) / 100);
		double width = excludeRect.width;
		double height = excludeRect.height;

		return new Rect(x, y, width, height).toExcludeRect().toRectangleArea();
	}

}
