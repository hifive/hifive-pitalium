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
import java.util.List;

import com.htmlhifive.pitalium.core.selenium.PtlWebElement;
import com.htmlhifive.pitalium.image.model.RectangleArea;

/**
 * スクリーンショット撮影用のパラメータークラス
 */
public class ScreenshotParams {

	private final ScreenAreaWrapper target;
	private final List<ScreenAreaWrapper> excludes;
	private final List<PtlWebElement> hiddenElements;
	private final boolean moveTarget;
	private final boolean scrollTarget;
	private final Integer index;

	private RectangleArea initialTargetArea;
	private List<RectangleArea> initialExcludeAreas;

	/**
	 * パラメータオブジェクトを生成します。
	 *
	 * @param target 撮影対象の領域
	 * @param excludes 比較時に除外する領域
	 * @param hiddenElements 撮影時に非表示にする要素
	 * @param moveTarget 撮影時に対象を定位置に移動させるか否か
	 * @param scrollTarget 撮影時に対象をスクロールさせるか否か
	 * @param index インデックス
	 */
	public ScreenshotParams(ScreenAreaWrapper target, List<ScreenAreaWrapper> excludes,
			List<PtlWebElement> hiddenElements, boolean moveTarget, boolean scrollTarget, Integer index) {
		this.target = target;
		this.excludes = excludes;
		this.hiddenElements = hiddenElements;
		this.moveTarget = moveTarget;
		this.scrollTarget = scrollTarget;
		this.index = index;
	}

	/**
	 * 撮影対象範囲、除外領域の矩形情報を更新します。
	 */
	public void updateInitialArea() {
		initialTargetArea = target.getArea();
		initialExcludeAreas = new ArrayList<RectangleArea>(excludes.size());
		for (ScreenAreaWrapper exclude : excludes) {
			initialExcludeAreas.add(exclude.getArea());
		}
	}

	/**
	 * 撮影対象の領域を取得します。
	 *
	 * @return 撮影対象領域
	 */
	public ScreenAreaWrapper getTarget() {
		return target;
	}

	/**
	 * 比較時に除外する領域を取得します。
	 *
	 * @return 比較時に除外する領域のリスト
	 */
	public List<ScreenAreaWrapper> getExcludes() {
		return excludes;
	}

	/**
	 * 撮影時に非表示にする要素を取得します。
	 *
	 * @return 撮影時に非表示にする要素のリスト
	 */
	public List<PtlWebElement> getHiddenElements() {
		return hiddenElements;
	}

	/**
	 * 撮影時に対象を定位置に移動させるか否かを取得します。
	 *
	 * @return 移動させる場合はtrue、させない場合はfalse
	 */
	public boolean isMoveTarget() {
		return moveTarget;
	}

	/**
	 * 撮影時に対象をスクロールさせるか否かを取得します。
	 *
	 * @return スクロールさせる場合はtrue、させない場合はfalse
	 */
	public boolean isScrollTarget() {
		return scrollTarget;
	}

	/**
	 * インデックスを取得します。
	 *
	 * @return インデックス番号
	 */
	public Integer getIndex() {
		return index;
	}

	/**
	 * 撮影の対象となる範囲を取得します。
	 *
	 * @return 撮影対象の矩形範囲
	 */
	public RectangleArea getInitialTargetArea() {
		return initialTargetArea;
	}

	/**
	 * 比較時に除外する範囲を取得します。
	 *
	 * @return 除外範囲のリスト
	 */
	public List<RectangleArea> getInitialExcludeAreas() {
		return initialExcludeAreas;
	}

}
