/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.it.exec.base;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.htmlhifive.testlib.core.ParameterizedThreads;
import com.htmlhifive.testlib.core.rules.AssertionView;
import com.htmlhifive.testlib.core.rules.ResultCollector;
import com.htmlhifive.testlib.core.selenium.MrtCapabilities;
import com.htmlhifive.testlib.core.selenium.MrtWebDriver;

/**
 * MrtBaseを利用しないテストの実行のテスト
 */
@RunWith(ParameterizedThreads.class)
public class WithoutBaseTest {

	/**
	 * Capabilityの読み込みを行います.
	 * 
	 * @return Capabilityのリスト
	 */
	@Parameterized.Parameters(name = "{0}")
	public static List<MrtCapabilities[]> readCapabilities() {
		return MrtCapabilities.readCapabilities();
	}

	/**
	 * 結果収集・出力クラス.
	 */
	@ClassRule
	public static ResultCollector collector = new ResultCollector();

	/**
	 * 表示内容のassert実行クラス.
	 */
	@Rule
	public AssertionView assertionView = new AssertionView();

	/**
	 * このインスタンスに割り当てられたcapability.
	 */
	@Parameterized.Parameter
	public MrtCapabilities capabilities;

	/**
	 * ブラウザ操作用driver.
	 */
	private MrtWebDriver driver;

	/**
	 * テストクラスセットアップ.
	 */
	@Before
	public void setUp() {
		driver = assertionView.createDriver(capabilities);
	}

	/**
	 * MrtBaseを利用しないテストの実行のテスト<br>
	 * 前提条件：このテストをSET_EXPECTEDモードで1度実行し、スクリーンショットを取得している<br>
	 * 実行環境：IE7～11/FireFox/Chrome/Android 2.3, 4.0, 4.4/iOS 8.1<br>
	 * 期待結果：テストが正しく実行でき、assertViewの比較で一致する
	 */
	@Test
	public void checkField() {
		assertNotNull(driver);
		driver.get(null);

		// driverから取得できるcapabailitiesをプロパティに持つ
		assertEquals(driver.getCapabilities(), capabilities);

		assertNotNull(assertionView);
		assertionView.assertView("withBaseTest");

		assertNotNull(collector);
	}
}
