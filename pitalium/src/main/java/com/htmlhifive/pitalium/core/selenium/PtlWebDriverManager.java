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

package com.htmlhifive.pitalium.core.selenium;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.htmlhifive.pitalium.common.exception.TestRuntimeException;
import com.htmlhifive.pitalium.core.annotation.PtlWebDriverStrategy;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import com.htmlhifive.pitalium.core.config.WebDriverSessionLevel;

/**
 * {@link PtlWebDriver}のインスタンスを管理するクラス
 * 
 * @author nakatani
 */
public final class PtlWebDriverManager {

	private static final Logger LOG = LoggerFactory.getLogger(PtlWebDriverManager.class);

	private static PtlWebDriverManager instance = new PtlWebDriverManager();

	private static final long MAX_CACHE_SIZE = 200L;
	private final Map<DriverKey, WebDriver> drivers = new HashMap<DriverKey, WebDriver>();
	private final LoadingCache<Class<?>, WebDriverSessionLevel> driverReuses = CacheBuilder.newBuilder()
			.maximumSize(MAX_CACHE_SIZE).build(new CacheLoader<Class<?>, WebDriverSessionLevel>() {
				@Override
				public WebDriverSessionLevel load(Class<?> clss) throws Exception {
					PtlWebDriverStrategy strategy = clss.getAnnotation(PtlWebDriverStrategy.class);
					if (strategy != null) {
						switch (strategy.sessionLevel()) {
							case GLOBAL:
								LOG.debug("[Get WebDriver] reuse level: {} (annotated, {})",
										WebDriverSessionLevel.GLOBAL, clss.getName());
								return WebDriverSessionLevel.GLOBAL;

							case TEST_CASE:
								LOG.debug("[Get WebDriver] reuse level: {} (annotated, {})",
										WebDriverSessionLevel.TEST_CASE, clss.getName());
								return WebDriverSessionLevel.TEST_CASE;

							case TEST_CLASS:
								LOG.debug("[Get WebDriver] reuse level: {} (annotated, {})",
										WebDriverSessionLevel.TEST_CLASS, clss.getName());
								return WebDriverSessionLevel.TEST_CLASS;
						}
					}

					LOG.debug("[Get WebDriver] reuse level: {} (config, {})", configSessionLevel, clss);
					return configSessionLevel;
				}
			});
	private final Map<PtlCapabilities, WebDriver> allClassesDrivers = new HashMap<PtlCapabilities, WebDriver>();

	private WebDriverSessionLevel configSessionLevel = PtlTestConfig.getInstance().getEnvironment()
			.getWebDriverSessionLevel();

	/**
	 * コンストラクタ
	 */
	private PtlWebDriverManager() {
	}

	/**
	 * {@link PtlWebDriverManager}を取得します。
	 * 
	 * @return 自身のインスタンス
	 */
	public static PtlWebDriverManager getInstance() {
		return instance;
	}

	/**
	 * 内部キャッシュをリセットします。
	 * 
	 * @param sessionLevel WebDriverのセッションを共有するレベル
	 */
	@VisibleForTesting
	synchronized void resetCache(WebDriverSessionLevel sessionLevel) {
		this.configSessionLevel = sessionLevel;

		drivers.clear();
		driverReuses.invalidateAll();
		allClassesDrivers.clear();
	}

	/**
	 * クラスとCapabilitiesから登録済みWebDriverのインスタンスを取得します。未登録だった場合は{@code supplier}からWebDriverのインスタンスが生成され、登録されます。
	 * 
	 * @param clss テスト対象のクラス
	 * @param capabilities テスト対象のブラウザ情報
	 * @param supplier WebDriverのインスタンスが未登録の場合のインスタンス生成デリゲート
	 * @return 登録済みWebDriverインスタンス、またはデリゲートで生成されたWebDriverインスタンス
	 */
	public synchronized WebDriverContainer getWebDriver(Class<?> clss, PtlCapabilities capabilities,
			Supplier<WebDriver> supplier) {
		if (clss == null) {
			throw new NullPointerException("clss");
		}
		if (capabilities == null) {
			throw new NullPointerException("capabilities");
		}
		if (supplier == null) {
			throw new NullPointerException("supplier");
		}

		LOG.trace("[Get WebDriver] ({}) capabilities: {}", clss.getName(), capabilities);

		WebDriverSessionLevel level;
		try {
			level = driverReuses.get(clss);
		} catch (ExecutionException e) {
			throw new TestRuntimeException(e);
		}

		// ドライバーを再利用しない場合はSupplierから取得したドライバーを返す。
		if (level == WebDriverSessionLevel.TEST_CASE) {
			WebDriver driver = supplier.get();
			LOG.info("[Get WebDriver] new session created.");

			return new WebDriverContainer(driver, WebDriverSessionLevel.TEST_CASE);
		}

		DriverKey key = new DriverKey(clss, capabilities);
		WebDriver driver = drivers.get(key);
		if (driver != null) {
			LOG.info("[Get WebDriver] use cached session.");
			return new WebDriverContainer(driver, level);
		}

		// クラス単位
		if (level == WebDriverSessionLevel.TEST_CLASS) {
			driver = supplier.get();
			LOG.info("[Get WebDriver] new session created ({}).", clss.getName());
			drivers.put(key, driver);
			return new WebDriverContainer(driver, level);
		}

		// 全体
		driver = allClassesDrivers.get(capabilities);
		if (driver == null) {
			driver = supplier.get();
			LOG.info("[Get WebDriver] new session created (for global use).");
			drivers.put(key, driver);
			allClassesDrivers.put(capabilities, driver);
		}

		return new WebDriverContainer(driver, level);
	}

