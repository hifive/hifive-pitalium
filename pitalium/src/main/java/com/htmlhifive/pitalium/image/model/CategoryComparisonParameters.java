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

import java.util.Arrays;

/**
 * カテゴリ分類による比較のパラメータを保持するクラス
 */
public class CategoryComparisonParameters extends ComparisonParameters {

	/**
	 * 許容するカテゴリのリスト
	 */
	private final DiffCategory[] acceptCategories;

	/**
	 * shift（位置のずれ）による差分を許容するか
	 */
	public boolean isShiftAccepted;

	/**
	 * missing（要素が存在しないこと）による差分を許容するか
	 */
	private boolean isMissingAccepted;

	/**
	 * scaling（領域の拡縮）による差分を許容するか
	 */
	private boolean isScalingAccepted;

	/**
	 * text（テキストのサブピクセルレンダリング）によるずれを許容するか
	 */
	private boolean isTextAccepted;

	/**
	 * パラメータありのコンストラクタ
	 *
	 * @param shift
	 * @param missing
	 * @param scaling
	 * @param text
	 */
	public CategoryComparisonParameters(DiffCategory[] acceptCategories) {
		super();
		this.acceptCategories = acceptCategories;

		for (DiffCategory category : acceptCategories) {
			switch (category) {
				case SHIFT:
					isShiftAccepted = true;
					break;
				case MISSING:
					isMissingAccepted = true;
					break;
				case SCALING:
					isScalingAccepted = true;
					break;
				case TEXT:
					isTextAccepted = true;
					break;
				default:
					break;
			}
		}
	}

	/**
	 * 許容する差分のカテゴリの配列を返します。
	 *
	 * @return 許容する差分のカテゴリの配列
	 */
	public DiffCategory[] getAcceptedCategories() {
		return acceptCategories.clone();
	}

	public boolean isAccept(DiffCategory category) {
		return Arrays.asList(acceptCategories).contains(category);
	}

	/**
	 * shift（位置のずれ）による差分を許容するかを返します。
	 */
	public boolean isShiftAccepted() {
		return isShiftAccepted;
	}

	/**
	 * missing（要素が存在しないこと）による差分を許容するかを返します。
	 */
	public boolean isMissingAccepted() {
		return isMissingAccepted;
	}

	/**
	 * scaling（領域の拡縮）による差分を許容するかを返します。
	 */
	public boolean isScalingAccepted() {
		return isScalingAccepted;
	}

	/**
	 * text（テキストのサブピクセルレンダリング）によるずれを許容するかを返します。
	 */
	public boolean isTextAccepted() {
		return isTextAccepted;
	}

}
