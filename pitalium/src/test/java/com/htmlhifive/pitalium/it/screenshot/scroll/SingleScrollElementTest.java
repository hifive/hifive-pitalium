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
import static org.junit.Assume.*;

import java.awt.image.BufferedImage;

import org.junit.Test;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.BrowserType;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.it.RequireVisualCheck;
import com.htmlhifive.pitalium.it.screenshot.PtlItScreenshotTestBase;

/**
 * ページの特定要素のスクリーンショットが正しくとれているかのテスト
 */
public class SingleScrollElementTest extends PtlItScreenshotTestBase {

	/**
	 * 要素内スクロールがあるDIV要素を要素内スクロールオプションあり、移動オプションありを設定して撮影する。
	 *
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	public void takeDivScreenshot_scroll_move() throws Exception {
		assumeFalse("Skip IE8 test (getComputedStyle is not supported)", isInternetExplorer8());

		openScrollPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("div-scroll").scrollTarget(true)
				.moveTarget(true).build();
		assertionView.assertView(arg);

		// Check
		double ratio = getPixelRatio();
		Rect rect = getPixelRectBySelector("#div-scroll > #div-scroll-inner");
		// IEはborderWidthが空文字なのでborderLeftWidthを見る
		double border = driver.<Number> executeJavaScript("" + "var el = document.getElementById('div-scroll');"
				+ "var style = window.getComputedStyle(el);"
				+ "var borderWidth = parseFloat(style.borderWidth || style.borderLeftWidth);" + "return borderWidth;")
				.doubleValue();
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image.getHeight(), is((int) (rect.height + Math.round(border * ratio) * 2)));

		if (!isSkipColorCheck()) {
			assertThat(image, is(gradationWithBorder(ratio, Color.BLACK, 1)));
		}
	}

	/**
	 * 要素内スクロールがあるTEXTAREA要素を要素内スクロールオプションあり、移動オプションありを設定して撮影する。
	 *
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	@RequireVisualCheck
	public void takeTextareaScreenshot_scroll_move() throws Exception {
		openScrollPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("textarea-scroll").scrollTarget(true)
				.moveTarget(true).build();
		assertionView.assertView(arg);

		// Check
		Rect rect = getPixelRectById("textarea-scroll");
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image.getHeight(), is(greaterThan((int) rect.height)));
	}

	/**
	 * 要素内スクロールがあるTABLE要素を要素内スクロールオプションあり、移動オプションありを設定して撮影する。
	 *
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	public void takeTableScreenshot_scroll_move() throws Exception {
		assumeFalse("Skip IE8 table test.", isInternetExplorer8());
		assumeFalse("Skip IE9 table test.", isInternetExplorer9());

		openScrollPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#table-scroll > tbody")
				.scrollTarget(true).moveTarget(true).build();
		assertionView.assertView(arg);

		// Check
		double ratio = getPixelRatio();
		Rect rect = getPixelRectById("table-scroll");
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image.getHeight(), is(greaterThan((int) rect.height)));

		int x = image.getWidth() / 2;
		int y = 0;
		int cellCount = 0;
		float cellHeight = 20;
		if (BrowserType.FIREFOX.equals(capabilities.getBrowserName())
				&& Platform.MAC.equals(capabilities.getPlatform())) {
			cellHeight = 20.45f;
		}
		while (cellCount <= 15) {
			Color expect = Color.valueOf(image.getRGB(0, (int) Math.round(cellCount * cellHeight * ratio)));
			cellCount++;
			int maxY = (int) Math.round(cellCount * cellHeight * ratio);
			while (y < maxY) {
				Color actual = Color.valueOf(image.getRGB(x, y));
				assertThat(String.format("Point (%d, %d) is not match.", x, y), actual, is(expect));
				y++;
			}
		}
	}

	/**
	 * 要素内スクロールがあるIFRAME要素を要素内スクロールオプションあり、移動オプションありを設定して撮影する。
	 *
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	public void takeIFrameScreenshot_scroll_move() throws Exception {
		openScrollPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByName("iframe-scroll").scrollTarget(true)
				.moveTarget(true).build();
		assertionView.assertView(arg);

		// Check
		double ratio = getPixelRatio();
		Rect rect = getPixelRect(driver.findElementByName("iframe-scroll"));
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image.getHeight(), is(greaterThan((int) rect.height)));

		if (!isSkipColorCheck()) {
			// 2pxボーダーが写るので無視する
			int border = (int) Math.round(ratio * 2);
			image = image.getSubimage(border, border, image.getWidth() - border * 2, image.getHeight() - border * 2);
			assertThat(image, is(gradationWithBorder(ratio)));
		}
	}

	/**
	 * 要素内スクロールがあるDIV要素を要素内スクロールオプションあり、移動オプションなしを設定して撮影する。
	 *
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	public void takeDivScreenshot_scroll_notMove() throws Exception {
		assumeFalse("Skip IE8 test (getComputedStyle is not supported)", isInternetExplorer8());

		openScrollPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("div-scroll").scrollTarget(true)
				.moveTarget(false).build();
		assertionView.assertView(arg);

		// Check
		double ratio = getPixelRatio();
		Rect rect = getPixelRectBySelector("#div-scroll > #div-scroll-inner");
		// IEはborderWidthが空文字なのでborderLeftWidthを見る
		double border = driver.<Number> executeJavaScript("" + "var el = document.getElementById('div-scroll');"
				+ "var style = window.getComputedStyle(el);"
				+ "var borderWidth = parseFloat(style.borderWidth || style.borderLeftWidth);" + "return borderWidth;")
				.doubleValue();
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image.getHeight(), is((int) (rect.height + Math.round(border * ratio) * 2)));
		if (!isSkipColorCheck()) {
			assertThat(image, is(gradationWithBorder(ratio, Color.BLACK, 1)));
		}
	}

	/**
	 * 要素内スクロールがあるTEXTAREA要素を要素内スクロールオプションあり、移動オプションなしを設定して撮影する。
	 *
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	@RequireVisualCheck
	public void takeTextareaScreenshot_scroll_notMove() throws Exception {
		openScrollPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("textarea-scroll").scrollTarget(true)
				.moveTarget(false).build();
		assertionView.assertView(arg);

		// Check
		Rect rect = getPixelRectById("textarea-scroll");
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image.getHeight(), is(greaterThan((int) rect.height)));
	}

	/**
	 * 要素内スクロールがあるTABLE要素を要素内スクロールオプションあり、移動オプションなしを設定して撮影する。
	 *
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	public void takeTableScreenshot_scroll_notMove() throws Exception {
		assumeFalse("Skip IE8 table test.", isInternetExplorer8());
		assumeFalse("Skip IE9 table test.", isInternetExplorer9());

		openScrollPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#table-scroll > tbody")
				.scrollTarget(true).moveTarget(false).build();
		assertionView.assertView(arg);

		// Check
		double ratio = getPixelRatio();
		Rect rect = getPixelRectById("table-scroll");
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image.getHeight(), is(greaterThan((int) rect.height)));

		int x = image.getWidth() / 2;
		int y = 0;
		int cellCount = 0;
		while (y < image.getHeight()) {
			Color expect = Color.valueOf(image.getRGB(0, (int) Math.round(cellCount * 20 * ratio)));
			Color actual = Color.valueOf(image.getRGB(x, y));
			if (!expect.equals(actual)) {
				cellCount++;
				continue;
			}
			y++;
		}
	}

	/**
	 * 要素内スクロールがあるIFRAME要素を要素内スクロールオプションあり、移動オプションなしを設定して撮影する。
	 *
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	public void takeIFrameScreenshot_scroll_notMove() throws Exception {
		openScrollPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByName("iframe-scroll").scrollTarget(true)
				.moveTarget(false).build();
		assertionView.assertView(arg);

		// Check
		double ratio = getPixelRatio();
		Rect rect = getPixelRect(driver.findElementByName("iframe-scroll"));
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image.getHeight(), is(greaterThan((int) rect.height)));

		if (!isSkipColorCheck()) {
			// 1pxボーダーが写るので無視する
			int border = (int) Math.round(ratio * 2);
			image = image.getSubimage(border, border, image.getWidth() - border * 2, image.getHeight() - border * 2);
			assertThat(image, is(gradationWithBorder(ratio)));
		}
	}

	/**
	 * 要素内スクロールがあるDIV要素を要素内スクロールオプションなし、移動オプションありを設定して撮影する。
	 *
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	public void takeDivScreenshot_notScroll_move() throws Exception {
		openScrollPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("div-scroll").scrollTarget(false)
				.moveTarget(true).build();
		assertionView.assertView(arg);

		// Check
		Rect rect = getPixelRectById("div-scroll");
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image.getHeight(), is((int) rect.height));

		// スクロールバーの検証が出来ないため、大きさだけチェックする
	}

	/**
	 * 要素内スクロールがあるTEXTAREA要素を要素内スクロールオプションなし、移動オプションありを設定して撮影する。
	 *
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	@RequireVisualCheck
	public void takeTextareaScreenshot_notScroll_move() throws Exception {
		openScrollPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("textarea-scroll").scrollTarget(false)
				.moveTarget(true).build();
		assertionView.assertView(arg);

		// Check
		Rect rect = getPixelRectById("textarea-scroll");
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image.getHeight(), is((int) rect.height));
	}

	/**
	 * 要素内スクロールがあるTABLE要素を要素内スクロールオプションなし、移動オプションありを設定して撮影する。
	 *
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	public void takeTableScreenshot_notScroll_move() throws Exception {
		openScrollPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#table-scroll > tbody")
				.scrollTarget(false).moveTarget(true).build();
		assertionView.assertView(arg);

		// Check
		double ratio = getPixelRatio();
		Rect rect = getPixelRectById("table-scroll");
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image.getHeight(), is((int) rect.height));

		int x = image.getWidth() / 2;
		int y = 0;
		int cellCount = 0;
		float cellHeight = 20;
		if (BrowserType.FIREFOX.equals(capabilities.getBrowserName())
				&& Platform.MAC.equals(capabilities.getPlatform())) {
			cellHeight = 20.45f;
		}
		while (cellCount * 16 <= image.getHeight() && y < image.getHeight()) {
			Color expect = Color.valueOf(image.getRGB(0, (int) Math.round(cellCount * cellHeight * ratio) + 1));
			cellCount++;
			int maxY = (int) Math.round(cellCount * cellHeight * ratio);
			while (y < maxY && y < image.getHeight()) {
				Color actual = Color.valueOf(image.getRGB(x, y));
				try {
					assertThat(String.format("Point (%d, %d) is not match.", x, y), expect, is(actual));
				} catch (AssertionError e) {
					// FIXME Edgeは謎のオーバーレイがある模様
					if (isMicrosoftEdge()) {
						if (y == 0 || y == image.getHeight() - 1) {
							LOG.info(e.getMessage());
							y++;
							continue;
						}
					}

					throw e;
				}
				y++;
			}
		}
	}

	/**
	 * 要素内スクロールがあるIFRAME要素を要素内スクロールオプションなし、移動オプションありを設定して撮影する。
	 *
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	public void takeIFrameScreenshot_notScroll_move() throws Exception {
		openScrollPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByName("iframe-scroll").scrollTarget(false)
				.moveTarget(true).build();
		assertionView.assertView(arg);

		// Check
		Rect rect = getPixelRect(driver.findElementByName("iframe-scroll"));
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image.getHeight(), is((int) rect.height));

		// スクロールバーの検証が出来ないため、大きさだけチェックする
	}

	/**
	 * 要素内スクロールがあるDIV要素を要素内スクロールオプションなし、移動オプションなしを設定して撮影する。
	 *
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	public void takeDivScreenshot_notScroll_notMove() throws Exception {
		openScrollPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("div-scroll").scrollTarget(false)
				.moveTarget(false).build();
		assertionView.assertView(arg);

		// Check
		Rect rect = getPixelRectById("div-scroll");
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image.getHeight(), is((int) rect.height));

		// スクロールバーの検証が出来ないため、大きさだけチェックする
	}

	/**
	 * 要素内スクロールがあるTEXTAREA要素を要素内スクロールオプションなし、移動オプションなしを設定して撮影する。
	 *
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	@RequireVisualCheck
	public void takeTextareaScreenshot_notScroll_notMove() throws Exception {
		openScrollPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("textarea-scroll").scrollTarget(false)
				.moveTarget(false).build();
		assertionView.assertView(arg);

		// Check
		Rect rect = getPixelRectById("textarea-scroll");
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image.getHeight(), is((int) rect.height));
	}

	/**
	 * 要素内スクロールがあるTABLE要素を要素内スクロールオプションなし、移動オプションなしを設定して撮影する。
	 *
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	public void takeTableScreenshot_notScroll_notMove() throws Exception {
		openScrollPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByCssSelector("#table-scroll > tbody")
				.scrollTarget(false).moveTarget(false).build();
		assertionView.assertView(arg);

		// Check
		double ratio = getPixelRatio();
		Rect rect = getPixelRectById("table-scroll");
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image.getHeight(), is((int) rect.height));

		int x = image.getWidth() / 2;
		int y = 0;
		int cellCount = 0;
		float cellHeight = 20;
		if (BrowserType.FIREFOX.equals(capabilities.getBrowserName())
				&& Platform.MAC.equals(capabilities.getPlatform())) {
			cellHeight = 20.45f;
		}
		while (cellCount * 16 <= image.getHeight() && y < image.getHeight()) {
			Color expect = Color.valueOf(image.getRGB(0, (int) Math.round(cellCount * cellHeight * ratio) + 1));
			cellCount++;
			int maxY = (int) Math.round(cellCount * cellHeight * ratio);
			while (y < maxY && y < image.getHeight()) {
				Color actual = Color.valueOf(image.getRGB(x, y));
				try {
					assertThat(String.format("Point (%d, %d) is not match.", x, y), expect, is(actual));
				} catch (AssertionError e) {
					// FIXME Edgeは謎のオーバーレイがある模様
					if (isMicrosoftEdge()) {
						if (y == 0 || y == image.getHeight() - 1) {
							LOG.info(e.getMessage());
							y++;
							continue;
						}
					}

					throw e;
				}
				y++;
			}
		}
	}

	/**
	 * 要素内スクロールがあるIFRAME要素を要素内スクロールオプションなし、移動オプションなしを設定して撮影する。
	 *
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	public void takeIFrameScreenshot_notScroll_notMove() throws Exception {
		openScrollPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByName("iframe-scroll").scrollTarget(false)
				.moveTarget(false).build();
		assertionView.assertView(arg);

		// Check
		Rect rect = getPixelRect(driver.findElementByName("iframe-scroll"));
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image.getHeight(), is((int) rect.height));

		// スクロールバーの検証が出来ないため、大きさだけチェックする
	}

}
