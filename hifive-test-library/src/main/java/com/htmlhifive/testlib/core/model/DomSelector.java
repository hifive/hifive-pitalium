/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.core.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.htmlhifive.testlib.common.util.JSONUtils;

/**
 * DOM要素を指定するためのセレクタを保持するクラス。
 */
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
	 * DOM要素をセレクタの種別と値で指定します。
	 * 
	 * @param type セレクタの種別
	 * @param value セレクタの値
	 */
	@JsonCreator
	public DomSelector(@JsonProperty("type") SelectorType type, @JsonProperty("value") String value) {
		this.type = type;
		this.value = value;
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
	 * 同じDOMを表すセレクタか否かを調べます。
	 * 
	 * @param o 比較対象オブジェクト
	 * @return セレクタの種別と値が一致すればtrue。
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		DomSelector that = (DomSelector) o;

		if (type != that.type) {
			return false;
		}
		return value.equals(that.value);
	}

	@Override
	public int hashCode() {
		int result = type.hashCode();
		final int hashPrime = 31;
		result = hashPrime * result + value.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return JSONUtils.toString(this);
	}

}
