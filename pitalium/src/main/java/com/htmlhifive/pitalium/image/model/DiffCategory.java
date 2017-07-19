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
package com.htmlhifive.pitalium.image.model;

/**
 * 画像の比較オプションを表すenumクラス。
 */
public enum DiffCategory {

	/**
	 * shift（位置のずれ）による差分
	 */
	SHIFT,

	/**
	 * missing（要素が存在しないこと）による差分
	 */
	MISSING,

	/**
	 * scaling（領域の拡縮）による差分
	 */
	SCALING,

	/**
	 * text（テキストのサブピクセルレンダリング）による差分
	 */
	TEXT,

	/**
	 * その他の原因による差分
	 */
	SIMILAR
}
