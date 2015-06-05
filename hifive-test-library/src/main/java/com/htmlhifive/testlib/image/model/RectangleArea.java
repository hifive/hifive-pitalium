/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.image.model;

import java.awt.Rectangle;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.htmlhifive.testlib.common.util.JSONUtils;

/**
 * 矩形領域を表すクラス。
 */
public class RectangleArea implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 左上のx座標
	 */
	private final double x;

	/**
	 * 左上のy座標
	 */
	private final double y;
	/**
	 * 幅
	 */
	private final double width;
	/**
	 * 高さ
	 */
	private final double height;

	/**
	 * 位置とサイズを指定して、矩形領域オブジェクトを生成します。
	 * 
	 * @param x 左上のx座標
	 * @param y 左上のy座標
	 * @param width 幅
	 * @param height 高さ
	 */
	@JsonCreator
	public RectangleArea(@JsonProperty("x") double x, @JsonProperty("y") double y, @JsonProperty("width") double width,
			@JsonProperty("height") double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/**
	 * x座標を取得します。
	 * 
	 * @return x座標
	 */
	public double getX() {
		return x;
	}

	/**
	 * y座標を取得します。
	 * 
	 * @return y座標
	 */
	public double getY() {
		return y;
	}

	/**
	 * 要素の幅を取得します。
	 * 
	 * @return 幅
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * 要素の高さを取得します。
	 * 
	 * @return 高さ
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * 座標・サイズの小数点以下の値を切り捨てた矩形領域を生成します。
	 * 
	 * @return 小数点以下を切り捨てた値を持つ矩形領域
	 */
	public RectangleArea floor() {
		return new RectangleArea((int) x, (int) y, (int) width, (int) height);
	}

	/**
	 * 指定量移動させた矩形領域を生成します。
	 * 
	 * @param deltaX X座標方向の移動量
	 * @param deltaY Y座標方向の移動量
	 * @return 移動後の矩形領域
	 */
	public RectangleArea move(double deltaX, double deltaY) {
		return new RectangleArea(x + deltaX, y + deltaY, width, height);
	}

	/**
	 * スケールを適用します。
	 * 
	 * @param scale スケール
	 * @return スケールを適用した矩形領域
	 */
	public RectangleArea applyScale(double scale) {
		double scaledX = Math.round(x * scale);
		double scaledY = Math.round(y * scale);
		double scaledWidth = Math.round(width * scale);
		double scaledHeight = Math.round(height * scale);

		return new RectangleArea(scaledX, scaledY, scaledWidth, scaledHeight);
	}

	/**
	 * {@link Rectangle}に変換します。
	 * 
	 * @return 同じ位置・座標を持った{@link Rectangle}
	 */
	public Rectangle toRectangle() {
		return new Rectangle((int) Math.round(x), (int) Math.round(y), (int) Math.round(width),
				(int) Math.round(height));
	}

	/**
	 * 同じ矩形領域を表すか調べます。
	 * 
	 * @param o 比較対象のオブジェクト
	 * @return 位置・サイズが一致すればtrue
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		RectangleArea that = (RectangleArea) o;

		if (Double.compare(that.x, x) != 0) {
			return false;
		}
		if (Double.compare(that.y, y) != 0) {
			return false;
		}
		if (Double.compare(that.width, width) != 0) {
			return false;
		}
		return Double.compare(that.height, height) == 0;
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		final int hashPrime = 31;
		final int shiftBit = 32;
		temp = Double.doubleToLongBits(x);
		result = (int) (temp ^ (temp >>> shiftBit));
		temp = Double.doubleToLongBits(y);
		result = hashPrime * result + (int) (temp ^ (temp >>> shiftBit));
		temp = Double.doubleToLongBits(width);
		result = hashPrime * result + (int) (temp ^ (temp >>> shiftBit));
		temp = Double.doubleToLongBits(height);
		result = hashPrime * result + (int) (temp ^ (temp >>> shiftBit));
		return result;
	}

	@Override
	public String toString() {
		return JSONUtils.toString(this);
	}

}
