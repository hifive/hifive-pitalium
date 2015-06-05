/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
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
