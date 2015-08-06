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

import java.util.List;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.parameterized.BlockJUnit4ClassRunnerWithParameters;
import org.junit.runners.parameterized.TestWithParameters;

/**
 * @author nakatani
 */
public class PtlBlockJUnit4ClassRunnerWithParameters extends BlockJUnit4ClassRunnerWithParameters {

	private final Object[] parameters;

	public PtlBlockJUnit4ClassRunnerWithParameters(TestWithParameters test) throws InitializationError {
		super(test);
		parameters = test.getParameters().toArray(new Object[test.getParameters().size()]);
	}

	@Override
	protected Statement childrenInvoker(RunNotifier notifier) {
		Statement statement = super.childrenInvoker(notifier);
		statement = withParameterizedBeforesClass(statement);
		statement = withParameterizedAftersClass(statement);
		return statement;
	}

	protected Statement withParameterizedBeforesClass(Statement statement) {
		List<FrameworkMethod> befores = getTestClass().getAnnotatedMethods(ParameterizedBeforeClass.class);
		return befores.isEmpty() ? statement : new RunParameterizedBeforesClass(statement, befores, parameters);
	}

	protected Statement withParameterizedAftersClass(Statement statement) {
		List<FrameworkMethod> afters = getTestClass().getAnnotatedMethods(ParameterizedAfterClass.class);
		return afters.isEmpty() ? statement : new RunParameterizedAftersClass(statement, afters, parameters);
	}

}
