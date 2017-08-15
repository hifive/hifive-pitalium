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

package com.htmlhifive.pitalium.it.screenshot.hidden;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeFalse;

import java.awt.image.BufferedImage;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.remote.BrowserType;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.it.screenshot.PtlItScreenshotTestBase;

/**
 * 単一要素を非表示設定にするテスト
 */
public class HideSingleElementTest extends PtlItScreenshotTestBase {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	/**
	 * 単体セレクタで単一要素を指定して非表示にする。
	 * 
	 * @ptl.expect 非表示に指定した要素が写っていないこと。
	 */
	@Test
	public void singleTarget() throws Exception {
		assumeFalse("Skip Safari hidden test.", BrowserType.SAFARI.equals(capabilities.getBrowserName()));
		openBasicColorPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTarget().addHiddenElementsById("colorColumn0")
				.build();
		assertionView.assertView(arg);

		// Check
		// 特定の色のピクセルが存在しないこと
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		int width = image.getWidth();
		int height = image.getHeight();
		Color hiddenColor = Color.RED;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Color actual = Color.valueOf(image.getRGB(x, y));
				assertThat(actual, is(not(hiddenColor)));
			}
		}
	}

	/**
	 * 単体セレクタで存在しない要素を指定して非表示にする。
	 * 
	 * @ptl.expect エラーが発生しないこと。
	 */
	@Test
	public void notExists() throws Exception {
		openBasicColorPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTarget().addHiddenElementsById("not-exists")
				.build();
		if (BrowserType.SAFARI.equals(capabilities.getBrowserName())) {
			expectedException.expect(NoSuchElementException.class);
		}
		assertionView.assertView(arg);

		// エラーとならない
	}

}
