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
package com.htmlhifive.pitalium.it.screenshot_old.scroll;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import com.htmlhifive.pitalium.core.model.CompareTarget;
import com.htmlhifive.pitalium.core.model.DomSelector;
import com.htmlhifive.pitalium.core.model.ScreenArea;
import com.htmlhifive.pitalium.core.model.SelectorType;
import com.htmlhifive.pitalium.core.selenium.PtlWebDriverWait;

/**
 * Marginありの要素のスクリーンショットが正しくとれているかのテスト
 */
public class TakeScrollMarginPartTest extends PtlTestBase {

	private static final PtlTestConfig config = PtlTestConfig.getInstance();
	private static final String BASE_URL = config.getTestAppConfig().getBaseUrl();

	/**
	 * マージン有りの場合に正しく撮影できるか確認するテスト。
	 */
	@Test
	public void takeMarginScreenshotTest() {

		driver.get(BASE_URL);
		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		// Targetの設定
		DomSelector divSelector = new DomSelector(SelectorType.ID, "about-scroll");
		ScreenArea divScreenArea = ScreenArea.of(divSelector.getType(), divSelector.getValue());

		DomSelector textareaSelector = new DomSelector(SelectorType.ID, "textarea-scroll");
		ScreenArea textScreenArea = ScreenArea.of(textareaSelector.getType(), textareaSelector.getValue());

		DomSelector tbodySelector = new DomSelector(SelectorType.CSS_SELECTOR, "#table-scroll tbody");
		ScreenArea tbodyScreenArea = ScreenArea.of(tbodySelector.getType(), tbodySelector.getValue());

		CompareTarget[] targets = { new CompareTarget(divScreenArea, null, true, true),
				new CompareTarget(textScreenArea, null, true, true),
				new CompareTarget(tbodyScreenArea, null, true, true) };

		// margin: 100px
		driver.executeScript("document.getElementById('about-scroll').style.margin = '100px';");
		driver.executeScript("document.getElementById('textarea-scroll').style.margin = '100px';");
		WebElement tbodyElement = driver.findElement(By.cssSelector("#table-scroll tbody"));
		driver.executeScript("arguments[0].style.margin = '100px';", tbodyElement);

		assertionView.assertView("normalMarginScreenshot", targets);

	}

	/**
	 * マージン無しの場合に正しく撮影できるか確認するテスト。
	 */
	@Test
	public void takeNoMarginScreenshotTest() {

		driver.get(BASE_URL);
		PtlWebDriverWait wait = new PtlWebDriverWait(driver, 30);
		wait.untilLoad();

		// Targetの設定
		DomSelector divSelector = new DomSelector(SelectorType.ID, "about-scroll");
		ScreenArea divScreenArea = ScreenArea.of(divSelector.getType(), divSelector.getValue());

		DomSelector textareaSelector = new DomSelector(SelectorType.ID, "textarea-scroll");
		ScreenArea textScreenArea = ScreenArea.of(textareaSelector.getType(), textareaSelector.getValue());

		DomSelector tbodySelector = new DomSelector(SelectorType.CSS_SELECTOR, "#table-scroll tbody");
		ScreenArea tbodyScreenArea = ScreenArea.of(tbodySelector.getType(), tbodySelector.getValue());

		CompareTarget[] targets = { new CompareTarget(divScreenArea, null, true, true),
				new CompareTarget(textScreenArea, null, true, true),
				new CompareTarget(tbodyScreenArea, null, true, true) };

		WebElement tbodyElement = driver.findElement(By.cssSelector("#table-scroll tbody"));

		// margin: 0px
		driver.executeScript("document.getElementById('about-scroll').style.margin = '0px';");
		driver.executeScript("document.getElementById('textarea-scroll').style.margin = '0px';");
		driver.executeScript("arguments[0].style.margin = '0px';", tbodyElement);
		assertionView.assertView("noMarginScreenshot", targets);

	}
}