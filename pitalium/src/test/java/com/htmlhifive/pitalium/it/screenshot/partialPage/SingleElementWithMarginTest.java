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

package com.htmlhifive.pitalium.it.screenshot.partialPage;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.awt.image.BufferedImage;

import org.junit.Test;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.it.screenshot.PtlItScreenshotTestBase;

/**
 * マージンが設定された要素をマージンを除いて撮影できるこ
 */
public class SingleElementWithMarginTest extends PtlItScreenshotTestBase {

	/**
	 * 正のマージンが設定された要素のスクリーンショットを撮影する。
	 * 
	 * @ptl.expect 正しく撮影されていること。
	 */
	@Test
	public void elementWithPositiveMargin() throws Exception {
		openBasicColorPage();

		// 正のマージンを設定する
		driver.executeJavaScript("document.getElementById('colorColumn0').style.marginTop = '10px';");

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("colorColumn0").build();
		assertionView.assertView(arg);

		// Check screenshot
		Rect rect = getPixelRectById("colorColumn0");
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		int width = image.getWidth();
		int height = image.getHeight();
		assertThat((double) width, is(closeTo(rect.width, 1.0)));
		assertThat((double) height, is(closeTo(rect.height, 1.0)));

		Color expect = Color.RED;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color actual = Color.valueOf(image.getRGB(x, y));
				assertThat(String.format("Point (%d, %d) is not match.", x, y), actual, is(expect));
			}
		}
	}

	/**
	 * 負のマージンが設定された要素のスクリーンショットを撮影する。
	 * 
	 * @ptl.expect 正しく撮影されていること。
	 */
	@Test
	public void elementWithNegativeMargin() throws Exception {
		openBasicColorPage();

		// 正のマージンを設定する
		driver.executeJavaScript("document.getElementById('colorColumn0').style.marginTop = '-10px';");

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("colorColumn0").build();
		assertionView.assertView(arg);

		// Check screenshot
		Rect rect = getPixelRectById("colorColumn0");
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		int width = image.getWidth();
		int height = image.getHeight();
		assertThat((double) width, is(closeTo(rect.width, 1.0)));
		assertThat((double) height, is(closeTo(rect.height, 1.0)));

		Color expect = Color.RED;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color actual = Color.valueOf(image.getRGB(x, y));
				assertThat(String.format("Point (%d, %d) is not match.", x, y), actual, is(expect));
			}
		}
	}

}
