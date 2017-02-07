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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.it.screenshot2.PtlItScreenshotTestBase;

/**
 * 座標が固定されている要素に対するスクリーンショット取得のテスト
 */
public class PositionFixedElementTest extends PtlItScreenshotTestBase {

	/**
	 * position: fixedが指定された要素を撮影する。
	 * 
	 * @ptl.expect 正しく撮影されていること。
	 */
	@Test
	public void moveFixedElement() throws Exception {
		openBasicColorPage();

		driver.executeJavaScript("" + "var element = document.getElementById('colorColumn2');"
				+ "element.style.position = 'absolute';" + "element.style.top = '30px';"
				+ "element.style.left = '20px';");
		Map<String, Object> beforeStyles = fetchStyles();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("colorColumn2").moveTarget(true)
				.build();
		assertionView.assertView(arg);

		// Check style
		Map<String, Object> afterStyles = fetchStyles();
		assertThat(afterStyles, is(beforeStyles));

		// Check screenshot
		Rect rect = getPixelRectById("colorColumn2");
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		int width = image.getWidth();
		int height = image.getHeight();
		assertThat((double) width, is(closeTo(rect.width, 1.0)));
		assertThat((double) height, is(closeTo(rect.height, 1.0)));

		Color expect = Color.BLUE;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color actual = Color.valueOf(image.getRGB(x, y));
				assertThat(String.format("Point (%d, %d) is not match.", x, y), actual, is(expect));
			}
		}

	}

	private Map<String, Object> fetchStyles() {
		Map<String, Object> styles = driver.executeJavaScript(""
				+ "var styles = document.getElementById('colorColumn2').getInlineStyles();" + "return styles;");
		return new HashMap<>(styles);
	}

}
