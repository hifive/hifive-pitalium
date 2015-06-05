/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.image.model;

/**
 * 画像の比較オプションを表すenumクラス。
 */
public enum CompareOption {

	/**
	 * 画像の位置・サイズを含め厳密に比較します。
	 */
	STRICT,
	/**
	 * 画像の余白部分を無視して比較します。
	 */
	IGNORE_BLANK_SPACE;

}
