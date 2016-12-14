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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.htmlhifive.pitalium.common.util.JSONUtils;
import com.htmlhifive.pitalium.image.model.RectangleArea;

/**
 * 画面上の特定の領域をセレクタおよび矩形領域で表すクラス。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScreenArea implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 領域のセレクタ
	 */
	private DomSelector selector;
	/**
	 * 領域の矩形オブジェクト
	 */
	private RectangleArea rectangle;

	/**
	 * 空のオブジェクトを生成します。
	 */
	public ScreenArea() {
	}

	/**
	 * セレクタを指定して領域オブジェクトを生成します。
	 * 
	 * @param selector セレクタ
	 */
	public ScreenArea(DomSelector selector) {
		this.selector = selector;
	}

	/**
	 * 矩形領域を指定して領域オブジェクトを生成します。
	 * 
	 * @param rectangle 矩形領域
	 */
	public ScreenArea(RectangleArea rectangle) {
		this.rectangle = rectangle;
	}

	/**
	 * セレクタを使用して画面上の領域を指定します。
	 * 
	 * @param type セレクタの種別
	 * @param value セレクタの値
	 * @return 画面上の領域を表すオブジェクト
	 */
	public static ScreenArea of(SelectorType type, String value) {
		return new ScreenArea(new DomSelector(type, value));
	}

	/**
	 * セレクタを使用して画面上の領域を指定します。
	 * 
	 * @param type セレクタの種別
	 * @param value セレクタの値
	 * @param frameSelectorType フレームを指定するセレクタの種別
	 * @param frameSelectorValue フレームを指定するセレクタの値
	 * @return 画面上の領域を表すオブジェクト
	 */
	public static ScreenArea of(SelectorType type, String value, SelectorType frameSelectorType,
			String frameSelectorValue) {
		return new ScreenArea(new DomSelector(type, value, new DomSelector(frameSelectorType, frameSelectorValue)));
	}

	/**
	 * 座標を直接指定して画面上の領域を指定します。
	 * 
	 * @param x 領域の左上のx座標
	 * @param y 領域の左上のy座標
	 * @param width 領域の幅
	 * @param height 領域の高さ
	 * @return 画面上の領域を表すオブジェクト
	 */
	public static ScreenArea of(double x, double y, double width, double height) {
		return new ScreenArea(new RectangleArea(x, y, width, height));
	}

	/**
	 * 領域のセレクタを取得します。
	 * 
	 * @return セレクタ
	 */
	public DomSelector getSelector() {
		return selector;
	}

	/**
	 * 矩形領域を取得します。
	 * 
	 * @return 矩形領域
	 */
	public RectangleArea getRectangle() {
		return rectangle;
	}

	/**
	 * 同じ領域を指すかどうか調べます。
	 * 
	 * @param o 比較対象オブジェクト
	 * @return セレクタ・矩形領域が一致すればtrue
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ScreenArea that = (ScreenArea) o;

		if (selector != null ? !selector.equals(that.selector) : that.selector != null) {
			return false;
		}
		return !(rectangle != null ? !rectangle.equals(that.rectangle) : that.rectangle != null);

	}

	@Override
	public int hashCode() {
		int result = selector != null ? selector.hashCode() : 0;
		final int hashPrime = 31;
		result = hashPrime * result + (rectangle != null ? rectangle.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return JSONUtils.toString(this);
	}

}
