/*
 * Copyright (C) 2016 NS Solutions Corporation
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

import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

/**
 * TODO javadoc
 * 
 * @see org.junit.rules.TestWatcher
 * @author nakatani
 */
public abstract class ParameterizedTestWatcher implements ParameterizedTestRule {

	@Override
	public Statement apply(final Statement base, final Description description, final Object[] params) {
		return new Statement() {
			public void evaluate() throws Throwable {
				ArrayList<Throwable> errors = new ArrayList<Throwable>();
				ParameterizedTestWatcher.this.startingQuietly(description, errors, params);

				try {
					base.evaluate();
					ParameterizedTestWatcher.this.succeededQuietly(description, errors, params);
				} catch (AssumptionViolatedException var7) {
					errors.add(var7);
					ParameterizedTestWatcher.this.skippedQuietly(var7, description, errors, params);
				} catch (Throwable var8) {
					errors.add(var8);
					ParameterizedTestWatcher.this.failedQuietly(var8, description, errors, params);
				} finally {
					ParameterizedTestWatcher.this.finishedQuietly(description, errors, params);
				}

				MultipleFailureException.assertEmpty(errors);
			}
		};
	}

	private void succeededQuietly(Description description, List<Throwable> errors, Object[] params) {
		try {
			this.succeeded(description, params);
		} catch (Throwable var4) {
			errors.add(var4);
		}

	}

	private void failedQuietly(Throwable e, Description description, List<Throwable> errors, Object[] params) {
		try {
			this.failed(e, description, params);
		} catch (Throwable var5) {
			errors.add(var5);
		}

	}

	private void skippedQuietly(AssumptionViolatedException e, Description description, List<Throwable> errors,
			Object[] params) {
		try {
			if (e instanceof org.junit.AssumptionViolatedException) {
				this.skipped((org.junit.AssumptionViolatedException) e, description, params);
			} else {
				this.skipped(e, description, params);
			}
		} catch (Throwable var5) {
			errors.add(var5);
		}

	}

	private void startingQuietly(Description description, List<Throwable> errors, Object[] params) {
		try {
			this.starting(description, params);
		} catch (Throwable var4) {
			errors.add(var4);
		}

	}

	private void finishedQuietly(Description description, List<Throwable> errors, Object[] params) {
		try {
			this.finished(description, params);
		} catch (Throwable var4) {
			errors.add(var4);
		}

	}

	protected void succeeded(Description description, Object[] params) {
	}

	protected void failed(Throwable e, Description description, Object[] params) {
	}

	protected void skipped(org.junit.AssumptionViolatedException e, Description description, Object[] params) {
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	protected void skipped(AssumptionViolatedException e, Description description, Object[] params) {
	}

	protected void starting(Description description, Object[] params) {
	}

	protected void finished(Description description, Object[] params) {
	}

}
