package com.htmlhifive.pitalium.image.model;

public class SimilarityImageComparedResult extends ImageComparedResult {

	/**
	 * 類似度に基づく計算結果
	 */
	private SimilarityUnit similarityUnit;

	/**
	 * 判定結果
	 */
	private boolean result;

	public SimilarityImageComparedResult(boolean result, SimilarityUnit similarityUnit) {
		this.result = result;
		this.similarityUnit = similarityUnit;
	}

	/**
	 * @return similarityUnit
	 */
	public SimilarityUnit getSimilarityUnit() {
		return similarityUnit;
	}

	@Override
	public boolean isSucceeded() {
		return result;
	}

}