	/**
	 * クラスとCapabilitiesから登録済みWebDriverのセッションをクローズします。
	 * 
	 * @param clss テスト対象のクラス
	 * @param capabilities テスト対象のブラウザ情報
	 */
	public synchronized void closeWebDriverSession(Class<?> clss, PtlCapabilities capabilities) {
		if (clss == null) {
			throw new NullPointerException("clss");
		}
		if (capabilities == null) {
			throw new NullPointerException("capabilities");
		}

		LOG.trace("[Close WebDriver] ({}) capabilities: {}", clss.getName(), capabilities);

		WebDriverSessionLevel level;
		try {
			level = driverReuses.get(clss);
		} catch (ExecutionException e) {
			throw new TestRuntimeException(e);
		}

		if (level == WebDriverSessionLevel.TEST_CASE || level == WebDriverSessionLevel.GLOBAL) {
			LOG.debug("[Close WebDriver] Don't close session. ({}) level: {}", clss.getName(), level);
			return;
		}

		DriverKey key = new DriverKey(clss, capabilities);
		WebDriver driver = drivers.remove(key);
		if (driver == null) {
			LOG.debug("[Close WebDriver] No session cached. ({}) capabilities: {}", clss.getName(), capabilities);
			return;
		}

		try {
			LOG.debug("[Close WebDriver] Close cached session. ({}, {})", clss.getName(), driver);
			driver.quit();
		} catch (Exception e) {
			LOG.warn("[Close WebDriver] Failed to close cached session. ({}, {})", clss, driver, e);
		}
	}

	private static class DriverKey {
		private final Class<?> clss;
		private final PtlCapabilities capabilities;

		/**
		 * @param clss
		 * @param capabilities
		 */
		public DriverKey(Class<?> clss, PtlCapabilities capabilities) {
			this.clss = clss;
			this.capabilities = capabilities;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			DriverKey driverKey = (DriverKey) o;

			if (!clss.equals(driverKey.clss)) {
				return false;
			}
			return capabilities.equals(driverKey.capabilities);
		}

		@Override
		public int hashCode() {
			int result = clss.hashCode();
			result = 31 * result + capabilities.hashCode();
			return result;
		}
	}

	/**
	 * {@link #getWebDriver(Class, PtlCapabilities, Supplier)}で取得される{@link PtlWebDriver}のコンテナです。<br />
	 * WebDriverの利用戦略情報を持ち、{@link WebDriver#quit()}を呼び出す判断を行うことができます。
	 */
	public static class WebDriverContainer {

		private final WebDriver driver;
		private final WebDriverSessionLevel sessionLevel;

		/**
		 * {@link PtlWebDriver}のコンテナを生成します。
		 * 
		 * @param driver {@link WebDriver}
		 * @param sessionLevel WebDriverセッションの利用レベル
		 */
		public WebDriverContainer(WebDriver driver, WebDriverSessionLevel sessionLevel) {
			this.driver = driver;
			this.sessionLevel = sessionLevel;
		}

		/**
		 * {@link WebDriver}を取得します。
		 * 
		 * @param <T> {@link WebDriver}を継承したWebDriverクラス
		 * @return 自身の持つ{@link PtlWebDriver}
		 */
		@SuppressWarnings("unchecked")
		public <T extends WebDriver> T get() {
			return (T) driver;
		}

		/**
		 * {@link WebDriver#quit()}をコールします。
		 */
		public void quit() {
			if (sessionLevel == WebDriverSessionLevel.TEST_CASE) {
				LOG.debug("[Close WebDriver] Close session for each case. ({})", driver);
				driver.quit();
			}
		}

	}

}
