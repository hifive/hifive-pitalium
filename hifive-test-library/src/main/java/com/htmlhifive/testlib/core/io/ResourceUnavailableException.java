/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.core.io;

import com.htmlhifive.testlib.common.exception.TestRuntimeException;

/**
 * {@link Persister}で取得対象のリソースが見つからない場合の例外
 */
public class ResourceUnavailableException extends TestRuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * コンストラクタ
	 */
	public ResourceUnavailableException() {
	}

	/**
	 * コンストラクタ
	 * 
	 * @param message 例外メッセージ
	 */
	public ResourceUnavailableException(String message) {
		super(message);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param cause 例外の原因
	 */
	public ResourceUnavailableException(Throwable cause) {
		super(cause);
	}

}
