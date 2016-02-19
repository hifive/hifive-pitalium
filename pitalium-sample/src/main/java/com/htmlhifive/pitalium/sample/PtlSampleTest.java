/*
 * Copyright (C) 2015-2016 NS Solutions Corporation
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
import com.htmlhifive.pitalium.core.model.DomSelector;
import com.htmlhifive.pitalium.core.model.ScreenshotArgument;
import com.htmlhifive.pitalium.core.model.SelectorType;
import com.htmlhifive.pitalium.core.selenium.DoubleValueRect;
import com.htmlhifive.pitalium.core.selenium.PtlWebElement;

/**
 * @author msakai
 */
public class PtlSampleTest extends PtlTestBase {

	private static final DomSelector GOTOTOP_DOM_ELEMENT = new DomSelector(SelectorType.CLASS_NAME, "gototop");

	@Test
	public void testCaptureTop() throws Exception {
		driver.get("");

		ScreenshotArgument arg = ScreenshotArgument.builder("sampleCapture")
				// 撮影対象を指定
				.addNewTarget()
					.addExclude(SelectorType.CLASS_NAME, "fb-like-box")
				.addNewTarget(SelectorType.ID, "about")
				.build();

		driver.executeScript("document.getElementById('about').style.marginTop='20px';");

		// ロードの完了を待つ（hifiveサイトではfacebookプラグインの表示）
		Wait<WebDriver> wait = new WebDriverWait(driver, 30);
		wait.until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector("iframe[title=\"fb:like_box Facebook Social Plugin\"]")));
		
		// 画面キャプチャ
		assertionView.assertView(arg);
	}

	@Test
	public void testCaptureTutorial13() throws Exception {
		String url = "conts/web/view/tutorial/interacting-with-controllers";
		driver.get(url);

		// 画面キャプチャ
		ScreenshotArgument arg = ScreenshotArgument.builder("tutorial13")
				// 撮影対象を指定
				.addNewTarget()
				.addHiddenElementSelectors(GOTOTOP_DOM_ELEMENT)
				.build();
		
		// 画面キャプチャ
		assertionView.assertView(arg);
	}

	@Test
	public void testCaptureTutorialTop() throws Exception {
		String url = "conts/web/view/tutorial/menu";
		driver.get(url);

		PtlWebElement e = (PtlWebElement) driver.findElement(By.cssSelector(".wikimodel-freestanding"));

		DoubleValueRect rect = e.getDoubleValueRect();
		driver.executeScript("arguments[0].width=arguments[1]", e, rect.getWidth());
		driver.executeScript("arguments[0].height=arguments[1]", e, rect.getHeight());
		driver.executeScript("arguments[0].src=''", e);

		// 画面キャプチャ
		ScreenshotArgument arg = ScreenshotArgument.builder("tutorialTop")
				// 撮影対象を指定
				.addNewTarget()
				.addHiddenElementSelectors(GOTOTOP_DOM_ELEMENT)
				.build();
		
		// 画面キャプチャ
		assertionView.assertView(arg);
	}
}