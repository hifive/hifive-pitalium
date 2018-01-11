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

package com.htmlhifive.pitalium.it.screenshot.partialPage;

import static com.htmlhifive.pitalium.it.PtlItTestBase.IsGradation.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.awt.image.BufferedImage;

import org.junit.Test;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.it.screenshot.PtlItScreenshotTestBase;

/**
 * 範囲指定をしてスクリーンショットを撮影するテスト
 */
public class SingleElementByAreaTest extends PtlItScreenshotTestBase {

	/**
	 * 範囲を指定してスクリーンショットを撮影するテスト。
	 *
	 * @ptl.expect 正しく撮影されていること。
	 */
	@Test
	public void move() throws Exception {
		openGradationPage();

		Rect pixelRect = getPixelRectById("container");
		ScreenshotArgument arg = ScreenshotArgument.builder("s")
				.addNewTarget(pixelRect.x, pixelRect.y, pixelRect.width, pixelRect.height).moveTarget(true).build();
		assertionView.assertView(arg);

		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		if (!isSkipColorCheck()) {
			// Check image
			double ratio = getPixelRatio();
			assertThat(image, is(gradation(ratio)));
		}

		Rect rect = getRectById("container");
		assertThat(image.getWidth(), is((int) rect.width));
		assertThat(image.getHeight(), is((int) rect.height));
	}

	/**
	 * 範囲を指定してスクリーンショットを撮影するテスト。
	 *
	 * @ptl.expect 正しく撮影されていること。
	 */
	@Test
	public void notMove() throws Exception {
		openGradationPage();

		Rect pixelRect = getPixelRectById("container");
		ScreenshotArgument arg = ScreenshotArgument.builder("s")
				.addNewTarget(pixelRect.x, pixelRect.y, pixelRect.width, pixelRect.height).moveTarget(false).build();
		assertionView.assertView(arg);

		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		if (!isSkipColorCheck()) {
			// Check image
			double ratio = getPixelRatio();
			assertThat(image, is(gradation(ratio)));
		}

		Rect rect = getRectById("container");
		assertThat(image.getWidth(), is((int) rect.width));
		assertThat(image.getHeight(), is((int) rect.height));
	}

	/**
	 * スクロールがあるページで範囲を指定してスクリーンショットを撮影するテスト。
	 *
	 * @ptl.expect 正しく撮影されていること。
	 */
	@Test
	public void inScrollPage() throws Exception {
		openGradationPageLarge();

		Rect rect = getPixelRectById("container");
		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTarget(rect.x, rect.y, rect.width, rect.height)
				.moveTarget(true).build();
		assertionView.assertView(arg);

		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		if (!isSkipColorCheck()) {
			// Check image
			double ratio = getPixelRatio();
			assertThat(image, is(gradation(ratio)));
		}

		Rect pixelRect = getPixelRectById("container");
		assertThat(image.getWidth(), is((int) Math.round(pixelRect.width)));
		assertThat(image.getHeight(), is((int) Math.round(pixelRect.height)));
	}

}
