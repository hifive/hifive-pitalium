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
package com.htmlhifive.pitalium.common.exception;

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
