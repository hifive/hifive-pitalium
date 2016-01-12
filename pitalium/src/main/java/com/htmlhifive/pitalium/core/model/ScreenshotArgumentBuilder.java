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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * スクリーンショットを撮影するためのパラメーターを構築するクラス
 *
 * @author nakatani
 */
public class ScreenshotArgumentBuilder {

	static class TargetParamHolder {
		final ScreenArea target;
		final List<ScreenArea> excludes = new ArrayList<ScreenArea>();
		boolean moveTarget = true;
		boolean scrolling = false;

		public TargetParamHolder(ScreenArea target) {
			this.target = target;
		}
	}

	private String screenshotId;
	private final List<TargetParamHolder> targets = new ArrayList<TargetParamHolder>();
	private final List<DomSelector> hiddenElementSelectors = new ArrayList<DomSelector>();

	private TargetParamHolder currentHolder;

	//<editor-fold desc="Constructor">

	/**
	 * スクリーンショットIDを{@code null}で初期化します。
	 */
	protected ScreenshotArgumentBuilder() {
	}

	/**
	 * スクリーンショットIDを設定し、初期化します。
	 *
	 * @param screenshotId スクリーンショットID
	 */
	protected ScreenshotArgumentBuilder(String screenshotId) {
		this.screenshotId = screenshotId;
	}

	//</editor-fold>

	private TargetParamHolder getCurrentHolder() {
		if (currentHolder == null) {
			throw new IllegalStateException("addNewTarget is not called");
		}
		return currentHolder;
	}

	private void setCurrentHolder(ScreenArea target) {
		targets.add(currentHolder = new TargetParamHolder(target));
	}

	/**
	 * このビルダーに対して指定した条件でスクリーンショットを撮影するパラメーターを持つオブジェクトを生成します。
	 *
	 * @return スクリーンショット撮影パラメーター
	 * @throws IllegalStateException スクリーンショットIDが指定されていない場合
	 */
	public ScreenshotArgument build() {
		// Validate screenshot id
		if (Strings.isNullOrEmpty(screenshotId)) {
			throw new IllegalStateException("screenshotId must not be empty");
		}

		List<CompareTarget> compareTargets = Lists.transform(targets, new Function<TargetParamHolder, CompareTarget>() {
			@Override
			public CompareTarget apply(TargetParamHolder holder) {
				return new CompareTarget(holder.target,
						holder.excludes.toArray(new ScreenArea[holder.excludes.size()]), holder.moveTarget,
						holder.scrolling);
			}
		});

		return new ScreenshotArgument(screenshotId, new ArrayList<CompareTarget>(compareTargets),
				new ArrayList<DomSelector>(hiddenElementSelectors));
	}

