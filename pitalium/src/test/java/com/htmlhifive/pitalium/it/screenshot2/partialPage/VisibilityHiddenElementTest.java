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

package com.htmlhifive.pitalium.it.screenshot2.partialPage;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.core.model.TargetResult;
import com.htmlhifive.pitalium.it.screenshot2.PtlItScreenshotTestBase;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * visibility: hiddenを対象にしたテスト
 */
public class VisibilityHiddenElementTest extends PtlItScreenshotTestBase {

	/**
	 * visibility: hiddenが設定された要素を撮影する。
	 * 
	 * @ptl.expect エラーが発生せず、正しく撮影されていること。
	 */
	@Test
	public void singleTarget() throws Exception {
		openBasicColorPage();

		// hidden
		driver.executeJavaScript("" + "var element = document.getElementById('colorColumn0');"
				+ "element.style.visibility = 'hidden';");

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("colorColumn0").build();
		assertionView.assertView(arg);

		// Check screenshot
		Rect rect = getPixelRectById("colorColumn0");
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		int width = image.getWidth();
		int height = image.getHeight();
		assertThat((double) width, is(closeTo(rect.width, 1.0)));
		assertThat((double) height, is(closeTo(rect.height, 1.0)));

		Color expect = Color.WHITE;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color actual = Color.valueOf(image.getRGB(x, y));
				assertThat(String.format("Point (%d, %d) is not match.", x, y), actual, is(expect));
			}
		}
	}

	/**
	 * visibility: hiddenが設定された要素を含む、複数の要素を撮影する。
	 * 
	 * @ptl.expect エラーが発生せず、正しく撮影されていること。
	 */
	@Test
	public void inMultiTargets() throws Exception {
		openBasicColorPage();

		// hidden
		driver.executeJavaScript("" + "var element = document.getElementById('colorColumn0');"
				+ "element.style.visibility = 'hidden';");

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("colorColumn1")
				.addNewTargetById("colorColumn2").addNewTargetById("colorColumn0").build();
		assertionView.assertView(arg);

		// Check screenshot
		List<TargetResult> results = loadTargetResults("s");
		assertThat(results, hasSize(3));
		Color[] expects = { Color.GREEN, Color.BLUE, Color.WHITE };
		for (int i = 0; i < 3; i++) {
			Rect rect = getPixelRectById("colorColumn" + i);
			BufferedImage image = results.get(i).getImage().get();
			int width = image.getWidth();
			int height = image.getHeight();
			assertThat((double) width, is(closeTo(rect.width, 1.0)));
			assertThat((double) height, is(closeTo(rect.height, 1.0)));

			Color expect = expects[i];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					Color actual = Color.valueOf(image.getRGB(x, y));
					assertThat(String.format("Point (%d, %d) is not match.", x, y), actual, is(expect));
				}
			}
		}
	}

}
