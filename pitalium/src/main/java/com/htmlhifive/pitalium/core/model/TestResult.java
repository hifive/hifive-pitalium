/*
 * Copyright (C) 2016 NS Solutions Corporation
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
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.htmlhifive.pitalium.common.util.JSONUtils;

/**
 * テストクラス全体の実行結果を保持するクラス。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestResult implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 今回の結果を表すID
	 */
	private String resultId;
	/**
	 * テストクラスとしての実行結果
	 */
	private ExecResult result;
	/**
	 * {@link com.htmlhifive.pitalium.core.rules.AssertionView#assertView(String, String, List, List)}および
	 * {@link com.htmlhifive.pitalium.core.rules.AssertionView#assertScreenshot(String, ScreenshotResult)}を実行した結果のリスト
	 */
	private List<ScreenshotResult> screenshotResults;

	/**
	 * 空の結果オブジェクトを生成します。
	 */
	public TestResult() {
	}

	/**
	 * 結果オブジェクトを生成します。
	 * 
	 * @param resultId 結果ID
	 * @param result 実行結果
	 * @param screenshotResults 実行結果のリスト
	 */
	public TestResult(String resultId, ExecResult result, List<ScreenshotResult> screenshotResults) {
		this.resultId = resultId;
		this.result = result;

		setScreenshotResults(screenshotResults);
	}

	/**
	 * 結果配列にスクリーンショットID毎の結果を追加します。結果配列が空の場合は初期化します。
	 * 
	 * @param screenshotResults 1つのスクリーンショットIDに対する結果リスト
	 */
	void setScreenshotResults(List<ScreenshotResult> screenshotResults) {
		if (screenshotResults == null || screenshotResults.isEmpty()) {
			this.screenshotResults = Collections.emptyList();
			return;
		}

		this.screenshotResults = Collections.unmodifiableList(screenshotResults);
	}

	/**
	 * 結果IDを取得します。
	 * 
	 * @return 結果ID
	 */
	public String getResultId() {
		return resultId;
	}

	/**
	 * テストクラスとしての実行結果を取得します。
	 * 
	 * @return 実行結果。テスト成功ならtrue
	 */
	public ExecResult getResult() {
		return result;
	}

	/**
	 * {@link com.htmlhifive.pitalium.core.rules.AssertionView#assertView(String, String, List, List)}および
	 * {@link com.htmlhifive.pitalium.core.rules.AssertionView#assertScreenshot(String, ScreenshotResult)}の全実行結果を取得します。
	 * 
	 * @return 結果の配列
	 */
	public List<ScreenshotResult> getScreenshotResults() {
		return screenshotResults;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		TestResult result1 = (TestResult) o;

		if (resultId != null ? !resultId.equals(result1.resultId) : result1.resultId != null) {
			return false;
		}
		if (result != result1.result) {
			return false;
		}
		return !(screenshotResults != null ? !screenshotResults.equals(result1.screenshotResults)
				: result1.screenshotResults != null);

	}

	@Override
	public int hashCode() {
		int result1 = resultId != null ? resultId.hashCode() : 0;
		final int hashPrime = 31;
		result1 = hashPrime * result1 + (result != null ? result.hashCode() : 0);
		result1 = hashPrime * result1 + (screenshotResults != null ? screenshotResults.hashCode() : 0);
		return result1;
	}

	@Override
	public String toString() {
		return JSONUtils.toString(this);
	}

}
