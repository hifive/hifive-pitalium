/*
 * Copyright (C) 2015 NS Solutions Corporation
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
package com.htmlhifive.pitalium.it.exec.base;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.htmlhifive.pitalium.core.ParameterizedThreads;
import com.htmlhifive.pitalium.core.rules.AssertionView;
import com.htmlhifive.pitalium.core.rules.ResultCollector;
import com.htmlhifive.pitalium.core.selenium.PtlCapabilities;
import com.htmlhifive.pitalium.core.selenium.PtlWebDriver;

/**
 * PtlTestBaseを利用しないテストの実行のテスト
 */
@RunWith(ParameterizedThreads.class)
public class WithoutBaseTest {

	/**
	 * Capabilityの読み込みを行います.
	 * 
	 * @return Capabilityのリスト
	 */
	@Parameterized.Parameters(name = "{0}")
	public static List<PtlCapabilities[]> readCapabilities() {
		return PtlCapabilities.readCapabilities();
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
	public PtlCapabilities capabilities;

	/**
	 * ブラウザ操作用driver.
	 */
	private PtlWebDriver driver;

	/**
	 * テストクラスセットアップ.
	 */
	@Before
	public void setUp() {
		driver = assertionView.createDriver(capabilities);
	}

	/**
	 * PtlTestBaseを利用しないテストの実行のテスト<br>
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
