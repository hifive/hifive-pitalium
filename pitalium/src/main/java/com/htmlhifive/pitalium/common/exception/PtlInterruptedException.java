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
 * スレッドウェイトのタイムアウト時に発生する例外
 * 
 * @see InterruptedException
 * @author nakatani
 */
public class PtlInterruptedException extends TestRuntimeException {

	private static final long serialVersionUID = 1L;

	public PtlInterruptedException(InterruptedException cause) {
		super(cause);
	}

	public PtlInterruptedException(String message, InterruptedException cause) {
		super(message, cause);
	}

}
