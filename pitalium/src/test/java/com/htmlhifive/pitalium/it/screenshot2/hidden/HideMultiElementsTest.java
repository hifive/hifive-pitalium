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

package com.htmlhifive.pitalium.it.screenshot2.hidden;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.awt.image.BufferedImage;

import org.junit.Test;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.it.screenshot2.PtlItScreenshotTestBase;

/**
 * 複数の要素を非表示設定とするテスト
 */
public class HideMultiElementsTest extends PtlItScreenshotTestBase {

	/**
	 * 単体セレクタで複数の要素を指定して非表示にする。
	 * 
	 * @ptl.expect 非表示に指定した要素が写っていないこと。
	 */
	@Test
	public void singleTarget() throws Exception {
		openBasicColorPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTarget()
				.addHiddenElementsByClassName("color-column").build();
		assertionView.assertView(arg);

		// Check
		// 特定の色のピクセルが存在しないこと
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		int width = image.getWidth();
		int height = image.getHeight();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Color actual = Color.valueOf(image.getRGB(x, y));
				assertThat(actual, is(not(anyOf(equalTo(Color.RED), equalTo(Color.GREEN), equalTo(Color.BLUE)))));
			}
		}
	}

	/**
	 * 複数セレクタで複数の要素を指定して非表示にする。
	 * 
	 * @ptl.expect 非表示に指定した要素が写っていないこと。
	 */
	@Test
	public void multiTargets() throws Exception {
		openBasicColorPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTarget().addHiddenElementsById("colorColumn0")
				.addHiddenElementsById("colorColumn1").addHiddenElementsById("colorColumn2").build();
		assertionView.assertView(arg);

		// Check
		// 特定の色のピクセルが存在しないこと
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		int width = image.getWidth();
		int height = image.getHeight();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Color actual = Color.valueOf(image.getRGB(x, y));
				assertThat(actual, is(not(anyOf(equalTo(Color.RED), equalTo(Color.GREEN), equalTo(Color.BLUE)))));
			}
		}
	}

	/**
	 * 複数セレクタで複数の要素を指定して非表示にするが、そのうちのいずれかは存在しない要素である。
	 * 
	 * @ptl.expect エラーが発生せず、非表示に指定した要素が写っていないこと。
	 */
	@Test
	public void multiTargets_notExist() throws Exception {
		openBasicColorPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTarget().addHiddenElementsById("not-exists")
				.addHiddenElementsById("colorColumn1").addHiddenElementsById("colorColumn2").build();
		assertionView.assertView(arg);

		// Check
		// 特定の色のピクセルが存在しないこと
		BufferedImage image = loadTargetResults("s").get(0).getImage().get();
		int width = image.getWidth();
		int height = image.getHeight();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Color actual = Color.valueOf(image.getRGB(x, y));
				assertThat(actual, is(not(anyOf(equalTo(Color.GREEN), equalTo(Color.BLUE)))));
			}
		}
	}

}
