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
package com.htmlhifive.pitalium.image.util;

import static org.junit.Assert.*;

import org.junit.Test;

import com.htmlhifive.pitalium.image.model.CompareOption;
import com.htmlhifive.pitalium.image.model.CompareOptionType;
import com.htmlhifive.pitalium.image.model.ComparisonParameters;

public class ImageComparatorFactoryTest {

	ImageComparatorFactory instance = ImageComparatorFactory.getInstance();

	@Test
	public void compareOptionがnullの場合はデフォルト() throws Exception {
		ImageComparator<? extends ComparisonParameters> actual = instance.getImageComparator(null);
		assertTrue(actual instanceof DefaultImageComparator);
	}

	@Test
	public void compareOptionの要素数が0の場合はデフォルト() throws Exception {
		ImageComparator<? extends ComparisonParameters> actual = instance.getImageComparator(new CompareOption[0]);
		assertTrue(actual instanceof DefaultImageComparator);
	}

	/**
	 * compareOptionがIGNORE_CLEAR_PIXELSの場合はIgnoringClearPixelsImageComparatorのインスタンスを返すテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void compareOptionIgnoreClearPixels() throws Exception {
		CompareOption[] options = new CompareOption[] { new CompareOption(CompareOptionType.IGNORE_CLEAR_PIXELS) };

		ImageComparator<? extends ComparisonParameters> actual = instance.getImageComparator(options);
		assertTrue(actual instanceof IgnoringClearPixelsImageComparator);
	}

	/**
	 * compareOptionがDEFAULTの場合はDefaultImageComparatorのインスタンスを返すテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void compareOptionDefault() throws Exception {
		CompareOption[] options = new CompareOption[] { new CompareOption(CompareOptionType.DEFAULT) };

		ImageComparator<? extends ComparisonParameters> actual = instance.getImageComparator(options);
		assertTrue(actual instanceof DefaultImageComparator);
	}

	/**
	 * compareOptionがSIMILARITYの場合はSimilarityImageComparatorのインスタンスを返すテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void compareOptionSimilarity() throws Exception {
		CompareOption[] options = new CompareOption[] { new CompareOption(CompareOptionType.SIMILARITY) };

		ImageComparator<? extends ComparisonParameters> actual = instance.getImageComparator(options);
		assertTrue(actual instanceof SimilarityImageComparator);
	}

	/**
	 * compareOptionがCATEGORYの場合はCategoryImageComparatorのインスタンスを返すテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void compareOptionCategory() throws Exception {
		CompareOption[] options = new CompareOption[] { new CompareOption(CompareOptionType.CATEGORY) };

		ImageComparator<? extends ComparisonParameters> actual = instance.getImageComparator(options);
		assertTrue(actual instanceof CategoryImageComparator);
	}

	/**
	 * compareOptionが複数の場合は配列の1番目のオプションが適応されたインスタンスを返すテスト。
	 *
	 * @throws Exception
	 */
	@Test
	public void compareOptions() throws Exception {
		CompareOption[] options = new CompareOption[] { new CompareOption(CompareOptionType.IGNORE_CLEAR_PIXELS),
				new CompareOption(CompareOptionType.SIMILARITY), new CompareOption(CompareOptionType.CATEGORY) };

		ImageComparator<? extends ComparisonParameters> actual = instance.getImageComparator(options);
		assertTrue(actual instanceof IgnoringClearPixelsImageComparator);
	}

}