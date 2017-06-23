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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.it.screenshot.PtlItScreenshotTestBase;

/**
 * 要素内スクロール終了後に要素の位置を元に戻しているかテスト
 */
public class ScrollElementPositionResetTest extends PtlItScreenshotTestBase {

	/**
	 * 要素内スクロールがあるDIV要素を要素内スクロールオプションあり、移動オプションありで撮影する。
	 * 
	 * @ptl.expect 撮影後に要素のスクロール位置をトップに戻していること。
	 */
	@Test
	public void withMoveOption() throws Exception {
		openScrollPage();

		// check element scroll position
		assertThat(getScrollTop(), is(0));

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("div-scroll").moveTarget(true)
				.scrollTarget(true).build();
		assertionView.assertView(arg);

		// check element scroll position
		assertThat(getScrollTop(), is(0));
	}

	/**
	 * 要素内スクロールがあるDIV要素を要素内スクロールオプションあり、移動オプションなしで撮影する。
	 * 
	 * @ptl.expect 撮影後に要素のスクロール位置をトップに戻していること。
	 */
	@Test
	public void withoutMoveOption() throws Exception {
		openScrollPage();

		// check element scroll position
		assertThat(getScrollTop(), is(0));

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("div-scroll").moveTarget(false)
				.scrollTarget(true).build();
		assertionView.assertView(arg);

		// check element scroll position
		assertThat(getScrollTop(), is(0));
	}

	private int getScrollTop() {
		Number top = driver.executeJavaScript("return document.getElementById('div-scroll').scrollTop;");
		return top.intValue();
	}

}
