/*
 * Copyright (C) 2016 NS Solutions Corporation
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.htmlhifive.pitalium.common.util.JSONUtils;

/**
 * テスト対象のページの共通設定を保持するクラス
 */
@PtlConfiguration
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

	/**
	 * {@link TestAppConfig}を構築するためのビルダーオブジェクトを取得します。
	 * 
	 * @return ビルダーオブジェクト
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * テスト対象アプリケーションの設定を構築するクラス。
	 */
	public static class Builder {

		/**
		 * 設定を保持するための{@link TestAppConfig}
		 */
		private final TestAppConfig config = new TestAppConfig();

		/**
		 * このビルダーに対して指定したテスト対象アプリケーションの設定を持つオブジェクトを生成します。
		 * 
		 * @return テスト対象アプリケーションの設定
		 */
		public TestAppConfig build() {
			TestAppConfig buildConfig = new TestAppConfig();
			buildConfig.setBaseUrl(this.config.baseUrl);
			buildConfig.setWindowWidth(this.config.windowWidth);
			buildConfig.setWindowHeight(this.config.windowHeight);
			return buildConfig;
		}

		/**
		 * テスト対象アプリケーションのベースURLを設定します。
		 * 
		 * @param baseUrl ベースURL
		 * @return このビルダーオブジェクト自身
		 */
		public Builder baseUrl(String baseUrl) {
			config.baseUrl = baseUrl;
			return this;
		}

		/**
		 * テスト実行時のウィンドウ幅を設定します。
		 * 
		 * @param windowWidth ウィンドウ幅
		 * @return このビルダーオブジェクト自身
		 */
		public Builder windowWidth(int windowWidth) {
			config.windowWidth = windowWidth;
			return this;
		}

		/**
		 * テスト実行時のウィンドウの高さを設定します。
		 * 
		 * @param windowHeight ウィンドウの高さ
		 * @return このビルダーオブジェクト自身
		 */
		public Builder windowHeight(int windowHeight) {
			config.windowHeight = windowHeight;
			return this;
		}

	}

}
