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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.htmlhifive.pitalium.image.model.RectangleArea;

/**
 * スクリーンショット取得・比較の対象とした領域を表すクラス。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScreenAreaResult implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 領域のセレクタ
	 */
	private IndexDomSelector selector;

	/**
	 * 領域の矩形オブジェクト
	 */
	private RectangleArea rectangle;

	/**
	 * {@link CompareTarget}で比較対象として指定した {@link ScreenArea}オブジェクト
	 */
	private ScreenArea screenArea;

	/**
	 * 空のオブジェクトを生成します。
	 */
	public ScreenAreaResult() {
	}

	/**
	 * セレクタ、矩形領域、{@link ScreenArea}を指定してオブジェクトを生成します。
	 * 
	 * @param selector セレクタ
	 * @param rectangle 矩形領域
	 * @param screenArea {@link ScreenArea}オブジェクト
	 */
	public ScreenAreaResult(IndexDomSelector selector, RectangleArea rectangle, ScreenArea screenArea) {
		this.selector = selector;
		this.rectangle = rectangle;
		this.screenArea = screenArea;
	}

	/**
	 * 領域のセレクタを取得します。
	 * 
	 * @return セレクタ
	 */
	public IndexDomSelector getSelector() {
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
	 * {@link CompareTarget}で比較対象として指定した{@link ScreenArea}オブジェクトを取得します。
	 * 
	 * @return {@link ScreenArea}オブジェクト
	 */
	public ScreenArea getScreenArea() {
		return screenArea;
	}

	/**
	 * {@link CompareTarget}で指定した領域が同じかどうか調べます。
	 * 
	 * @param other 比較対象
	 * @return 指定領域が同一である場合true、同一でない場合false
	 */
	public boolean areaEquals(ScreenAreaResult other) {
		// selectorが等しい場合
		if (selector != null) {
			return selector.equals(other.selector);
		}

		// Rectangleが等しい場合
		return screenArea.getRectangle().equals(other.getScreenArea().getRectangle());
	}

	/**
	 * 同じ領域を指すかどうか調べます。
	 * 
	 * @param o 比較対象オブジェクト
	 * @return セレクタ、矩形領域、{@link ScreenArea}が一致すればtrue
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ScreenAreaResult that = (ScreenAreaResult) o;

		if (selector != null ? !selector.equals(that.selector) : that.selector != null) {
			return false;
		}
		if (rectangle != null ? !rectangle.equals(that.rectangle) : that.rectangle != null) {
			return false;
		}
		return !(screenArea != null ? !screenArea.equals(that.screenArea) : that.screenArea != null);
	}

	@Override
	public int hashCode() {
		int result = selector != null ? selector.hashCode() : 0;
		final int hashPrime = 31;
		result = hashPrime * result + (rectangle != null ? rectangle.hashCode() : 0);
		result = hashPrime * result + (screenArea != null ? screenArea.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "ScreenAreaResult{" + "selector=" + selector + ", rectangle=" + rectangle + ", screenArea=" + screenArea
				+ '}';
	}

}
