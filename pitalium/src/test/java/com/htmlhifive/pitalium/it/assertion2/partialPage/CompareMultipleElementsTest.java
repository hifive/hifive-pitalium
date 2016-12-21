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

package com.htmlhifive.pitalium.it.assertion2.partialPage;

import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.it.assertion2.PtlItAssertionTestBase;
import org.junit.Test;

/**
 * 複数要素を単一セレクタで指定して同一条件で比較するテスト
 */
public class CompareMultipleElementsTest extends PtlItAssertionTestBase {

	@Test
	public void compareWithMoveOptionBySingleSelector() throws Exception {
		openBasicTextPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByClassName("text-column")
				.moveTarget(true).build();
		assertionView.assertView(arg);
	}

	@Test
	public void compareWithoutMoveOptionBySingleSelector() throws Exception {
		openBasicTextPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetByClassName("text-column")
				.moveTarget(false).build();
		assertionView.assertView(arg);
	}

	@Test
	public void compareWithMoveOptionByMultipleSelectors() throws Exception {
		openBasicTextPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("textColumn0").moveTarget(true)
				.addNewTargetById("textColumn1").moveTarget(true).addNewTargetById("textColumn2").moveTarget(true)
				.build();
		assertionView.assertView(arg);
	}

	@Test
	public void compareWithoutMoveOptionByMultipleSelectors() throws Exception {
		openBasicTextPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("textColumn0").moveTarget(false)
				.addNewTargetById("textColumn1").moveTarget(false).addNewTargetById("textColumn2").moveTarget(false)
				.build();
		assertionView.assertView(arg);
	}

	@Test
	public void compareByMultipleSelectorsIncorrectOrder() throws Exception {
		openBasicTextPage();

		if (isRunTest()) {
			ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("textColumn2")
					.addNewTargetById("textColumn0").addNewTargetById("textColumn1").build();
			assertionView.assertView(arg);
		} else {
			ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("textColumn0")
					.addNewTargetById("textColumn1").addNewTargetById("textColumn2").build();
			assertionView.assertView(arg);
		}
	}

}
