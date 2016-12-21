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

package com.htmlhifive.pitalium.it.screenshot2.scroll;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.it.screenshot2.PtlItScreenshotTestBase;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import java.awt.image.BufferedImage;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * ボーダーがある要素をスクロール撮影するテスト
 */
public class ScrollBorderElementTest extends PtlItScreenshotTestBase {

	@Test
	public void borderDivElement() throws Exception {
		openScrollPage();

		WebElement target = driver.findElementById("div-scroll");
		setBorderTo(target);

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("div-scroll").scrollTarget(true)
				.moveTarget(true).build();
		assertionView.assertView(arg);

		validateHasBorder();
	}

	@Test
	public void borderTextareaElement() throws Exception {
		openScrollPage();

		WebElement target = driver.findElementById("textarea-scroll");
		setBorderTo(target);

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("textarea-scroll").scrollTarget(true)
				.moveTarget(true).build();
		assertionView.assertView(arg);

		validateHasBorder();
	}

	@Test
	public void borderTableElement() throws Exception {
		openScrollPage();

		WebElement target = driver.findElementByCssSelector("#table-scroll > tbody");
		setBorderTo(target);

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#table-scroll > tbody")
				.scrollTarget(true).moveTarget(true).build();
		assertionView.assertView(arg);

		validateHasBorder();
	}

	@Test
	public void borderIFrameElement() throws Exception {
		openScrollPage();

		WebElement target = driver.findElementByName("iframe-scroll");
		setBorderTo(target);

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByName("iframe-scroll").scrollTarget(true)
				.moveTarget(true).build();
		assertionView.assertView(arg);

		validateHasBorder();
	}

	private void setBorderTo(WebElement target) {
		driver.executeJavaScript("" + "var element = arguments[0];" + "element.style.border = '5px #123456 solid';",
				target);
	}

	private void validateHasBorder() throws Exception {
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		int width = image.getWidth();
		int height = image.getHeight();
		int centerX = width / 2;
		int centerY = height / 2;

		// border
		double ratio = getPixelRatio();
		int border = (int) Math.round(5.0 * ratio);
		Color borderColor = Color.valueOf("#123456");
		for (int i = 0; i < border; i++) {
			assertThat(Color.valueOf(image.getRGB(i, centerY)), is(borderColor));
			assertThat(Color.valueOf(image.getRGB(width - i - 1, centerY)), is(borderColor));
			assertThat(Color.valueOf(image.getRGB(centerX, i)), is(borderColor));
			assertThat(Color.valueOf(image.getRGB(centerX, height - i - 1)), is(borderColor));
		}

		// contents
		BufferedImage contentImage = image.getSubimage(border, border, width - border * 2, height - border * 2);
		int contentWidth = contentImage.getWidth();
		int contentHeight = contentImage.getHeight();
		for (int x = 0; x < contentWidth; x++) {
			for (int y = 0; y < contentHeight; y++) {
				assertThat(Color.valueOf(contentImage.getRGB(x, y)), is(not(borderColor)));
			}
		}
	}

}
