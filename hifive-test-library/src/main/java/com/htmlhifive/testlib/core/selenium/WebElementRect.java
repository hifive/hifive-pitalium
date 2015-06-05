/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.core.selenium;

import java.io.Serializable;

/**
 * DOM要素の矩形領域を表すクラス
 */
public class WebElementRect implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 左上のx座標
	 */
	private final double left;
	/**
	 * 左上のy座標
	 */
	private final double top;
	/**
	 * 要素の幅
	 */
	private final double width;
	/**
	 * 要素の高さ
	 */
	private final double height;

	/**
	 * 指定した座標・サイズのオブジェクトを生成します。
	 * 
	 * @param left 左上のx座標
	 * @param top 左上のy座標
	 * @param width 要素の幅
	 * @param height 要素の高さ
	 */
	public WebElementRect(double left, double top, double width, double height) {
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
	}

	/**
	 * 左上のx座標を取得します。
	 * 
	 * @return 左上のx座標
	 */
	public double getLeft() {
		return left;
	}

	/**
	 * 左上のy座標を取得します。
	 * 
	 * @return 左上のy座標
	 */
	public double getTop() {
		return top;
	}

	/**
	 * 要素の幅を取得します。
	 * 
	 * @return 要素の幅
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * 要素の高さを取得します。
	 * 
	 * @return 要素の高さ
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * 同一かどうか調べます。
	 * 
	 * @param o 比較対象
	 * @return 座標・サイズが一致すればtrue
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		WebElementRect that = (WebElementRect) o;

		if (Double.compare(that.left, left) != 0) {
			return false;
		}
		if (Double.compare(that.top, top) != 0) {
			return false;
		}
		if (Double.compare(that.width, width) != 0) {
			return false;
		}
		if (Double.compare(that.height, height) != 0) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		final int hashPrime = 31;
		final int shiftBit = 32;
		temp = Double.doubleToLongBits(left);
		result = (int) (temp ^ (temp >>> shiftBit));
		temp = Double.doubleToLongBits(top);
		result = hashPrime * result + (int) (temp ^ (temp >>> shiftBit));
		temp = Double.doubleToLongBits(width);
		result = hashPrime * result + (int) (temp ^ (temp >>> shiftBit));
		temp = Double.doubleToLongBits(height);
		result = hashPrime * result + (int) (temp ^ (temp >>> shiftBit));
		return result;
	}

	@Override
	public String toString() {
		return "WebElementRect{" + "left=" + left + ", top=" + top + ", width=" + width + ", height=" + height + '}';
	}
}
