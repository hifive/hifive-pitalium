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
