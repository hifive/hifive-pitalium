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
package com.htmlhifive.pitalium.core;

import java.util.List;

import com.htmlhifive.pitalium.core.rules.AssumeCapability;
import com.htmlhifive.pitalium.core.rules.PtlWebDriverCloser;
import com.htmlhifive.pitalium.junit.ParameterizedClassRule;
import com.htmlhifive.pitalium.junit.PtlBlockJUnit4ClassRunnerWithParametersFactory;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.htmlhifive.pitalium.core.rules.AssertionView;
import com.htmlhifive.pitalium.core.rules.ResultCollector;
import com.htmlhifive.pitalium.core.selenium.PtlCapabilities;
import com.htmlhifive.pitalium.core.selenium.PtlWebDriver;

/**
 * テスト実行用の基底クラス。テスト実行に必要な&#064;Rule、&#064;ClassRuleが定義されています。<br/>
 * 本テストツールの機能を利用する場合は、このクラスを拡張してテストクラスを実装してください。
 */
@RunWith(ParameterizedThreads.class)
@Parameterized.UseParametersRunnerFactory(PtlBlockJUnit4ClassRunnerWithParametersFactory.class)
public abstract class PtlTestBase {

	//CHECKSTYLE:OFF
	/**
	 * &#064;ClassRule テストクラスに共通の処理を実行するクラス。スクリーンショット取得とテストの結果収集・出力を行う
	 */
	@ClassRule
	public static ResultCollector collector = new ResultCollector();

	/**
	 * &#064;ParameterizedClassRule テストクラスに共通の処理を実行するクラス。WebDriver管理を行う。
	 */
	@ParameterizedClassRule
	public static PtlWebDriverCloser driverCloser = new PtlWebDriverCloser();

	/**
	 * &#064;Rule メソッドに共通の処理を実行するクラス。driverのセットアップやassertを行う。
	 */
	@Rule
	public AssertionView assertionView = new AssertionView();

	/**
	 * &#064;Rule {@link com.htmlhifive.pitalium.core.annotation.CapabilityFilter}を使用してテスト実行のフィルタリングを行う。
	 */
	@Rule
	public AssumeCapability assumeCapability = new AssumeCapability();

	/**
	 * このインスタンスに割り当てられたcapability
	 */
	@Parameterized.Parameter
	public PtlCapabilities capabilities;
	//CHECKSTYLE:ON

	/**
	 * ブラウザ操作用Webdriver
	 */
	protected PtlWebDriver driver;

	/**
	 * Capabilityの読み込みを行います。
	 * 
	 * @return Capabilityのリスト
	 */
	@Parameterized.Parameters(name = "{0}")
	public static List<PtlCapabilities[]> readCapabilities() {
		return PtlCapabilities.readCapabilities();
	}

	/**
	 * テスト実行時のセットアップを行います。
	 */
	@Before
	public void setUp() {
		assumeCapability.assumeCapability(capabilities);
		driver = assertionView.createDriver(capabilities);
	}
}
