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
package com.htmlhifive.pitalium.it.screenshot_old.partialapge;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import com.htmlhifive.pitalium.core.model.CompareTarget;
import com.htmlhifive.pitalium.core.model.ScreenArea;
import com.htmlhifive.pitalium.core.model.SelectorType;

/**
 * 座標が固定されている要素に対するスクリーンショット取得のテスト
 */
public class KeepBodyStyleTest extends PtlTestBase {

	private static final String BASE_URL = PtlTestConfig.getInstance().getTestAppConfig().getBaseUrl();

	/**
	 * bodyにスタイルが定義されている固定されている要素に対してisMoveオプションを指定して<br>
	 * 正しくスクリーンショットがとれていることを確認する。<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：スクリーンショット取得後もbodyのスタイルが変わっていない
	 */
	@Test
	public void keepBodyStyleOffset() {
		driver.get(BASE_URL);

		final String position = "absolute";
		final String offsetLeft = "20px";
		final String offsetTop = "30px";

		// 取得対象要素の座標を固定する。
		StringBuilder sb = new StringBuilder();
		sb.append("document.body.style.position = '");
		sb.append(position);
		sb.append("'; document.body.style.top = '");
		sb.append(offsetTop);
		sb.append("'; document.body.style.left = '");
		sb.append(offsetLeft);
		sb.append("';");
		System.out.println(sb.toString());
		driver.executeJavaScript(sb.toString());

		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.ID, "about"), null, true));
		assertionView.assertView("CssSelector", targets);

		// スクリーンショット取得後もbody座標が保存されている
		WebElement body = driver.findElement(By.tagName("body"));
		//body.getCssValue("position");
		assertThat(body.getCssValue("position"), is(position));
		assertThat(body.getCssValue("top"), is(offsetTop));
		assertThat(body.getCssValue("left"), is(offsetLeft));
	}

	/**
	 * bodyにスタイルが定義されている固定されている要素に対してisMoveオプションを指定して<br>
	 * 正しくスクリーンショットがとれていることを確認する。<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：スクリーンショット取得後もbodyのスタイルが変わっていない
	 */
	@Test
	public void keepBodyStyleMargin() {
		driver.get(BASE_URL);

		final String position = "absolute";
		final String marginWidth = "20px";

		// 取得対象要素の座標を固定する。
		StringBuilder sb = new StringBuilder();
		sb.append("document.body.style.position = '");
		sb.append(position);
		sb.append("'; document.body.style.margin = '");
		sb.append(marginWidth);
		sb.append("';");
		System.out.println(sb.toString());
		driver.executeJavaScript(sb.toString());

		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.ID, "about"), null, true));
		assertionView.assertView("CssSelector", targets);

		// スクリーンショット取得後もbody座標が保存されている
		WebElement body = driver.findElement(By.tagName("body"));
		//body.getCssValue("position");
		assertThat(body.getCssValue("position"), is(position));
		assertThat(body.getCssValue("margin-top"), is(String.valueOf(marginWidth)));
		assertThat(body.getCssValue("margin-right"), is(String.valueOf(marginWidth)));
		assertThat(body.getCssValue("margin-bottom"), is(String.valueOf(marginWidth)));
		assertThat(body.getCssValue("margin-left"), is(String.valueOf(marginWidth)));
	}
}