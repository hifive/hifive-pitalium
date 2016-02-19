/*
 * Copyright (C) 2015-2016 NS Solutions Corporation
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

import java.util.ArrayList;
import java.util.List;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

/**
 * @author nakatani
 */
public class RunParameterizedAftersClass extends Statement {

	private final Statement next;

	private final Object[] parameters;

	private final List<FrameworkMethod> afters;

	public RunParameterizedAftersClass(Statement next, List<FrameworkMethod> afters, Object[] parameters) {
		this.next = next;
		this.afters = afters;
		this.parameters = parameters;
	}

	@Override
	public void evaluate() throws Throwable {
		List<Throwable> errors = new ArrayList<Throwable>();
		try {
			next.evaluate();
		} catch (Throwable e) {
			errors.add(e);
		} finally {
			for (FrameworkMethod each : afters) {
				try {
					each.invokeExplosively(null, parameters);
				} catch (Throwable e) {
					errors.add(e);
				}
			}
		}
		MultipleFailureException.assertEmpty(errors);
	}

}