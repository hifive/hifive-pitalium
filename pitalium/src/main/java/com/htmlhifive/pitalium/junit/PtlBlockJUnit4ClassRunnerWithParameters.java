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

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.parameterized.BlockJUnit4ClassRunnerWithParameters;
import org.junit.runners.parameterized.TestWithParameters;

/**
 * パラメータ付きテストクラスを実行するためのRunner
 * 
 * @author nakatani
 */
public class PtlBlockJUnit4ClassRunnerWithParameters extends BlockJUnit4ClassRunnerWithParameters {

	private final Object[] parameters;

	/**
	 * コンストラクタ
	 * 
	 * @param test パラメータ付きテストクラス
	 * @throws InitializationError 初期化に失敗した場合
	 */
	public PtlBlockJUnit4ClassRunnerWithParameters(TestWithParameters test) throws InitializationError {
		super(test);
		parameters = test.getParameters().toArray(new Object[test.getParameters().size()]);
	}

	@Override
	protected void collectInitializationErrors(List<Throwable> errors) {
		super.collectInitializationErrors(errors);

		validateParameterizedBeforeClass(errors);
		validateParameterizedAfterClass(errors);
		validateParameterizedClassRules(errors);
	}

	/**
	 * {@link ParameterizedBeforeClass}が設定されているフィールドをチェックします。
	 * 
	 * @param errors エラーのリスト。チェックした結果エラーがあった場合、このリストに追加される
	 */
	protected void validateParameterizedBeforeClass(List<Throwable> errors) {
		for (FrameworkMethod method : getTestClass().getAnnotatedMethods(ParameterizedBeforeClass.class)) {
			if (!method.isStatic()) {
				errors.add(new Exception("Method " + method.getName() + "() should be static"));
			}
			if (!method.isPublic()) {
				errors.add(new Exception("Method " + method.getName() + "() should be public"));
			}
			// このメソッドはコンストラクタのsuper内部で実行されるため、パラメーター数の取得ができない
			//			if (method.getMethod().getParameterTypes().length != parameters.length) {
			//				errors.add(new Exception(
			//						"Method " + method.getName() + "() should have " + parameters.length + " parameters"));
			//			}
			if (method.getType() != Void.TYPE) {
				errors.add(new Exception("Method " + method.getName() + "() should be void"));
			}
		}
	}

	/**
	 * {@link ParameterizedAfterClass}が設定されているフィールドをチェックします。
	 * 
	 * @param errors エラーのリスト。チェックした結果エラーがあった場合、このリストに追加される
	 */
	protected void validateParameterizedAfterClass(List<Throwable> errors) {
		for (FrameworkMethod method : getTestClass().getAnnotatedMethods(ParameterizedAfterClass.class)) {
			if (!method.isStatic()) {
				errors.add(new Exception("Method " + method.getName() + "() should be static"));
			}
			if (!method.isPublic()) {
				errors.add(new Exception("Method " + method.getName() + "() should be public"));
			}
			// このメソッドはコンストラクタのsuper内部で実行されるため、パラメーター数の取得ができない
			//			if (method.getMethod().getParameterTypes().length != parameters.length) {
			//				errors.add(new Exception(
			//						"Method " + method.getName() + "() should have " + parameters.length + " parameters"));
			//			}
			if (method.getType() != Void.TYPE) {
				errors.add(new Exception("Method " + method.getName() + "() should be void"));
			}
		}
	}

