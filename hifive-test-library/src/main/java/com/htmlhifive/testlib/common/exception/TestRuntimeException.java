/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.common.exception;

/**
 * 実行時に発生する例外
 */
public class TestRuntimeException extends RuntimeException {

	/**
	 * コンストラクタ
	 */
	public TestRuntimeException() {
		super();
	}

	/**
	 * コンストラクタ
	 * 
	 * @param message 例外メッセージ
	 * @param cause 例外の原因
	 */
	public TestRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param message 例外メッセージ
	 */
	public TestRuntimeException(String message) {
		super(message);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param cause 例外の原因
	 */
	public TestRuntimeException(Throwable cause) {
		super(cause);
	}
}
