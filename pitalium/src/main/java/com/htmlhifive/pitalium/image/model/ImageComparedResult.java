package com.htmlhifive.pitalium.image.model;

import java.io.Serializable;

/**
 * 比較結果を保持するクラス
 */
public abstract class ImageComparedResult implements Serializable {

	/**
	 * 比較した結果、画像が一致したか否かを調べる。
	 *
	 * @return 比較結果。一致していれば（差分がなければ）true。
	 */
	public abstract boolean isSucceeded();

	/**
	 * 比較した結果、画像が一致しないかどうかを調べる。
	 *
	 * @return 比較結果。一致しなければ（差分があれば）true。
	 */
	public boolean isFailed() {
		return !isSucceeded();
	}
}
