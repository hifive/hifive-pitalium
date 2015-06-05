/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.image.model;

import java.awt.Point;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * {@link com.htmlhifive.testlib.image.util.ImageComparator}の比較結果として、2枚の画像間の差分を保持するクラス。
 */
public class DiffPoints implements Serializable {

	private static final long serialVersionUID = 1L;

	private final List<Point> diffPoints;
	private final List<Point> sizeDiffPoints;

	/**
	 * 差分データを受け取って結果オブジェクトを生成する。
	 * 
	 * @param diffPoints 画像間で差異があった点の集合
	 * @param sizeDiffPoints サイズの違い（2枚の画像を重ねた際に重ならない部分）を表す点の集合
	 */
	public DiffPoints(List<Point> diffPoints, List<Point> sizeDiffPoints) {
		this.diffPoints = Collections.unmodifiableList(diffPoints);
		this.sizeDiffPoints = Collections.unmodifiableList(sizeDiffPoints);
	}

	/**
	 * 画像の差異データを取得する。
	 * 
	 * @return 差異を表す点の集合
	 */
	public List<Point> getDiffPoints() {
		return diffPoints;
	}

	/**
	 * 画像のサイズの差異データを取得する。
	 * 
	 * @return サイズの違い（2枚の画像を重ねた際に重ならない部分）を表す点の集合
	 */
	public List<Point> getSizeDiffPoints() {
		return sizeDiffPoints;
	}

	/**
	 * 比較した結果、画像が一致したか否かを調べる。
	 * 
	 * @return 比較結果。一致していれば（差分がなければ）true。
	 */
	public boolean isSucceeded() {
		return diffPoints.isEmpty() && sizeDiffPoints.isEmpty();
	}

	/**
	 * 比較した結果、画像が一致しないかどうかを調べる。
	 * 
	 * @return 比較結果。一致しなければ（差分があれば）true。
	 */
	public boolean isFailed() {
		return !isSucceeded();
	}

}
