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

package com.htmlhifive.pitalium.it.screenshot2.fullPage;

import com.htmlhifive.pitalium.it.screenshot2.PtlItScreenshotTestBase;
import org.junit.Test;

import java.awt.image.BufferedImage;

import static com.htmlhifive.pitalium.it.PtlItTestBase.IsGradation.gradationWithBorder;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * ページ全体のスクリーンショットを撮影する際に、スクロールの量がMarginTopよりも小さい場合のテスト
 */
public class ScrollLessThanMarginTest extends PtlItScreenshotTestBase {

	/**
	 * 縦方向のスクロールが、BODYに設定されたマージンよりも小さい場合。
	 * 
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	public void scrollLessThanMarginVertical() throws Exception {
		openGradationPage("100%", "90%");

		// マージンの追加
		int margin = driver.<Number> executeJavaScript(
				"" + "var windowHeight = window.innerHeight;" + "var body = document.body;"
						+ "var height = body.getBoundingClientRect().height;"
						+ "var margin = (windowHeight - height) * 2;" + "body.style.marginTop = margin + 'px';"
						+ "return margin;").intValue();

		assertionView.assertView("s");

		double ratio = getPixelRatio();

		// 画像のチェック
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		int marginPixel = (int) Math.round(margin * ratio);
		BufferedImage gradationImage = image.getSubimage(0, marginPixel, image.getWidth(), image.getHeight()
				- marginPixel);
		assertThat(gradationImage, is(gradationWithBorder(ratio)));
	}

	/**
	 * 横方向のスクロールが、BODYに設定されたマージンよりも小さい場合。
	 * 
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	public void scrollLessThanMarginHorizontal() throws Exception {
		openGradationPage("90%", "100%");

		// マージンの追加
		int margin = driver.<Number> executeJavaScript(
				"" + "var windowWidth = window.innerWidth;" + "var body = document.body;"
						+ "var width = body.getBoundingClientRect().width;" + "var margin = (windowWidth - width) * 2;"
						+ "body.style.marginLeft = margin + 'px';" + "return margin;").intValue();

		assertionView.assertView("s");

		double ratio = getPixelRatio();

		// 画像のチェック
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		int marginPixel = (int) Math.round(margin * ratio);
		BufferedImage gradationImage = image.getSubimage(marginPixel, 0, image.getWidth() - marginPixel,
				image.getHeight());
		assertThat(gradationImage, is(gradationWithBorder(ratio)));
	}

}