	/**
	 * スクリーンショットIDを設定します。
	 *
	 * @param screenshotId スクリーンショットID
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder screenshotId(String screenshotId) {
		this.screenshotId = screenshotId;
		return this;
	}

	//<editor-fold desc="AddNewTarget">

	/**
	 * &lt;body&gt;タグを対象としたスクリーンショットを撮影する対象を追加します。
	 *
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addNewTarget() {
		return addNewTarget(new CompareTarget());
	}

	/**
	 * 既存の{@link CompareTarget}を元にスクリーンショットを撮影する対象を追加します。追加された対象には{@code target}で指定したCompareTargetの情報が全て含まれます。
	 *
	 * @param target スクリーンショットを取得、比較するための条件
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addNewTarget(CompareTarget target) {
		return addNewTarget(target.getCompareArea()).addExcludes(target.getExcludes())
				.moveTarget(target.isMoveTarget()).scrolling(target.doScroll());
	}

	/**
	 * 既存の{@link ScreenArea}を元にスクリーンショットを撮影する対象を追加します。
	 *
	 * @param target スクリーンショットを取得する対象、または領域の情報
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addNewTarget(ScreenArea target) {
		setCurrentHolder(target);
		return this;
	}

	/**
	 * スクリーンショットを撮影する対象を追加します。
	 *
	 * @param type セレクタ種別
	 * @param value 取得条件
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addNewTarget(SelectorType type, String value) {
		return addNewTarget(ScreenArea.of(type, value));
	}

	/**
	 * スクリーンショットを取得する対象をIDで指定して追加します。
	 *
	 * @param value 取得条件
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addNewTargetById(String value) {
		return addNewTarget(SelectorType.ID, value);
	}

	/**
	 * スクリーンショットを取得する対象をCSSクラス名で指定して追加します。
	 *
	 * @param value 取得条件
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addNewTargetByClassName(String value) {
		return addNewTarget(SelectorType.CLASS_NAME, value);
	}

	/**
	 * スクリーンショットを取得する対象をCSSセレクタで指定して追加します。
	 *
	 * @param value 取得条件
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addNewTargetByCssSelector(String value) {
		return addNewTarget(SelectorType.CSS_SELECTOR, value);
	}

	/**
	 * スクリーンショットを取得する対象をリンクの文字列で指定して追加します。
	 *
	 * @param value 取得条件
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addNewTargetByLinkText(String value) {
		return addNewTarget(SelectorType.LINK_TEXT, value);
	}

	/**
	 * スクリーンショットを取得する対象を&lt;input&gt;タグのname属性で指定して追加します。
	 *
	 * @param value 取得条件
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addNewTargetByName(String value) {
		return addNewTarget(SelectorType.NAME, value);
	}

	/**
	 * スクリーンショットを取得する対象をリンクの文字列の部分一致で指定して追加します。
	 *
	 * @param value 取得条件
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addNewTargetByPartialLinkText(String value) {
		return addNewTarget(SelectorType.PARTIAL_LINK, value);
	}

	/**
	 * スクリーンショットを取得する対象をタグ名で指定して追加します。
	 *
	 * @param value 取得条件
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addNewTargetByTagName(String value) {
		return addNewTarget(SelectorType.TAG_NAME, value);
	}

	/**
	 * スクリーンショットを取得する対象をXPathで指定して追加します。
	 *
	 * @param value 取得条件
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addNewTargetByXPath(String value) {
		return addNewTarget(SelectorType.XPATH, value);
	}

	/**
	 * スクリーンショットを取得する対象の座標で指定して追加します。
	 *
	 * @param x 領域の左上のx座標
	 * @param y 領域の左上のy座標
	 * @param width 領域の幅
	 * @param height 領域の高さ
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addNewTarget(double x, double y, double width, double height) {
		return addNewTarget(ScreenArea.of(x, y, width, height));
	}

	//</editor-fold>

	//<editor-fold desc="AddExclude">

	/**
	 * {@link #addNewTarget() addNewTarget}で追加したスクリーンショット取得対象に対して、除外条件を追加します。
	 *
	 * @param type セレクタ種別
	 * @param value 除外条件
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addExclude(SelectorType type, String value) {
		return addExclude(ScreenArea.of(type, value));
	}

	/**
	 * {@link #addNewTarget() addNewTarget}で追加したスクリーンショット取得対象に対して、IDを指定して除外条件を追加します。
	 *
	 * @param value 除外条件
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addExcludeById(String value) {
		return addExclude(SelectorType.ID, value);
	}

	/**
	 * {@link #addNewTarget() addNewTarget}で追加したスクリーンショット取得対象に対して、CSSクラス名を指定して除外条件を追加します。
	 *
	 * @param value 除外条件
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addExcludeByClassName(String value) {
		return addExclude(SelectorType.CLASS_NAME, value);
	}

	/**
	 * {@link #addNewTarget() addNewTarget}で追加したスクリーンショット取得対象に対して、CSSセレクタを指定して除外条件を追加します。
	 *
	 * @param value 除外条件
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addExcludeByCssSelector(String value) {
		return addExclude(SelectorType.CSS_SELECTOR, value);
	}

	/**
	 * {@link #addNewTarget() addNewTarget}で追加したスクリーンショット取得対象に対して、リンクの文字列を指定して除外条件を追加します。
	 *
	 * @param value 除外条件
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addExcludeByLinkText(String value) {
		return addExclude(SelectorType.LINK_TEXT, value);
	}

	/**
	 * {@link #addNewTarget() addNewTarget}で追加したスクリーンショット取得対象に対して、&lt;input&gt;タグのname属性を指定して除外条件を追加します。
	 *
	 * @param value 除外条件
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addExcludeByName(String value) {
		return addExclude(SelectorType.NAME, value);
	}

	/**
	 * {@link #addNewTarget() addNewTarget}で追加したスクリーンショット取得対象に対して、リンクの文字列の部分一致で除外条件を追加します。
	 *
	 * @param value 除外条件
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addExcludeByPartialLinkText(String value) {
		return addExclude(SelectorType.PARTIAL_LINK, value);
	}

	/**
	 * {@link #addNewTarget() addNewTarget}で追加したスクリーンショット取得対象に対して、タグを指定して除外条件を追加します。
	 *
	 * @param value 除外条件
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addExcludeByTagName(String value) {
		return addExclude(SelectorType.TAG_NAME, value);
	}

	/**
	 * {@link #addNewTarget() addNewTarget}で追加したスクリーンショット取得対象に対して、XPathで除外条件を追加します。
	 *
	 * @param value 除外条件
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addExcludeByXPath(String value) {
		return addExclude(SelectorType.XPATH, value);
	}

	/**
	 * {@link #addNewTarget() addNewTarget}で追加したスクリーンショット取得対象に対して、除外条件を追加します。
	 *
	 * @param exclude 除外条件
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addExclude(ScreenArea exclude) {
		TargetParamHolder holder = getCurrentHolder();
		holder.excludes.add(exclude);
		return this;
	}

	/**
	 * {@link #addNewTarget() addNewTarget}で追加したスクリーンショット取得対象に対して、座標を指定して除外条件を追加します。
	 *
	 * @param x 領域の左上のx座標
	 * @param y 領域の左上のy座標
	 * @param width 領域の幅
	 * @param height 領域の高さ
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addExclude(double x, double y, double width, double height) {
		return addExclude(ScreenArea.of(x, y, width, height));
	}

	/**
	 * {@link #addNewTarget() addNewTarget}で追加したスクリーンショット取得対象に対して、既存のセレクタまたは座標のコレクションを除外状件として追加します。
	 *
	 * @param excludes セレクタまたは座標のコレクション
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addExcludes(Collection<ScreenArea> excludes) {
		TargetParamHolder holder = getCurrentHolder();
		holder.excludes.addAll(excludes);
		return this;
	}

	/**
	 * {@link #addNewTarget() addNewTarget}で追加したスクリーンショット取得対象に対して、既存のセレクタまたは座標の配列を除外状件として追加します。
	 *
	 * @param excludes セレクタまたは座標の配列
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addExcludes(ScreenArea... excludes) {
		TargetParamHolder holder = getCurrentHolder();
		Collections.addAll(holder.excludes, excludes);
		return this;
	}

	//</editor-fold>

	/**
	 * {@link #addNewTarget() addNewTarget}で追加したスクリーンショット取得対象に対して、スクリーンショット撮影時に指定領域を定位置に移動するか否かを指定します。
	 *
	 * @param moveTarget スクリーンショット撮影時に指定領域を定位置に移動するか否か
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder moveTarget(boolean moveTarget) {
		getCurrentHolder().moveTarget = moveTarget;
		return this;
	}

	/**
	 * {@link #addNewTarget() addNewTarget}で追加したスクリーンショット取得対象に対して、スクロールを展開して撮影するか否かを指定します。
	 *
	 * @param scrolling スクロールを展開して撮影するか否か
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder scrolling(boolean scrolling) {
		getCurrentHolder().scrolling = scrolling;
		return this;
	}

	//<editor-fold desc="HiddenElementSelector">

	/**
	 * スクリーンショット撮影時に非表示にする要素を追加します。
	 *
	 * @param type セレクタ種別
	 * @param value 非表示条件
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addHiddenElementSelector(SelectorType type, String value) {
		hiddenElementSelectors.add(new DomSelector(type, value));
		return this;
	}

	/**
	 * スクリーンショット撮影時に非表示にする要素をIDを指定して追加します。
	 *
	 * @param value 非表示条件
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addHiddenElementsById(String value) {
		return addHiddenElementSelector(SelectorType.ID, value);
	}

	/**
	 * スクリーンショット撮影時に非表示にする要素をCSSクラス名を指定して追加します。
	 *
	 * @param value 非表示条件
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addHiddenElementsByClassName(String value) {
		return addHiddenElementSelector(SelectorType.CLASS_NAME, value);
	}

	/**
	 * スクリーンショット撮影時に非表示にする要素をCSSセレクタで追加します。
	 *
	 * @param value 非表示条件
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addHiddenElementsByCssSelector(String value) {
		return addHiddenElementSelector(SelectorType.CSS_SELECTOR, value);
	}

	/**
	 * スクリーンショット撮影時に非表示にする要素をリンクの文字列を指定して追加します。
	 *
	 * @param value 非表示条件
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addHiddenElementsByLinkText(String value) {
		return addHiddenElementSelector(SelectorType.LINK_TEXT, value);
	}

	/**
	 * スクリーンショット撮影時に非表示にする要素を&lt;input&gt;タグのname属性を指定して追加します。
	 *
	 * @param value 非表示条件
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addHiddenElementsByName(String value) {
		return addHiddenElementSelector(SelectorType.NAME, value);
	}

	/**
	 * スクリーンショット撮影時に非表示にする要素をリンクの文字列の部分一致で追加します。
	 *
	 * @param value 非表示条件
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addHiddenElementsByPartialLinkText(String value) {
		return addHiddenElementSelector(SelectorType.PARTIAL_LINK, value);
	}

	/**
	 * スクリーンショット撮影時に非表示にする要素をタグ名を指定して追加します。
	 *
	 * @param value 非表示条件
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addHiddenElementsByTagName(String value) {
		return addHiddenElementSelector(SelectorType.TAG_NAME, value);
	}

	/**
	 * スクリーンショット撮影時に非表示にする要素をXPathで追加します。
	 *
	 * @param value 非表示条件
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addHiddenElementsByXPath(String value) {
		return addHiddenElementSelector(SelectorType.XPATH, value);
	}

	/**
	 * 既存のセレクタまたは座標のコレクションをスクリーンショット撮影時に非表示にする要素に追加します。
	 *
	 * @param selectors 非表示にする要素を示すセレクタまたは座標のコレクション
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addHiddenElementSelectors(Collection<DomSelector> selectors) {
		hiddenElementSelectors.addAll(selectors);
		return this;
	}

	/**
	 * 既存のセレクタまたは座標の配列をスクリーンショット撮影時に非表示にする要素に追加します。
	 *
	 * @param selectors 非表示にする要素を示すセレクタまたは座標の配列
	 * @return このビルダーオブジェクト自身
	 */
	public ScreenshotArgumentBuilder addHiddenElementSelectors(DomSelector... selectors) {
		Collections.addAll(hiddenElementSelectors, selectors);
		return this;
	}

	//</editor-fold>

}
