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

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.it.screenshot.PtlItScreenshotTestBase;

/**
 * スクロールが無い要素を撮影するテスト
 */
public class NoScrollElementTest extends PtlItScreenshotTestBase {

	/**
	 * 要素内スクロールが無い要素を要素内スクロールオプションあり、移動オプションありを設定して撮影する。
	 * 
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	public void move() throws Exception {
		openGradationPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTarget().scrollTarget(true).moveTarget(true)
				.build();
		assertionView.assertView(arg);

		// Check
		double ratio = getPixelRatio();
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image, is(gradationWithBorder(ratio).ignoreCorner(isIgnoreCorners())));
	}

	/**
	 * 要素内スクロールが無い要素を要素内スクロールオプションあり、移動オプション無しを設定して撮影する。
	 * 
	 * @ptl.expect スクリーンショット撮影結果が正しいこと。
	 */
	@Test
	public void notMove() throws Exception {
		openGradationPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTarget().scrollTarget(true).moveTarget(false)
				.build();
		assertionView.assertView(arg);

		// Check
		double ratio = getPixelRatio();
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		assertThat(image, is(gradationWithBorder(ratio).ignoreCorner(isIgnoreCorners())));
	}

}
