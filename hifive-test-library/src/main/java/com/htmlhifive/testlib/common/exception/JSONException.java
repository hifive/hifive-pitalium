/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.common.exception;

/**
 * JSONのIOエラー
 */
public class JSONException extends TestRuntimeException {

	/**
	 * コンストラクタ
	 * 
	 * @param cause 例外の原因
	 */
	public JSONException(Throwable cause) {
		super(cause);
	}

}
