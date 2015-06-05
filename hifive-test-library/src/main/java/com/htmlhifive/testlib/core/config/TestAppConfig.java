/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.core.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.htmlhifive.testlib.common.util.JSONUtils;

/**
 * テスト対象のページの共通設定を保持するクラス
 */
@MrtConfiguration
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestAppConfig {

	private static final int DEFAULT_WINDOW_WIDTH = 1280;
	private static final int DEFAULT_WINDOW_HEIGHT = 800;

	/**
	 * テスト対象ページのベースURL。この項目を指定した場合、テストコード内のURLはこのURLからの相対URLとみなされます。ただし、http、httpsから始まる場合は、絶対URLとみなされます。
	 */
	private String baseUrl;

	/**
	 * PCでテストを実行する場合の、初期状態のウィンドウの高さを指定します。
	 */
	private int windowWidth = DEFAULT_WINDOW_WIDTH;
	/**
	 * PCでテストを実行する場合の、初期状態のウィンドウの幅を指定します。
	 */
	private int windowHeight = DEFAULT_WINDOW_HEIGHT;

	/**
	 * テスト対象ページのベースURLを取得します。
	 * 
	 * @return ベースURL
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * テスト実行時のウィンドウの幅を取得します。
	 * 
	 * @return ウィンドウの幅
	 */
	public int getWindowWidth() {
		return windowWidth;
	}

	/**
	 * テスト実行時のウィンドウの高さを取得します。
	 * 
	 * @return ウィンドウの高さ
	 */
	public int getWindowHeight() {
		return windowHeight;
	}

	/**
	 * テスト対象ページのベースURLを設定します。
	 * 
	 * @param baseUrl ベースURL
	 */
	void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	/**
	 * テスト実行時のウィンドウの幅を設定します。
	 * 
	 * @param windowWidth ウィンドウの幅
	 */
	void setWindowWidth(int windowWidth) {
		this.windowWidth = windowWidth;
	}

	/**
	 * テスト実行時のウィンドウの高さを設定します。
	 * 
	 * @param windowHeight ウィンドウの高さ
	 */
	void setWindowHeight(int windowHeight) {
		this.windowHeight = windowHeight;
	}

	@Override
	public String toString() {
		return JSONUtils.toString(this);
	}

}
