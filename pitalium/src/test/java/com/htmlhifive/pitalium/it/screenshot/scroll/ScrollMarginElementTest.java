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

package com.htmlhifive.pitalium.it.screenshot.scroll;

import static com.htmlhifive.pitalium.it.PtlItTestBase.IsGradation.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.awt.image.BufferedImage;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.it.RequireVisualCheck;
import com.htmlhifive.pitalium.it.screenshot.PtlItScreenshotTestBase;

/**
 * マージンが設定された要素の要素内スクロールテスト
 */
public class ScrollMarginElementTest extends PtlItScreenshotTestBase {

	/**
	 * 要素内スクロール、マージンがあるDIV要素を要素内スクロールオプションあり、移動オプションありを設定して撮影する。
	 * 
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	public void marginDivElement() throws Exception {
		openScrollPage();

		WebElement target = driver.findElementById("div-scroll");
		setMarginTo(target);

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("div-scroll").scrollTarget(true)
				.moveTarget(true).build();
		assertionView.assertView(arg);

		// Check
		double ratio = getPixelRatio();
		Rect rect = getPixelRectById("div-scroll");
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image.getHeight(), is(greaterThan((int) rect.height)));
		assertThat(image, is(gradationWithBorder(ratio, Color.BLACK, 1)));
	}

	/**
	 * 要素内スクロール、マージンがあるTEXTAREA要素を要素内スクロールオプションあり、移動オプションありを設定して撮影する。
	 * 
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	@RequireVisualCheck
	public void marginTextareaElement() throws Exception {
		openScrollPage();

		WebElement target = driver.findElementById("textarea-scroll");
		setMarginTo(target);

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("textarea-scroll").scrollTarget(true)
				.moveTarget(true).build();
		assertionView.assertView(arg);

		// Check
		Rect rect = getPixelRectById("textarea-scroll");
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image.getHeight(), is(greaterThan((int) rect.height)));
	}

	/**
	 * 要素内スクロール、マージンがあるTABLE要素を要素内スクロールオプションあり、移動オプションありを設定して撮影する。
	 * 
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	public void marginTableElement() throws Exception {
		openScrollPage();

		WebElement target = driver.findElementByCssSelector("#table-scroll > tbody");
		setMarginTo(target);

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#table-scroll > tbody")
				.scrollTarget(true).moveTarget(true).build();
		assertionView.assertView(arg);

		// Check
		double ratio = getPixelRatio();
		Rect rect = getPixelRectById("table-scroll");
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		// マージンが入った状態で計算されるらしいので
		assertThat(image.getHeight(), is(greaterThan((int) (rect.height - 200 * ratio))));

		int x = image.getWidth() / 2;
		int y = 0;
		int cellCount = 0;
		while (cellCount * 16 <= 240) {
			int color = cellCount * 16;
			Color expect = Color.rgb(color, color, color);
			cellCount++;
			int maxY = (int) Math.round(cellCount * 20 * ratio);
			while (y < maxY) {
				Color actual = Color.valueOf(image.getRGB(x, y));
				assertThat(String.format("Point (%d, %d) is not match.", x, y), actual, is(expect));
				y++;
			}
		}
	}

	/**
	 * 要素内スクロール、マージンがあるIFRAME要素を要素内スクロールオプションあり、移動オプションありを設定して撮影する。
	 * 
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	public void marginIFrameElement() throws Exception {
		openScrollPage();

		WebElement target = driver.findElementByName("iframe-scroll");
		setMarginTo(target);

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByName("iframe-scroll").scrollTarget(true)
				.moveTarget(true).build();
		assertionView.assertView(arg);

		// Check
		double ratio = getPixelRatio();
		Rect rect = getPixelRect(driver.findElementByName("iframe-scroll"));
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image.getHeight(), is(greaterThan((int) rect.height)));

		// 2pxボーダーが写るので無視する
		int border = (int) Math.round(2 * ratio);
		image = image.getSubimage(border, border, image.getWidth() - border * 2, image.getHeight() - border * 2);
		assertThat(image, is(gradationWithBorder(ratio)));
	}

	private void setMarginTo(WebElement target) {
		driver.executeJavaScript("" + "var element = arguments[0];" + "element.style.margin = '100px';", target);
	}

}
