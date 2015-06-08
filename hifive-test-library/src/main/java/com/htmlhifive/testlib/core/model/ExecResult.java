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
package com.htmlhifive.testlib.core.model;

/**
 * テストの実行結果を表す列挙
 */
public enum ExecResult {

	/**
	 * テスト成功
	 */
	SUCCESS {
		@Override
		public boolean isSuccess() {
			return true;
		}
	},
	/**
	 * テスト失敗
	 */
	FAILURE;

	/**
	 * テスト実行結果が成功したかどうかを取得します。
	 * 
	 * @return テスト実行結果が成功の場合true、それ以外はfalse
	 */
	public boolean isSuccess() {
		return false;
	}

}
