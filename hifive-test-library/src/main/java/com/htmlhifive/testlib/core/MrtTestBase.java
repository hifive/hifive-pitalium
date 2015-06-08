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
package com.htmlhifive.testlib.core;

import java.util.List;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.htmlhifive.testlib.core.rules.AssertionView;
import com.htmlhifive.testlib.core.rules.ResultCollector;
import com.htmlhifive.testlib.core.selenium.MrtCapabilities;
import com.htmlhifive.testlib.core.selenium.MrtWebDriver;

/**
 * テスト実行用の基底クラス。テスト実行に必要な&#064;Rule、&#064;ClassRuleが定義されています。<br/>
 * 本テストツールの機能を利用する場合は、このクラスを拡張してテストクラスを実装してください。
 */
@RunWith(ParameterizedThreads.class)
public abstract class MrtTestBase {

	//CHECKSTYLE:OFF
	/**
	 * &#064;ClassRule テストクラスに共通の処理を実行するクラス。スクリーンショット取得とテストの結果収集・出力を行う
	 */
	@ClassRule
	public static ResultCollector collector = new ResultCollector();

	/**
	 * &#064;Rule メソッドに共通の処理を実行するクラス。driverのセットアップやassertを行う。
	 */
	@Rule
	public AssertionView assertionView = new AssertionView();

	/**
	 * このインスタンスに割り当てられたcapability
	 */
	@Parameterized.Parameter
	public MrtCapabilities capabilities;
	//CHECKSTYLE:ON

	/**
	 * ブラウザ操作用Webdriver
	 */
	protected MrtWebDriver driver;

	/**
	 * Capabilityの読み込みを行います。
	 * 
	 * @return Capabilityのリスト
	 */
	@Parameterized.Parameters(name = "{0}")
	public static List<MrtCapabilities[]> readCapabilities() {
		return MrtCapabilities.readCapabilities();
	}

	/**
	 * テスト実行時のセットアップを行います。
	 */
	@Before
	public void setUp() {
		driver = assertionView.createDriver(capabilities);
	}
}
