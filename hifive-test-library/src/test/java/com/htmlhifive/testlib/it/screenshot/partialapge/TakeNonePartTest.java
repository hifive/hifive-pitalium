/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.it.screenshot.partialapge;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.htmlhifive.testlib.core.MrtTestBase;
import com.htmlhifive.testlib.core.config.MrtTestConfig;
import com.htmlhifive.testlib.core.model.CompareTarget;
import com.htmlhifive.testlib.core.model.ScreenArea;
import com.htmlhifive.testlib.core.model.SelectorType;

/**
 * 非表示（display: none）要素のスクリーンショット取得のテスト<br>
 */
public class TakeNonePartTest extends MrtTestBase {

	private static final String BASE_URL = MrtTestConfig.getInstance().getTestAppConfig().getBaseUrl();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/**
	 * 非表示（display : none）の要素をターゲットとして指定すると例外が発生することを確認する。<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：例外が発生する
	 */
	@Test
	public void takeNonePart() {
		driver.get(BASE_URL);
		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		// 取得対象要素を非表示にする
		driver.executeJavaScript("document.getElementById('about').style.display = 'none';");

		thrown.expect(AssertionError.class);
		thrown.expectMessage("Invalid selector found");
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.CSS_SELECTOR, "#about")));

		assertionView.assertView("takeNonePart", targets);
	}

	/**
	 * 要素がターゲットに追加された後で非表示（display : none）になった場合、assertViewで例外が発生することを確認する。<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：例外が発生する
	 */
	@Test
	public void takeNonePartAfterAdding() {
		driver.get(BASE_URL);
		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.CSS_SELECTOR, "#about")));

		// 取得対象要素を非表示にする
		driver.executeJavaScript("document.getElementById('about').style.display = 'none';");

		thrown.expect(AssertionError.class);
		thrown.expectMessage("Invalid selector found");
		assertionView.assertView("takeNonePartAfterAdding", targets);
	}

	/**
	 * 親要素が非表示（display : none）である要素のスクリーンショット取得時に例外が発生することを確認する。<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：例外が発生する
	 */
	@Test
	public void takeNoneParent() {
		driver.get(BASE_URL);
		List<CompareTarget> targets = new ArrayList<CompareTarget>();
		targets.add(new CompareTarget(ScreenArea.of(SelectorType.CSS_SELECTOR, "#about")));

		// 取得対象要素を非表示にする
		driver.executeJavaScript("document.getElementById('wrapper').style.display = 'none';");

		thrown.expect(AssertionError.class);
		thrown.expectMessage("Invalid selector found");
		assertionView.assertView("takeNoneParent", targets);
	}

	/**
	 * 複数要素からなるターゲットを指定して、その中のひとつが非表示（display : none）である場合、<br>
	 * 正しくスクリーンショットが取得されることを確認する。 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：非表示要素を除いた要素のスクリーンショットが取得される。<br>
	 * 　　　　　この場合"2014/07/02"および"2014/06/10"の更新履歴の行が取得される。
	 */
	@Test
	public void takeIncludeNonePart() {
		driver.get(BASE_URL);

		// 取得対象要素のうち一つを非表示にする
		driver.executeJavaScript("document.getElementById('news').getElementsByTagName('tr')[0].style.display = 'none'");
		List<CompareTarget> targets = new ArrayList<CompareTarget>();

		targets.add(new CompareTarget(ScreenArea.of(SelectorType.CSS_SELECTOR, "#news tr")));

		thrown.expect(AssertionError.class);
		thrown.expectMessage("Invalid selector found");
		assertionView.assertView("takeIncludeNonePar", targets);
	}
}