	/**
	 * {@link ParameterizedClassRule}が設定されているフィールドまたはメソッドをチェックします。
	 * 
	 * @param errors エラーのリスト。チェックした結果エラーがあった場合、このリストに追加される
	 */
	protected void validateParameterizedClassRules(List<Throwable> errors) {
		for (FrameworkMethod method : getTestClass().getAnnotatedMethods(ParameterizedClassRule.class)) {
			if (!method.isStatic()) {
				errors.add(new Exception("Method " + method.getName() + "() should be static"));
			}
			if (!method.isPublic()) {
				errors.add(new Exception("Method " + method.getName() + "() should be public"));
			}
			if (method.getMethod().getParameterTypes().length != 0) {
				errors.add(new Exception("Method " + method.getName() + "() should have no parameters"));
			}
			if (!ParameterizedTestRule.class.isAssignableFrom(method.getType())) {
				errors.add(new Exception("Method " + method.getName()
						+ "() must return an implementation of ParameterizedTestRule"));
			}
		}

		for (FrameworkField field : getTestClass().getAnnotatedFields(ParameterizedClassRule.class)) {
			if (!field.isStatic()) {
				errors.add(new Exception("Field " + field.getName() + " should be static"));
			}
			if (!field.isPublic()) {
				errors.add(new Exception("Field " + field.getName() + " should be public"));
			}
			if (!ParameterizedTestRule.class.isAssignableFrom(field.getType())) {
				errors.add(new Exception("Field " + field.getName() + " must implement ParameterizedTestRule"));
			}
		}
	}

	@Override
	protected Statement childrenInvoker(RunNotifier notifier) {
		Statement statement = super.childrenInvoker(notifier);
		statement = withParameterizedBeforeClasses(statement);
		statement = withParameterizedAfterClasses(statement);
		statement = withParameterizedClassRules(statement);
		return statement;
	}

	/**
	 * テストクラス中の{@link ParameterizedBeforeClass}が付与されたメソッドを含めてテストを実行します。
	 * 
	 * @param statement 実行用{@link Statement}
	 * @return {@link ParameterizedBeforeClass}が付与されたメソッドを含めた{@link Statement}
	 */
	protected Statement withParameterizedBeforeClasses(Statement statement) {
		List<FrameworkMethod> befores = getTestClass().getAnnotatedMethods(ParameterizedBeforeClass.class);
		return befores.isEmpty() ? statement : new RunParameterizedBeforesClass(statement, befores, parameters);
	}

	/**
	 * テストクラス中の{@link ParameterizedAfterClass}が付与されたメソッドを含めてテストを実行します。
	 * 
	 * @param statement 実行用{@link Statement}
	 * @return {@link ParameterizedAfterClass}が付与されたメソッドを含めた{@link Statement}
	 */
	protected Statement withParameterizedAfterClasses(Statement statement) {
		List<FrameworkMethod> afters = getTestClass().getAnnotatedMethods(ParameterizedAfterClass.class);
		return afters.isEmpty() ? statement : new RunParameterizedAftersClass(statement, afters, parameters);
	}

	/**
	 * テストクラス中の{@link ParameterizedTestRule}が付与されたメソッドまたはフィールドを含めてテストを実行します。
	 * 
	 * @param statement 実行用{@link Statement}
	 * @return {@link ParameterizedTestRule}が付与されたメソッドまたはフィールドを含めた{@link Statement}
	 */
	protected Statement withParameterizedClassRules(Statement statement) {
		List<ParameterizedTestRule> classRules = parameterizedClassRules();
		return classRules.isEmpty() ? statement : new RunParameterizedRules(statement, classRules,
				getTestDescription(), parameters);
	}

	/**
	 * テストクラス内の{@link ParameterizedClassRule}が付与されたメソッドまたはフィールドを集めて返します。
	 * 
	 * @return {@link ParameterizedClassRule}が付与されたメソッドまたはフィールドのリスト
	 */
	protected List<ParameterizedTestRule> parameterizedClassRules() {
		List<ParameterizedTestRule> result = getTestClass().getAnnotatedMethodValues(null,
				ParameterizedClassRule.class, ParameterizedTestRule.class);
		result.addAll(getTestClass().getAnnotatedFieldValues(null, ParameterizedClassRule.class,
				ParameterizedTestRule.class));
		return result;
	}

	/**
	 * 実行されるテストのdescriptionを取得します。
	 * 
	 * @return description
	 */
	public Description getTestDescription() {
		Description description = getDescription();
		Description result = Description.createTestDescription(getTestClass().getJavaClass(), getName(),
				getRunnerAnnotations());
		for (Description d : description.getChildren()) {
			result.addChild(d);
		}
		return result;
	}

}
