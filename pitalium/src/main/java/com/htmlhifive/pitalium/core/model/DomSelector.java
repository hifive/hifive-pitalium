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

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.htmlhifive.pitalium.common.util.JSONUtils;

/**
 * DOM要素を指定するためのセレクタを保持するクラス。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DomSelector implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * セレクタの種別
	 */
	private final SelectorType type;

	/**
	 * セレクタの値
	 */
	private final String value;

	/**
	 * フレームを指定するセレクタ
	 */
	private final DomSelector frameSelector;

	/**
	 * DOM要素をセレクタの種別と値で指定します。
	 * 
	 * @param type セレクタの種別
	 * @param value セレクタの値
	 */
	public DomSelector(SelectorType type, String value) {
		this(type, value, null);
	}

	/**
	 * DOM要素をセレクタの種別と値で指定します。
	 * 
	 * @param type セレクタの種別
	 * @param value セレクタの値
	 * @param frameSelector フレームを指定するセレクタ
	 */
	@JsonCreator
	public DomSelector(@JsonProperty("type") SelectorType type, @JsonProperty("value") String value,
			@JsonProperty("frameSelector") DomSelector frameSelector) {
		this.type = type;
		this.value = value;
		this.frameSelector = frameSelector;
	}

	/**
	 * セレクタの種別を取得します。
	 * 
	 * @return セレクタの種別
	 */
	public SelectorType getType() {
		return type;
	}

	/**
	 * セレクタの値を取得します。
	 * 
	 * @return セレクタの値
	 */
	public String getValue() {
		return value;
	}

	/**
	 * フレームを指定するセレクタを取得します。
	 * 
	 * @return フレームを指定するセレクタ
	 */
	public DomSelector getFrameSelector() {
		return frameSelector;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		DomSelector that = (DomSelector) o;

		if (type != that.type)
			return false;
		if (!value.equals(that.value))
			return false;
		return frameSelector != null ? frameSelector.equals(that.frameSelector) : that.frameSelector == null;
	}

	@Override
	public int hashCode() {
		int result = type.hashCode();
		result = 31 * result + value.hashCode();
		result = 31 * result + (frameSelector != null ? frameSelector.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return JSONUtils.toString(this);
	}

}
