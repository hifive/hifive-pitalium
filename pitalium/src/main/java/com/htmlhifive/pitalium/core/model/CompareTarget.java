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
package com.htmlhifive.pitalium.core.model;

import java.io.Serializable;
import java.util.Arrays;

import com.htmlhifive.pitalium.image.model.CompareOption;

/**
 * スクリーンショット取得・比較の対象となる領域を指定するためのクラス。<br/>
 * 比較時に除外する領域、比較オプションを同時に指定することができます。
 */
public class CompareTarget implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * スクリーンショット・比較の対象領域
	 */
	private final ScreenArea compareArea;

	/**
	 * 比較オプション
	 */
	private final CompareOption[] options = null;

	/**
	 * 比較時に除外する領域
	 */
	private final ScreenArea[] excludes;

	/**
	 * スクリーンショット取得時に、指定領域を定位置に移動するか否か。trueに設定した場合、レンダリングによる不要な誤差の検出を軽減できますが、ブラウザによっては表示が崩れる場合があります。
	 */
	private final boolean moveTarget;

	/**
	 * ページ全体をスクリーンショット取得・比較の対象とします。
	 */
	public CompareTarget() {
		this(ScreenArea.of(SelectorType.TAG_NAME, "body"), null, true);
	}

	/**
	 * 指定領域をスクリーンショット取得・比較の対象とします。
	 *
	 * @param compareArea 指定領域
	 */
	public CompareTarget(ScreenArea compareArea) {
		this(compareArea, null, true);
	}

	/**
	 * 指定領域（{@link #excludes}の領域を除く）をスクリーンショット取得・比較の対象とします。
	 *
	 * @param compareArea 指定領域
	 * @param excludes 比較時に除外する領域
	 * @param moveTarget スクリーンショット撮影時に指定領域を定位置に移動するか否か。移動する場合はtrueを指定します。trueの場合、レンダリングによって発生する想定外の誤差を抑制しますが、
	 *            ページによっては画面のレイアウトが崩れる場合があります 。
	 */
	public CompareTarget(ScreenArea compareArea, ScreenArea[] excludes, boolean moveTarget) {
		this.compareArea = compareArea;
		this.excludes = excludes != null ? excludes : new ScreenArea[0];
		this.moveTarget = moveTarget;
	}

	/**
	 * スクリーンショット・比較の対象領域を取得します。
	 *
	 * @return 比較対象のエリア
	 */
	public ScreenArea getCompareArea() {
		return compareArea;
	}

	/**
	 * 比較オプションを取得します。
	 *
	 * @return 比較オプション
	 */
	public CompareOption[] getOptions() {
		return options;
	}

	/**
	 * 比較時に除外する領域を取得します。
	 *
	 * @return 除外する領域
	 */
	public ScreenArea[] getExcludes() {
		return excludes;
	}

	/**
	 * スクリーンショット撮影時に指定領域を定位置に移動するか否かの設定を取得します。
	 *
	 * @return 撮影時に指定領域を定位置に移動するか否か。移動する場合はtrue。
	 */
	public boolean isMoveTarget() {
		return moveTarget;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		CompareTarget that = (CompareTarget) o;

		if (moveTarget != that.moveTarget) {
			return false;
		}
		if (!compareArea.equals(that.compareArea)) {
			return false;
		}
		// Probably incorrect - comparing Object[] arrays with Arrays.equals
		if (!Arrays.equals(options, that.options)) {
			return false;
		}
		// Probably incorrect - comparing Object[] arrays with Arrays.equals
		return Arrays.equals(excludes, that.excludes);

	}

	@Override
	public int hashCode() {
		int result = compareArea.hashCode();
		result = 31 * result + (options != null ? Arrays.hashCode(options) : 0);
		result = 31 * result + Arrays.hashCode(excludes);
		result = 31 * result + (moveTarget ? 1 : 0);
		return result;
	}

}
