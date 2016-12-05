/*
 * Copyright (C) 2015-2016 NS Solutions Corporation
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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.htmlhifive.pitalium.common.util.JSONUtils;

/**
 * DOM要素を指定するためのセレクタを保持するクラス。セレクタに一致した要素のうち、指定したインデックスのものだけを対象とする。
 */
public class IndexDomSelector extends DomSelector {

	private static final long serialVersionUID = 1L;

	/**
	 * セレクタに一致する要素のうち、どの要素を対象とするかを表すインデックス。指定しなければ、すべての要素を対象とします。
	 */
	private final Integer index;

	/**
	 * セレクタに一致する全てのDOM要素を指定します。
	 * 
	 * @param selector セレクタ
	 */
	public IndexDomSelector(DomSelector selector) {
		this(selector, null);
	}

	/**
	 * セレクタに一致する要素のうち、index番目の要素を指定します。
	 * 
	 * @param selector セレクタ
	 * @param index 対象とする要素のインデックス
	 */
	public IndexDomSelector(DomSelector selector, Integer index) {
		this(selector.getType(), selector.getValue(), selector.getFrameSelector(), index);
	}

	/**
	 * DOM要素をセレクタの種別と値で指定します。セレクタに一致する要素のうち、index番目の要素を指定します。
	 * 
	 * @param type セレクタの種別
	 * @param value セレクタの値
	 * @param index 対象とする要素のインデックス
	 */
	public IndexDomSelector(SelectorType type, String value, Integer index) {
		this(type, value, null, index);
	}

	/**
	 * DOM要素をセレクタの種別と値で指定します。セレクタに一致する要素のうち、index番目の要素を指定します。
	 * 
	 * @param type セレクタの種別
	 * @param value セレクタの値
	 * @param index 対象とする要素のインデックス
	 */
	@JsonCreator
	public IndexDomSelector(@JsonProperty("type") SelectorType type, @JsonProperty("value") String value,
			@JsonProperty("frameSelector") DomSelector frameSelector, @JsonProperty("index") Integer index) {
		super(type, value, frameSelector);
		this.index = index;
	}

	/**
	 * 対象とする要素のインデックスを取得します。
	 * 
	 * @return インデックス
	 */
	public Integer getIndex() {
		return index;
	}

	/**
	 * 同じDOMを表すセレクタか否かを調べます。
	 * 
	 * @param o 比較対象オブジェクト
	 * @return セレクタの種別と値とインデックスが一致すればtrue
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}

		IndexDomSelector that = (IndexDomSelector) o;

		return !(index != null ? !index.equals(that.index) : that.index != null);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		final int hashPrime = 31;
		result = hashPrime * result + (index != null ? index.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return JSONUtils.toString(this);
	}

}
