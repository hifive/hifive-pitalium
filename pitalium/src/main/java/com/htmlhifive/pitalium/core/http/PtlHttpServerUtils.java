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

import com.google.common.collect.ImmutableMap;
import com.htmlhifive.pitalium.common.exception.TestRuntimeException;
import com.htmlhifive.pitalium.common.util.TextReplacer;
import com.htmlhifive.pitalium.core.config.HttpServerConfig;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import com.htmlhifive.pitalium.core.http.handler.TakeScreenshotHandler;
import com.htmlhifive.pitalium.core.rules.AssertionView;
import com.htmlhifive.pitalium.core.selenium.PtlWebDriver;

/**
 * TODO JavaDoc
 */
public class PtlHttpServerUtils {

	//@formatter:off
	private static final String SCRIPT_LOAD_PITALIUM_FUNCTIONS =
			"if (window.pitalium) {" +
					"  return;" +
					"}" +
					"" +
					"var el = document.createElement('script');" +
					"el.type = 'text/javascript';" +
					"el.src = '//${host}:${port}/res/pitalium-functions.js';" +
					"el.onload = function () {" +
					"  pitalium.capabilitiesId('${capabilitiesId}');" +
					"  pitalium.remoteHostname('${host}');" +
					"  pitalium.remotePort('${port}');" +
					"};" +
					"document.head.appendChild(el);";
	//@formatter:on

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
	}

	public static boolean isPitaliumFunctionsLoaded(PtlWebDriver driver) {
		return driver.executeJavaScript("return window.pitalium ? true : false;");
	}

	public static void requestTakeScreenshot(PtlWebDriver driver, final AssertionView view, final String screenshotId,
			long timeout, TakeScreenshotAction beforeAction) throws InterruptedException {
		requestTakeScreenshot(driver, timeout, beforeAction, new Runnable() {
			@Override
			public void run() {
				view.assertView(screenshotId);
			}
		});
	}

	public static void requestTakeScreenshot(final PtlWebDriver driver, long timeout,
			final TakeScreenshotAction beforeAction, Runnable screenshotAction) {
		startHttpServer();

		awaitRequest(driver.getCapabilities().getId(), TakeScreenshotHandler.MONITOR_TYPE, timeout, beforeAction);
		screenshotAction.run();
	}

	public static void awaitRequest(int id, String type, long timeout, Runnable action) {
		Executors.newSingleThreadExecutor().submit(action);
		PtlHttpObjectMonitor.getMonitor(id, type).lock(timeout);
	}

	private PtlHttpServerUtils() {
	}

	public static abstract class TakeScreenshotAction implements Runnable {

		private final PtlWebDriver driver;

		public TakeScreenshotAction(PtlWebDriver driver) {
			this.driver = driver;
		}

		public final void requestTakeScreenshot() {
			driver.executeJavaScript("pitalium.requestTakeScreenshot();");
		}

	}
}
