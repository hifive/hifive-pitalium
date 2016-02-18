/*
 * Copyright (C) 2016 NS Solutions Corporation
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
package com.htmlhifive.pitalium.sample;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.model.CompareTarget;
import com.htmlhifive.pitalium.core.model.DomSelector;
import com.htmlhifive.pitalium.core.model.ScreenArea;
import com.htmlhifive.pitalium.core.model.SelectorType;
import com.htmlhifive.pitalium.core.selenium.PtlWebElement;

/**
 * @author msakai
 */
public class PtlSampleTest extends PtlTestBase {

	private static final DomSelector[] HIDE_ELEMENTS = { new DomSelector(SelectorType.CLASS_NAME, "gototop") };

	@Test
	public void testCaptureTop() throws Exception {
		driver.get("");

		CompareTarget[] targets = {
				new CompareTarget(ScreenArea.of(SelectorType.TAG_NAME, "body"), new ScreenArea[] { ScreenArea.of(
						SelectorType.CLASS_NAME, "fb-like-box") }, true),
				new CompareTarget(ScreenArea.of(SelectorType.ID, "about")) };

		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("document.getElementById('about').style.marginTop='20px';");

		// 画面キャプチャ
		Wait<WebDriver> wait = new WebDriverWait(driver, 30);
		wait.until(ExpectedConditions.presenceOfElementLocated(By
				.cssSelector("iframe[title=\"fb:like_box Facebook Social Plugin\"]")));
		assertionView.assertView("sampleCapture", targets, HIDE_ELEMENTS);
	}

	@Test
	public void testCaptureTutorial13() throws Exception {
		String url = "conts/web/view/tutorial/interacting-with-controllers";
		driver.get(url);

		// 画面キャプチャ
		assertionView.assertView("tutorial13", null, HIDE_ELEMENTS);
	}

	@Test
	public void testCaptureTutorialTop() throws Exception {
		String url = "conts/web/view/tutorial/menu";
		driver.get(url);

		PtlWebElement e = (PtlWebElement) driver.findElement(By.cssSelector(".wikimodel-freestanding"));

		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("arguments[0].width=arguments[1]", e, e.getRect().getWidth());
		jse.executeScript("arguments[0].height=arguments[1]", e, e.getRect().getHeight());
		jse.executeScript("arguments[0].src=''", e);

		// 画面キャプチャ
		assertionView.assertView("tutorial13", null, HIDE_ELEMENTS);
	}
}