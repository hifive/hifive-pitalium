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
package com.htmlhifive.pitalium.it.screenshot.partialapge;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import com.htmlhifive.pitalium.core.PtlTestBase;

/**
 * スクリーンショット取得関数を単体で呼び出すテスト
 */
public class TakeScreenshotTest extends PtlTestBase {

	private static final String BASE_URL = PtlTestConfig.getInstance().getTestAppConfig().getBaseUrl();

	/**
	 * スクリーンショット取得関数を単体で呼び出すテスト
	 */

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void takeScreenshot() throws IOException {
		driver.get(BASE_URL);
		BufferedImage bi = ImageIO.read(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE));
	}
}