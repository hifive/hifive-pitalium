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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.htmlhifive.pitalium.core.http.PtlHttpHandler;
import com.htmlhifive.pitalium.core.http.PtlHttpObjectMonitor;
import com.sun.net.httpserver.HttpExchange;

@PtlHttpHandler("/takeScreenshot")
public class TakeScreenshotHandler extends AbstractHttpHandler {

	private static final Logger LOG = LoggerFactory.getLogger(TakeScreenshotHandler.class);

	/**
	 * {@link PtlHttpObjectMonitor}に利用する種別
	 */
	public static final String MONITOR_TYPE = "takeScreenshot";

	@Override
	protected void handle(HttpExchange httpExchange, Map<String, String> queryStrings) throws IOException {
		String capabilitiesId = queryStrings.get("id");
		if (capabilitiesId == null) {
			LOG.error("\"id\" query parameter is required for HTTP request \"/takeScreenshot\"");
			sendBadRequest(httpExchange);
			return;
		}

		int id;
		try {
			id = Integer.parseInt(capabilitiesId);
		} catch (NumberFormatException e) {
			LOG.error("{} is not number", capabilitiesId);
			sendBadRequest(httpExchange);
			return;
		}

		PtlHttpObjectMonitor.getMonitor(id, MONITOR_TYPE).unlock();
		sendNoResponse(httpExchange, HttpURLConnection.HTTP_OK);
	}

}
