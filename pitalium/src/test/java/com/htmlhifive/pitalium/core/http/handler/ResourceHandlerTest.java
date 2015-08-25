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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.htmlhifive.pitalium.core.http.PtlHttpHandler;
import com.sun.net.httpserver.HttpServer;

public class ResourceHandlerTest {

	private static HttpServer server;

	@BeforeClass
	public static void startServer() throws Exception {
		server = HttpServer.create(new InetSocketAddress("localhost", 18080), 0);
		server.setExecutor(Executors.newSingleThreadExecutor());

		PtlHttpHandler annotation = ResourceHandler.class.getAnnotation(PtlHttpHandler.class);
		server.createContext(annotation.value(), new ResourceHandler());
		server.start();
	}

	@AfterClass
	public static void stopServer() throws Exception {
		server.stop(0);
	}

	/**
	 * リソースファイルが存在する場合のテスト
	 */
	@Test
	public void resourceFileExists() throws Exception {
		URL url = new URL("http://localhost:18080/res/ResourceHandlerTest.json");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.connect();

		assertThat(conn.getResponseCode(), is(200));

		String expected = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(
				"pitalium/http/res/ResourceHandlerTest.json"));
		assertThat(IOUtils.toString(conn.getInputStream()), is(expected));

		//        assertThat(conn.getHeaderField("Content-Type"), is("application/json"));
	}

	/**
	 * リソースファイルが存在しない場合のテスト
	 */
	@Test
	public void resourceFileNotExists() throws Exception {
		URL url = new URL("http://localhost:18080/res/FileNotFound");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.connect();

		assertThat(conn.getResponseCode(), is(404));
	}

}