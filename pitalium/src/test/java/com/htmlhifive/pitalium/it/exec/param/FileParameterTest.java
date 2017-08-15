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
package com.htmlhifive.pitalium.it.exec.param;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.HttpCommandExecutor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.htmlhifive.pitalium.common.exception.JSONException;
import com.htmlhifive.pitalium.common.exception.TestRuntimeException;
import com.htmlhifive.pitalium.common.util.JSONUtils;
import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.config.EnvironmentConfig;
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
	public void checkConfig() throws Exception {
		Map<String, Object> sourceEnvironmentConfig = readEnvironmentConfig("environmentConfig.json",
				EnvironmentConfig.class);

		driver.get(null);

		PtlTestConfig config = PtlTestConfig.getInstance();

		// 実行設定の内容のチェック
		EnvironmentConfig env = config.getEnvironment();
		TestAppConfig appConfig = config.getTestAppConfig();
		assertEquals(sourceEnvironmentConfig.get("execMode"), env.getExecMode().toString()); //実行モード

		// hubのアドレスのチェック
		String EXPECTED_HUB_HOST = sourceEnvironmentConfig.get("hubHost").toString();
		int EXPECTED_HUB_POST = sourceEnvironmentConfig.containsKey("hubPost") ? Integer
				.parseInt(sourceEnvironmentConfig.get("hubPost").toString()) : 4444;

		assertEquals(EXPECTED_HUB_HOST, env.getHubHost());
		assertEquals(EXPECTED_HUB_POST, env.getHubPort());

		// 実際のhubのアドレスを確認する
		URL server = ((HttpCommandExecutor) driver.getCommandExecutor()).getAddressOfRemoteServer();
		assertEquals(EXPECTED_HUB_HOST, server.getHost());
		assertEquals(EXPECTED_HUB_POST, server.getPort());

		// capabilityの内容のチェック
		PtlCapabilities cap = driver.getCapabilities();
		String EXPECTED_CAPABILITIES_FILE_PATH = sourceEnvironmentConfig.containsKey("capabilitiesFilePath") ? sourceEnvironmentConfig
				.get("capabilitiesFilePath").toString() : "capabilities.json";
		List<Map<String, Object>> sourceCapabilities = readCapabilitiesFromFileOrResources(EXPECTED_CAPABILITIES_FILE_PATH);
		assertEquals(EXPECTED_CAPABILITIES_FILE_PATH, env.getCapabilitiesFilePath());
		boolean found = false;
		String os = null;
		String browserName = null;
		for (int i = 0; i < sourceCapabilities.size(); i++) {
			if (sourceCapabilities.get(i).get("platform").equals(cap.getPlatform().toString())
					&& sourceCapabilities.get(i).get("os").equals(cap.getCapability("os").toString())
					&& sourceCapabilities.get(i).get("browserName").equals(cap.getCapability("browserName").toString())) {
				os = cap.getCapability("os").toString().toLowerCase();
				browserName = cap.getCapability("browserName").toString().toLowerCase();
				found = true;
				break;
			}
		}
		assertTrue(found);

		// 実際のUAを取得して確認
		String userAgent = driver.executeScript("return navigator.userAgent").toString().toLowerCase();
		assertTrue(userAgent.contains(os));
		assertTrue(userAgent.contains(browserName));

		// driverのセッションレベル
		WebDriverSessionLevel webDriverSessionLevel = WebDriverSessionLevel.TEST_CASE;
		if (sourceEnvironmentConfig.containsKey("webDriverSessionLevel")) {
			switch (sourceEnvironmentConfig.get("webDriverSessionLevel").toString()) {
				case "TEST_CASE":
					webDriverSessionLevel = WebDriverSessionLevel.TEST_CASE;
					break;

				case "TEST_CLASS":
					webDriverSessionLevel = WebDriverSessionLevel.TEST_CLASS;
					break;

				default:
					webDriverSessionLevel = WebDriverSessionLevel.TEST_CASE;
					break;
			}
		}
		assertEquals(webDriverSessionLevel, env.getWebDriverSessionLevel());
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
	public void checkWebDriverSessionConfig() throws Exception {
		Map<String, Object> sourceEnvironmentConfig = readEnvironmentConfig("environmentConfig.json",
				EnvironmentConfig.class);
		if (sourceEnvironmentConfig.containsKey("webDriverSessionLevel")) {
			switch (sourceEnvironmentConfig.get("webDriverSessionLevel").toString()) {
			// WebDriverのセッションレベルの設定を確認
			// TEST_CLASSを指定しているので、1つめのテストとdriverが同じことを確認する
				case "TEST_CLASS":
					assertEquals(checkConfigTestDriver, driver);
					break;

				case "GLOBAL":
					assertEquals(checkConfigTestDriver, driver);
					break;

				default:
					assertTrue(checkConfigTestDriver != driver);
					break;
			}
		}
	}

	/**
	 * resourceをファイルまたはリソースファイルから読み込みます。
	 * 
	 * @param filePath ファイルパス
	 * @return 読み込んだCapabilities
	 * @throws IOException
	 */
	private static Map<String, Object> readEnvironmentConfig(String filePath, Class className) throws IOException {

		TypeReference<Map<String, Object>> reference = new TypeReference<Map<String, Object>>() {
		};
		try {
			// Read from file
			return JSONUtils.readValue(new File(filePath), reference);
		} catch (JSONException e) {
			// Read from resources
			InputStream in = null;
			try {
				in = className.getClassLoader().getResourceAsStream(filePath);
				return JSONUtils.readValue(in, reference);
			} catch (Exception e1) {
				throw new TestRuntimeException("Failed to load capabilities", e1);
			} finally {
				if (in != null) {
					in.close();
				}
			}
		}
	}

	/**
	 * Capabilitiesをファイルまたはリソースファイルから読み込みます。
	 * 
	 * @param filePath ファイルパス
	 * @return 読み込んだCapabilities
	 */
	private List<Map<String, Object>> readCapabilitiesFromFileOrResources(String filePath) throws Exception {
		TypeReference<List<Map<String, Object>>> reference = new TypeReference<List<Map<String, Object>>>() {
		};
		try {
			// Read from file
			return JSONUtils.readValue(new File(filePath), reference);
		} catch (JSONException e) {
			// Read from resources
			InputStream in = null;
			try {
				in = PtlCapabilities.class.getClassLoader().getResourceAsStream(filePath);
				return JSONUtils.readValue(in, reference);
			} catch (Exception e1) {
				throw new TestRuntimeException("Failed to load capabilities", e1);
			} finally {
				if (in != null) {
					in.close();
				}
			}
		}
	}

}
