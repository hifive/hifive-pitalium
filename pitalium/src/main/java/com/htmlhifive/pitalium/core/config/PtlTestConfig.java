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
package com.htmlhifive.pitalium.core.config;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.htmlhifive.pitalium.common.exception.JSONException;
import com.htmlhifive.pitalium.common.exception.TestRuntimeException;
import com.htmlhifive.pitalium.common.util.JSONUtils;

/**
 * テスト全体の設定情報を保持するクラス
 */
public class PtlTestConfig {

	private static final Logger LOG = LoggerFactory.getLogger(PtlTestConfig.class);
	private static final String SYSTEM_STARTUP_ARGUMENTS_PREFIX = "com.htmlhifive.pitalium.";

	private static PtlTestConfig instance;
	private final Map<String, String> startupArguments;
	private final Map<String, Object> configs = new HashMap<String, Object>();
	private EnvironmentConfig environment;
	private PersisterConfig persisterConfig;
	private TestAppConfig testApp;
	private HttpServerConfig httpServerConfig;

	private PtlTestConfig() {
		this(getSystemStartupArguments());
	}

	/**
	 * コンストラクタ
	 * 
	 * @param startupArguments プロパティ一覧
	 */
	@VisibleForTesting
	PtlTestConfig(Map<String, String> startupArguments) {
		this.startupArguments = Collections.unmodifiableMap(startupArguments);
	}

	/**
	 * JVM起動引数からテストに関するプロパティ一覧を取得します。
	 * 
	 * @return プロパティのマップ
	 */
	static Map<String, String> getSystemStartupArguments() {
		Properties properties = System.getProperties();
		Map<String, String> results = new HashMap<String, String>();
		int prefixLength = SYSTEM_STARTUP_ARGUMENTS_PREFIX.length();
		for (Object key : properties.keySet()) {
			String propertyKey = (String) key;
			if (!propertyKey.startsWith(SYSTEM_STARTUP_ARGUMENTS_PREFIX)) {
				continue;
			}

			String value = properties.getProperty(propertyKey);
			results.put(propertyKey.substring(prefixLength), value);
		}

		LOG.debug("System startup arguments: {}", results);
		return results;
	}

	/**
	 * {@link PtlTestConfig}のインスタンスを取得します。
	 * 
	 * @return インスタンス
	 */
	public static synchronized PtlTestConfig getInstance() {
		if (instance != null) {
			return instance;
		}

		instance = new PtlTestConfig();
		return instance;
	}

	/**
	 * 設定情報を取得します。
	 * 
	 * @param clss 設定情報を保持するためのクラス
	 * @param <T> 設定情報を保持するクラスの型
	 * @return 設定情報
	 */
	public <T> T getConfig(Class<T> clss) {
		return getConfig(clss, clss.getSimpleName());
	}

	/**
	 * 設定情報を取得します。
	 * 
	 * @param clss 設定情報を保持するためのクラス
	 * @param <T> 設定情報を保持するクラスの型
	 * @param name 設定項目名
	 * @return 設定情報
	 */
	@SuppressWarnings("unchecked")
	public <T> T getConfig(Class<T> clss, String name) {
		synchronized (configs) {
			// Check cached
			if (configs.containsKey(name)) {
				return (T) configs.get(name);
			}

			// Load configuration file
			PtlConfiguration configuration = clss.getAnnotation(PtlConfiguration.class);
			if (configuration == null) {
				throw new TestRuntimeException("Configuration class must be annotated with @PtlConfiguration");
			}

			String argumentName = configuration.argumentName();
			if (Strings.isNullOrEmpty(argumentName)) {
				argumentName = StringUtils.uncapitalize(clss.getSimpleName());
			}
			String defaultFileName = configuration.defaultFileName();
			if (Strings.isNullOrEmpty(defaultFileName)) {
				defaultFileName = StringUtils.uncapitalize(clss.getSimpleName()) + ".json";
			}

			T config = loadConfig(clss, startupArguments.get(argumentName), defaultFileName);
			fillConfigProperties(config, startupArguments);

			configs.put(name, config);
			return config;
		}
	}

	/**
	 * {@link PtlConfigurationProperty}が設定されているフィールドに対し値を設定します。
	 * 
	 * @param object 値を設定するオブジェクト
	 * @param arguments 設定する値
	 */
	private static void fillConfigProperties(Object object, Map<String, String> arguments) {
		// Collect all fields include super classes
		Class clss = object.getClass();
		List<Field> fields = new ArrayList<Field>();
		Collections.addAll(fields, clss.getDeclaredFields());
		while ((clss = clss.getSuperclass()) != Object.class) {
			Collections.addAll(fields, clss.getDeclaredFields());
		}

		for (Field field : fields) {
			PtlConfigurationProperty propertyConfig = field.getAnnotation(PtlConfigurationProperty.class);
			if (propertyConfig == null) {
				PtlConfiguration config = field.getAnnotation(PtlConfiguration.class);
				if (config == null) {
					continue;
				}

				// Field is nested config class
				try {
					field.setAccessible(true);
					Object prop = field.get(object);
					if (prop != null) {
						fillConfigProperties(prop, arguments);
					}
				} catch (TestRuntimeException e) {
					throw e;
				} catch (Exception e) {
					throw new TestRuntimeException(e);
				}

				continue;
			}

			String value = arguments.get(propertyConfig.value());
			if (value == null) {
				continue;
			}

			try {
				Object applyValue = convertFromString(field.getType(), value);
				field.setAccessible(true);
				field.set(object, applyValue);
			} catch (TestRuntimeException e) {
				throw e;
			} catch (Exception e) {
				throw new TestRuntimeException("ConfigurationProperty convert error", e);
			}
		}
	}

