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
 * 比較用のパラメータを保持するクラス
 */
public class DefaultComparisonParameters extends ComparisonParameters {

	/**
	 * 色を違うとみなすための閾値
	 */
	private double threshold = 0.0;

	/**
	 * デフォルトコンストラクタ
	 */
	public DefaultComparisonParameters() {

	}

	/**
	 * パラメータありのコンストラクタ
	 *
	 * @param threshold 閾値
	 */
	public DefaultComparisonParameters(double threshold) {
		this.threshold = threshold;
	}

	/**
	 * mapを受けるコンストラクタ
	 *
	 * @param map 閾値を持つmap
	 */
	public DefaultComparisonParameters(Map<String, Object> map) {
		Object thresholdStr = map.get("threshold");
		if (thresholdStr != null) {
			this.threshold = (Double) thresholdStr;
		}
	}

	/**
	 * 閾値を取得します。
	 *
	 * @return 閾値
	 */
	public double getThreshold() {
		return threshold;
	}
}
