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

package com.htmlhifive.pitalium.core.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.htmlhifive.pitalium.common.exception.TestRuntimeException;
import com.htmlhifive.pitalium.core.config.HttpServerConfig;
import com.htmlhifive.pitalium.core.config.PtlTestConfig;
import com.htmlhifive.pitalium.core.http.handler.ResourceHandler;
import com.htmlhifive.pitalium.core.http.handler.UnlockThreadRequestHandler;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * テストケース実行中にPitaliumライブラリがHTTPアクセスによる制御を受けるためのHTTPサーバー
 * 
 * @author nakatani
 */
public class PtlHttpServer {

	private static final Logger LOG = LoggerFactory.getLogger(PtlHttpServer.class);

	private static HttpServerConfig config;
	private static Class<? extends HttpHandler>[] handlerClasses;
	private static HttpServer server;
	private static ExecutorService executor;

	static {
		initialize(PtlTestConfig.getInstance().getHttpServerConfig());
	}

	public static void initialize(HttpServerConfig config) {
		// TODO initialize..where..??
		// TODO collect handlers by reflection
		initialize(config, ResourceHandler.class, UnlockThreadRequestHandler.class);
	}

	@SuppressWarnings("unchecked")
	public static void initialize(HttpServerConfig config, Class... handlerClasses) {
		LOG.trace("initialize");

		PtlHttpServer.config = config;
		PtlHttpServer.handlerClasses = (Class<? extends HttpHandler>[]) handlerClasses;
	}

	/**
	 * HTTPサーバーが開始済みかどうかを取得します。
	 * 
	 * @return 開始済みの場合true、開始していない場合false
	 */
	public static synchronized boolean isStarted() {
		return server != null;
	}

	/**
	 * HTTPサーバーを開始します。既に開始済みの場合特に何もしません。
	 */
	public static synchronized void start() {
		if (server != null) {
			return;
		}
		if (config == null || handlerClasses == null) {
			throw new TestRuntimeException("Not initialized");
		}

		InetSocketAddress address = new InetSocketAddress(config.getPort());
		try {
			server = HttpServer.create(address, -1);
		} catch (IOException e) {
			LOG.error("HttpServer creation error. Check port [" + config.getPort() + "] usage.", e);
			throw new TestRuntimeException(e);
		}

		server.setExecutor(executor = Executors.newSingleThreadExecutor());
		for (Class<? extends HttpHandler> clss : handlerClasses) {
			PtlHttpHandler annotation = clss.getAnnotation(PtlHttpHandler.class);
			if (annotation == null || annotation.value().isEmpty()) {
				LOG.warn("Class [{}] is not annotated PtlHttpServer", clss.getName());
				continue;
			}

			try {
				HttpHandler handler = clss.newInstance();
				server.createContext(annotation.value(), handler);
			} catch (InstantiationException e) {
				throw new TestRuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new TestRuntimeException(e);
			}
		}

		server.start();
	}

	/**
	 * HTTPサーバーを終了します。
	 */
	public static synchronized void stop() {
		if (server == null) {
			LOG.debug("HttpServer is not started.");
			return;
		}

		server.stop(0);
		executor.shutdown();

		server = null;
		executor = null;
	}

}
