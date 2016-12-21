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
 * セレクタで指定した要素が存在しない場合のテスト
 */
public class NoSuchElementTest extends PtlItScreenshotTestBase {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void noSuchElement() throws Exception {
		openBasicTextPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("notExists").build();

		expectedException.expect(AssertionError.class);
		expectedException.expectMessage("Invalid selector found");
		assertionView.assertView(arg);

		fail();
	}

	@Test
	public void noSuchElementInMultiTarget() throws Exception {
		openBasicTextPage();

		ScreenshotArgument arg = ScreenshotArgument.builder("s").addNewTargetById("container") // exists
				.addNewTargetById("notExists") // not exists
				.build();

		expectedException.expect(AssertionError.class);
		expectedException.expectMessage("Invalid selector found");
		assertionView.assertView(arg);

		fail();
	}

}
