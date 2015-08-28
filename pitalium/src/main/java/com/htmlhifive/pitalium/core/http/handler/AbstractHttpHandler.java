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

package com.htmlhifive.pitalium.core.http.handler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * {@link HttpHandler}の簡易実装クラス。CORS対応やQueryStringのパースを行う。
 * 
 * @author nakatani
 */
public abstract class AbstractHttpHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		addCORSHeaders(httpExchange);
		handle(httpExchange, parseQueryString(httpExchange.getRequestURI().getQuery()));
	}

	/**
	 * Cross Origin Resource Sharingを可能にするヘッダーを追加します。
	 */
	protected void addCORSHeaders(HttpExchange httpExchange) throws IOException {
		Headers headers = httpExchange.getResponseHeaders();
		headers.add("Access-Control-Allow-Origin", "*");
		headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		headers.add("Access-Control-Max-Age", "60");
	}

	/**
	 * HTTPリクエストを処理します。
	 * 
	 * @param httpExchange
	 * @param queryStrings ブラウザから渡されたQueryStringのマップ
	 * @throws IOException
	 * @see #handle(HttpExchange)
	 */
	protected abstract void handle(HttpExchange httpExchange, Map<String, String> queryStrings) throws IOException;

	/**
	 * BODYを空の状態でレスポンスを送信します。
	 *
	 * @param httpExchange
	 * @param responseCode レスポンスのステータスコード
	 * @throws IOException
	 */
	protected void sendNoResponse(HttpExchange httpExchange, int responseCode) throws IOException {
		httpExchange.sendResponseHeaders(responseCode, 0);
		httpExchange.close();
	}

	/**
	 * ステータスコード400で空のレスポンスを送信します。
	 *
	 * @param httpExchange
	 * @throws IOException
	 */
	protected void sendBadRequest(HttpExchange httpExchange) throws IOException {
		sendNoResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST);
	}

	/**
	 * ステータスコード404で空のレスポンスを送信します。
	 * 
	 * @param httpExchange
	 * @throws IOException
	 */
	protected void sendNotFound(HttpExchange httpExchange) throws IOException {
		sendNoResponse(httpExchange, HttpURLConnection.HTTP_NOT_FOUND);
	}

	/**
	 * クエリストリングをパースします。
	 * 
	 * @param queryString パース前のクエリストリング
	 * @return パースしてKey-Valueペア
	 */
	protected Map<String, String> parseQueryString(String queryString) {
		Map<String, String> map = new HashMap<String, String>();
		if (Strings.isEmpty(queryString)) {
			return map;
		}

		for (String s : queryString.split("&")) {
			if (s.isEmpty()) {
				continue;
			}

			String[] strings = s.split("=");
			map.put(strings[0], strings[1]);
		}

		return map;
	}

}
