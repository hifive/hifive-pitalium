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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.htmlhifive.pitalium.image.model.ComparedRectangleArea;
import com.htmlhifive.pitalium.image.model.ImageComparedResult;
import com.htmlhifive.pitalium.image.model.Offset;
import com.htmlhifive.pitalium.image.model.SimilarityComparisonParameters;
import com.htmlhifive.pitalium.image.model.SimilarityImageComparedResult;
import com.htmlhifive.pitalium.image.model.SimilarityUnit;

/**
 * 類似度に基づき、比較の合否判定を行うComparator
 */
class SimilarityImageComparator extends ImageComparator<SimilarityComparisonParameters> {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultImageComparator.class);

	/**
	 * デフォルトコンストラクタ
	 */
	SimilarityImageComparator() {
	}

	/**
	 * 引数ありのコンストラクタ
	 *
	 * @param parameters 比較用パラメータ
	 */
	SimilarityImageComparator(SimilarityComparisonParameters parameters) {
		double pixleByPixelThreshold = truncateDecimalPoint(parameters.getPixleByPixelThreshold());
		double featherMatrixThreshold = truncateDecimalPoint(parameters.getFeatherMatrixThreshold());
		double thresDiffThreshold = truncateDecimalPoint(parameters.getThresDiffThreshold());
		double totalDiffThreshold = truncateDecimalPoint(parameters.getTotalDiffThreshold());

		this.parameters = new SimilarityComparisonParameters(pixleByPixelThreshold, featherMatrixThreshold,
				thresDiffThreshold, totalDiffThreshold);
	}

	@Override
	protected List<Point> compare(BufferedImage image1, BufferedImage image2, int offsetX, int offsetY) {
		return null;
	}

	@Override
	public ImageComparedResult compare(BufferedImage img1, Rectangle img1Area, BufferedImage img2, Rectangle img2Area) {
		Offset offset = new Offset(img1Area.x - img2Area.x, img1Area.y - img2Area.y);
		Rectangle rect = new Rectangle(img1Area.x, img1Area.y, Math.min(img1Area.width, img2Area.width), Math.min(
				img1Area.height, img2Area.height));
		SimilarityUnit unit = SimilarityUtils.calcSimilarity(img2, img1, rect, new ComparedRectangleArea(rect), offset);

		boolean isSucceed = true;
		if (parameters.getPixleByPixelThreshold() > unit.getSimilarityPixelByPixel()) {
			isSucceed = false;
		} else if (parameters.getFeatherMatrixThreshold() > unit.getSimilarityFeatureMatrix()) {
			isSucceed = false;
		} else if (parameters.getThresDiffThreshold() > unit.getSimilarityThresDiff()) {
			isSucceed = false;
		} else if (parameters.getTotalDiffThreshold() > unit.getSimilarityTotalDiff()) {
			isSucceed = false;
		}
		return new SimilarityImageComparedResult(isSucceed, unit);
	}

	/**
	 * 実数の小数点以下の桁数を2桁にする。3桁目以降は切り捨てる。
	 *
	 * @param number 実数
	 * @return 小数点3桁目以降を切り捨てた値
	 */
	private double truncateDecimalPoint(double number) {
		int scale = getDecimalPointLength(number);
		double result = number;
		if (scale > 2) {
			result = Math.floor(number * 100) / 100;
			LOG.warn("Truncating similarlity threshold to {}. Please set the threshold with two decimal places.",
					result);
		}
		return result;
	}

	/**
	 * 実数の小数点以下の桁数を取得する。
	 *
	 * @param number 実数
	 * @return 小数点以下の桁数
	 */
	private int getDecimalPointLength(double number) {
		String[] numbers = String.valueOf(number).split(Pattern.quote("."));
		int result = 0;

		if (numbers.length == 2) {
			result = numbers[1].length();
		}
		return result;
	}
}
