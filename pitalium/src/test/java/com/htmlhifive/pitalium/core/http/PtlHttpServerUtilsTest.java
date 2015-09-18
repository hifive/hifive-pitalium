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

package com.htmlhifive.pitalium.core.http;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.htmlhifive.pitalium.common.exception.PtlTimeoutException;
import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.config.HttpServerConfig;

public class PtlHttpServerUtilsTest extends PtlTestBase {

	private static final int PORT = 18080;

	@BeforeClass
	public static void startServer() throws Exception {
		PtlHttpServer.start();
	}

	@AfterClass
	public static void stopServer() throws Exception {
		PtlHttpServer.stop();
	}

	@Rule
	public ExpectedException expected = ExpectedException.none();

	HttpServerConfig config = HttpServerConfig.builder().port(PORT).build();

	/**
	 * Pitalium functionsを読み込む関数のテスト
	 */
	@Test
	public void loadPitaliumFunctions() throws Exception {
		driver.get(null);
		PtlHttpServerUtils.loadPitaliumFunctions(driver, config);

		String id = driver.executeJavaScript("return pitalium.capabilitiesId();");
		assertThat(id, is(String.valueOf(capabilities.getId())));
	}

	/**
	 * Pitalium functionsが読み込まれているかチェックする関数のテスト
	 */
	@Test
	public void isPitaliumFunctionsLoaded() throws Exception {
		driver.get(null);
		assertThat(PtlHttpServerUtils.isPitaliumFunctionsLoaded(driver), is(false));

		PtlHttpServerUtils.loadPitaliumFunctions(driver, config);
		assertThat(PtlHttpServerUtils.isPitaliumFunctionsLoaded(driver), is(true));
	}

	/**
	 * UnlockThread経由の非同期アクションを実行するテスト
	 */
	@Test
	public void awaitUnlockRequest() throws Exception {
		driver.get(null);
		PtlHttpServerUtils.loadPitaliumFunctions(driver, config);

		final StringBuffer sb = new StringBuffer();
		sb.append("begin ");

		PtlHttpServerUtils.awaitUnlockRequest(driver, 30000L, new Runnable() {
			@Override
			public void run() {
				sb.append("before_action ");
				driver.executeJavaScript("setTimeout(function () {pitalium.sendUnlockRequest();}, 3000);");
				sb.append("after_execute ");
			}
		}, new Runnable() {
			@Override
			public void run() {
				sb.append("await_action ");
			}
		});

		sb.append("end ");

		final String expected = "begin before_action after_execute await_action end ";
		assertThat(sb.toString(), is(expected));
	}

	/**
	 * UnlockThread経由の非同期アクションがタイムアウトするテスト
	 */
	@Test
	public void awaitUnlockRequest_timeout() throws Exception {
		driver.get(null);
		PtlHttpServerUtils.loadPitaliumFunctions(driver, config);

		expected.expect(PtlTimeoutException.class);

		PtlHttpServerUtils.awaitUnlockRequest(driver, TimeUnit.SECONDS.toMillis(1L), new Runnable() {
			@Override
			public void run() {
				driver.executeJavaScript("setTimeout(function () {pitalium.sendUnlockRequest();}, 3000);");
			}
		}, new Runnable() {
			@Override
			public void run() {
				fail();
			}
		});

		fail();
	}

	/**
	 * クリックして非同期アクションを実行するテスト
	 */
	@Test
	public void clickAndAwaitUnlockRequest() throws Exception {
		driver.get("http://localhost:18080/res/PtlHttpServerUtilsTest.html");
		PtlHttpServerUtils.loadPitaliumFunctions(driver, config);

		PtlHttpServerUtils.clickAndAwaitUnlockRequest(driver, driver.findElementById("click"));
		assertTrue(true);
	}

	/**
	 * サブミットして非同期アクションを実行するテスト
	 */
	@Test
	public void submitAndAwaitUnlockRequest() throws Exception {
		driver.get("http://localhost:18080/res/PtlHttpServerUtilsTest.html");
		PtlHttpServerUtils.loadPitaliumFunctions(driver, config);

		PtlHttpServerUtils.submitAndAwaitUnlockRequest(driver, driver.findElementById("submit"));
		assertTrue(true);
	}

	/**
	 * 文字入力をして非同期アクションを実行するテスト
	 */
	@Test
	public void sendKeysAndAwaitUnlockRequest() throws Exception {
		driver.get("http://localhost:18080/res/PtlHttpServerUtilsTest.html");
		PtlHttpServerUtils.loadPitaliumFunctions(driver, config);

		PtlHttpServerUtils.sendKeysAndAwaitUnlockRequest(driver, driver.findElementById("sendKeys"), "a");
		assertTrue(true);
	}

	/**
	 * スクリプト実行をして非同期アクションを実行するテスト
	 */
	@Test
	public void executeScriptAndAwaitUnlockRequest() throws Exception {
		driver.get("http://localhost:18080/res/PtlHttpServerUtilsTest.html");
		PtlHttpServerUtils.loadPitaliumFunctions(driver, config);

		PtlHttpServerUtils.executeScriptAndAwaitUnlockRequest(driver,
				"setTimeout(function(){pitalium.sendUnlockRequest();},3000);");
		assertTrue(true);
	}

}