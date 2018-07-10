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
package com.htmlhifive.pitalium.core.selenium;

import static org.openqa.selenium.remote.DriverCommand.NEW_SESSION;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Platform;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.profiler.HttpProfilerLogEntry;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.Dialect;
import org.openqa.selenium.remote.ErrorCodes;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.SessionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.htmlhifive.pitalium.common.exception.TestRuntimeException;
import com.htmlhifive.pitalium.core.config.EnvironmentConfig;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import com.htmlhifive.pitalium.core.config.TestAppConfig;
import com.htmlhifive.pitalium.core.config.WebDriverSessionLevel;

/**
 * 各ブラウザに対応するWebDriverを生成するファクトリクラス
 */
public abstract class PtlWebDriverFactory {

	private static final Logger LOG = LoggerFactory.getLogger(PtlWebDriverFactory.class);

	private final EnvironmentConfig environmentConfig;
	private final TestAppConfig testAppConfig;
	private final PtlCapabilities capabilities;

	/**
	 * コンストラクタ
	 *
	 * @param environmentConfig 環境設定
	 * @param testAppConfig テスト対象アプリケーション設定
	 * @param capabilities Capability
	 */
	protected PtlWebDriverFactory(EnvironmentConfig environmentConfig, TestAppConfig testAppConfig,
			PtlCapabilities capabilities) {
		this.environmentConfig = environmentConfig;
		this.testAppConfig = testAppConfig;
		this.capabilities = capabilities;
	}

	/**
	 * ブラウザに対応する{@link PtlWebDriverFactory}のインスタンスを取得します。
	 *
	 * @param capabilities Capability（ブラウザの情報を含む）
	 * @return {@link PtlWebDriverFactory}のインスタンス
	 */
	public static PtlWebDriverFactory getInstance(PtlCapabilities capabilities) {
		PtlTestConfig config = PtlTestConfig.getInstance();
		EnvironmentConfig environmentConfig = config.getEnvironment();
		TestAppConfig testAppConfig = config.getTestAppConfig();

		String browserName = Strings.nullToEmpty(capabilities.getBrowserName()).toLowerCase(Locale.ENGLISH);

		// IE
		if ("internet explorer".equals(browserName)) {
			String version = Strings.nullToEmpty(capabilities.getVersion());
			if (version.startsWith("7")) {
				return new PtlInternetExplorer7DriverFactory(environmentConfig, testAppConfig, capabilities);
			}
			if (version.startsWith("8")) {
				return new PtlInternetExplorer8DriverFactory(environmentConfig, testAppConfig, capabilities);
			}

			return new PtlInternetExplorerDriverFactory(environmentConfig, testAppConfig, capabilities);
		}

		// Edge
		if ("microsoftedge".equals(browserName)) {
			return new PtlEdgeDriverFactory(environmentConfig, testAppConfig, capabilities);
		}

		// Android
		if (capabilities.getPlatform() == Platform.ANDROID) {
			// Selendroid (Android 2.3+)
			String automationName = (String) capabilities.getCapability("automationName");
			if (automationName != null && "selendroid".equalsIgnoreCase(automationName)) {
				return new PtlSelendroidDriverFactory(environmentConfig, testAppConfig, capabilities);
			}

			// Default (Android 4.2+)
			return new PtlAndroidDriverFactory(environmentConfig, testAppConfig, capabilities);
		}

		// Chrome
		if ("chrome".equals(browserName)) {
			return new PtlChromeWebDriverFactory(environmentConfig, testAppConfig, capabilities);
		}

		// Safari
		if ("safari".equals(browserName)) {
			// MacOSX
			if (capabilities.getPlatform() == Platform.MAC) {
				return new PtlSafariDriverFactory(environmentConfig, testAppConfig, capabilities);
			}

			String deviceName = capabilities.getDeviceName();
			if (Strings.isNullOrEmpty(deviceName)) {
				throw new TestRuntimeException("\"deviceName\" is required for iOS devices");
			}
			if (deviceName.contains("iPad")) {
				return new PtlIPadDriverFactory(environmentConfig, testAppConfig, capabilities);
			}
			if (deviceName.contains("iPhone")) {
				return new PtlIPhoneDriverFactory(environmentConfig, testAppConfig, capabilities);
			}

			throw new TestRuntimeException("Unknown deviceName \"" + deviceName + "\"");
		}

		// Other
		return new PtlFirefoxWebDriverFactory(environmentConfig, testAppConfig, capabilities);
	}

