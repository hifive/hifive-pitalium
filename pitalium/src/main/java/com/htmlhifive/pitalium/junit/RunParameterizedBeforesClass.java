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

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * @author nakatani
 */
public class RunParameterizedBeforesClass extends Statement {

	private final Statement next;

	private final Object[] parameters;

	private final List<FrameworkMethod> befores;

	public RunParameterizedBeforesClass(Statement next, List<FrameworkMethod> befores, Object[] parameters) {
		this.next = next;
		this.befores = befores;
		this.parameters = parameters;
	}

	@Override
	public void evaluate() throws Throwable {
		for (FrameworkMethod before : befores) {
			before.invokeExplosively(null, parameters);
		}
		next.evaluate();
	}

}
