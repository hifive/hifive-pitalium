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

package com.htmlhifive.pitalium.core.rules;

import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.htmlhifive.pitalium.common.exception.TestRuntimeException;
import com.htmlhifive.pitalium.core.selenium.PtlCapabilities;
import com.htmlhifive.pitalium.core.selenium.PtlWebDriverManager;
import com.htmlhifive.pitalium.junit.ParameterizedTestWatcher;

/**
 * パラメーター単位でWebDriverを自動クローズするクラス<br />
 * 以下のサンプルの様に利用してください。
 * 
 * <pre>
 * &#064;RunWith(Parameterized.class)
 * &#064;Parameterized.UseParametersRunnerFactory(PtlBlockJUnit4ClassRunnerWithParametersFactory.class)
 * &#064;PtlWebDriverStrategy(reuse = true)
 * public class SampleTest {
 * 
 *     <b>&#064;ParameterizedClassRule</b>
 *     <b>public static PtlWebDriverCloser webDriverCloser = new PtlWebDriverCloser();</b>
 * 
 * }
 * </pre>
 * 
 * @author nakatani
 */
public class PtlWebDriverCloser extends ParameterizedTestWatcher {

	private static final Logger LOG = LoggerFactory.getLogger(PtlWebDriverCloser.class);

	@Override
	protected void finished(Description description, Object[] params) {
		if (!(params[0] instanceof PtlCapabilities)) {
			throw new TestRuntimeException("Parameter[0] must be PtlCapabilities.");
		}

		PtlCapabilities capabilities = (PtlCapabilities) params[0];
		Class<?> clss = description.getTestClass();

		PtlWebDriverManager.getInstance().closeWebDriverSession(clss, capabilities);
	}

}