	private Map<String, StoredSeleniumSession> readSessionsFromFile(File file) throws TestRuntimeException {
		// セッション情報取得
		JsonReader reader = null;
		Map<String, StoredSeleniumSession> map = new LinkedTreeMap<>();
		try {
			reader = new JsonReader(new FileReader(file));
			Gson gson = new Gson();

			JsonObject jObj = gson.fromJson(reader, JsonObject.class);
			for (Map.Entry<String, JsonElement> entry : jObj.entrySet()) {
				String key = entry.getKey();
				JsonObject sessionJObj = entry.getValue().getAsJsonObject();

				String sessionId = sessionJObj.get("sessionId").getAsString();
				Map<String, Object> rawCapabilities = new LinkedTreeMap<>();
				JsonObject rawCapJObj = sessionJObj.get("rawCapabilities").getAsJsonObject();
				for (Map.Entry<String, JsonElement> capEntry : rawCapJObj.entrySet()) {
					JsonElement elem = capEntry.getValue();
					if (elem.isJsonPrimitive()) {
						rawCapabilities.put(capEntry.getKey(), capEntry.getValue().getAsString());
					} else {
						rawCapabilities.put(capEntry.getKey(),
								gson.fromJson(gson.toJson(elem.getAsJsonObject()), LinkedTreeMap.class));
					}
				}
				String dialect = sessionJObj.get("dialect").getAsString();
				map.put(key, new StoredSeleniumSession(sessionId, rawCapabilities, dialect));
			}
		} catch (IOException e) {
			throw new TestRuntimeException(file.getName() + "を開けません。", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new TestRuntimeException(file.getName() + "を閉じられません。", e);
				}
			}
		}

