/*
 * Copyright (C) 2015-2017 NS Solutions Corporation
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
package com.htmlhifive.pitalium.image.model;

import java.util.Map;

/**
 * 類似度に基づき、比較を行う機能のパラメータを保持するクラス
 */
public class SimilarityComparisonParameters extends ComparisonParameters {
	/**
	 * 各pixelの色の差分ノルムに基づく類似度で一致とみなす閾値
	 */
	private final double pixleByPixelThreshold;

	/**
	 * 各セルの特徴量を計算した行列に基づく類似度で一致とみなす閾値
	 */
	private final double featherMatrixThreshold;

	/**
	 * ある程度色が異なるセルの色の違いに基づく類似度で一致とみなす閾値
	 */
	private final double thresDiffThreshold;

	/**
	 * 全ての異なる色のセル違いに基づく類似度で一致とみなす閾値
	 */
	private final double totalDiffThreshold;

	/**
	 * パラメータありコンストラクタ
	 *
	 * @param pixleByPixelThreshold
	 * @param featherMatrixThreshold
	 * @param thresDiffThreshold
	 * @param totalDiffThreshold
	 */
	public SimilarityComparisonParameters(double pixleByPixelThreshold, double featherMatrixThreshold,
			double thresDiffThreshold, double totalDiffThreshold) {
		super();
		this.pixleByPixelThreshold = pixleByPixelThreshold;
		this.featherMatrixThreshold = featherMatrixThreshold;
		this.thresDiffThreshold = thresDiffThreshold;
		this.totalDiffThreshold = totalDiffThreshold;
	}

	/**
	 * mapを引数とするコンストラクタ
	 *
	 * @param parameters
	 */
	public SimilarityComparisonParameters(Map<String, Object> parameters) {
		super();
		this.pixleByPixelThreshold = (Double) parameters.get("getpixleByPixelThreshold");
		this.featherMatrixThreshold = (Double) parameters.get("featherMatrixThreshold");
		this.thresDiffThreshold = (Double) parameters.get("thresDiffThreshold");
		this.totalDiffThreshold = (Double) parameters.get("totalDiffThreshold");
	}

	/**
	 * @return pixleByPixelThreshold
	 */
	public double getPixleByPixelThreshold() {
		return pixleByPixelThreshold;
	}

	/**
	 * @return featherMatrixThreshold
	 */
	public double getFeatherMatrixThreshold() {
		return featherMatrixThreshold;
	}

	/**
	 * @return thresDiffThreshold
	 */
	public double getThresDiffThreshold() {
		return thresDiffThreshold;
	}

	/**
	 * @return totalDiffThreshold
	 */
	public double getTotalDiffThreshold() {
		return totalDiffThreshold;
	}

}
