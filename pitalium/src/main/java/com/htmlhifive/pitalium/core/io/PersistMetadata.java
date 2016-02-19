/*
 * Copyright (C) 2015-2016 NS Solutions Corporation
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
package com.htmlhifive.pitalium.core.io;

import java.io.Serializable;

import com.htmlhifive.pitalium.common.util.JSONUtils;
import com.htmlhifive.pitalium.core.model.IndexDomSelector;
import com.htmlhifive.pitalium.core.selenium.PtlCapabilities;
import com.htmlhifive.pitalium.image.model.RectangleArea;

/**
 * データ永続化のメタデータ
 */
public class PersistMetadata implements Serializable {

	/**
	 * 空のメタデータ
	 */
	public static final PersistMetadata EMPTY = new PersistMetadata(null, null, null, null, null, null, null);
	private static final long serialVersionUID = 1L;

	/**
	 * 期待結果ID
	 */
	private final String expectedId;
	/**
	 * テストクラス名
	 */
	private final String className;
	/**
	 * テストメソッド名
	 */
	private final String methodName;
	/**
	 * スクリーンショットID
	 */
	private final String screenshotId;
	/**
	 * 対象エリアを指定するDOMセレクター
	 */
	private final IndexDomSelector selector;
	/**
	 * 対象エリアを指定する範囲
	 */
	private final RectangleArea rectangle;
	/**
	 * Capabilities
	 */
	private final PtlCapabilities capabilities;

	/**
	 * コンストラクタ
	 * 
	 * @param expectedId 期待結果ID
	 * @param className テストクラス名
	 */
	public PersistMetadata(String expectedId, String className) {
		this(expectedId, className, null, null, null, null, null);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param expectedId 期待結果ID
	 * @param className テストクラス名
	 * @param methodName テストメソッド名
	 * @param screenshotId スクリーンショットID
	 * @param capabilities Capability
	 */
	public PersistMetadata(String expectedId, String className, String methodName, String screenshotId,
			PtlCapabilities capabilities) {
		this(expectedId, className, methodName, screenshotId, null, null, capabilities);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param expectedId 期待結果ID
	 * @param className テストクラス名
	 * @param methodName テストメソッド名
	 * @param screenshotId スクリーンショットID
	 * @param selector セレクタ
	 * @param rectangle 対象エリアの矩形範囲
	 * @param capabilities Capability
	 */
	public PersistMetadata(String expectedId, String className, String methodName, String screenshotId,
			IndexDomSelector selector, RectangleArea rectangle, PtlCapabilities capabilities) {
		this.expectedId = expectedId;
		this.className = className;
		this.methodName = methodName;
		this.screenshotId = screenshotId;
		this.selector = selector;
		this.rectangle = rectangle;
		this.capabilities = capabilities;
	}

	/**
	 * 期待結果IDを取得します。
	 * 
	 * @return 期待結果ID
	 */
	public String getExpectedId() {
		return expectedId;
	}

	/**
	 * テストクラス名を取得します。
	 * 
	 * @return テストクラス名
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * テストメソッド名を取得します。
	 * 
	 * @return テストメソッド名
	 */
	public String getMethodName() {
		return methodName;
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
	 * セレクタを取得します。
	 * 
	 * @return セレクタ
	 */
	public IndexDomSelector getSelector() {
		return selector;
	}

	/**
	 * 対象エリアの矩形範囲を取得します。
	 * 
	 * @return 対象エリアの矩形範囲
	 */
	public RectangleArea getRectangle() {
		return rectangle;
	}

	/**
	 * Capabilityを取得します。
	 * 
	 * @return Capability
	 */
	public PtlCapabilities getCapabilities() {
		return capabilities;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		PersistMetadata metadata = (PersistMetadata) o;

		if (expectedId != null ? !expectedId.equals(metadata.expectedId) : metadata.expectedId != null) {
			return false;
		}
		if (className != null ? !className.equals(metadata.className) : metadata.className != null) {
			return false;
		}
		if (methodName != null ? !methodName.equals(metadata.methodName) : metadata.methodName != null) {
			return false;
		}
		if (screenshotId != null ? !screenshotId.equals(metadata.screenshotId) : metadata.screenshotId != null) {
			return false;
		}
		if (selector != null ? !selector.equals(metadata.selector) : metadata.selector != null) {
			return false;
		}
		if (rectangle != null ? !rectangle.equals(metadata.rectangle) : metadata.rectangle != null) {
			return false;
		}
		return !(capabilities != null ? !capabilities.equals(metadata.capabilities) : metadata.capabilities != null);

	}

	@Override
	public int hashCode() {
		final int hashPrime = 31;
		int result = expectedId != null ? expectedId.hashCode() : 0;
		result = hashPrime * result + (className != null ? className.hashCode() : 0);
		result = hashPrime * result + (methodName != null ? methodName.hashCode() : 0);
		result = hashPrime * result + (screenshotId != null ? screenshotId.hashCode() : 0);
		result = hashPrime * result + (selector != null ? selector.hashCode() : 0);
		result = hashPrime * result + (rectangle != null ? rectangle.hashCode() : 0);
		result = hashPrime * result + (capabilities != null ? capabilities.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return JSONUtils.toString(this);
	}

}
