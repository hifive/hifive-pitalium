/*
 * Copyright (C) 2015-2017 NS Solutions Corporation
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.ErrorCodes;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.http.W3CHttpCommandCodec;
import org.openqa.selenium.remote.http.W3CHttpResponseCodec;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class ReusableWebDriver extends RemoteWebDriver {
	private static final String FILE_NAME = "store.json";
	private Map<String, String> sessionIdMap = new HashMap<String, String>();
	private RemoteWebDriver driver;

	public ReusableWebDriver(URL remoteAddress, PtlCapabilities capabilities) {
		//		super(remoteAddress, capabilities);
		driver = null;
		String str = capabilities.getPlatform().name() + capabilities.getPlatformName()
				+ capabilities.getPlatformVersion() + capabilities.getVersion() + capabilities.getBrowserName()
				+ capabilities.getDeviceName();

		try {
			// セッション情報読み込み
			Gson gson = new Gson();
			Type listType = new TypeToken<HashMap<String, String>>() {
			}.getType();
			JsonReader reader = new JsonReader(new FileReader(FILE_NAME));
			this.sessionIdMap = gson.fromJson(reader, listType);

			// Check Session is exist
			HttpURLConnection con = null;
			try {
				String storedSession = this.sessionIdMap.get(str);
				URL myurl = new URL("http://localhost:4444/wd/hub/session/" + storedSession);
				con = (HttpURLConnection) myurl.openConnection();

				con.setRequestMethod("GET");

				StringBuilder content;
				InputStreamReader reponse = new InputStreamReader(con.getInputStream());
				try (BufferedReader in = new BufferedReader(reponse)) {
					String line;
					content = new StringBuilder();
					while ((line = in.readLine()) != null) {
						content.append(line);
						content.append(System.lineSeparator());
					}
				}

				JsonParser parser = new JsonParser();
				JsonObject sessions = (JsonObject) parser.parse(content.toString());
				int status = sessions.get("status").getAsInt();
				if (status == 0) {
					CommandExecutor executor = new CustomHttpCommandExecutor(remoteAddress) {

						@Override
						public Response execute(Command command) throws IOException {
							Response response = null;
							if (command.getName().equals("newSession")) {
								response = new Response();
								response.setSessionId(storedSession.toString());
								response.setStatus(ErrorCodes.SUCCESS);
								response.setState(ErrorCodes.SUCCESS_STRING);
								response.setValue(Collections.<String, String> emptyMap());

								this.commandCodec = new W3CHttpCommandCodec();
								this.responseCodec = new W3CHttpResponseCodec();
							} else {
								response = super.execute(command);
							}
							return response;
						}
					};

					new RemoteWebDriver(executor, capabilities);
				}
			} catch (Exception e) {
				System.out.println(e);
			} finally {
				con.disconnect();
			}
		} catch (Exception e) {
		}

		if (driver == null) {
			try {
				driver = new RemoteWebDriver(new CustomHttpCommandExecutor(new URL("http://localhost:4444/wd/hub")),
						capabilities);
				this.sessionIdMap.put(str, driver.getSessionId().toString());
			} catch (Exception e) {

			}
		}

	}

	@Override
	public void quit() {
		try {
			//　セッション情報出力
			JsonWriter writer = new JsonWriter(new FileWriter(FILE_NAME));
			writer.setIndent("  ");

			writer.beginObject();
			for (Entry<String, String> entry : this.sessionIdMap.entrySet()) {
				writer.name(entry.getKey()).value(entry.getValue());
			}
			writer.endObject();
			writer.close();
		} catch (Exception e) {

		}
	}
}
