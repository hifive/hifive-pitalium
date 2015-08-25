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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.htmlhifive.pitalium.core.PtlTestBase;
import com.htmlhifive.pitalium.core.config.HttpServerConfig;

public class PtlHttpServerUtilsTest extends PtlTestBase {

	private static final int PORT = 18080;

	@BeforeClass
	public static void startServer() throws Exception {
		PtlHttpServer.start();
	}

	@AfterClass
	public static void stopServer() throws Exception {
		PtlHttpServer.stop();
	}

	@Test
	public void testLoadPitaliumFunctions() throws Exception {
		driver.get(null);
		HttpServerConfig config = HttpServerConfig.builder().port(PORT).build();
		PtlHttpServerUtils.loadPitaliumFunctions(driver, config);

		String id = driver.executeJavaScript("return pitalium.capabilitiesId();");
		assertThat(id, is(String.valueOf(capabilities.getId())));
	}

	@Test
	public void testIsPitaliumFunctionsLoaded() throws Exception {
		driver.get(null);
		assertThat(PtlHttpServerUtils.isPitaliumFunctionsLoaded(driver), is(false));

		HttpServerConfig config = HttpServerConfig.builder().port(PORT).build();
		PtlHttpServerUtils.loadPitaliumFunctions(driver, config);
		assertThat(PtlHttpServerUtils.isPitaliumFunctionsLoaded(driver), is(true));
	}

}