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

package com.htmlhifive.pitalium.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link com.htmlhifive.pitalium.core.selenium.PtlWebDriver}の利用戦略設定を行います。
 * 
 * @author nakatani
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PtlWebDriverStrategy {

	/**
	 * WebDriverセッションの利用範囲を指定する列挙値
	 */
	enum SessionLevel {
		/**
		 * {@link PtlWebDriverStrategy#sessionLevel()}のデフォルト値です。
		 * {@link com.htmlhifive.pitalium.core.config.EnvironmentConfig#reuseDriverForAllClasses}の設定を利用します。
		 */
		DEFAULT, /**
					 * テストケース毎に個別のWebDriverセッションを利用します。テストケース開始時にWebDriverセッションが開始され、テストケース終了時に自動クローズされます。
					 * {@link com.htmlhifive.pitalium.core.config.EnvironmentConfig#reuseDriverForAllClasses}
					 * の設定をクラス単位で上書きします。
					 */
		TEST_CASE, /**
					 * テストクラス内の全てのテストケースで同一のWebDriverセッションを利用します。テストクラスの開始時にWebDriverセッションが開始され、
					 * テストクラスの全ケース終了時に自動クローズされます。
					 * {@link com.htmlhifive.pitalium.core.config.EnvironmentConfig#reuseDriverForAllClasses}
					 * の設定をクラス単位で上書きします。
					 */
		TEST_CLASS;
	}

	/**
	 * WebDriverセッションの利用範囲を指定します。
	 * 
	 * @return WebDriverセッションの利用範囲
	 */
	SessionLevel sessionLevel() default SessionLevel.DEFAULT;

}
