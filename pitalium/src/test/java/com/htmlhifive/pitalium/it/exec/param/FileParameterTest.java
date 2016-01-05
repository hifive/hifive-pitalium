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
package com.htmlhifive.pitalium.it.exec.param;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;

import org.junit.Test;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.HttpCommandExecutor;

import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.config.EnvironmentConfig;
import com.htmlhifive.pitalium.core.config.ExecMode;
import com.htmlhifive.pitalium.core.config.FilePersisterConfig;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import com.htmlhifive.pitalium.core.config.TestAppConfig;
import com.htmlhifive.pitalium.core.selenium.PtlCapabilities;

public class FileParameterTest extends PtlTestBase {

	/**
	 * 設定ファイルの内容が設定されているかのテスト<br>
	 * 設定値：com.htmlhifive.test.exec.param以下の各設定フォルダを参照.<br>
	 * 実行環境：FireFox<br>
	 * 期待結果：設定値で設定した内容を各設定クラスから取得できる.<br>
	 * 　　　　　　　設定が反映されたことを確認できるものは、実際の挙動を確認する.
	 */
	@Test
	public void checkConfig() {
		driver.get(null);

		PtlTestConfig config = PtlTestConfig.getInstance();

		// 実行設定の内容のチェック
		// TODO: MrtRunnerConfigに変える
		EnvironmentConfig env = config.getEnvironment();
		TestAppConfig appConfig = config.getTestAppConfig();
		// TODO: EXEC_TESTに変える
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
		assertEquals("com\\htmlhifive\\test\\exec\\cap\\capabilities_FileParameterTest.json",
				env.getCapabilitiesFilePath());
		assertEquals(Platform.WINDOWS, cap.getPlatform());
		assertEquals("WINDOWS", cap.getCapability("os"));
		assertEquals("firefox", cap.getBrowserName());

		//		assertEquals("WINDOWS", driver.getRemoteStatus().getOsName());
		assertTrue(((String) driver.executeScript("return navigator.userAgent")).contains("FireFox"));

		// persisterの内容のチェック
		// TODO: MrtPersisterConfigに変える
		FilePersisterConfig persisterConf = config.getPersisterConfig().getFile();
		String EXPECTED_FOLDER = "results_for_FileParameterTest";
		assertEquals(EXPECTED_FOLDER, persisterConf.getResultDirectory());
		assertTrue(new File(EXPECTED_FOLDER).exists()); // 指定したフォルダが生成されている.

		// ページ情報の内容のチェック
		// TODO: MrtPageConfigに変える
		//		MrtPageConfig pageConf = config.getPageConfig();

		// TODO: v1.0の対象外になるかも？
		String EXPECTED_BASE_URL = PtlTestConfig.getInstance().getTestAppConfig().getBaseUrl();
		//		assertEquals(EXPECTED_BASE_URL, pageConf.getBaseUrl());
		assertEquals(EXPECTED_BASE_URL, driver.getCurrentUrl()); // 空文字で開いたのでベースURLがそのまま開く

		// ウィンドウの設定のチェック
		// TODO environment => pageに設定が移動しているので要リソース修正
		int EXPECTED_WIDTH = 980;
		assertEquals(EXPECTED_WIDTH, appConfig.getWindowWidth());
		assertEquals(EXPECTED_WIDTH, driver.getWindowWidth());

		int EXPECTED_HEIGHT = 1200;
		assertEquals(EXPECTED_HEIGHT, appConfig.getWindowHeight());
		assertEquals(EXPECTED_HEIGHT, driver.getWindowHeight());
	}
}
