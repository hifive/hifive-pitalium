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

package com.htmlhifive.pitalium.it.screenshot.iframe;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.google.common.base.Supplier;
import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.core.model.TargetResult;
import com.htmlhifive.pitalium.core.selenium.PtlWebElement;
import com.htmlhifive.pitalium.image.model.RectangleArea;
import com.htmlhifive.pitalium.it.screenshot.PtlItScreenshotTestBase;

/**
 * iframe内の要素を除外設定とするテスト
 */
public class ExcludeInFrameTest extends PtlItScreenshotTestBase {

	/**
	 * BODYを撮影する際にiframe内外の要素を除外する。
	 * 
	 * @ptl.expect iframe内外の除外領域が正しく保存されていること。
	 */
	@Test
	public void captureBody_excludeInsideAndOutsideFrame() throws Exception {
		openIFramePage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTarget().moveTarget(true).scrollTarget(false)
				.addExcludeByClassName("exclude-target").addExcludeByClassName("content-left")
				.inFrameByClassName("content").build();
		assertionView.assertView(arg);

		// Check
		TargetResult result = loadTargetResults("s").get(0);
		assertThat(result.getExcludes(), hasSize(2));

		// Outside iframe
		Rect rect = getPixelRectBySelector(".exclude-target");
		assertThat(result.getExcludes().get(0).getRectangle(), is(rect.toRectangleArea()));

		// Inside iframe
		PtlWebElement frame = (PtlWebElement) driver.findElementByClassName("content");
		frame.setFrameParent(frame);
		Rect frameRect = getPixelRect(frame);
		Rect targetRect = frame.executeInFrame(new Supplier<Rect>() {
			@Override
			public Rect get() {
				return getPixelRectBySelector(".content-left");
			}
		});

		double x = Math.round(frameRect.x + targetRect.x);
		double y = Math.round(frameRect.y + targetRect.y);
		double width = Math.round(frameRect.width - targetRect.x);
		double height = Math.round(frameRect.height - targetRect.y);
		assertThat(result.getExcludes().get(1).getRectangle(), is(new RectangleArea(x, y, width, height)));
	}

	/**
	 * iframeを撮影する際にiframe内の要素を除外する。
	 * 
	 * @ptl.expect 除外領域が正しく保存されていること。
	 */
	@Test
	public void captureIFrame() throws Exception {
		openIFramePage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByClassName("content").moveTarget(true)
				.scrollTarget(true).addExcludeByClassName("content-left").build();
		assertionView.assertView(arg);

		// Check
		TargetResult result = loadTargetResults("s").get(0);
		assertThat(result.getExcludes(), hasSize(1));

		// Inside iframe
		// frame
		PtlWebElement frame = (PtlWebElement) driver.findElementByClassName("content");
		frame.setFrameParent(frame);
		Rect rect = frame.executeInFrame(new Supplier<Rect>() {
			@Override
			public Rect get() {
				return getPixelRectBySelector(".content-left");
			}
		});

		assertThat(result.getExcludes().get(0).getRectangle(), is(rect.toRectangleArea()));
	}

	/**
	 * BODYを撮影する際にiframe内外のs存在しない要素を除外する。
	 * 
	 * @ptl.expect エラーが発生せず、除外領域が保存されていないこと。
	 */
	@Test
	public void captureBody_excludeNotExistElementInsideAndOutsideFrame() throws Exception {
		openIFramePage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTarget().moveTarget(true).scrollTarget(false)
				.addExcludeByClassName("not-exists").addExcludeByClassName("not-exists").inFrameByClassName("content")
				.build();
		assertionView.assertView(arg);

		// Check
		TargetResult result = loadTargetResults("s").get(0);
		assertThat(result.getExcludes(), is(empty()));
	}

	/**
	 * BODYを撮影する際にiframe内のスクロールしないと見えない要素を除外する。
	 * 
	 * @ptl.expect エラーが発生せず、除外領域が保存されていないこと。
	 */
	@Test
	public void captureBody_excludeNotVisibleElement() throws Exception {
		openIFramePage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTarget().moveTarget(true).scrollTarget(false)
				.addExcludeByClassName("content-right").inFrameByClassName("content").build();
		assertionView.assertView(arg);

		// Check
		TargetResult result = loadTargetResults("s").get(0);
		assertThat(result.getExcludes(), hasSize(1));

		RectangleArea area = result.getExcludes().get(0).getRectangle();
		assertThat(area.getWidth(), is(0.0));
		assertThat(area.getHeight(), is(0.0));
	}

	/**
	 * iframeを撮影する際にiframe内の存在しない要素を除外する。
	 * 
	 * @ptl.expect エラーが発生せず、除外領域が保存されていないこと。
	 */
	@Test
	public void captureIFrame_excludeNotExists() throws Exception {
		openIFramePage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByClassName("content").moveTarget(true)
				.scrollTarget(true).addExcludeByClassName("not-exists").build();
		assertionView.assertView(arg);

		// Check
		TargetResult result = loadTargetResults("s").get(0);
		assertThat(result.getExcludes(), is(empty()));
	}

}
