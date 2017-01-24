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

package com.htmlhifive.pitalium.it.screenshot2.partialPage;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.it.screenshot2.PtlItScreenshotTestBase;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.fail;

/**
 * 大きさが無い要素を撮影するテスト
 */
public class ZeroSizeElementTest extends PtlItScreenshotTestBase {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	/**
	 * 大きさが無い要素を撮影する。
	 * 
	 * @ptl.expect AssertionErrorが発生すること。
	 */
	@Test
	public void singleTarget() throws Exception {
		openBasicColorPage();

		driver.executeJavaScript("" + "var element = document.getElementById('colorColumn0');"
				+ "element.style.width = '0';" + "element.style.height = '0';" + "element.style.padding = '0';");

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("colorColumn0").build();

		expectedException.expect(AssertionError.class);
		expectedException.expectMessage("Invalid selector found");
		assertionView.assertView(arg);

		fail();
	}

	/**
	 * 大きさが無い要素を含む、複数の要素を撮影する。
	 * 
	 * @ptl.expect AssertionErrorが発生すること。
	 */
	@Test
	public void inMultiTargets() throws Exception {
		openBasicColorPage();

		driver.executeJavaScript("" + "var element = document.getElementById('colorColumn0');"
				+ "element.style.width = '0';" + "element.style.height = '0';" + "element.style.padding = '0';");

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("colorColumn1")
				.addNewTargetById("colorColumn2").addNewTargetById("colorColumn0").build();

		expectedException.expect(AssertionError.class);
		expectedException.expectMessage("Invalid selector found");
		assertionView.assertView(arg);

		fail();
	}

}