	/**
	 * 文字列から指定された型のオブジェクトに変換します。
	 * 
	 * @param type 変換後の型（String, int(Integer), double(Double),Enumのうちどれか）
	 * @param value 変換する文字列
	 * @return 変換後のオブジェクト
	 * @throws IllegalArgumentException Enumへの変換が失敗した場合
	 * @throws TestRuntimeException どの型にも変換できない場合
	 */
	static Object convertFromString(Class<?> type, String value) throws IllegalArgumentException, TestRuntimeException {
		if (type == String.class) {
			return value;
		} else if (type == int.class || type == Integer.class) {
			return Integer.parseInt(value);
		} else if (type == double.class || type == Double.class) {
			return Double.parseDouble(value);
		} else if (type.isEnum()) {
			for (Object o : type.getEnumConstants()) {
				if (((Enum) o).name().equals(value)) {
					return o;
				}
			}
			throw new IllegalArgumentException();
		} else {
			throw new TestRuntimeException("Cannot convert type \"" + type.getName() + "\"");
		}
	}

	/**
	 * ツールを実行するための共通設定を取得します。
	 * 
	 * @return ツール共通設定
	 */
	public EnvironmentConfig getEnvironment() {

		synchronized (this) {
			if (environment != null) {
				return environment;
			}

			environment = getConfig(EnvironmentConfig.class);

			LOG.debug("environment: {}", environment);
			return environment;
		}
	}

	/**
	 * テスト対象のページの共通設定を取得します。
	 * 
	 * @return テスト対象ページの共通設定
	 */
	public TestAppConfig getTestAppConfig() {

		synchronized (this) {
			if (testApp != null) {
				return testApp;
			}

			testApp = getConfig(TestAppConfig.class);

			LOG.debug("testApp: {}", testApp);
			return testApp;
		}
	}

	/**
	 * 画像やテスト結果の入出力に関する設定を取得します。
	 * 
	 * @return 画像やテスト結果の入出力に関する設定
	 */
	public PersisterConfig getPersisterConfig() {

		synchronized (this) {
			if (persisterConfig != null) {
				return persisterConfig;
			}

			persisterConfig = getConfig(PersisterConfig.class);

			LOG.debug("persisterConfig: {}", persisterConfig);
			return persisterConfig;
		}
	}

	/**
	 * Pitalium HTTPサーバーに関する設定を取得します。
	 * 
	 * @return Pitalium HTTPサーバーに関する設定
	 */
	public synchronized HttpServerConfig getHttpServerConfig() {
		if (httpServerConfig != null) {
			return httpServerConfig;
		}

		httpServerConfig = getConfig(HttpServerConfig.class);

		LOG.debug("httpServerConfig: {}", httpServerConfig);
		return httpServerConfig;
	}

	/**
	 * 設定情報をファイルから読み出します。
	 * 
	 * @param clss 読み出すクラス
	 * @param fileName ファイル名
	 * @param defaultFileName デフォルトのファイル名
	 */
	private <T> T loadConfig(Class<T> clss, String fileName, String defaultFileName) {
		// ファイル名が指定されている場合、ファイルから設定情報を取得
		// 取得に失敗した場合エラーを投げる
		if (!Strings.isNullOrEmpty(fileName)) {
			LOG.debug("{} fileName: {}", clss.getSimpleName(), fileName);
			try {
				return JSONUtils.readValue(new File(fileName), clss);
			} catch (JSONException e) {
				try {
					return JSONUtils
							.readValue(PtlTestConfig.class.getClassLoader().getResourceAsStream(fileName), clss);
				} catch (JSONException e1) {
					LOG.error(String.format("Load config file failed. Config: %s, File: %s.", clss.getSimpleName(),
							fileName), e1);
					throw e1;
				}
			}
		}

		// ファイル名が指定されていない場合デフォルトのファイル名で設定情報を取得
		// 取得に失敗した場合何もしない
		try {
			return JSONUtils.readValue(PtlTestConfig.class.getClassLoader().getResourceAsStream(defaultFileName), clss);
		} catch (JSONException e) {
			LOG.debug("Load config file failed as \"{}\". Config: {}, File: {}.", e.getMessage(), clss.getSimpleName(),
					defaultFileName);
		}

		// デフォルトコンストラクタでインスタンス作成
		try {
			return clss.newInstance();
		} catch (Exception e) {
			String message = String.format("Config \"%s\" must have default constructor.", clss.getSimpleName());
			LOG.error(message, e);
			throw new TestRuntimeException(message, e);
		}
	}

}
