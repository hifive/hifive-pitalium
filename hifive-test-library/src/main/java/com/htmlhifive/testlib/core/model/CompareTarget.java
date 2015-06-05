/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.core.model;

import java.io.Serializable;

import com.htmlhifive.testlib.image.model.CompareOption;

/**
 * スクリーンショット取得・比較の対象となる領域を指定するためのクラス。<br/>
 * 比較時に除外する領域、比較オプションを同時に指定することができます。
 */
public class CompareTarget implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * スクリーンショット・比較の対象領域
	 */
	private final ScreenArea compareArea;

	/**
	 * 比較オプション
	 */
	private final CompareOption[] options = null;

	/**
	 * 比較時に除外する領域
	 */
	private final ScreenArea[] excludes;

	/**
	 * スクリーンショット取得時に、指定領域を定位置に移動するか否か。trueに設定した場合、レンダリングによる不要な誤差の検出を軽減できますが、ブラウザによっては表示が崩れる場合があります。
	 */
	private final boolean moveTarget;

	/**
	 * ページ全体をスクリーンショット取得・比較の対象とします。
	 */
	public CompareTarget() {
		this(ScreenArea.of(SelectorType.TAG_NAME, "body"), null, true);
	}

	/**
	 * 指定領域をスクリーンショット取得・比較の対象とします。
	 *
	 * @param compareArea 指定領域
	 */
	public CompareTarget(ScreenArea compareArea) {
		this(compareArea, null, true);
	}

	/**
	 * 指定領域（{@link #excludes}の領域を除く）をスクリーンショット取得・比較の対象とします。
	 *
	 * @param compareArea 指定領域
	 * @param excludes 比較時に除外する領域
	 * @param moveTarget 
	 *            スクリーンショット撮影時に指定領域を定位置に移動するか否か。移動する場合はtrueを指定します。trueの場合、レンダリングによって発生する想定外の誤差を抑制しますが、ページによっては画面のレイアウトが崩れる場合があります
	 *            。
	 */
	public CompareTarget(ScreenArea compareArea, ScreenArea[] excludes, boolean moveTarget) {
		this.compareArea = compareArea;
		this.excludes = excludes != null ? excludes : new ScreenArea[0];
		this.moveTarget = moveTarget;
	}

	/**
	 * スクリーンショット・比較の対象領域を取得します。
	 *
	 * @return 比較対象のエリア
	 */
	public ScreenArea getCompareArea() {
		return compareArea;
	}

	/**
	 * 比較オプションを取得します。
	 *
	 * @return 比較オプション
	 */
	public CompareOption[] getOptions() {
		return options;
	}

	/**
	 * 比較時に除外する領域を取得します。
	 *
	 * @return 除外する領域
	 */
	public ScreenArea[] getExcludes() {
		return excludes;
	}

	/**
	 * スクリーンショット撮影時に指定領域を定位置に移動するか否かの設定を取得します。
	 *
	 * @return 撮影時に指定領域を定位置に移動するか否か。移動する場合はtrue。
	 */
	public boolean isMoveTarget() {
		return moveTarget;
	}

}
