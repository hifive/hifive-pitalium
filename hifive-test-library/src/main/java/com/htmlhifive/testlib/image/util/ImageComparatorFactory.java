/*
 * Copyright (C) 2015 NS Solutions Corporation, All Rights Reserved.
 */

package com.htmlhifive.testlib.image.util;

import com.htmlhifive.testlib.image.model.CompareOption;

/**
 * 比較方法に対応するImageComparatorを生成するファクトリクラス
 */
public final class ImageComparatorFactory {

	private static final ImageComparatorFactory INSTANCE = new ImageComparatorFactory();

	private ImageComparatorFactory() {
	}

	/**
	 * ImageComparatorFactoryのインスタンスを取得します。
	 * 
	 * @return ImageComparatorFactoryのインスタンス（シングルトン）
	 */
	public static ImageComparatorFactory getInstance() {
		return INSTANCE;
	}

	/**
	 * 比較方法に対応したImageComparatorを取得します。
	 * 
	 * @param options 比較方法（比較オプション）
	 * @return ImageComparatorオブジェクト
	 */
	public ImageComparator getImageComparator(CompareOption[] options) {
		// TODO implements ImageComparator for each options.
		return new DefaultImageComparator();
	}
}
