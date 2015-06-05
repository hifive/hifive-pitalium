/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.it.screenshot.partialapge;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.htmlhifive.testlib.core.MrtTestBase;
import com.htmlhifive.testlib.core.config.MrtTestConfig;
import com.htmlhifive.testlib.core.model.CompareTarget;
import com.htmlhifive.testlib.core.model.ScreenArea;
import com.htmlhifive.testlib.core.model.SelectorType;

/**
 * 座標が固定されている要素に対するスクリーンショット取得のテスト
 */
public class TakeFixedElementWithMoveOptionTest extends MrtTestBase {

	private static final String BASE_URL = MrtTestConfig.getInstance().getTestAppConfig().getBaseUrl();

	/**
	 * 元々座標が固定されている要素に対してisMoveオプションを指定して<br>
	 * 正しくスクリーンショットがとれていることを確認する。<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：スクリーンショット取得後も要素の座標が変わっていない
	 */
	@Test
	public void moveFixedElement() {
		driver.get(BASE_URL);

		final String position = "absolute";
		final String offsetTop = "30px";
		final String offsetLeft = "20px";

		// 取得対象要素の座標を固定する。
		StringBuilder sb = new StringBuilder();
		sb.append("var about = document.getElementById('about');");
		sb.append("about.style.position = '");
		sb.append(position);
		sb.append("'; about.style.top = '");
		sb.append(offsetTop);
		sb.append("'; about.style.left = '");
		sb.append(offsetLeft);
		sb.append("';");
		driver.executeJavaScript(sb.toString());

		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.ID, "about"), null, true));
		assertionView.assertView("CssSelector", targets);

		// スクリーンショット取得後も取得対象の座標が保存されている
		WebElement about = driver.findElement(By.id("about"));
		assertThat(about.getCssValue("position"), is(position));
		assertThat(about.getCssValue("top"), is(offsetTop));
		assertThat(about.getCssValue("left"), is(offsetLeft));
	}
}