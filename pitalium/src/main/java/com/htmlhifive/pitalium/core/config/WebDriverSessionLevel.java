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

package com.htmlhifive.pitalium.core.config;

/**
 * WebDriverセッションの利用レベル
 *
 * @author nakatani
 */
public enum WebDriverSessionLevel {

	/**
	 * テストケース毎に個別のWebDriverセッションを利用します。テストケース開始時にWebDriverセッションが開始され、テストケース終了時に自動クローズされます。
	 */
	TEST_CASE,
	/**
	 * テストクラス内の全てのテストケースで同一のWebDriverセッションを利用します。テストクラスの開始時にWebDriverセッションが開始され、 テストクラスの全ケース終了時に自動クローズされます。
	 */
	TEST_CLASS,
	/**
	 * テスト全体を通じて共通のWebDriverセッションを利用します。WebDriverセッションは必要に応じて開始されますが自動でクローズはされません。
	 */
	GLOBAL,
	/**
	 * テスト情報をファイルに格納し、次回再利用します。
	 */
	PERSISTED

}
