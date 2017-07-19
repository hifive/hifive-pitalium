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
public enum CompareOptionType {

	/**
	 * 各pixelごとに色が一致するかどうかを比較します。
	 */
	DEFAULT,
	/**
	 * 透明度が0xFF以外のpixelは無視して比較します。
	 */
	IGNORE_CLEAR_PIXELS,
	/**
	 * 類似度に基づき画像の一致を判定します。
	 */
	SIMILARITY,

	/**
	 * 差分の原因にの種類に基づき画像の一致を判定します。
	 */
	CATEGORY;
}
