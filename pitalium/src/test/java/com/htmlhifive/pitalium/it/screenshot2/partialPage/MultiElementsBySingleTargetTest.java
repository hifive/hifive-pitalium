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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * 単一セレクタで複数個所を撮影するテスト
 */
public class MultiElementsBySingleTargetTest extends PtlItScreenshotTestBase {

	/**
	 * 単一セレクタで複数の要素を指定して撮影する。
	 * 
	 * @ptl.expect 正しく撮影されていること。
	 */
	@Test
	public void byClassName() throws Exception {
		openBasicColorPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByClassName("color-column").build();
		assertionView.assertView(arg);

		// 画像チェック
		List<TargetResult> results = loadTargetResults("s");
		assertThat(results, hasSize(3));

		List<Map<String, Number>> sizes = driver.executeJavaScript(""
				+ "var elements = document.getElementsByClassName('color-column');"
				+ "var ratio = window.devicePixelRatio;" + "var result = [];"
				+ "for (var i = 0; i < elements.length; i++) {" + "  var rect = elements[i].getBoundingClientRect();"
				+ "  result.push({" + "    width:  rect.width  * ratio," + "    height: rect.height * ratio" + "  });"
				+ "}" + "return result;");

		// 0 -> RED
		// 1 -> GREEN
		// 2 -> BLUE
		List<Color> expects = Arrays.asList(Color.RED, Color.GREEN, Color.BLUE);
		for (int i = 0; i < results.size(); i++) {
			BufferedImage image = results.get(i).getImage().get();
			int width = image.getWidth();
			int height = image.getHeight();

			Map<String, Number> size = sizes.get(i);
			assertThat((double) width, is(closeTo(size.get("width").doubleValue(), 1.0)));
			assertThat((double) height, is(closeTo(size.get("height").doubleValue(), 1.0)));

			Color expect = expects.get(i);
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					Color actual = Color.valueOf(image.getRGB(x, y));
					assertThat(String.format("Point (%d, %d) is not match.", x, y), actual, is(expect));
				}
			}
		}
	}

}