		return map;
	}

	private String getKey(Capabilities cap) {
		return cap.toString();
	}

	private boolean isSessionExistsOnServer(String sessionId) throws TestRuntimeException {
		HttpURLConnection con;
		// HTTP Status-Codeではありえない値
		int responseCode = -1;
		try {
			// TODO: 2018/7/9現在 /sessions コマンドが実装されていないため代わりに /session/{sessionId}/url コマンドでsessionIdが存在することを確認している
			URL myUrl = new URL(getGridHubURL() + "/session/" + sessionId + "/url");
			try {
				con = (HttpURLConnection) myUrl.openConnection();
				con.setRequestMethod("GET");
				responseCode = con.getResponseCode();
			} catch (IOException e) {
				// NOTE: 接続に失敗する場合はセッションが存在しないとみなす
				return false;
			}
		} catch (MalformedURLException e) {
			throw new TestRuntimeException("不正なURLです。", e);
		}

		// HTTP Status-Codeが200ならば指定したsessionが存在するとみなす
		if (responseCode == HttpURLConnection.HTTP_OK) {
			// sessionの接続確認
			BufferedReader in = null;
			StringBuilder content = null;
			try {
				InputStreamReader reponse = new InputStreamReader(con.getInputStream());
				in = new BufferedReader(reponse);
				content = new StringBuilder();
				String line;
				while ((line = in.readLine()) != null) {
					content.append(line);
					content.append(System.lineSeparator());
				}
			} catch (IOException e) {
				throw new TestRuntimeException(e);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						LOG.debug("{}", e.toString());
					}
				}
			}

			// 返ってきた文字列をJSONにパース
			JsonObject sessions;
			try {
				JsonParser parser = new JsonParser();
				sessions = (JsonObject) parser.parse(content.toString());
			} catch (JsonSyntaxException e) {
				throw new TestRuntimeException("サーバからの戻り値が不正です", e);
			}

			if (sessions.has("status")) {
				// JSONにstatusがある場合はstatusの値によってsessionに接続できるかを判定する
				int status = sessions.get("status").getAsInt();
				return status == 0;
			}

			// JSONにstatusがない場合はそのままtrue
			return true;
		}

		// HTTP Status-Codeが200でない場合は指定したsessionが存在しない
		return false;
	}

	private void writeSessionsToFile(File file, Map<String, StoredSeleniumSession> sessions)
			throws TestRuntimeException {

		//　セッション情報出力
		JsonWriter writer = null;
		try {
			writer = new JsonWriter(new FileWriter(file));
			writer.setIndent("    ");
			Gson gson = new Gson();
			gson.toJson(sessions, LinkedTreeMap.class, writer);
		} catch (IOException e) {
			throw new TestRuntimeException(file.getName() + "を開けません。", e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					throw new TestRuntimeException(file.getName() + "を閉じられません。", e);
				}
			}
		}
	}

	/**
	 * 初期設定（baseUrl、タイムアウト時間、ウィンドウサイズ）済のWebDriverを取得します。
	 *
	 * @return WebDriver
	 */
	public PtlWebDriver getDriver() throws TestRuntimeException {
		synchronized (PtlWebDriverFactory.class) {
			LOG.debug("[Get WebDriver] create new session.");

			PtlWebDriver driver = null;

			WebDriverSessionLevel sessionLevel = PtlTestConfig.getInstance().getEnvironment()
					.getWebDriverSessionLevel();
			if (sessionLevel != WebDriverSessionLevel.PERSISTED) {
				driver = createWebDriver(getGridHubURL());
			} else {
				Map<String, StoredSeleniumSession> sessions = new LinkedTreeMap<>();
				File file = new File("src/main/resources/session.json");

				if (file.exists()) {
					sessions = readSessionsFromFile(file);
					StoredSeleniumSession session = sessions.get(getKey(capabilities));

					// セッションを再利用
					if (session != null) {
						if (isSessionExistsOnServer(session.getSessionId())) {
							LOG.debug("reuse ({})", session.getSessionId());
							driver = createReusableWebDriver(createCommandExecutorFromSession(
									new SessionId(session.getSessionId()), getGridHubURL(), new DesiredCapabilities(),
									session.getRawCapabilities(), session.getDialect()));
						} else {
							// 接続できないセッションがある場合、driverを停止してdriverを新規作成する
							driver = createReusableWebDriver(createCommandExecutorFromSession(
									new SessionId(session.getSessionId()), getGridHubURL(), new DesiredCapabilities(),
									session.getRawCapabilities(), session.getDialect()));
							// driverが終了している状態でquit()すると例外が発生する
							// ブラウザが開いていてdriverが終了していてセッションに接続できない場合にもdriverを新規作成したいので例外を握りつぶして処理を続ける
							try {
								driver.quit();
								LOG.debug("quit ({})", session.getSessionId());
							} catch (WebDriverException e) {
								LOG.debug("quit ({}) {}", session.getSessionId(), e.toString());
							}
							driver = null;
						}
					}
				}

				if (driver == null) {
					CustomHttpCommandExecutor executor = new CustomHttpCommandExecutor(getGridHubURL());
					driver = createReusableWebDriver(executor);

					// 利用可能なセッションが無い場合、
					// 永続化のためにReusableDriverを使ってインスタンス化する
					StoredSeleniumSession s = new StoredSeleniumSession(driver.getSessionId().toString(),
							(Map<String, Object>) driver.getRawCapabilities().asMap(),
							executor.getDialect().toString());
					sessions.put(getKey(capabilities), s);
					writeSessionsToFile(file, sessions);
				}
			}

			// Pitalium独自の設定の追加

			driver.setEnvironmentConfig(environmentConfig);
			driver.setBaseUrl(testAppConfig.getBaseUrl());
			driver.manage().timeouts().implicitlyWait(environmentConfig.getMaxDriverWait(), TimeUnit.SECONDS)
					.setScriptTimeout(environmentConfig.getScriptTimeout(), TimeUnit.SECONDS);
			if (!isMobile()) {
				driver.manage().window()
						.setSize(new Dimension(testAppConfig.getWindowWidth(), testAppConfig.getWindowHeight()));
			}

			LOG.debug("[Get WebDriver] new session created. ({})", driver);
			return driver;
		}

	}

	/**
	 * Selenium Grid HubのURLを取得します。
	 *
	 * @return HubのURL
	 */
	protected URL getGridHubURL() {
		try {
			return new URL("http", environmentConfig.getHubHost(), environmentConfig.getHubPort(), "/wd/hub");
		} catch (MalformedURLException e) {
			throw new TestRuntimeException(e);
		}
	}

	protected CommandExecutor createCommandExecutorFromSession(final SessionId sessionId, URL command_executor,
			Capabilities capabilities, Map<String, Object> rawCapabilities, String dialectValue) {

		CommandExecutor executor = new CustomHttpCommandExecutor(command_executor) {
			@Override
			public Response execute(Command command) throws IOException {
				Response response = null;
				if (NEW_SESSION.equals(command.getName())) {
					if (commandCodec != null) {
						throw new SessionNotCreatedException("Session already exists");
					}
					Dialect dialect = Dialect.valueOf(dialectValue);
					commandCodec = dialect.getCommandCodec();
					for (Map.Entry<String, CommandInfo> entry : additionalCommands.entrySet()) {
						defineCommand(entry.getKey(), entry.getValue());
					}
					responseCodec = dialect.getResponseCodec();
					log(LogType.PROFILER, new HttpProfilerLogEntry(command.getName(), false));

					response = new Response();
					response.setSessionId(sessionId.toString());
					response.setStatus(ErrorCodes.SUCCESS);
					response.setState(ErrorCodes.SUCCESS_STRING);
					response.setValue(rawCapabilities);
				} else {
					response = super.execute(command);
				}
				return response;
			}
		};
		return executor;
	}

	/**
	 * モバイル端末用のdriverか否かを返します。
	 *
	 * @return モバイル端末用driverならtrue、そうでなければfalse
	 */
	abstract boolean isMobile();

	/**
	 * WebDriverを生成します。
	 *
	 * @param url WebDriverServerのURL
	 * @return 生成したWebDriverのインスタンス
	 */
	public abstract PtlWebDriver createWebDriver(URL url);

	/**
	 * WebDriverを生成します。
	 *
	 * @param executor CommandExecutor
	 * @return 生成したWebDriverのインスタンス
	 */
	public abstract PtlWebDriver createReusableWebDriver(CommandExecutor executor);

	/**
	 * テスト実行用の共通設定を取得します。
	 *
	 * @return 共通設定
	 */
	public EnvironmentConfig getEnvironmentConfig() {
		return environmentConfig;
	}

	/**
	 * Capabilityを取得します。
	 *
	 * @return Capability
	 */
	public PtlCapabilities getCapabilities() {
		return capabilities;
	}

	/**
	 * Firefox用WebDriverを生成するファクトリクラス
	 */
	static class PtlFirefoxWebDriverFactory extends PtlWebDriverFactory {

		/**
		 * コンストラクタ
		 *
		 * @param environmentConfig 環境設定
		 * @param testAppConfig テスト対象アプリケーション設定
		 * @param capabilities Capability
		 */
		public PtlFirefoxWebDriverFactory(EnvironmentConfig environmentConfig, TestAppConfig testAppConfig,
				PtlCapabilities capabilities) {
			super(environmentConfig, testAppConfig, capabilities);
		}

		@Override
		boolean isMobile() {
			return false;
		}

		@Override
		public PtlWebDriver createWebDriver(URL url) {
			return new PtlFirefoxDriver(url, getCapabilities());
		}

		@Override
		public PtlWebDriver createReusableWebDriver(CommandExecutor executor) {
			return new PtlFirefoxDriver(executor, getCapabilities());
		}
	}

	/**
	 * Google chrome用WebDriverを生成するファクトリクラス
	 */
	static class PtlChromeWebDriverFactory extends PtlWebDriverFactory {

		/**
		 * コンストラクタ
		 *
		 * @param environmentConfig 環境設定
		 * @param testAppConfig テスト対象アプリケーション設定
		 * @param capabilities Capability
		 */
		public PtlChromeWebDriverFactory(EnvironmentConfig environmentConfig, TestAppConfig testAppConfig,
				PtlCapabilities capabilities) {
			super(environmentConfig, testAppConfig, capabilities);
		}

		@Override
		boolean isMobile() {
			return false;
		}

		@Override
		public PtlWebDriver createWebDriver(URL url) {
			return new PtlChromeDriver(url, getCapabilities());
		}

		@Override
		public PtlWebDriver createReusableWebDriver(CommandExecutor executor) {
			return new PtlChromeDriver(executor, getCapabilities());
		}
	}

	/**
	 * Internet Explorer用WebDriverを生成するファクトリクラス
	 */
	static class PtlInternetExplorerDriverFactory extends PtlWebDriverFactory {

		/**
		 * デフォルトのクロム幅（px）
		 */
		static final int DEFAULT_CHROME_WIDTH = 16;

		/**
		 * コンストラクタ
		 *
		 * @param environmentConfig 環境設定
		 * @param testAppConfig テスト対象アプリケーション設定
		 * @param capabilities Capability
		 */
		public PtlInternetExplorerDriverFactory(EnvironmentConfig environmentConfig, TestAppConfig testAppConfig,
				PtlCapabilities capabilities) {
			super(environmentConfig, testAppConfig, capabilities);

			if (capabilities.getCapability("chromeWidth") == null) {
				capabilities.setCapability("chromeWidth", DEFAULT_CHROME_WIDTH);
			}
		}

		@Override
		boolean isMobile() {
			return false;
		}

		@Override
		public PtlWebDriver createWebDriver(URL url) {
			return new PtlInternetExplorerDriver(url, getCapabilities());
		}

		@Override
		public PtlWebDriver createReusableWebDriver(CommandExecutor executor) {
			return new PtlInternetExplorerDriver(executor, getCapabilities());
		}
	}

	/**
	 * Internet Explorer 7用WebDriverを生成するファクトリクラス
	 */
	static class PtlInternetExplorer7DriverFactory extends PtlInternetExplorerDriverFactory {

		/**
		 * コンストラクタ
		 *
		 * @param environmentConfig 環境設定
		 * @param testAppConfig テスト対象アプリケーション設定
		 * @param capabilities Capability
		 */
		public PtlInternetExplorer7DriverFactory(EnvironmentConfig environmentConfig, TestAppConfig testAppConfig,
				PtlCapabilities capabilities) {
			super(environmentConfig, testAppConfig, capabilities);
		}

		@Override
		public PtlWebDriver createWebDriver(URL url) {
			return new PtlInternetExplorer7Driver(url, getCapabilities());
		}

		@Override
		public PtlWebDriver createReusableWebDriver(CommandExecutor executor) {
			return new PtlInternetExplorer7Driver(executor, getCapabilities());
		}
	}

	/**
	 * Internet Explorer 8用WebDriverを生成するファクトリクラス
	 */
	static class PtlInternetExplorer8DriverFactory extends PtlInternetExplorerDriverFactory {

		/**
		 * コンストラクタ
		 *
		 * @param environmentConfig 環境設定
		 * @param testAppConfig テスト対象アプリケーション設定
		 * @param capabilities Capability
		 */
		public PtlInternetExplorer8DriverFactory(EnvironmentConfig environmentConfig, TestAppConfig testAppConfig,
				PtlCapabilities capabilities) {
			super(environmentConfig, testAppConfig, capabilities);
		}

		@Override
		public PtlWebDriver createWebDriver(URL url) {
			return new PtlInternetExplorer8Driver(url, getCapabilities());
		}

		@Override
		public PtlWebDriver createReusableWebDriver(CommandExecutor executor) {
			return new PtlInternetExplorer8Driver(executor, getCapabilities());
		}
	}

	/**
	 * MicrosoftEdge用WebDriverを生成するファクトリクラス
	 */
	static class PtlEdgeDriverFactory extends PtlWebDriverFactory {

		/**
		 * コンストラクタ
		 *
		 * @param environmentConfig 環境設定
		 * @param testAppConfig テスト対象アプリケーション設定
		 * @param capabilities Capability
		 */
		public PtlEdgeDriverFactory(EnvironmentConfig environmentConfig, TestAppConfig testAppConfig,
				PtlCapabilities capabilities) {
			super(environmentConfig, testAppConfig, capabilities);
		}

		@Override
		boolean isMobile() {
			return false;
		}

		@Override
		public PtlWebDriver createWebDriver(URL url) {
			return new PtlEdgeDriver(url, getCapabilities());
		}

		@Override
		public PtlWebDriver createReusableWebDriver(CommandExecutor executor) {
			return new PtlEdgeDriver(executor, getCapabilities());
		}
	}

	/**
	 * Safari on MacOSX用WebDriverを生成するファクトリクラス
	 */
	static class PtlSafariDriverFactory extends PtlWebDriverFactory {

		/**
		 * コンストラクタ
		 *
		 * @param environmentConfig 環境設定
		 * @param testAppConfig テスト対象アプリケーション設定
		 * @param capabilities Capability
		 */
		public PtlSafariDriverFactory(EnvironmentConfig environmentConfig, TestAppConfig testAppConfig,
				PtlCapabilities capabilities) {
			super(environmentConfig, testAppConfig, capabilities);
		}

		@Override
		boolean isMobile() {
			return false;
		}

		@Override
		public PtlWebDriver createWebDriver(URL url) {
			return new PtlSafariDriver(url, getCapabilities());
		}

		@Override
		public PtlWebDriver createReusableWebDriver(CommandExecutor executor) {
			return new PtlSafariDriver(executor, getCapabilities());
		}
	}

	/**
	 * Safari on iPhone devices用WebDriverを生成するファクトリクラス
	 */
	static class PtlIPhoneDriverFactory extends PtlWebDriverFactory {

		/**
		 * iPhoneのデフォルトのヘッダ（アドレスバーなど）幅
		 */
		static final int DEFAULT_IPHONE_HEADER_HEIGHT = 128;

		/**
		 * iPhoneのデフォルトのフッタ幅
		 */
		static final int DEFAULT_IPHONE_FOOTER_HEIGHT = 88;

		/**
		 * コンストラクタ
		 *
		 * @param environmentConfig 環境設定
		 * @param testAppConfig テスト対象アプリケーション設定
		 * @param capabilities Capability
		 */
		public PtlIPhoneDriverFactory(EnvironmentConfig environmentConfig, TestAppConfig testAppConfig,
				PtlCapabilities capabilities) {
			super(environmentConfig, testAppConfig, capabilities);

			if (capabilities.getCapability("headerHeight") == null) {
				capabilities.setCapability("headerHeight", DEFAULT_IPHONE_HEADER_HEIGHT);
			}

			if (capabilities.getCapability("footerHeight") == null) {
				capabilities.setCapability("footerHeight", DEFAULT_IPHONE_FOOTER_HEIGHT);
			}
		}

		@Override
		boolean isMobile() {
			return true;
		}

		@Override
		public PtlWebDriver createWebDriver(URL url) {
			return new PtlIPhoneDriver(url, getCapabilities());
		}

		@Override
		public PtlWebDriver createReusableWebDriver(CommandExecutor executor) {
			return new PtlIPhoneDriver(executor, getCapabilities());
		}
	}

	/**
	 * Safari on iPad devices用WebDriverを生成するファクトリクラス
	 */
	static class PtlIPadDriverFactory extends PtlWebDriverFactory {

		/**
		 * iPadのデフォルトのヘッダ幅。タブバーを除いた値をデフォルトとする
		 */
		static final int DEFAULT_IPAD_HEADER_HEIGHT = 128;

		/**
		 * iPadのデフォルトのフッタ幅
		 */
		static final int DEFAULT_IPAD_FOOTER_HEIGHT = 0;

		/**
		 * コンストラクタ
		 *
		 * @param environmentConfig 環境設定
		 * @param testAppConfig テスト対象アプリケーション設定
		 * @param capabilities Capability
		 */
		public PtlIPadDriverFactory(EnvironmentConfig environmentConfig, TestAppConfig testAppConfig,
				PtlCapabilities capabilities) {
			super(environmentConfig, testAppConfig, capabilities);

			if (capabilities.getCapability("headerHeight") == null) {
				capabilities.setCapability("headerHeight", DEFAULT_IPAD_HEADER_HEIGHT);
			}
			if (capabilities.getCapability("footerHeight") == null) {
				capabilities.setCapability("footerHeight", DEFAULT_IPAD_FOOTER_HEIGHT);
			}
		}

		@Override
		boolean isMobile() {
			return true;
		}

		@Override
		public PtlWebDriver createWebDriver(URL url) {
			return new PtlIPadDriver(url, getCapabilities());
		}

		@Override
		public PtlWebDriver createReusableWebDriver(CommandExecutor executor) {
			return new PtlIPadDriver(executor, getCapabilities());
		}
	}

	/**
	 * Android devices(4.2+)用WebDriverを生成するファクトリクラス
	 */
	static class PtlAndroidDriverFactory extends PtlWebDriverFactory {

		/**
		 * コンストラクタ
		 *
		 * @param environmentConfig 環境設定
		 * @param testAppConfig テスト対象アプリケーション設定
		 * @param capabilities Capability
		 */
		public PtlAndroidDriverFactory(EnvironmentConfig environmentConfig, TestAppConfig testAppConfig,
				PtlCapabilities capabilities) {
			super(environmentConfig, testAppConfig, capabilities);
		}

		@Override
		boolean isMobile() {
			return true;
		}

		@Override
		public PtlWebDriver createWebDriver(URL url) {
			return new PtlAndroidDriver(url, getCapabilities());
		}

		@Override
		public PtlWebDriver createReusableWebDriver(CommandExecutor executor) {
			return new PtlAndroidDriver(executor, getCapabilities());
		}
	}

	/**
	 * Android devices(2.3+)用WebDriverを生成するファクトリクラス
	 */
	static class PtlSelendroidDriverFactory extends PtlAndroidDriverFactory {

		/**
		 * コンストラクタ
		 *
		 * @param environmentConfig 環境設定
		 * @param testAppConfig テスト対象アプリケーション設定
		 * @param capabilities Capability
		 */
		public PtlSelendroidDriverFactory(EnvironmentConfig environmentConfig, TestAppConfig testAppConfig,
				PtlCapabilities capabilities) {
			super(environmentConfig, testAppConfig, capabilities);
		}

		@Override
		public PtlWebDriver createWebDriver(URL url) {
			return new PtlSelendroidDriver(url, getCapabilities());
		}

		@Override
		public PtlWebDriver createReusableWebDriver(CommandExecutor executor) {
			return new PtlSelendroidDriver(executor, getCapabilities());
		}
	}

}
