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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import org.junit.Test;
import org.openqa.selenium.Dimension;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.core.model.TargetResult;
import com.htmlhifive.pitalium.image.model.RectangleArea;
import com.htmlhifive.pitalium.it.screenshot2.PtlItScreenshotTestBase;

/**
 * 単一要素を除外するテスト
 */
public class ExcludeSingleElementTest extends PtlItScreenshotTestBase {

	/**
	 * 単体要素撮影時に、単体要素を指定して除外する。
	 *
	 * @ptl.expect 除外領域が正しく保存されていること。
	 */
	@Test
	public void singleTarget() throws Exception {
		assumeFalse("Skip IE8 test (getElementsByClassName is not supported)", isInternetExplorer8());

		openBasicTextPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByTagName("body")
				.addExcludeByClassName("navbar").build();
		assertionView.assertView(arg);

		// Check
		Rect rect = getRectBySelector(".navbar");
		TargetResult result = loadTargetResults("s").get(0);
		assertThat(result.getExcludes(), hasSize(1));

		RectangleArea area = result.getExcludes().get(0).getRectangle();
		RectangleArea expectArea = rect.toExcludeRect().toRectangleArea();
		assertThat(area, is(expectArea));
	}

	/**
	 * 単体要素撮影時に、小数点以下がある幅を持つ単体要素を指定して除外する。
	 *
	 * @ptl.expect 除外領域が正しく保存されていること。
	 */
	@Test
	public void singleTestWithDecimal() {
		openBasicColorPage();
		driver.manage().window().setSize(new Dimension(1100, (int) driver.getWindowHeight()));

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTarget().addExcludeById("colorColumn1").build();
		assertionView.assertView(arg);

		// Check
		Rect rect = getRectById("colorColumn1");
		TargetResult result = loadTargetResults("s").get(0);
		assertThat(result.getExcludes(), hasSize(1));

		RectangleArea area = result.getExcludes().get(0).getRectangle();
		RectangleArea expectArea = rect.toExcludeRect().toRectangleArea();
		assertThat(area, is(expectArea));
	}
}
