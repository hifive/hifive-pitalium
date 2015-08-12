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
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.htmlhifive.pitalium.common.exception.TestRuntimeException;
import com.htmlhifive.pitalium.core.annotation.PtlWebDriverStrategy;

/**
 * {@link PtlWebDriver}のインスタンスを管理するクラス
 * 
 * @author nakatani
 */
public class PtlWebDriverManager {

	private static class DriverKey {
		final Class<?> clss;
		final PtlCapabilities capabilities;

		public DriverKey(Class<?> clss, PtlCapabilities capabilities) {
			this.clss = clss;
			this.capabilities = capabilities;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			DriverKey driverKey = (DriverKey) o;

			if (!clss.equals(driverKey.clss))
				return false;
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

		final WebDriver driver;
		final boolean reuse;

		public WebDriverContainer(WebDriver driver, boolean reuse) {
			this.driver = driver;
			this.reuse = reuse;
		}

		/**
		 * {@link PtlWebDriver}を取得します。
		 */
		@SuppressWarnings("unchecked")
		public <T extends WebDriver> T get() {
			return (T) driver;
		}

		/**
		 * {@link WebDriver#quit()}をコールします。
		 */
		public void quit() {
			if (!reuse) {
				driver.quit();
			}
		}

	}

	private static final Logger LOG = LoggerFactory.getLogger(PtlWebDriverManager.class);

	private static PtlWebDriverManager instance = new PtlWebDriverManager();

	/**
	 * {@link PtlWebDriverManager}を取得します。
	 */
	public static PtlWebDriverManager getInstance() {
		return instance;
	}

	private final Map<DriverKey, WebDriver> drivers = new HashMap<DriverKey, WebDriver>();
	private final LoadingCache<Class<?>, Boolean> driverReuses = CacheBuilder.newBuilder().maximumSize(200L)
			.build(new CacheLoader<Class<?>, Boolean>() {
				@Override
				public Boolean load(Class<?> clss) throws Exception {
					PtlWebDriverStrategy strategy = clss.getAnnotation(PtlWebDriverStrategy.class);
					return strategy != null && strategy.reuse();
				}
			});

	private PtlWebDriverManager() {
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

		boolean reuse;
		try {
			reuse = driverReuses.get(clss);
		} catch (ExecutionException e) {
			throw new TestRuntimeException(e);
		}

		// ドライバーを再利用しない場合はSupplierから取得したドライバーを返す。
		if (!reuse) {
			return new WebDriverContainer(supplier.get(), false);
		}

		DriverKey key = new DriverKey(clss, capabilities);
		WebDriver driver = drivers.get(key);
		if (driver != null) {
			LOG.debug("");
			return new WebDriverContainer(driver, true);
		}

		driver = supplier.get();
		drivers.put(key, driver);
		return new WebDriverContainer(driver, true);
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

		boolean reuse;
		try {
			reuse = driverReuses.get(clss);
		} catch (ExecutionException e) {
			throw new TestRuntimeException(e);
		}

		if (!reuse) {
			return;
		}

		DriverKey key = new DriverKey(clss, capabilities);
		WebDriver driver = drivers.remove(key);
		if (driver == null) {
			LOG.debug("No driver entry found for %s:[{}]", clss.getName(), capabilities);
			return;
		}

		try {
			driver.quit();
		} catch (Exception e) {
			LOG.warn(String.format(Locale.US, "Failed to close selenium session for %s:[%s]", clss.getName(),
					capabilities), e);
		}
	}

}
