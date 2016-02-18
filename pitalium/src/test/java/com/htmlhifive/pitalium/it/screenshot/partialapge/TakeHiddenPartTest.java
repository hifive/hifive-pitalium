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
package com.htmlhifive.pitalium.it.screenshot.partialapge;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.ArrayList;
import java.util.List;

import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.model.CompareTarget;
import com.htmlhifive.pitalium.core.model.ScreenArea;
import com.htmlhifive.pitalium.core.model.SelectorType;

/**
 * 非表示（visibility: hidden）要素のスクリーンショット取得のテスト<br>
 */
public class TakeHiddenPartTest extends PtlTestBase {

	private static final String BASE_URL = PtlTestConfig.getInstance().getTestAppConfig().getBaseUrl();

	/**
	 * 非表示（visibility: hidden）の要素のスクリーンショットが正しくとれていることを確認する。<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、非表示の要素(空白)のスクリーンショットがとれていることを目視で確認する。<br>
	 * また、スクリーンショット取得後も要素が非表示のままであることを確認する。
	 */
	@Test
	public void takeHiddenPart() {
		driver.get(BASE_URL);
		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.CSS_SELECTOR, "#about")));

		// 取得対象要素を非表示にする
		driver.executeJavaScript("document.getElementById('about').style.visibility = 'hidden';");
		assertionView.assertView("takeHiddenPart", targets);

		// スクリーンショット取得後も要素は非表示のまま
		WebElement about = driver.findElement(By.id("about"));
		assertThat(about.getCssValue("visibility"), is("hidden"));

	}
}