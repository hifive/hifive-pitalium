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

import static com.htmlhifive.pitalium.it.PtlItTestBase.IsGradation.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.awt.image.BufferedImage;

import org.junit.Test;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.it.screenshot2.PtlItScreenshotTestBase;

/**
 * 画面全体のスクリーンショットを撮影するテスト
 */
public class EntirePageTest extends PtlItScreenshotTestBase {

	/**
	 * ターゲットを指定せずにBODYを撮影する。
	 * 
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	public void captureBodyByNoTarget() throws Exception {
		openGradationPage();
		assertionView.assertView("s");

		double pixelRatio = getPixelRatio();

		// 画像のチェック
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image, is(gradationWithBorder(pixelRatio)));
	}

	/**
	 * TAG_NAME=BODYを指定して撮影する。
	 * 
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	public void captureBodyByTagName() throws Exception {
		openGradationPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByTagName("body").build();
		assertionView.assertView(arg);

		double pixelRatio = getPixelRatio();

		// 画像のチェック
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image, is(gradationWithBorder(pixelRatio)));
	}

	/**
	 * BODYに設定したCLASS_NAMEを指定して撮影する。
	 * 
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	public void captureBodyByClassName() throws Exception {
		openGradationPage();

		// bodyにclass="body"を付加する
		driver.executeJavaScript("document.body.addClassName('body');");

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByClassName("body").build();
		assertionView.assertView(arg);

		double pixelRatio = getPixelRatio();

		// 画像のチェック
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image, is(gradationWithBorder(pixelRatio)));
	}

	/**
	 * スクロールが無いページを撮影する。
	 * 
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	public void captureNonScrollPage() throws Exception {
		openGradationPage("100%", "100%");
		assertionView.assertView("s");

		double pixelRatio = getPixelRatio();

		// 画像のチェック
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image, is(gradationWithBorder(pixelRatio)));
	}

	/**
	 * 縦スクロール0回、横スクロール1回のページを撮影する。
	 * 
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	public void captureScrollPage_v0_h1() throws Exception {
		openGradationPage("160%", "100%");
		assertionView.assertView("s");

		double pixelRatio = getPixelRatio();

		// 画像のチェック
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image, is(gradationWithBorder(pixelRatio)));
	}

	/**
	 * 縦スクロール0回、横スクロール2回のページを撮影する。
	 * 
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	public void captureScrollPage_v0_h2() throws Exception {
		openGradationPage("240%", "100%");
		assertionView.assertView("s");

		double pixelRatio = getPixelRatio();

		// 画像のチェック
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image, is(gradationWithBorder(pixelRatio)));
	}

	/**
	 * 縦スクロール1回、横スクロール0回のページを撮影する。
	 * 
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	public void captureScrollPage_v1_h0() throws Exception {
		openGradationPage("100%", "160%");
		assertionView.assertView("s");

		double pixelRatio = getPixelRatio();

		// 画像のチェック
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image, is(gradationWithBorder(pixelRatio)));
	}

	/**
	 * 縦スクロール2回、横スクロール0回のページを撮影する。
	 * 
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	public void captureScrollPage_v2_h0() throws Exception {
		openGradationPage("100%", "240%");
		assertionView.assertView("s");

		double pixelRatio = getPixelRatio();

		// 画像のチェック
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image, is(gradationWithBorder(pixelRatio)));
	}

	/**
	 * 縦スクロール1回、横スクロール1回のページを撮影する。
	 * 
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	public void captureScrollPage_v1_h1() throws Exception {
		openGradationPage("160%", "160%");
		assertionView.assertView("s");

		double pixelRatio = getPixelRatio();

		// 画像のチェック
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image, is(gradationWithBorder(pixelRatio)));
	}

	/**
	 * 縦スクロール2回、横スクロール2回のページを撮影する。
	 * 
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	public void captureScrollPage_v2_h2() throws Exception {
		openGradationPage("240%", "240%");
		assertionView.assertView("s");

		double pixelRatio = getPixelRatio();

		// 画像のチェック
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image, is(gradationWithBorder(pixelRatio)));
	}

}
