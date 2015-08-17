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

package com.htmlhifive.pitalium.junit;

import org.junit.runner.Runner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.parameterized.ParametersRunnerFactory;
import org.junit.runners.parameterized.TestWithParameters;

/**
 * {@link PtlBlockJUnit4ClassRunnerWithParameters}Çê∂ê¨Ç∑ÇÈ{@link ParametersRunnerFactory}
 *
 * @author nakatani
 */
public class PtlBlockJUnit4ClassRunnerWithParametersFactory implements ParametersRunnerFactory {

	@Override
	public Runner createRunnerForTestWithParameters(TestWithParameters test) throws InitializationError {
		return new PtlBlockJUnit4ClassRunnerWithParameters(test);
	}

}
