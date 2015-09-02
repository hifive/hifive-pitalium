/*
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

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.htmlhifive.pitalium.common.util.JSONUtils;
import com.htmlhifive.pitalium.core.io.FilePersister;

/**
 * テストを実行するための共通設定を保持するクラス
 */
@PtlConfiguration
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnvironmentConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_HUB_PORT = 4444;
	private static final int DEFAULT_MAX_THREAD_COUNT = 16;
	private static final int DEFAULT_MAX_THREAD_EXECUTE_TIME = 3600;
	private static final int DEFAULT_MAX_DRIVER_WAIT = 30;

	/**
	 * テスト実行モード
	 */
	@PtlConfigurationProperty("execMode")
	private ExecMode execMode = ExecMode.SET_EXPECTED;

	/**
	 * Selenium Grid Hubのホスト名、またはIPアドレス
	 */
	private String hubHost = "localhost";
	/**
	 * Selenium Grid Hubのポート番号
	 */
	private int hubPort = DEFAULT_HUB_PORT;

	/**
	 * テストスレッドの同時最大実行数
	 */
	private int maxThreadCount = DEFAULT_MAX_THREAD_COUNT;

	/**
	 * 各スレッドの最大実行時間（秒）
	 */
	private int maxThreadExecuteTime = DEFAULT_MAX_THREAD_EXECUTE_TIME;

	/**
	 * WebDriverによるブラウザ操作の最大待ち時間（秒）
	 */
	private int maxDriverWait = DEFAULT_MAX_DRIVER_WAIT;

	/**
	 * Capabilitie設定ファイルのファイルパス
	 */
	private String capabilitiesFilePath = "capabilities.json";

	/**
	 * 結果保存に用いるPersisterの種類
	 */
	private String persister = FilePersister.class.getName();

	/**
	 * 全テストクラスの全テストケースでWebDriverを閉じずに再利用します。
	 */
	private boolean reuseDriverForAllClasses;

	/**
	 * デフォルトの設定値を持つオブジェクトを生成します。
	 */
	public EnvironmentConfig() {
	}

	/**
	 * テスト実行モードを取得します。
	 * 
	 * @return {@link ExecMode}で定義されたテスト実行モード
	 */
	public ExecMode getExecMode() {
		return execMode;
	}

	/**
	 * Selenium Grid Hubのアドレスを取得します。
	 * 
	 * @return Hubのホスト名、またはIPアドレス
	 */
	public String getHubHost() {
		return hubHost;
	}

	/**
	 * Selenium Grid Hubのポート番号を取得します。
	 * 
	 * @return ポート番号
	 */
	public int getHubPort() {
		return hubPort;
	}

	/**
	 * テストスレッドの最大同時実行数を取得します。
	 * 
	 * @return 最大同時実行スレッド数
	 */
	public int getMaxThreadCount() {
		return maxThreadCount;
	}

	/**
	 * テストスレッドの最大実行時間を取得します。
	 * 
	 * @return スレッドの最大実行時間（秒）
	 */
	public int getMaxThreadExecuteTime() {
		return maxThreadExecuteTime;
	}

	/**
	 * WebDriverによるブラウザ操作の最大待ち時間を取得します。
	 * 
	 * @return WebDriverの最大待ち時間（秒）
	 */
	public int getMaxDriverWait() {
		return maxDriverWait;
	}

	/**
	 * Capability設定ファイルのファイルパスを取得します。
	 * 
	 * @return Capability設定ファイルのファイルパス
	 */
	public String getCapabilitiesFilePath() {
		return capabilitiesFilePath;
	}

	/**
	 * 実行結果保存に用いるPersister名を取得します。
	 * 
	 * @return Persister名
	 */
	public String getPersister() {
		return persister;
	}

	/**
	 * 全テストクラスの全テストケースでWebDriverを閉じずに再利用するかどうかを取得します。
	 *
	 * @return 全テストクラスの全テストケースでWebDriverを閉じずに再利用するかどうか
	 */
	public boolean isReuseDriverForAllClasses() {
		return reuseDriverForAllClasses;
	}

	/**
	 * テスト実行モードを設定します。
	 * 
	 * @param execMode テスト実行モード
	 */
	void setExecMode(ExecMode execMode) {
		this.execMode = execMode;
	}

	/**
	 * Selenium Grid Hubのアドレスを設定します。
	 * 
	 * @param hubHost Hubのホスト名、またはIPアドレス
	 */
	void setHubHost(String hubHost) {
		this.hubHost = hubHost;
	}

	/**
	 * Selenium Grid Hubのポート番号を設定します。
	 * 
	 * @param hubPort ポート番号
	 */
	void setHubPort(int hubPort) {
		this.hubPort = hubPort;
	}

	/**
	 * テストスレッドの最大同時実行数を設定します。
	 * 
	 * @param maxThreadCount 最大同時実行スレッド数
	 */
	void setMaxThreadCount(int maxThreadCount) {
		this.maxThreadCount = maxThreadCount;
	}

	/**
	 * テストスレッドの最大実行時間を設定します。
	 * 
	 * @param maxThreadExecuteTime スレッドの最大実行時間
	 */
	void setMaxThreadExecuteTime(int maxThreadExecuteTime) {
		this.maxThreadExecuteTime = maxThreadExecuteTime;
	}

	/**
	 * WebDriverによるブラウザ操作の最大待ち時間を設定します。
	 * 
	 * @param maxDriverWait WebDriverの最大待ち時間
	 */
	void setMaxDriverWait(int maxDriverWait) {
		this.maxDriverWait = maxDriverWait;
	}

	/**
	 * Capability設定ファイルのファイルパスを設定します。
	 * 
	 * @param capabilitiesFilePath Capability設定ファイルのファイルパス
	 */
	void setCapabilitiesFilePath(String capabilitiesFilePath) {
		this.capabilitiesFilePath = capabilitiesFilePath;
	}

	/**
	 * 実行結果保存に用いるPersister名を設定します。
	 * 
	 * @param persister Persister名
	 */
	void setPersister(String persister) {
		this.persister = persister;
	}

	/**
	 * 全テストクラスの全テストケースでWebDriverを閉じずに再利用するかどうかを設定します。
	 *
	 * @param reuseDriverForAllClasses 全テストクラスの全テストケースでWebDriverを閉じずに再利用するかどうか
	 */
	void setReuseDriverForAllClasses(boolean reuseDriverForAllClasses) {
		this.reuseDriverForAllClasses = reuseDriverForAllClasses;
	}

	@Override
	public String toString() {
		return JSONUtils.toString(this);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private final EnvironmentConfig config = new EnvironmentConfig();

		public EnvironmentConfig build() {
			final EnvironmentConfig ev = new EnvironmentConfig();
			ev.setExecMode(config.execMode);
			ev.setHubHost(config.hubHost);
			ev.setHubPort(config.hubPort);
			ev.setMaxThreadCount(config.maxThreadCount);
			ev.setMaxThreadExecuteTime(config.maxThreadExecuteTime);
			ev.setMaxDriverWait(config.maxDriverWait);
			ev.setCapabilitiesFilePath(config.capabilitiesFilePath);
			ev.setPersister(config.persister);
			ev.setReuseDriverForAllClasses(config.reuseDriverForAllClasses);
			return ev;
		}

		public Builder execMode(ExecMode execMode) {
			config.execMode = execMode;
			return this;
		}

		public Builder hubHost(String hubHost) {
			config.hubHost = hubHost;
			return this;
		}

		public Builder hubPort(int hubPort) {
			config.hubPort = hubPort;
			return this;
		}

		public Builder maxThreadCount(int maxThreadCount) {
			config.maxThreadCount = maxThreadCount;
			return this;
		}

		public Builder maxThreadExecuteTime(int maxThreadExecuteTime) {
			config.maxThreadExecuteTime = maxThreadExecuteTime;
			return this;
		}

		public Builder maxDriverWait(int maxDriverWait) {
			config.maxDriverWait = maxDriverWait;
			return this;
		}

		public Builder capabilitiesFilePath(String capabilitiesFilePath) {
			config.capabilitiesFilePath = capabilitiesFilePath;
			return this;
		}

		public Builder persister(String persister) {
			config.persister = persister;
			return this;
		}

		public Builder reuseDriverForAllClasses(boolean reuseDriverForAllClasses) {
			config.reuseDriverForAllClasses = reuseDriverForAllClasses;
			return this;
		}

	}

}
