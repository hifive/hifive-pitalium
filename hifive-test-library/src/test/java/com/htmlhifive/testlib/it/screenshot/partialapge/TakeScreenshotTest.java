/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.it.screenshot.partialapge;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import com.htmlhifive.testlib.core.MrtTestBase;
import com.htmlhifive.testlib.core.config.MrtTestConfig;

/**
 * スクリーンショット取得関数を単体で呼び出すテスト
 */
public class TakeScreenshotTest extends MrtTestBase {

	private static final String BASE_URL = MrtTestConfig.getInstance().getTestAppConfig().getBaseUrl();

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