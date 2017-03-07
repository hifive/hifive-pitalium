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

import org.junit.AssumptionViolatedException;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.it.RequireVisualCheck;
import com.htmlhifive.pitalium.it.screenshot2.PtlItScreenshotTestBase;

/**
 * ページの特定要素のスクリーンショットが正しくとれているかのテスト
 */
public class SingleElementBySingleTargetTest extends PtlItScreenshotTestBase {

	/**
	 * IDで指定した要素のスクリーンショットを撮影する。
	 * 
	 * @ptl.expect 正しく撮影されていること。
	 */
	@Test
	public void byId() throws Exception {
		openBasicColorPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("colorColumn0").build();
		assertionView.assertView(arg);

		WebElement target = driver.findElementById("colorColumn0");
		Rect rect = getPixelRect(target);

		// 画像チェック
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
	 * NAMEで指定した要素のスクリーンショットを撮影する。
	 * 
	 * @ptl.expect 正しく撮影されていること。
	 */
	@Test
	@RequireVisualCheck
	public void byName() throws Exception {
		openBasicColorPage();

		// Mobileでは表示する必要あり
		boolean mobile = driver.executeJavaScript(""
				+ "var button = document.getElementsByClassName('navbar-toggle')[0];"
				+ "var rect = button.getPtlBoundingClientRect();" + "return !!rect.width;");
		if (mobile) {
			driver.findElementByClassName("navbar-toggle").click();
		}

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByName("search").build();
		assertionView.assertView(arg);
	}

	/**
	 * TAG_NAMEで指定した要素のスクリーンショットを撮影する。
	 * 
	 * @ptl.expect 正しく撮影されていること。
	 */
	@Test
	public void byTagName() throws Exception {
		throw new AssumptionViolatedException("Full pageテストで実行");
	}

	/**
	 * CLASS_NAMEで指定した要素のスクリーンショットを撮影する。
	 * 
	 * @ptl.expect 正しく撮影されていること。
	 */
	@Test
	public void byClassName() throws Exception {
		openBasicColorPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByClassName("color-column-0").build();
		assertionView.assertView(arg);

		WebElement target = driver.findElementByClassName("color-column-0");
		Rect rect = getPixelRect(target);

		// 画像チェック
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
	 * CSS_SELECTORで指定した要素のスクリーンショットを撮影する。
	 * 
	 * @ptl.expect 正しく撮影されていること。
	 */
	@Test
	public void byCssSelector() throws Exception {
		openBasicColorPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector(".color-column-0").build();
		assertionView.assertView(arg);

		WebElement target = driver.findElementByCssSelector(".color-column-0");
		Rect rect = getPixelRect(target);

		// 画像チェック
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
	 * LINK_TEXTで指定した要素のスクリーンショットを撮影する。
	 * 
	 * @ptl.expect 正しく撮影されていること。
	 */
	@Test
	@RequireVisualCheck
	public void byLinkText() throws Exception {
		openBasicColorPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByLinkText("Pitalium Test Page").build();
		assertionView.assertView(arg);
	}

	/**
	 * PARTIAL_LINK_TEXTで指定した要素のスクリーンショットを撮影する。
	 * 
	 * @ptl.expect 正しく撮影されていること。
	 */
	@Test
	@RequireVisualCheck
	public void byPartialLinkText() throws Exception {
		openBasicColorPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByPartialLinkText("Pitalium").build();
		assertionView.assertView(arg);
	}

	/**
	 * XPATHで指定した要素のスクリーンショットを撮影する。
	 * 
	 * @ptl.expect 正しく撮影されていること。
	 */
	@Test
	public void byXPath() throws Exception {
		openBasicColorPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByXPath("id(\"colorColumn0\")").build();
		assertionView.assertView(arg);

		WebElement target = driver.findElementByXPath("id(\"colorColumn0\")");
		Rect rect = getPixelRect(target);

		// 画像チェック
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
