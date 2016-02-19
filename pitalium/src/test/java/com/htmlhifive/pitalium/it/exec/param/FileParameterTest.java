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
package com.htmlhifive.pitalium.it.exec.param;

import static org.junit.Assert.*;

import java.net.URL;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.HttpCommandExecutor;

import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.config.EnvironmentConfig;
import com.htmlhifive.pitalium.core.config.ExecMode;
import com.htmlhifive.pitalium.core.config.FilePersisterConfig;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import com.htmlhifive.pitalium.core.config.TestAppConfig;
import com.htmlhifive.pitalium.core.config.WebDriverSessionLevel;
import com.htmlhifive.pitalium.core.selenium.PtlCapabilities;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileParameterTest extends PtlTestBase {

	private static WebDriver checkConfigTestDriver = null;

	/**
	 * 設定ファイルの内容が設定されているかのテスト<br>
	 * 設定値：com.htmlhifive.test.it.exec.param以下の各設定フォルダを参照.<br>
	 * 実行環境：FireFox<br>
	 * 期待結果：設定値で設定した内容を各設定クラスから取得できる.<br>
	 * 設定が反映されたことを確認できるものは、実際の挙動を確認する.
	 */
	@Test
	public void checkConfig() {
		driver.get(null);

		PtlTestConfig config = PtlTestConfig.getInstance();

		// 実行設定の内容のチェック
		EnvironmentConfig env = config.getEnvironment();
		TestAppConfig appConfig = config.getTestAppConfig();
		assertEquals(ExecMode.RUN_TEST, env.getExecMode()); //実行モード

		// hubのアドレスのチェック
		String EXPECTED_HUB_HOST = "localhost";
		int EXPECTED_HUB_POST = 4444;

		assertEquals(EXPECTED_HUB_HOST, env.getHubHost());
		assertEquals(EXPECTED_HUB_POST, env.getHubPort());

		// 実際のhubのアドレスを確認する
		URL server = ((HttpCommandExecutor) driver.getCommandExecutor()).getAddressOfRemoteServer();
		assertEquals(EXPECTED_HUB_HOST, server.getHost());
		assertEquals(EXPECTED_HUB_POST, server.getPort());

		// capabilityの内容のチェック
		PtlCapabilities cap = driver.getCapabilities();
		assertEquals("com\\htmlhifive\\pitalium\\it\\exec\\param\\capabilities_FileParameterTest.json",
				env.getCapabilitiesFilePath());
		assertEquals(Platform.WINDOWS, cap.getPlatform());
		assertEquals("WINDOWS", cap.getCapability("os"));
		assertEquals("firefox", cap.getBrowserName());

		// 実際のUAを取得して確認
		String userAgent = (String) driver.executeScript("return navigator.userAgent");
		assertTrue(userAgent.contains("Windows"));
		assertTrue(userAgent.contains("Firefox"));

		// driverのセッションレベル
		assertEquals(WebDriverSessionLevel.TEST_CLASS, env.getWebDriverSessionLevel());
		checkConfigTestDriver = driver; // 2つめのテストで同一であることを確認するため、プロパティで保持

		// persisterの内容のチェック
		// TODO: MrtPersisterConfigに変える
		FilePersisterConfig persisterConf = config.getPersisterConfig().getFile();
		String EXPECTED_FOLDER = "results_for_FileParameterTest";
		assertEquals(EXPECTED_FOLDER, persisterConf.getResultDirectory());
		//		assertTrue(new File(EXPECTED_FOLDER).exists()); // 指定したフォルダが生成されている.

		String EXPECTED_BASE_URL = "http://localhost:8080/dummyUrl";
		assertEquals(EXPECTED_BASE_URL, driver.getCurrentUrl()); // 空文字で開いたのでベースURLがそのまま開く

		// ウィンドウの設定のチェック
		long EXPECTED_WIDTH = 1200l;
		assertEquals(EXPECTED_WIDTH, appConfig.getWindowWidth());
		assertEquals(EXPECTED_WIDTH, driver.executeScript("return window.outerWidth"));

		long EXPECTED_HEIGHT = 980l;
		assertEquals(EXPECTED_HEIGHT, appConfig.getWindowHeight());
		assertEquals(EXPECTED_HEIGHT, driver.executeScript("return window.outerHeight"));
	}

	@Test
	public void checkWebDriverSessionConfig() {
		// WebDriverのセッションレベルの設定を確認
		// TEST_CLASSを指定しているので、1つめのテストとdriverが同じことを確認する
		assertEquals(checkConfigTestDriver, driver);
	}
}
