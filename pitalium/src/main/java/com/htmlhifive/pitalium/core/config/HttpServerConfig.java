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

/**
 * Pitalium HTTPサーバーの設定
 */
@PtlConfiguration
@JsonIgnoreProperties(ignoreUnknown = true)
public class HttpServerConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * テスト実行中のブラウザからPitalium HTTPサーバーへアクセスするためのホスト名
	 */
	private String hostname = "localhost";

	/**
	 * Pitalium HTTPサーバーを実行するポート番号
	 */
	private int port = 8080;

	/**
	 * リクエストを待機するデフォルトのタイムアウト時間（秒）
	 */
	private long awaitTimeout = 30L;

	/**
	 * テスト実行中のブラウザからPitalium HTTPサーバーへアクセスするためのホスト名を取得します。
	 * 
	 * @return テスト実行中のブラウザからPitalium HTTPサーバーへアクセスするためのホスト名
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * テスト実行中のブラウザからPitalium HTTPサーバーへアクセスするためのホスト名を設定します。
	 * 
	 * @param hostname テスト実行中のブラウザからPitalium HTTPサーバーへアクセスするためのホスト名
	 */
	void setHostname(String hostname) {
		this.hostname = hostname;
	}

	/**
	 * Pitalium HTTPサーバーを実行するポート番号を取得します。
	 * 
	 * @return Pitalium HTTPサーバーを実行するポート番号
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Pitalium HTTPサーバーを実行するポート番号を設定します。
	 * 
	 * @param port Pitalium HTTPサーバーを実行するポート番号
	 */
	void setPort(int port) {
		this.port = port;
	}

	/**
	 * リクエストを待機するデフォルトのタイムアウト時間（秒）を取得します。
	 * 
	 * @return リクエストを待機するデフォルトのタイムアウト時間
	 */
	public long getAwaitTimeout() {
		return awaitTimeout;
	}

	/**
	 * リクエストを待機するデフォルトのタイムアウト時間（秒）を設定します
	 * 
	 * @param awaitTimeout リクエストを待機するデフォルトのタイムアウト時間
	 */
	void setAwaitTimeout(long awaitTimeout) {
		this.awaitTimeout = awaitTimeout;
	}

	@Override
	public String toString() {
		return JSONUtils.toString(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		HttpServerConfig that = (HttpServerConfig) o;

		if (port != that.port) {
			return false;
		}
		if (awaitTimeout != that.awaitTimeout) {
			return false;
		}
		return !(hostname != null ? !hostname.equals(that.hostname) : that.hostname != null);
	}

	@Override
	public int hashCode() {
		int result = hostname != null ? hostname.hashCode() : 0;
		result = 31 * result + port;
		result = 31 * result + (int) (awaitTimeout ^ (awaitTimeout >>> 32));
		return result;
	}

	/**
	 * {@link HttpServerConfig}を生成するビルダーオブジェクトの新規インスタンスを取得します。
	 * 
	 * @return ビルダーオブジェクトのインスタンス
	 */
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		final HttpServerConfig config = new HttpServerConfig();

		/**
		 * 設定した値で{@link HttpServerConfig}の新規インスタンスを生成します。
		 * 
		 * @return {@link HttpServerConfig}の新規インスタンス
		 */
		public HttpServerConfig build() {
			HttpServerConfig c = new HttpServerConfig();
			c.setHostname(config.hostname);
			c.setPort(config.port);
			c.setAwaitTimeout(config.awaitTimeout);
			return config;
		}

		/**
		 * テスト実行中のブラウザからPitalium HTTPサーバーへアクセスするためのホスト名を設定します。
		 * 
		 * @param hostname テスト実行中のブラウザからPitalium HTTPサーバーへアクセスするためのホスト名
		 * @return ビルダーインスタンス自身
		 */
		public Builder hostname(String hostname) {
			config.hostname = hostname;
			return this;
		}

		/**
		 * Pitalium HTTPサーバーを実行するポート番号を設定します。
		 * 
		 * @param port Pitalium HTTPサーバーを実行するポート番号
		 * @return ビルダーインスタンス自身
		 */
		public Builder port(int port) {
			config.port = port;
			return this;
		}

		/**
		 * リクエストを待機するデフォルトのタイムアウト時間（秒）を設定します
		 * 
		 * @param awaitTimeout リクエストを待機するデフォルトのタイムアウト時間
		 * @return ビルダーインスタンス自身
		 */
		public Builder awaitTimeout(long awaitTimeout) {
			config.awaitTimeout = awaitTimeout;
			return this;
		}

	}

}
