package com.htmlhifive.pitalium.image.model;

import java.util.List;

/**
 * カテゴリ分類に基づく、比較結果を保持するクラス
 */
public class CategoryImageComparedResult extends ImageComparedResult {

	/**
	 * 一致とみなすかどうか
	 */
	private boolean isSucceed;
	/**
	 * 比較を実行した短径のリスト
	 */
	private List<ComparedRectangleArea> comparedRectangles;

	public CategoryImageComparedResult(boolean isSucceed, List<ComparedRectangleArea> comparedRectangles) {
		this.isSucceed = isSucceed;
		this.comparedRectangles = comparedRectangles;
	}

	/**
	 * 比較を実行した短径のリストを取得する
	 *
	 * @return 比較を実行した短径のリスト
	 */
	public List<ComparedRectangleArea> getComparedRectangles() {
		return comparedRectangles;
	}

	@Override
	public boolean isSucceeded() {
		return isSucceed;
	}
}
