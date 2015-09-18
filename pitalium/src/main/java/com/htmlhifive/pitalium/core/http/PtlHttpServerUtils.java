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

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.htmlhifive.pitalium.common.exception.TestRuntimeException;
import com.htmlhifive.pitalium.common.util.TextReplacer;
import com.htmlhifive.pitalium.core.config.HttpServerConfig;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import com.htmlhifive.pitalium.core.http.handler.UnlockThreadRequestHandler;
import com.htmlhifive.pitalium.core.rules.AssertionView;
import com.htmlhifive.pitalium.core.selenium.PtlWebDriver;

/**
 * TODO JavaDoc
 */
public class PtlHttpServerUtils {

	/**
	 * HTTPサーバーを開始します。
	 */
	static void startHttpServer() {
		PtlHttpServer.start();
	}

	/**
	 * Pitalium HTTP Serverで利用するスクリプトファイルをブラウザにロードします。
	 *
	 * @param driver 対象ブラウザのWebDriver
	 */
	public static void loadPitaliumFunctions(PtlWebDriver driver) {
		loadPitaliumFunctions(driver, PtlTestConfig.getInstance().getHttpServerConfig());
	}

	/**
	 * Pitalium HTTP Serverで利用するスクリプトファイルをブラウザにロードします。
	 *
	 * @param driver 対象ブラウザのWebDriver
	 * @param config HTTP Serverの設定
	 */
	static void loadPitaliumFunctions(final PtlWebDriver driver, HttpServerConfig config) {
		startHttpServer();

		URL resource = PtlHttpServerUtils.class.getResource("loadPitaliumFunctions.js");
		String s;
		try {
			s = IOUtils.toString(resource);
		} catch (IOException e) {
			throw new TestRuntimeException(e);
		}

		Map<String, ?> params = ImmutableMap.of("host", config.getHostname(), "port", config.getPort(),
				"capabilitiesId", driver.getCapabilities().getId());
		String script = TextReplacer.replace(s, params);

		driver.executeJavaScript(script);

		// Pitalium JS functionsが確実に読み込まれるまで待機
		new WebDriverWait(driver, 30L).until(new Predicate<WebDriver>() {
			@Override
			public boolean apply(WebDriver d) {
				return isPitaliumFunctionsLoaded(driver);
			}
		});
	}

	/**
	 * Pitalium HTTP Serverで利用するスクリプトファイルがブラウザにロードされているかどうか取得します。
	 *
	 * @param driver 対象ブラウザのWebDriver
	 * @return スクリプトファイルがブラウザにロードされている場合true、ロードされていない場合false
	 */
	public static boolean isPitaliumFunctionsLoaded(PtlWebDriver driver) {
		return driver.executeJavaScript("return window.pitalium ? true : false;");
	}

	public static void awaitAssertView(PtlWebDriver driver, long timeout, Runnable asyncAction, AssertionView assertion,
			String screenshotId) {
		awaitUnlockRequest(driver, timeout, asyncAction);
		assertion.assertView(screenshotId);
	}

	/**
	 * Webブラウザからスレッドロック解除の通知があるまで指定の時間待機します。<br />
	 * &quot;/unlockThread&quot;へXmlHTTPRequestを送信する、または&quot;pitalium.sendUnlockRequest()&quot;を実行することで待機を解除します。
	 *
	 * @param driver 対象ブラウザのWebDriver
	 * @param timeout 待機タイムアウト（ミリ秒）
	 * @param asyncAction 待機中に非同期で行う動作
	 */
	public static void awaitUnlockRequest(PtlWebDriver driver, long timeout, Runnable asyncAction) {
		awaitUnlockRequest(driver, timeout, asyncAction, null);
	}

	/**
	 * Webブラウザからスレッドロック解除の通知があるまで指定の時間待機します。<br />
	 * &quot;/unlockThread&quot;へXmlHTTPRequestを送信する、または&quot;pitalium.sendUnlockRequest()&quot;を実行することで待機を解除します。
	 *
	 * @param driver 対象ブラウザのWebDriver
	 * @param timeout 待機タイムアウト（ミリ秒）
	 * @param asyncAction 待機中に非同期で行う動作
	 * @param awaitAction 待機後に行う動作
	 */
	public static void awaitUnlockRequest(PtlWebDriver driver, long timeout, Runnable asyncAction,
			Runnable awaitAction) {
		awaitRequest(driver.getCapabilities().getId(), UnlockThreadRequestHandler.MONITOR_TYPE, timeout, asyncAction);

		if (awaitAction != null) {
			awaitAction.run();
		}
	}

	public static void awaitRequest(int id, String type, long timeout, Runnable asyncAction) {
		startHttpServer();

		Executors.newSingleThreadExecutor().submit(asyncAction);
		PtlHttpObjectMonitor.getMonitor(id, type).lock(timeout);
	}

	private PtlHttpServerUtils() {
	}

}
