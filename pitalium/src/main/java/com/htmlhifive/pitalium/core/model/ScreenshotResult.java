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
package com.htmlhifive.pitalium.core.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.htmlhifive.pitalium.common.util.JSONUtils;
import com.htmlhifive.pitalium.image.model.ScreenshotImage;

/**
 * スクリーンショットID毎のスクリーンショット取得・比較結果を保持するクラス。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScreenshotResult implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * どのスクリーンショットかを示すID
	 */
	private String screenshotId;
	/**
	 * スクリーンショットの比較結果
	 */
	private ExecResult result;
	/**
	 * 期待結果として使用したID
	 */
	private String expectedId;
	/**
	 * {@link CompareTarget}で指定した領域のスクリーンショットおよび比較結果のリスト
	 */
	private List<TargetResult> targetResults;
	/**
	 * 実行したテストクラス名
	 */
	private String testClass;
	/**
	 * 実行したテストメソッド名
	 */
	private String testMethod;
	/**
	 * 実行時に指定したCapability
	 */
	private Map<String, ?> capabilities;
	/**
	 * 全体スクリーンショット画像
	 */
	@JsonIgnore
	private ScreenshotImage entireScreenshotImage;
	/**
	 * メッセージ
	 */
	private String message;

	/**
	 * 空の結果オブジェクトを生成します。
	 */
	public ScreenshotResult() {
	}

	/**
	 * 結果オブジェクトを生成します。
	 *
	 * @param screenshotId スクリーンショットID
	 * @param targetResults 指定した領域のスクリーンショット・比較結果のリスト
	 * @param entireScreenshotImage 全体スクリーンショット画像
	 */
	public ScreenshotResult(String screenshotId, List<TargetResult> targetResults,
			ScreenshotImage entireScreenshotImage) {
		this(screenshotId, null, null, targetResults, null, null, null, entireScreenshotImage, null);
	}

	/**
	 * 結果オブジェクトを生成します。
	 *
	 * @param screenshotId スクリーンショットID
	 * @param result 比較結果
	 * @param expectedId 期待結果のID
	 * @param targetResults 指定した領域のスクリーンショット・比較結果のリスト
	 * @param testClass 実行したテストクラス名
	 * @param testMethod 実行したテストメソッド名
	 * @param capabilities 実行時に指定したCapability
	 * @param entireScreenshotImage 全体スクリーンショット画像
	 */
	public ScreenshotResult(String screenshotId, ExecResult result, String expectedId, List<TargetResult> targetResults,
			String testClass, String testMethod, Map<String, ?> capabilities, ScreenshotImage entireScreenshotImage) {
		this(screenshotId, result, expectedId, targetResults, testClass, testMethod, capabilities,
				entireScreenshotImage, null);
	}

	/**
	 * 結果オブジェクトを生成します。
	 *
	 * @param screenshotId スクリーンショットID
	 * @param result 比較結果
	 * @param expectedId 期待結果のID
	 * @param targetResults 指定した領域のスクリーンショット・比較結果のリスト
	 * @param testClass 実行したテストクラス名
	 * @param testMethod 実行したテストメソッド名
	 * @param capabilities 実行時に指定したCapability
	 * @param entireScreenshotImage 全体スクリーンショット画像
	 * @param message メッセージ
	 */
	public ScreenshotResult(String screenshotId, ExecResult result, String expectedId, List<TargetResult> targetResults,
			String testClass, String testMethod, Map<String, ?> capabilities, ScreenshotImage entireScreenshotImage,
			String message) {
		this.screenshotId = screenshotId;
		this.result = result;
		this.expectedId = expectedId;
		this.testClass = testClass;
		this.testMethod = testMethod;
		this.capabilities = capabilities;
		this.entireScreenshotImage = entireScreenshotImage;
		this.message = message;

		setTargetResults(targetResults);
	}

	/**
	 * 対象領域のスクリーンショット・比較結果リストを設定します。
	 *
	 * @param targetResults 対象領域の結果オブジェクトのリスト
	 */
	void setTargetResults(List<TargetResult> targetResults) {
		if (targetResults == null || targetResults.isEmpty()) {
			this.targetResults = Collections.emptyList();
			return;
		}

		this.targetResults = Collections.unmodifiableList(targetResults);
	}

	/**
	 * スクリーンショットIDを取得します。
	 *
	 * @return スクリーンショットID
	 */
	public String getScreenshotId() {
		return screenshotId;
	}

	/**
	 * 比較結果を取得します。
	 *
	 * @return 比較結果
	 */
	public ExecResult getResult() {
		return result;
	}

	/**
	 * 期待結果として使用したIDを取得します。
	 *
	 * @return 期待結果のID
	 */
	public String getExpectedId() {
		return expectedId;
	}

	/**
	 * 対象領域のスクリーンショット・比較結果リストを取得します。
	 *
	 * @return 対象領域の結果オブジェクトのリスト
	 */
	public List<TargetResult> getTargetResults() {
		return targetResults;
	}

	/**
	 * 実行したテストクラス名を取得します。
	 *
	 * @return テストクラス名
	 */
	public String getTestClass() {
		return testClass;
	}

	/**
	 * 実行したテストメソッド名を取得します。
	 *
	 * @return テストメソッド名
	 */
	public String getTestMethod() {
		return testMethod;
	}

	/**
	 * 実行時に指定したCapabilityを取得します。
	 *
	 * @return capability
	 */
	public Map<String, ?> getCapabilities() {
		return capabilities;
	}

	/**
	 * 全体スクリーンショット画像を取得します。
	 *
	 * @return 全体スクリーンショット画像
	 */
	public ScreenshotImage getEntireScreenshotImage() {
		return entireScreenshotImage;
	}

	/**
	 * メッセージを取得します。
	 *
	 * @return メッセージ
	 */
	public String getMessage() {
		return message;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ScreenshotResult that = (ScreenshotResult) o;

		if (screenshotId != null ? !screenshotId.equals(that.screenshotId) : that.screenshotId != null) {
			return false;
		}
		if (result != that.result) {
			return false;
		}
		if (expectedId != null ? !expectedId.equals(that.expectedId) : that.expectedId != null) {
			return false;
		}
		if (targetResults != null ? !targetResults.equals(that.targetResults) : that.targetResults != null) {
			return false;
		}
		if (testClass != null ? !testClass.equals(that.testClass) : that.testClass != null) {
			return false;
		}
		if (testMethod != null ? !testMethod.equals(that.testMethod) : that.testMethod != null) {
			return false;
		}
		if (capabilities != null ? !capabilities.equals(that.capabilities) : that.capabilities != null) {
			return false;
		}
		return !(message != null ? !message.equals(that.message) : that.message != null);

	}

	@Override
	public int hashCode() {
		int result1 = screenshotId != null ? screenshotId.hashCode() : 0;
		final int hashPrime = 31;
		result1 = hashPrime * result1 + (result != null ? result.hashCode() : 0);
		result1 = hashPrime * result1 + (expectedId != null ? expectedId.hashCode() : 0);
		result1 = hashPrime * result1 + (targetResults != null ? targetResults.hashCode() : 0);
		result1 = hashPrime * result1 + (testClass != null ? testClass.hashCode() : 0);
		result1 = hashPrime * result1 + (testMethod != null ? testMethod.hashCode() : 0);
		result1 = hashPrime * result1 + (capabilities != null ? capabilities.hashCode() : 0);
		result1 = hashPrime * result1 + (message != null ? message.hashCode() : 0);
		return result1;
	}

	@Override
	public String toString() {
		return JSONUtils.toString(this);
	}

}
