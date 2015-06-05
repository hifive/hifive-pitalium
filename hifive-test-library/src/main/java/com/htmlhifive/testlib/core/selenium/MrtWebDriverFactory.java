/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.core.selenium;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.htmlhifive.testlib.common.exception.TestRuntimeException;
import com.htmlhifive.testlib.core.config.EnvironmentConfig;
import com.htmlhifive.testlib.core.config.MrtTestConfig;
import com.htmlhifive.testlib.core.config.TestAppConfig;

/**
 * 各ブラウザに対応するWebDriverを生成するファクトリクラス
 */
public abstract class MrtWebDriverFactory {

	private static final Logger LOG = LoggerFactory.getLogger(MrtWebDriverFactory.class);

	private final EnvironmentConfig environmentConfig;
	private final TestAppConfig testAppConfig;
	private final MrtCapabilities capabilities;

	/**
	 * コンストラクタ
	 * 
	 * @param environmentConfig 環境設定
	 * @param testAppConfig テスト対象アプリケーション設定
	 * @param capabilities Capability
	 */
	protected MrtWebDriverFactory(EnvironmentConfig environmentConfig, TestAppConfig testAppConfig,
			MrtCapabilities capabilities) {
		this.environmentConfig = environmentConfig;
		this.testAppConfig = testAppConfig;
		this.capabilities = capabilities;
	}

	/**
	 * ブラウザに対応する{@link MrtWebDriverFactory}のインスタンスを取得します。
	 * 
	 * @param capabilities Capability（ブラウザの情報を含む）
	 * @return {@link MrtWebDriverFactory}のインスタンス
	 */
	public static MrtWebDriverFactory getInstance(MrtCapabilities capabilities) {
		MrtTestConfig config = MrtTestConfig.getInstance();
		EnvironmentConfig environmentConfig = config.getEnvironment();
		TestAppConfig testAppConfig = config.getTestAppConfig();

		String browserName = Strings.nullToEmpty(capabilities.getBrowserName()).toLowerCase(Locale.ENGLISH);

		// IE
		if ("internet explorer".equals(browserName)) {
			String version = Strings.nullToEmpty(capabilities.getVersion());
			if (version.startsWith("7")) {
				return new MrtInternetExplorer7DriverFactory(environmentConfig, testAppConfig, capabilities);
			}
			if (version.startsWith("8")) {
				return new MrtInternetExplorer8DriverFactory(environmentConfig, testAppConfig, capabilities);
			}

			return new MrtInternetExplorerDriverFactory(environmentConfig, testAppConfig, capabilities);
		}

		// Android
		if (capabilities.getPlatform() == Platform.ANDROID) {
			// Selendroid (Android 2.3+)
			String automationName = (String) capabilities.getCapability("automationName");
			if (automationName != null && "selendroid".equalsIgnoreCase(automationName)) {
				return new MrtSelendroidDriverFactory(environmentConfig, testAppConfig, capabilities);
			}

			// Default (Android 4.2+)
			return new MrtAndroidDriverFactory(environmentConfig, testAppConfig, capabilities);
		}

		// Chrome
		if ("chrome".equals(browserName)) {
			return new MrtChromeWebDriverFactory(environmentConfig, testAppConfig, capabilities);
		}

		// Safari
		if ("safari".equals(browserName)) {
			// MacOSX
			if (capabilities.getPlatform() == Platform.MAC) {
				return new MrtSafariDriverFactory(environmentConfig, testAppConfig, capabilities);
			}

			String deviceName = capabilities.getDeviceName();
			if (Strings.isNullOrEmpty(deviceName)) {
				throw new TestRuntimeException("\"deviceName\" is required for iOS devices");
			}
			if (deviceName.contains("iPad")) {
				return new MrtIPadDriverFactory(environmentConfig, testAppConfig, capabilities);
			}
			if (deviceName.contains("iPhone")) {
				return new MrtIPhoneDriverFactory(environmentConfig, testAppConfig, capabilities);
			}

			throw new TestRuntimeException("Unknown deviceName \"" + deviceName + "\"");
		}

		// Other
		return new MrtFirefoxWebDriverFactory(environmentConfig, testAppConfig, capabilities);
	}

