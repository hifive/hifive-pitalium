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
package com.htmlhifive.testlib.core.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.htmlhifive.testlib.image.model.CompareOption;
import com.htmlhifive.testlib.image.model.ScreenshotImage;

/**
 * スクリーンショット1枚毎の画像・比較結果を保持するクラス。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TargetResult implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * スクリーンショットの比較結果
	 */
	private ExecResult result;
	/**
	 * スクリーンショット取得・比較対象として指定した領域
	 */
	private ScreenAreaResult target;
	/**
	 * 比較時に除外した領域
	 */
	private List<ScreenAreaResult> excludes;
	/**
	 * スクリーンショット取得時に{@link #target}の領域を定位置に移動したかどうか
	 */
	private Boolean moveTarget;
	/**
	 * スクリーンショット取得時に非表示にした要素のセレクタ
	 */
	private List<DomSelector> hiddenElementSelectors;
	/**
	 * 撮影されたスクリーンショット画像
	 */
	@JsonIgnore
	private ScreenshotImage image;

	/**
	 * 比較オプション
	 */
	@JsonIgnore
	private CompareOption[] options;

	/**
	 * 空の結果オブジェクトを生成します。
	 */
	public TargetResult() {
	}

	/**
	 * 結果オブジェクトを生成します。
	 * 
	 * @param target スクリーンショット・比較の対象領域
	 * @param excludes 比較時の除外領域
	 * @param image スクリーンショット画像
	 */
	public TargetResult(ScreenAreaResult target, List<ScreenAreaResult> excludes, ScreenshotImage image) {
		this(null, target, excludes, null, null, image, null);
	}

	/**
	 * 結果オブジェクトを生成します。
	 * 
	 * @param result 比較結果
	 * @param target スクリーンショット・比較の対象領域
	 * @param excludes 比較時の除外領域
	 * @param moveTarget スクリーンショット取得時に{@link #target}の領域を定位置に移動したかどうか
	 * @param hiddenElementSelectors スクリーンショット取得時に非表示にした要素のセレクタ
	 */
	public TargetResult(ExecResult result, ScreenAreaResult target, List<ScreenAreaResult> excludes,
			Boolean moveTarget, List<DomSelector> hiddenElementSelectors) {
		this(result, target, excludes, moveTarget, hiddenElementSelectors, null, null);
	}

	/**
	 * 結果オブジェクトを生成します。
	 * 
	 * @param result 比較結果
	 * @param target スクリーンショット・比較の対象領域
	 * @param excludes 比較時の除外領域
	 * @param moveTarget スクリーンショット取得時に{@link #target}の領域を定位置に移動したかどうか
	 * @param hiddenElementSelectors スクリーンショット取得時に非表示にした要素のセレクタ
	 * @param image スクリーンショット画像
	 * @param options 比較オプション
	 */
	public TargetResult(ExecResult result, ScreenAreaResult target, List<ScreenAreaResult> excludes,
			Boolean moveTarget, List<DomSelector> hiddenElementSelectors, ScreenshotImage image, CompareOption[] options) {
		this.result = result;
		this.target = target;
		this.moveTarget = moveTarget;
		this.image = image;
		this.options = options;

		setExcludes(excludes);
		setHiddenElementSelectors(hiddenElementSelectors);
	}

	/**
	 * 比較時に除外した領域を設定します。
	 * 
	 * @param excludes 比較時の除外領域
	 */
	void setExcludes(List<ScreenAreaResult> excludes) {
		if (excludes == null || excludes.isEmpty()) {
			this.excludes = Collections.emptyList();
			return;
		}

		this.excludes = Collections.unmodifiableList(excludes);
	}

	/**
	 * スクリーンショット撮影時に非表示にした要素を設定します。
	 * 
	 * @param hiddenElementSelectors 非表示にした要素のセレクタ
	 */
	void setHiddenElementSelectors(List<DomSelector> hiddenElementSelectors) {
		if (hiddenElementSelectors == null || hiddenElementSelectors.isEmpty()) {
			this.hiddenElementSelectors = Collections.emptyList();
			return;
		}

		this.hiddenElementSelectors = Collections.unmodifiableList(hiddenElementSelectors);
	}

	/**
	 * 比較結果を取得します。
	 * 
	 * @return 比較結果
	 */
	public ExecResult getResult() {
		return result;
	}

	/**
	 * スクリーンショット取得・比較の対象として指定した領域を取得します。
	 * 
	 * @return 指定した領域を表すオブジェクト
	 */
	public ScreenAreaResult getTarget() {
		return target;
	}

	/**
	 * 比較時に除外した領域を取得します。
	 * 
	 * @return 比較時の除外領域
	 */
	public List<ScreenAreaResult> getExcludes() {
		return excludes;
	}

	/**
	 * スクリーンショット撮影時に{@link #target}の領域を定位置に移動したかどうかを返します。
	 * 
	 * @return 撮影時に指定領域を移動したか否か。移動した場合はtrue
	 */
	public Boolean isMoveTarget() {
		return moveTarget;
	}

	/**
	 * スクリーンショット撮影時に非表示にした要素を取得します。
	 * 
	 * @return 非表示にした要素
	 */
	public List<DomSelector> getHiddenElementSelectors() {
		return hiddenElementSelectors;
	}

	/**
	 * スクリーンショット画像を取得します。
	 * 
	 * @return スクリーンショット画像
	 */
	public ScreenshotImage getImage() {
		return image;
	}

	/**
	 * 比較オプションを取得します。
	 * 
	 * @return 比較オプション
	 */
	public CompareOption[] getOptions() {
		return options;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		TargetResult that = (TargetResult) o;

		if (result != that.result) {
			return false;
		}
		if (target != null ? !target.equals(that.target) : that.target != null) {
			return false;
		}
		if (excludes != null ? !excludes.equals(that.excludes) : that.excludes != null) {
			return false;
		}
		if (moveTarget != null ? !moveTarget.equals(that.moveTarget) : that.moveTarget != null) {
			return false;
		}
		if (hiddenElementSelectors != null ? !hiddenElementSelectors.equals(that.hiddenElementSelectors)
				: that.hiddenElementSelectors != null) {
			return false;
		}
		if (image != null ? !image.equals(that.image) : that.image != null) {
			return false;
		}
		// Probably incorrect - comparing Object[] arrays with Arrays.equals
		return Arrays.equals(options, that.options);

	}

	@Override
	public int hashCode() {
		int result1 = result != null ? result.hashCode() : 0;
		final int hashPrime = 31;
		result1 = hashPrime * result1 + (target != null ? target.hashCode() : 0);
		result1 = hashPrime * result1 + (excludes != null ? excludes.hashCode() : 0);
		result1 = hashPrime * result1 + (moveTarget != null ? moveTarget.hashCode() : 0);
		result1 = hashPrime * result1 + (hiddenElementSelectors != null ? hiddenElementSelectors.hashCode() : 0);
		result1 = hashPrime * result1 + (image != null ? image.hashCode() : 0);
		result1 = hashPrime * result1 + (options != null ? Arrays.hashCode(options) : 0);
		return result1;
	}

	@Override
	public String toString() {
		return "TargetResult{" + "result=" + result + ", target=" + target + ", excludes=" + excludes + ", moveTarget="
				+ moveTarget + ", hiddenElementSelectors=" + hiddenElementSelectors + ", image=" + image + ", options="
				+ Arrays.toString(options) + '}';
	}

}
