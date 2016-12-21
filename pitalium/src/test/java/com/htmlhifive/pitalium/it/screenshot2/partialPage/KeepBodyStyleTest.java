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
import com.htmlhifive.pitalium.it.screenshot2.PtlItScreenshotTestBase;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * moveのテストにおいて、移動前後でCSSスタイルが変化していないことのテスト
 */
public class KeepBodyStyleTest extends PtlItScreenshotTestBase {

	@Test
	public void keepBodyStyleOffset() throws Exception {
		openBasicColorPage();

		// Set style
		driver.executeJavaScript("" + "var body = document.body;" + "body.style.position = 'absolute';"
				+ "body.style.top = '20px';" + "body.style.left = '30px';");
		// Fetch style
		Map<String, Object> beforeStyles = fetchBodyStyles();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("colorColumn0").moveTarget(true)
				.build();
		assertionView.assertView(arg);

		// Check style
		Map<String, Object> afterStyles = fetchBodyStyles();
		assertThat(afterStyles, is(beforeStyles));

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

	@Test
	public void keepBodyStyleMargin() throws Exception {
		openBasicColorPage();

		// Set style
		driver.executeJavaScript("" + "var body = document.body;" + "body.style.position = 'absolute';"
				+ "body.style.margin = '20px';");
		// Fetch style
		Map<String, Object> beforeStyles = fetchBodyStyles();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("colorColumn0").moveTarget(true)
				.build();
		assertionView.assertView(arg);

		// Check style
		Map<String, Object> afterStyles = fetchBodyStyles();
		assertThat(afterStyles, is(beforeStyles));

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

	private Map<String, Object> fetchBodyStyles() {
		Map<String, Object> styles = driver.executeJavaScript("" + "var styles = document.body.getInlineStyles();"
				+ "return styles;");
		return new HashMap<>(styles);
	}

}
