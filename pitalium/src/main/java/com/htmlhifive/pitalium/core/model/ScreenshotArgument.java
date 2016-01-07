/*
 * Copyright (C) 2015 NS Solutions Corporation
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

import java.util.Collections;
import java.util.List;

import com.google.common.base.Strings;
import com.htmlhifive.pitalium.common.util.JSONUtils;

/**
 * <p>
 * スクリーンショットを撮影するための要素を指定するセレクタや座標、除外する要素の情報等を持つクラス
 * </p>
 * {@link #builder()}または{@link #builder(String)}でビルダーを取得してスクリーンショットの撮影方法を組み立てます。
 * 
 * @author nakatani
 */
public class ScreenshotArgument {

	/**
	 * {@link List}を要素を変更できないListへ変換します。{@code list}にnullが渡された場合空の変更できないリストが返ります。
	 * 
	 * @param list 変換するリスト
	 * @return 要素を変更できないリスト
	 */
	private static <T> List<T> toUnmodifiableList(List<T> list) {
		// TODO Move to CollectionUtils?
		if (list == null || list.isEmpty()) {
			return Collections.emptyList();
		} else {
			return Collections.unmodifiableList(list);
		}
	}

	/**
	 * {@link ScreenshotArgument}を構築するためのビルダーオブジェクトをスクリーンショットIDを指定せずに取得します。
	 * 
	 * @return ビルダーオブジェクト
	 */
	public static ScreenshotArgumentBuilder builder() {
		return new ScreenshotArgumentBuilder();
	}

	/**
	 * {@link ScreenshotArgument}を構築するためのビルダーオブジェクトをスクリーンショットIDを指定して取得します。
	 * 
	 * @param screenshotId スクリーンショットID
	 * @return ビルダーオブジェクト
	 */
	public static ScreenshotArgumentBuilder builder(String screenshotId) {
		return new ScreenshotArgumentBuilder(screenshotId);
	}

	private final String screenshotId;

	private final List<CompareTarget> targets;

	private final List<DomSelector> hiddenElementSelectors;

	/**
	 * 初期化します。
	 * 
	 * @param screenshotId スクリーンショットID
	 * @param targets スクリーンショット取得・比較の対象となる領域のリスト
	 * @param hiddenElementSelectors スクリーンショット撮影時に非表示にする要素を示すセレクタのリスト
	 * @throws NullPointerException {@code screenshotId}がnullまたは空文字だった場合
	 */
	protected ScreenshotArgument(String screenshotId, List<CompareTarget> targets,
			List<DomSelector> hiddenElementSelectors) {
		if (Strings.isNullOrEmpty(screenshotId)) {
			throw new NullPointerException("screenshotId");
		}

		this.screenshotId = screenshotId;
		this.targets = toUnmodifiableList(targets);
		this.hiddenElementSelectors = toUnmodifiableList(hiddenElementSelectors);
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
	 * スクリーンショット取得・比較の対象となる領域のリストを取得します。
	 * 
	 * @return スクリーンショット取得・比較の対象となる領域のリスト
	 */
	public List<CompareTarget> getTargets() {
		return targets;
	}

	/**
	 * スクリーンショット撮影時に非表示にする要素を示すセレクタのリストを取得します。
	 * 
	 * @return スクリーンショット撮影時に非表示にする要素を示すセレクタのリスト
	 */
	public List<DomSelector> getHiddenElementSelectors() {
		return hiddenElementSelectors;
	}

	/**
	 * {@link #getTargets() targets}、{@link #getHiddenElementSelectors() hiddenElementSelectors}
	 * のコレクションの中身を変更せず、スクリーンショットIDだけを{@code screenshotId}で指定した値に変更した新しい{@link ScreenshotArgument}を返します。
	 * 
	 * @param screenshotId 新しいスクリーンショットID
	 * @return スクリーンショットIDが変更された新しいオブジェクト
	 */
	public ScreenshotArgument withScreenshotId(String screenshotId) {
		return new ScreenshotArgument(screenshotId, targets, hiddenElementSelectors);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ScreenshotArgument that = (ScreenshotArgument) o;

		if (!screenshotId.equals(that.screenshotId)) {
			return false;
		}
		if (!targets.equals(that.targets)) {
			return false;
		}
		return hiddenElementSelectors.equals(that.hiddenElementSelectors);

	}

	@Override
	public int hashCode() {
		int result = screenshotId.hashCode();
		result = 31 * result + targets.hashCode();
		result = 31 * result + hiddenElementSelectors.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return JSONUtils.toString(this);
	}

}
