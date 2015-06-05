/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.common.exception;

/**
 * 未実装の機能を利用した際に発生する例外
 */
public class NotImplementedException extends TestRuntimeException {

	/**
	 * コンストラクタ
	 */
	public NotImplementedException() {
	}

	/**
	 * コンストラクタ
	 * 
	 * @param message 例外メッセージ
	 */
	public NotImplementedException(String message) {
		super(message);
	}
}
