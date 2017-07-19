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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.htmlhifive.pitalium.common.exception.TestRuntimeException;
import com.htmlhifive.pitalium.core.config.EnvironmentConfig;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import com.htmlhifive.pitalium.core.config.TestAppConfig;

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

	/**
	 * 初期設定（baseUrl、タイムアウト時間、ウィンドウサイズ）済のWebDriverを取得します。
	 * 
	 * @return WebDriver
	 */
	public PtlWebDriver getDriver() {
		synchronized (PtlWebDriverFactory.class) {
			LOG.debug("[Get WebDriver] create new session.");

			URL url = getGridHubURL();
			PtlWebDriver driver = createWebDriver(url);
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
	}

}
