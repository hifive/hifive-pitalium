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
import com.htmlhifive.pitalium.it.RequireVisualCheck;
import com.htmlhifive.pitalium.it.screenshot2.PtlItScreenshotTestBase;
import org.junit.AssumptionViolatedException;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import java.awt.image.BufferedImage;
import java.util.Map;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * ページの特定要素のスクリーンショットが正しくとれているかのテスト
 */
public class SingleElementBySingleTargetTest extends PtlItScreenshotTestBase {

	@Test
	public void byId() throws Exception {
		openBasicColorPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("colorColumn0").build();
		assertionView.assertView(arg);

		WebElement target = driver.findElementById("colorColumn0");
		Map<String, Number> size = driver.executeJavaScript(
				"" + "var el = arguments[0];" + "return el.getPixelSize();", target);

		// 画像チェック
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		int width = image.getWidth();
		int height = image.getHeight();
		assertThat((double) width, is(closeTo(size.get("width").doubleValue(), 1.0)));
		assertThat((double) height, is(closeTo(size.get("height").doubleValue(), 1.0)));

		Color expect = Color.RED;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color actual = Color.valueOf(image.getRGB(x, y));
				assertThat(String.format("Point (%d, %d) is not match.", x, y), actual, is(expect));
			}
		}
	}

	@Test
	@RequireVisualCheck
	public void byName() throws Exception {
		openBasicColorPage();

		// Mobileでは表示する必要あり
		boolean mobile = driver.executeJavaScript(""
				+ "var button = document.getElementsByClassName('navbar-toggle')[0];"
				+ "var rect = button.getBoundingClientRect();" + "return !!rect.width;");
		if (mobile) {
			driver.findElementByClassName("navbar-toggle").click();
		}

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByName("search").build();
		assertionView.assertView(arg);
	}

	@Test
	public void byTagName() throws Exception {
		throw new AssumptionViolatedException("Full pageテストで実行");
	}

	@Test
	public void byClassName() throws Exception {
		openBasicColorPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByClassName("color-column-0").build();
		assertionView.assertView(arg);

		WebElement target = driver.findElementByClassName("color-column-0");
		Map<String, Number> size = driver.executeJavaScript(
				"" + "var el = arguments[0];" + "return el.getPixelSize();", target);

		// 画像チェック
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		int width = image.getWidth();
		int height = image.getHeight();
		assertThat((double) width, is(closeTo(size.get("width").doubleValue(), 1.0)));
		assertThat((double) height, is(closeTo(size.get("height").doubleValue(), 1.0)));

		Color expect = Color.RED;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color actual = Color.valueOf(image.getRGB(x, y));
				assertThat(String.format("Point (%d, %d) is not match.", x, y), actual, is(expect));
			}
		}
	}

	@Test
	public void byCssSelector() throws Exception {
		openBasicColorPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector(".color-column-0").build();
		assertionView.assertView(arg);

		WebElement target = driver.findElementByCssSelector(".color-column-0");
		Map<String, Number> size = driver.executeJavaScript(
				"" + "var el = arguments[0];" + "return el.getPixelSize();", target);

		// 画像チェック
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		int width = image.getWidth();
		int height = image.getHeight();
		assertThat((double) width, is(closeTo(size.get("width").doubleValue(), 1.0)));
		assertThat((double) height, is(closeTo(size.get("height").doubleValue(), 1.0)));

		Color expect = Color.RED;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color actual = Color.valueOf(image.getRGB(x, y));
				assertThat(String.format("Point (%d, %d) is not match.", x, y), actual, is(expect));
			}
		}
	}

	@Test
	@RequireVisualCheck
	public void byLinkText() throws Exception {
		openBasicColorPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByLinkText("Pitalium Test Page").build();
		assertionView.assertView(arg);
	}

	@Test
	@RequireVisualCheck
	public void byPartialLinkText() throws Exception {
		openBasicColorPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByPartialLinkText("Pitalium").build();
		assertionView.assertView(arg);
	}

	@Test
	public void byXPath() throws Exception {
		openBasicColorPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByXPath("id(\"colorColumn0\")").build();
		assertionView.assertView(arg);

		WebElement target = driver.findElementByXPath("id(\"colorColumn0\")");
		Map<String, Number> size = driver.executeJavaScript(
				"" + "var el = arguments[0];" + "return el.getPixelSize();", target);

		// 画像チェック
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		int width = image.getWidth();
		int height = image.getHeight();
		assertThat((double) width, is(closeTo(size.get("width").doubleValue(), 1.0)));
		assertThat((double) height, is(closeTo(size.get("height").doubleValue(), 1.0)));

		Color expect = Color.RED;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color actual = Color.valueOf(image.getRGB(x, y));
				assertThat(String.format("Point (%d, %d) is not match.", x, y), actual, is(expect));
			}
		}
	}

}