	/**
	 * 初期設定（baseUrl、タイムアウト時間、ウィンドウサイズ）済のWebDriverを取得します。
	 * 
	 * @return WebDriver
	 */
	public MrtWebDriver getDriver() {
		synchronized (MrtWebDriverFactory.class) {
			LOG.debug("{}: BEGIN create web driver.");

			URL url = getGridHubURL();
			MrtWebDriver driver = createWebDriver(url);
			driver.setBaseUrl(testAppConfig.getBaseUrl());
			driver.manage().timeouts().implicitlyWait(environmentConfig.getMaxDriverWait(), TimeUnit.SECONDS);
			if (!isMobile()) {
				driver.manage().window()
						.setSize(new Dimension(testAppConfig.getWindowWidth(), testAppConfig.getWindowHeight()));
			}

			LOG.debug("{}: END create web driver.");
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
	public abstract MrtWebDriver createWebDriver(URL url);

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
	public MrtCapabilities getCapabilities() {
		return capabilities;
	}

	/**
	 * Firefox用WebDriverを生成するファクトリクラス
	 */
	static class MrtFirefoxWebDriverFactory extends MrtWebDriverFactory {

		/**
		 * コンストラクタ
		 * 
		 * @param environmentConfig 環境設定
		 * @param testAppConfig テスト対象アプリケーション設定
		 * @param capabilities Capability
		 */
		public MrtFirefoxWebDriverFactory(EnvironmentConfig environmentConfig, TestAppConfig testAppConfig,
				MrtCapabilities capabilities) {
			super(environmentConfig, testAppConfig, capabilities);
		}

		@Override
		boolean isMobile() {
			return false;
		}

		@Override
		public MrtWebDriver createWebDriver(URL url) {
			return new MrtFirefoxDriver(url, getCapabilities());
		}
	}

	/**
	 * Google chrome用WebDriverを生成するファクトリクラス
	 */
	static class MrtChromeWebDriverFactory extends MrtWebDriverFactory {

		/**
		 * コンストラクタ
		 * 
		 * @param environmentConfig 環境設定
		 * @param testAppConfig テスト対象アプリケーション設定
		 * @param capabilities Capability
		 */
		public MrtChromeWebDriverFactory(EnvironmentConfig environmentConfig, TestAppConfig testAppConfig,
				MrtCapabilities capabilities) {
			super(environmentConfig, testAppConfig, capabilities);
		}

		@Override
		boolean isMobile() {
			return false;
		}

		@Override
		public MrtWebDriver createWebDriver(URL url) {
			return new MrtChromeDriver(url, getCapabilities());
		}
	}

	/**
	 * Internet Explorer用WebDriverを生成するファクトリクラス
	 */
	static class MrtInternetExplorerDriverFactory extends MrtWebDriverFactory {

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
		public MrtInternetExplorerDriverFactory(EnvironmentConfig environmentConfig, TestAppConfig testAppConfig,
				MrtCapabilities capabilities) {
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
		public MrtWebDriver createWebDriver(URL url) {
			return new MrtInternetExplorerDriver(url, getCapabilities());
		}
	}

	/**
	 * Internet Explorer 7用WebDriverを生成するファクトリクラス
	 */
	static class MrtInternetExplorer7DriverFactory extends MrtInternetExplorerDriverFactory {

		/**
		 * コンストラクタ
		 * 
		 * @param environmentConfig 環境設定
		 * @param testAppConfig テスト対象アプリケーション設定
		 * @param capabilities Capability
		 */
		public MrtInternetExplorer7DriverFactory(EnvironmentConfig environmentConfig, TestAppConfig testAppConfig,
				MrtCapabilities capabilities) {
			super(environmentConfig, testAppConfig, capabilities);
		}

		@Override
		public MrtWebDriver createWebDriver(URL url) {
			return new MrtInternetExplorer7Driver(url, getCapabilities());
		}
	}

	/**
	 * Internet Explorer 8用WebDriverを生成するファクトリクラス
	 */
	static class MrtInternetExplorer8DriverFactory extends MrtInternetExplorerDriverFactory {

		/**
		 * コンストラクタ
		 * 
		 * @param environmentConfig 環境設定
		 * @param testAppConfig テスト対象アプリケーション設定
		 * @param capabilities Capability
		 */
		public MrtInternetExplorer8DriverFactory(EnvironmentConfig environmentConfig, TestAppConfig testAppConfig,
				MrtCapabilities capabilities) {
			super(environmentConfig, testAppConfig, capabilities);
		}

		@Override
		public MrtWebDriver createWebDriver(URL url) {
			return new MrtInternetExplorer8Driver(url, getCapabilities());
		}
	}

	/**
	 * Safari on MacOSX用WebDriverを生成するファクトリクラス
	 */
	static class MrtSafariDriverFactory extends MrtWebDriverFactory {

		/**
		 * コンストラクタ
		 * 
		 * @param environmentConfig 環境設定
		 * @param testAppConfig テスト対象アプリケーション設定
		 * @param capabilities Capability
		 */
		public MrtSafariDriverFactory(EnvironmentConfig environmentConfig, TestAppConfig testAppConfig,
				MrtCapabilities capabilities) {
			super(environmentConfig, testAppConfig, capabilities);
		}

		@Override
		boolean isMobile() {
			return false;
		}

		@Override
		public MrtWebDriver createWebDriver(URL url) {
			return new MrtSafariDriver(url, getCapabilities());
		}
	}

	/**
	 * Safari on iPhone devices用WebDriverを生成するファクトリクラス
	 */
	static class MrtIPhoneDriverFactory extends MrtWebDriverFactory {

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
		public MrtIPhoneDriverFactory(EnvironmentConfig environmentConfig, TestAppConfig testAppConfig,
				MrtCapabilities capabilities) {
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
		public MrtWebDriver createWebDriver(URL url) {
			return new MrtIPhoneDriver(url, getCapabilities());
		}
	}

	/**
	 * Safari on iPad devices用WebDriverを生成するファクトリクラス
	 */
	static class MrtIPadDriverFactory extends MrtWebDriverFactory {

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
		public MrtIPadDriverFactory(EnvironmentConfig environmentConfig, TestAppConfig testAppConfig,
				MrtCapabilities capabilities) {
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
		public MrtWebDriver createWebDriver(URL url) {
			return new MrtIPadDriver(url, getCapabilities());
		}
	}

	/**
	 * Android devices(4.2+)用WebDriverを生成するファクトリクラス
	 */
	static class MrtAndroidDriverFactory extends MrtWebDriverFactory {

		/**
		 * コンストラクタ
		 * 
		 * @param environmentConfig 環境設定
		 * @param testAppConfig テスト対象アプリケーション設定
		 * @param capabilities Capability
		 */
		public MrtAndroidDriverFactory(EnvironmentConfig environmentConfig, TestAppConfig testAppConfig,
				MrtCapabilities capabilities) {
			super(environmentConfig, testAppConfig, capabilities);
		}

		@Override
		boolean isMobile() {
			return true;
		}

		@Override
		public MrtWebDriver createWebDriver(URL url) {
			return new MrtAndroidDriver(url, getCapabilities());
		}
	}

	/**
	 * Android devices(2.3+)用WebDriverを生成するファクトリクラス
	 */
	static class MrtSelendroidDriverFactory extends MrtAndroidDriverFactory {

		/**
		 * コンストラクタ
		 * 
		 * @param environmentConfig 環境設定
		 * @param testAppConfig テスト対象アプリケーション設定
		 * @param capabilities Capability
		 */
		public MrtSelendroidDriverFactory(EnvironmentConfig environmentConfig, TestAppConfig testAppConfig,
				MrtCapabilities capabilities) {
			super(environmentConfig, testAppConfig, capabilities);
		}

		@Override
		public MrtWebDriver createWebDriver(URL url) {
			return new MrtSelendroidDriver(url, getCapabilities());
		}
	}

}
