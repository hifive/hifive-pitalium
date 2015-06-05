/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.it.screenshot.partialapge;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.htmlhifive.testlib.core.MrtTestBase;
import com.htmlhifive.testlib.core.config.MrtTestConfig;
import com.htmlhifive.testlib.core.model.CompareTarget;
import com.htmlhifive.testlib.core.model.ScreenArea;
import com.htmlhifive.testlib.core.model.SelectorType;

/**
 * ページの特定要素のスクリーンショットが正しくとれているかのテスト
 */
public class TakeMarginPartTest extends MrtTestBase {

	private static final String BASE_URL = MrtTestConfig.getInstance().getTestAppConfig().getBaseUrl();

	/**
	 * marginを持つ要素のスクリーンショットが正しくとれているかのテスト<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、「hifiveとは」部分のスクリーンショットが<br>
	 * marginを含まずにとれていることを目視で確認する
	 */
	@Test
	public void takeMarginPart() {
		final int marginWidth = 100;
		driver.get(BASE_URL);

		// 取得対象要素のマージンを設定する
		driver.executeJavaScript("document.getElementById('about').style.margin = '" + marginWidth + "';");

		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.ID, "about"), null, true));
		assertionView.assertView("takeMarginPart", targets);
	}

	/**
	 * 負の値のmarginを持つ要素のスクリーンショットが正しくとれているかのテスト<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、「hifiveとは」部分のスクリーンショットが<br>
	 * marginを含まずにとれていることを目視で確認する
	 */
	@Test
	public void takeMinusMarginPart() {
		final int marginWidth = -20;
		driver.get(BASE_URL);

		// 取得対象要素のマージンを設定する
		driver.executeJavaScript("document.getElementById('about').style.margin = '" + marginWidth + "';");

		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.ID, "about"), null, true));
		assertionView.assertView("takeMunusMarginPart", targets);
	}
}