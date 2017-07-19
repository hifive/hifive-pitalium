/*
 * Copyright (C) 2015-2017 NS Solutions Corpimport java.io.Serializable;
 the Apache License, Version 2.0 (the "License");
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
package com.htmlhifive.pitalium.image.model;

import java.io.Serializable;

/**
 * 比較結果を保持するクラス
 */
public abstract class ImageComparedResult implements Serializable {

	/**
	 * 比較した結果、画像が一致したか否かを調べる。
	 *
	 * @return 比較結果。一致していれば（差分がなければ）true。
	 */
	public abstract boolean isSucceeded();

	/**
	 * 比較した結果、画像が一致しないかどうかを調べる。
	 *
	 * @return 比較結果。一致しなければ（差分があれば）true。
	 */
	public boolean isFailed() {
		return !isSucceeded();
	}
}
