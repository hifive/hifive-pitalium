/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.core.config;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.htmlhifive.testlib.common.util.JSONUtils;
import com.htmlhifive.testlib.core.io.FilePersister;

/**
 * テストを実行するための共通設定を保持するクラス
 */
@MrtConfiguration
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnvironmentConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_HUB_PORT = 4444;
	private static final int DEFAULT_MAX_THREAD_COUNT = 16;
	private static final int DEFAULT_MAX_THREAD_EXECUTE_TIME = 600;
	private static final int DEFAULT_MAX_DRIVER_WAIT = 30;

	/**
	 * テスト実行モード
	 */
	@MrtConfigurationProperty("execMode")
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

	@Override
	public String toString() {
		return JSONUtils.toString(this);
	}

}
